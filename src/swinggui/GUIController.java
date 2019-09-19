package swinggui;

import java.util.ArrayList;

import mmcore.CodeProcessor;
import mmcore.HintStruct;
import mmcore.SimpleSettings;
import mmcore.StartupGenerator;

public class GUIController {
	private static GUIController instance = null;
	private int turnCount = 1;
	private MenuBar menuJPanel;
	private GamePanel mainJPanel;
	private ArrayList<DropImage> dropImageHolder;
	private CodeProcessor codeProcessor;
	private ArrayList<Integer> feeler;
	private ArrayList<Integer> theSecret;
	private HintStruct hint;
	private int numPawns, assortPawns;
	private int gameTime, turnTime;
	private Thread timerThread;
	private boolean stopTimerThread;
	private int turnTimeTicker;

    public static void main(String[] args) {
    	getInstance();
    }
	public static synchronized GUIController getInstance() {
		if(instance == null) {
			instance = new GUIController();
		}
		return instance;
	}
	private GUIController() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new MainFrame();
            }
        });
	}

	public void panelsBuiltEvent() {
		this.mainJPanel = GamePanel.getInstance();
		this.menuJPanel = MenuBar.getInstance();
		dropImageHolder = mainJPanel.dropImageHolder();
		createSettingsAndSecret();
		timerThread = null;
		restartTimer();
		buildAllFields();
	}
	
	private void restartTimer() {
		if(timerThread != null) {
			stopTimerThread = true;
			timerThread.interrupt();
			try {
				timerThread.join();
			} catch (InterruptedException e) {}
		}
		menuJPanel.setTurnTimerVisible(turnTime > 0);
		menuJPanel.setGameTimerVisible(gameTime > 0);
		if(turnTime > 0 || gameTime > 0) {
			timerThread = new Thread(new TimerThread());
			timerThread.start();
		}
	}
	
	private class TimerThread implements Runnable {
		private int gameTimeTicker, currentGameTime, currentTurnTime;
		public TimerThread() {
			stopTimerThread = false;
			gameTimeTicker = 0;
			turnTimeTicker = 0;
		}
	    public void run() {
	    	while(true) {
	    		if(gameTime > 0) {
	    			doGameTick();
	    		}
	    		if(turnTime > 0 && !stopTimerThread) {
	    			doTurnTick();
	    		}
		        try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					return;
				}
		        if(stopTimerThread || mainJPanel.getGameFrozen()) {
		        	return;
		        }
	    	}
	    }
	    private void doGameTick() {
    		gameTimeTicker++;
    		currentGameTime = (gameTimeTicker * 100) / (gameTime * 60);
    		menuJPanel.setGameTimerLabel(currentGameTime);
    		if(currentGameTime >= 100) {
    			mainJPanel.setGameFrozen(true);
    			fillPlayFields(theSecret);
    			new ToastMessage(mainJPanel, "You lost, game timed out");
    			stopTimerThread = true;
    		}
	    }
	    private void doTurnTick() {
    		turnTimeTicker++;
    		currentTurnTime = (turnTimeTicker * 100) / turnTime;
    		menuJPanel.setTurnTimerLabel(currentTurnTime);
    		if(currentTurnTime >= 100) {
				turnTimeTicker = 0;
    			turnCount++;
    			int maxTurns = SimpleSettings.getInt("maxTurns");
    			if(maxTurns > 0 && turnCount > maxTurns) {
    				mainJPanel.setGameFrozen(true);
    				fillPlayFields(theSecret);
    				new ToastMessage(mainJPanel, "You lost, turn timed out");
    				stopTimerThread = true;
    			} else {
    				mainJPanel.setTurnCountText(turnCount);
    			}
    		}
	    }
	}
	
	private void createSettingsAndSecret() {
		codeProcessor = new CodeProcessor();
		numPawns = SimpleSettings.getInt("numPawns");
		assortPawns = SimpleSettings.getInt("assortPawns");
		gameTime = SimpleSettings.getInt("gameTime");
		turnTime = SimpleSettings.getInt("turnTime");
		boolean allowCopies = SimpleSettings.getBool("allowCopies");
		theSecret = codeProcessor.newRandomSecret(numPawns, assortPawns, allowCopies);
	}
	
	private void buildAllFields() {
		mainJPanel.populateSideBar(assortPawns);
		mainJPanel.createPlayFields(numPawns);
		mainJPanel.clearHistoryFields();
		if(SimpleSettings.getBool("startupHints")) {
			addStartupHints();
		}
		turnCount = 1;
		mainJPanel.setTurnCountText(turnCount);
	}
	
	public void newGameRequested() {
		MainPanel.getInstance().repaint();
		createSettingsAndSecret();
		mainJPanel.setGameFrozen(false);
		mainJPanel.clearSideBar();
		mainJPanel.deletePlayFields();
		restartTimer();
		buildAllFields();
		mainJPanel.revalidate();
		mainJPanel.repaint();
	}
	
	public void maxTurnsChangedEvent() {
		int maxTurns = SimpleSettings.getInt("maxTurns");
		if(maxTurns > 0 && turnCount > maxTurns) {
			outOfTurns();
		}
	}
	
	private void addStartupHints() {
		for(ArrayList<Integer> feeler: new StartupGenerator(assortPawns, theSecret)) {
			mainJPanel.addHistoryLine(feeler, codeProcessor.computeHint(theSecret, feeler));
		}
		
	}
	
	public void processTurnEvent() {
		if(isTurnInputValid() == false) {
			new ToastMessage(mainJPanel, "Fill 'em all!");
			return;
		}
		if(SimpleSettings.getInt("turnTime") > 0) {
			turnTimeTicker = 0;
			menuJPanel.setTurnTimerLabel(0);
		}		
		getFeelerAndHintFromTurnInput();
		if(codeProcessor.gameWon(theSecret, hint)) {
			mainJPanel.setGameFrozen(true);
			mainJPanel.addHistoryLine(theSecret, hint);
			new ToastMessage(mainJPanel, "You rulez!");
			return;
		}
		turnCount++;
		int maxTurns = SimpleSettings.getInt("maxTurns");
		if(maxTurns > 0 && turnCount > maxTurns) {
			outOfTurns();
			return;
		}
		mainJPanel.setTurnCountText(turnCount);
		mainJPanel.addHistoryLine(feeler, codeProcessor.computeHint(theSecret, feeler));
		mainJPanel.clearPlayFields();
	}

	private Boolean isTurnInputValid() {
		for(int i = 0; i < dropImageHolder.size(); i++) {
			if(!dropImageHolder.get(i).isFilled()) {
				return false;
			}
		}
		return true;
	}
	
	private void getFeelerAndHintFromTurnInput() {
		feeler = new ArrayList<Integer>();
		for(int i = 0; i < dropImageHolder.size(); i++) {
			if(dropImageHolder.get(i).isFilled()) {
				feeler.add(dropImageHolder.get(i).getFilled().getID());
			}
		}
		hint = codeProcessor.computeHint(theSecret, feeler);
	}
	
	private void outOfTurns() {
		mainJPanel.setGameFrozen(true);
		mainJPanel.addHistoryLine(feeler, hint);
		fillPlayFields(theSecret);
		new ToastMessage(mainJPanel, "You lost, press NEW");
	}
	
	private void fillPlayFields(ArrayList<Integer> pawns) {
		mainJPanel.clearPlayFields();
		for(int i = 0; i < dropImageHolder.size(); i++) {
			DropImage sideBarDropImage = mainJPanel.getSideBarDropImage(pawns.get(i));
			dropImageHolder.get(i).cloneDragImageFrom(sideBarDropImage);
		}
	}

}
