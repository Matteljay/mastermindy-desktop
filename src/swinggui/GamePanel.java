package swinggui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import mmcore.HintStruct;

public class GamePanel extends JPanel {
	private static final long serialVersionUID = 1317730437559479054L;
	private static GamePanel instance = null;
	private final String[] pawnColors = { // File names
			"red", "green", "blue",
			"fuchsia", "yellow", "aqua",
			"maroon", "lime", "navy",
			"purple", "olive", "teal"};
	private final Color backgroundColor = new Color(214, 217, 223); // Default
	//private final Color backgroundColor = new Color(115, 191, 157);
	//private final Color backgroundColor = new Color(204, 204, 190);
	//private final Color backgroundColor = new Color(201, 201, 181);
	//private final Color backgroundColor = new Color(108, 131, 126);
	private final Color squareEven = new Color(200, 200, 200);
	private final Color squareOdd = new Color(226, 226, 226);
	private final int MAX_HISTORY_DISPLAYED = 128;
	private ArrayList<DropImage> dropImageHolder;
	private JScrollPane scroller;
	private JPanel historyGridPanel;
	private JPanel sideBarPanel;
	private JPanel playFieldPanel;
	private JButton btnFinishTurn;
	private Boolean isGameFrozen = false;
	private int historyDisplayedCounter = 0;
	private Dimension dimZero = new Dimension(0, 0);
	private BufferedImage hintBlackImage, hintWhiteImage, hintBlankImage;
	
	public static synchronized GamePanel getInstance() {
		if(instance == null) {
			instance = new GamePanel();
		}
		return instance;
	}
	
	private GamePanel() {
		setLayout(new GridBagLayout());
		
		dropImageHolder = new ArrayList<DropImage>();
		
		JPanel leftPanel = createLeftPanel();
		leftPanel.setPreferredSize(dimZero);
        add(leftPanel, putConstraints(0, 0, 1.0, 1.0));
		
        sideBarPanel = new JPanel(new GridBagLayout());
        sideBarPanel.setPreferredSize(dimZero);
		add(sideBarPanel, putConstraints(1, 0, 0.15, 1.0));
		
		isGameFrozen = false;
	}
	
