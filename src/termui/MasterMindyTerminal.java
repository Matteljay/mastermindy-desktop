package termui;

import java.io.*;
import java.util.ArrayList;

import mmcore.CodeProcessor;
import mmcore.HintStruct;
import mmcore.SimpleSettings;
import mmcore.StartupGenerator;

public class MasterMindyTerminal {
	private CodeProcessor codeProcessor;
	private ArrayList<Integer> theSecret;
	private CommandHandler commandHandler;
	private String partPawnArray;
	private int numPawns;
	private int turnCount;
	private int maxTurns;
	private long turnStartTime;
	private long gameStartTime;
	private Boolean hadTurnTimeout;

	public static void main(String[] args) {
		new MasterMindyTerminal();
	}
	
	public MasterMindyTerminal() {
		SimpleSettings.init();
		commandHandler = new CommandHandler(this);
		maxTurns = SimpleSettings.getInt("maxTurns");
		System.out.println("********************************");
		System.out.println("**   Welcome to MasterMindy   **");
		System.out.println("**                            **");
		System.out.println("********************************");
		System.out.println("\nType !help for more information");
		restartGame();
	}

	public void restartGame() {
		partPawnArray = Translator.getPawnArray(SimpleSettings.getInt("assortPawns"));
		numPawns = SimpleSettings.getInt("numPawns");
		printRestartMessage();
		createNewTheSecret();
		turnCount = 1;
		turnStartTime = System.currentTimeMillis();
		gameStartTime = System.currentTimeMillis();
		if(SimpleSettings.getBool("startupHints")) {
			showStartupHints();
		}
		while(true) {
			GameLoop();
		}
	}
	
	private void printRestartMessage() {
		System.out.println("\n**** New game ****");
		System.out.println("Secret: " + numPawns + " characters long, choose from: " + partPawnArray);
		System.out.print("Maxima: " + maxTurns + " turns per game");
		int gameTime = SimpleSettings.getInt("gameTime");
		int turnTime = SimpleSettings.getInt("turnTime");
		if(gameTime == 1) {
			System.out.print(", 1 minute per game");
		} else if(gameTime > 1) {
			System.out.print(", " + SimpleSettings.getInt("gameTime") + " minutes per game");
		}
		if(turnTime > 1) {
			System.out.print(", " + SimpleSettings.getInt("turnTime") + " seconds per turn");
		}
		System.out.println("");
	}

	private void createNewTheSecret() {
		codeProcessor = new CodeProcessor();
		int numPawns = SimpleSettings.getInt("numPawns");
		int assortPawns = SimpleSettings.getInt("assortPawns");
		boolean allowCopies = SimpleSettings.getBool("allowCopies");
		theSecret = codeProcessor.newRandomSecret(numPawns, assortPawns, allowCopies);
		//System.out.println("[DEBUG] The secret code is: " + Translator.mmFeelerEncode(theSecret));
	}
	
	private void showStartupHints() {
		for(ArrayList<Integer> feeler: new StartupGenerator(partPawnArray.length(), theSecret)) {
			HintStruct hint = codeProcessor.computeHint(theSecret, feeler);
			System.out.println("Hint: " + Translator.mmFeelerEncode(feeler) + " -> " + Translator.mmHintEncode(hint));
		}
	}
	
	private void GameLoop() {
		System.out.println("Turn " + turnCount + ", your input:");
		hadTurnTimeout = false;
		String userInput = stdinTimeoutLine();
		if(startsWithConfigChar(userInput)) {
			commandHandler.process(userInput.substring(1));
			return;
		}
		if(hadTurnTimeout) {
			return;
		}
		userInput = userInput.toUpperCase();
		if(!isInputValid(userInput)) {
			return;
		}
		processFeeler(userInput);
		turnCount++;
		handleTurnCountReached();
		turnStartTime = System.currentTimeMillis();
	}
	
	private Boolean isInputValid(String input) {
		if(input.isEmpty()) {
			System.out.println("Bad input: nothing typed in!");
			return false;
		}
		if(input.length() != numPawns) {
			System.out.println("Bad input: length should be " + numPawns + " characters");
			return false;
		}
		char[] partPawnArray_ca = partPawnArray.toCharArray(); 
		for(char c: input.toCharArray()) {
			if(!StrMTools.charContain(partPawnArray_ca, c)) {
				System.out.println("Bad input: character '" + c + "' is not a part of " + partPawnArray); 
				return false;
			}
		}
		return true;
	}

	private String stdinTimeoutLine() {
		String line = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while(!reader.ready()) {
				checkTimers();
				Thread.sleep(200);
			}
			return reader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}

	private void checkTimers() {
		long now = System.currentTimeMillis();
		int gameTimeSetting = SimpleSettings.getInt("gameTime");
		if(gameTimeSetting > 0 && now - gameStartTime > gameTimeSetting * 60000) {
			System.out.println("\n** You lost, game time-out. The answer was: " +
					Translator.mmFeelerEncode(theSecret));
			restartGame();
		}
		int turnTimeSetting = SimpleSettings.getInt("turnTime");
		if(turnTimeSetting > 0 && now - turnStartTime > turnTimeSetting * 1000) {
			hadTurnTimeout = true;
			System.out.println("\nTurn " + turnCount + " timed out! Press Enter");
			turnCount++;
			handleTurnCountReached();
			turnStartTime = System.currentTimeMillis();
		}
	}
	
	public void handleTurnCountReached() {
		if(maxTurns > 0 && turnCount > maxTurns) {
			System.out.println("** You lost, out of turns. The answer was: " + Translator.mmFeelerEncode(theSecret));
			restartGame();
		}
	}
	
	private Boolean startsWithConfigChar(String str) {
		return (!str.isEmpty() && "!/\\".indexOf(str.charAt(0)) >= 0);
	}
	
	private void processFeeler(String userInput) {
		ArrayList<Integer> feeler = Translator.mmFeelerDecode(userInput);
		HintStruct hint = codeProcessor.computeHint(theSecret, feeler);
		if(codeProcessor.gameWon(theSecret, hint)) {
			if(turnCount == 1) {
				System.out.println("** You won on the first attempt!! ( ^)o(^ )");
			} else {
				System.out.println("** You won in " + turnCount + " turns! (^o^)");
			}
			restartGame();
		} else {
			System.out.println("Your input was: '" + userInput + "'");
			System.out.println("Hint is: " + Translator.mmHintEncode(hint));
		}
	}

	public void exitAndCleanup() {
		System.out.println("Game exited.");
		System.exit(0);
	}
	
	public void setMaxTurns(int newMax) {		
		maxTurns = newMax;
	}
}
