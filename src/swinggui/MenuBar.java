package swinggui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MenuBar extends JPanel {
	
	private static final long serialVersionUID = 5584173666284037240L;
	private static MenuBar instance = null;
	private JProgressBar gameTimeBar, turnTimeBar;
    
	public static synchronized MenuBar getInstance() {
		if(instance == null) {
			instance = new MenuBar();
		}
		return instance;
	}
	
	private MenuBar() {
    	setBorder(new EmptyBorder(0, 3, 0, 1));
        setLayout(new GridBagLayout());
        Color menuBg = new Color(226, 226, 226);
        setBackground(menuBg);
        GridBagConstraints constraints = new GridBagConstraints();
        
        constraints.fill = GridBagConstraints.BOTH;
    	constraints.weightx = 0.5;
    	constraints.weighty = 1.0;
    	constraints.gridx = 0;
    	constraints.gridy = 0;
    	constraints.anchor = GridBagConstraints.LINE_START;
        turnTimeBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        turnTimeBar.setPreferredSize(turnTimeBar.getMinimumSize());
        turnTimeBar.setString("turn");
        turnTimeBar.setStringPainted(true);
        add(turnTimeBar, constraints);
        
        constraints.gridx = 1;
        gameTimeBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        gameTimeBar.setPreferredSize(gameTimeBar.getMinimumSize());
        gameTimeBar.setString("game");
        gameTimeBar.setStringPainted(true);
        add(gameTimeBar, constraints);
        
        constraints.weightx = 0.5;
        constraints.gridx = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        add(new JLabel(), constraints);
        
        constraints.weightx = 0.1;
        constraints.gridx = 3;
        constraints.anchor = GridBagConstraints.LINE_END;
        JButton toggleBtnNew = new JButton("New");
        toggleBtnNew.setFocusable(false);
        toggleBtnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUIController.getInstance().newGameRequested();
			}
		});
        add(toggleBtnNew, constraints);
        
        constraints.gridx = 4;
        JButton toggleBtnSet = new JButton("Set");
        toggleBtnSet.setFocusable(false);
        toggleBtnSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(MainPanel.getInstance().getCurrentCard() == "mainCard") {
					toggleBtnNew.setVisible(false);
					toggleBtnSet.setText("Back");
					MainPanel.getInstance().showCard("settingsCard");
				} else {
					toggleBtnNew.setVisible(true);
					toggleBtnSet.setText("Set");
					MainPanel.getInstance().showCard("mainCard");
				}
			}
		});
        add(toggleBtnSet, constraints);
    }
	
	public void setTurnTimerLabel(Integer prec) {
		turnTimeBar.setValue(prec);
	}
	
	public void setGameTimerLabel(Integer prec) {
		gameTimeBar.setValue(prec);
	}
	
	public void setTurnTimerVisible(boolean vis) {
		turnTimeBar.setVisible(vis);
	}
	
	public void setGameTimerVisible(boolean vis) {
		gameTimeBar.setVisible(vis);
	}

}