	private JPanel createLeftPanel() {
		JPanel leftPanel = new JPanel(new GridBagLayout());
		leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		
		playFieldPanel = new JPanel(new GridBagLayout());
		playFieldPanel.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				if(playFieldPanel.getComponentCount() > 0) {
					playFieldResized();
				}
			}
			public void componentShown(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
		playFieldPanel.setPreferredSize(dimZero);
		leftPanel.add(playFieldPanel, putConstraints(0, 0, 1.0, 0.2));
		
		JPanel historyHolderPanel = new JPanel(new GridBagLayout());
		historyHolderPanel.setPreferredSize(dimZero);
		historyGridPanel = new JPanel(new GridBagLayout());
		scroller = new JScrollPane(historyGridPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(null);
		historyHolderPanel.add(scroller, putConstraints(0, 0, 1.0, 1.0));
		leftPanel.add(historyHolderPanel, putConstraints(0, 1, 1.0, 1.0));
		
		hintBlackImage = getBufferedImageFromFile("res/hints/black.png");
		hintWhiteImage = getBufferedImageFromFile("res/hints/white.png");
		hintBlankImage = getBufferedImageFromFile("res/hints/blank.png");
		historyGridPanel.setBackground(backgroundColor);
		leftPanel.setBackground(backgroundColor);
		return leftPanel;
	}
	
	private void playFieldResized() {
		int newHeight = playFieldPanel.getHeight();
		for(Component comp: historyGridPanel.getComponents()) {
			if(!(comp instanceof JPanel)) {
				continue;
			}
			JPanel historyLine = (JPanel) comp;
			historyLine.setPreferredSize(new Dimension(0, (int)(newHeight * 0.5)));
		}
		historyGridPanel.revalidate();
		historyGridPanel.repaint();
		scroller.getVerticalScrollBar().setUnitIncrement(newHeight / 4);
	}
	
	public void populateSideBar(int amount) {
		for(int i = 0; i < amount; i++) {
			DropImage dropImage = new DropImage(9);
			dropImage.createDragImage(this, getBufferedImageFromFile("res/pawns/" + pawnColors[i] + ".png"), i);
			dropImage.addDragImageEvent(new SidebarDragImageEvent());
			dropImage.setBackground(i % 2 == 0 ? squareEven : squareOdd);
			sideBarPanel.add(dropImage, putConstraints(0, i, 1.0, 1.0));
		}
	}
	
	public void clearSideBar() {
		sideBarPanel.removeAll();
	}
	
	public DropImage getSideBarDropImage(int index) {
		return (DropImage) sideBarPanel.getComponent(index);
	}
	
	private class SidebarDragImageEvent implements DragImageEvent {
		public Boolean allowDrag(DropImage d) {
			return (!isGameFrozen);
		}
		public void startedDrag(DropImage dropImage) {
			dropImage.cloneDragImageFrom(dropImage);
		}
		public void clicked(DropImage dropImage) {
			if(isGameFrozen) {
				return;
			}
			for(DropImage freeDropImage: dropImageHolder) {
				if(freeDropImage.getGroup() == 9 && freeDropImage.getFilled() == null) {
					freeDropImage.cloneDragImageFrom(dropImage);
					return;
				}
			}
		}
	}
	
	public void createPlayFields(int amount) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.3;
		constraints.weighty = 1.0;
		constraints.gridheight = 3;
        for(int i = 0; i < amount; i++) {
        	DropImage dropImage = new DropImage(9);
        	dropImageHolder.add(dropImage);
        	dropImage.addDragImageEvent(new PlayfieldDragImageEvent());
        	dropImage.setBackground(i % 2 == 0 ? squareEven : squareOdd);
        	playFieldPanel.add(dropImage, constraints);
        }
        btnFinishTurn = new JButton("Ok");
        btnFinishTurn.setPreferredSize(dimZero);
        btnFinishTurn.setFocusable(false);
        btnFinishTurn.addActionListener(new finishTurn());
        constraints.weightx = 0.05;
        playFieldPanel.add(Box.createHorizontalStrut(0), constraints);
        constraints.gridheight = 1;
        constraints.weightx = 0.5;
        constraints.gridy = 0;
        constraints.weighty = 0.2;
        playFieldPanel.add(Box.createVerticalStrut(0), constraints);
        constraints.gridy = 1;
        constraints.weighty = 1.0;
        playFieldPanel.add(btnFinishTurn, constraints);
        constraints.gridy = 2;
        constraints.weighty = 0.2;
        playFieldPanel.add(Box.createVerticalStrut(0), constraints);
        constraints.gridheight = 3;
        constraints.weightx = 0.05;
        playFieldPanel.add(Box.createHorizontalStrut(0), constraints);
        playFieldPanel.setBackground(backgroundColor);
	}
	
	public void deletePlayFields() {
		clearPlayFields();
		dropImageHolder.clear();
		playFieldPanel.removeAll();
	}
	
	public void clearPlayFields() {
		for(int i = 0; i < dropImageHolder.size(); i++) {
			dropImageHolder.get(i).removeDragImage();
		}
		playFieldPanel.revalidate();
		playFieldPanel.repaint();
	}
	
	private class PlayfieldDragImageEvent implements DragImageEvent {
		public Boolean allowDrag(DropImage d) {
			return (!isGameFrozen);
		}
		public void startedDrag(DropImage d) {}
		public void clicked(DropImage dropImage) {
			if(!isGameFrozen) {
				dropImage.removeDragImage();
			}
		}
	}
	
	private class finishTurn implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(!isGameFrozen) {
				GUIController.getInstance().processTurnEvent();
			}
		}
	}
	
	public void setTurnCountText(Integer turnCount) {
		if(turnCount == null) {
			btnFinishTurn.setText("Ok");
		} else {
			btnFinishTurn.setText("Ok#" + turnCount);
		}
	}
	
	public void addHistoryLine(ArrayList<Integer> feeler, HintStruct hint) {
		if(historyDisplayedCounter >= MAX_HISTORY_DISPLAYED) {
			clearHistoryFields();
		}
		historyDisplayedCounter++;
		int gridypos = MAX_HISTORY_DISPLAYED - historyDisplayedCounter;
		JPanel historyLine = new JPanel(new GridBagLayout());
		historyLine.setPreferredSize(new Dimension(0, (int)(playFieldPanel.getSize().height * 0.5)));
		
		fillHistoryLine(historyLine, feeler, hint);

		historyGridPanel.add(historyLine, putConstraints(0, gridypos, 1.0, 0.0));
		historyGridPanel.revalidate();
		historyGridPanel.repaint();
		scroller.getVerticalScrollBar().setValue(0); // scroll back to top
	}
	
	private void fillHistoryLine(JPanel historyLine, ArrayList<Integer> feeler, HintStruct hint) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.8;
		constraints.weighty = 1.0;
		constraints.gridheight = 3;
		for(int i = 0; i < feeler.size(); i++) {
			DropImage sideBarDropImage = getSideBarDropImage(feeler.get(i));
			JImage imageCopy = new JImage(sideBarDropImage.getFilled().getImage());
			historyLine.add(imageCopy, constraints);
		}
		JPanel hintBlock = new JPanel(new GridBagLayout());
		for(int i = 0; i < hint.numBlacks; i++) {
			JImage imageCopy = new JImage(hintBlackImage);
			hintBlock.add(imageCopy, constraints);
		}
		for(int i = 0; i < hint.numWhites; i++) {
			JImage imageCopy = new JImage(hintWhiteImage);
			hintBlock.add(imageCopy, constraints);
		}
		for(int i = 0; i < hint.numBlanks; i++) {
			JImage imageCopy = new JImage(hintBlankImage);
			hintBlock.add(imageCopy, constraints);
		}
		constraints.weightx = 0.1;
		historyLine.add(Box.createHorizontalStrut(0), constraints);
		constraints.gridheight = 1;
		constraints.weightx = 1.0;
		constraints.gridy = 0;
		historyLine.add(Box.createVerticalStrut(0), constraints);
		constraints.gridy = 1;
		historyLine.add(hintBlock, constraints);
		constraints.gridy = 2;
		historyLine.add(Box.createVerticalStrut(0), constraints);
		constraints.weightx = 0.2;
		historyLine.add(Box.createHorizontalStrut(0), constraints);
		hintBlock.setBackground(backgroundColor);
		historyLine.setBackground(backgroundColor);
	}
	
	public void clearHistoryFields() {
		historyDisplayedCounter = 0;
		historyGridPanel.removeAll();
		historyGridPanel.add(Box.createVerticalStrut(0), putConstraints(0, MAX_HISTORY_DISPLAYED, 1.0, 1.0));
		historyGridPanel.revalidate();
		historyGridPanel.repaint();
	}
	
	private GridBagConstraints putConstraints(int gridx, int gridy, double weightx, double weighty) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.weightx = weightx;
		constraints.weighty = weighty;
		return constraints;
	}
	
	private BufferedImage getBufferedImageFromFile(String fileName) {
		URL url = getClass().getResource(fileName);
		BufferedImage img = null;
		try {
		    img = ImageIO.read(url);
		} catch (IOException e) {
		    e.printStackTrace();
		}
		return img;
	}

	public boolean getGameFrozen() {
		return isGameFrozen;
	}

	public void setGameFrozen(boolean frozen) {
		isGameFrozen = frozen;
		btnFinishTurn.setEnabled(!frozen);
	}

	public ArrayList<DropImage> dropImageHolder() {
		return dropImageHolder;
	}

	public JPanel getHistoryPanel() {
		return historyGridPanel;
	}

}
