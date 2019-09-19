package swinggui;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

	private static final long serialVersionUID = 3784552066765192399L;
	private static MainPanel instance = null;
    private String currentCard;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
	public static synchronized MainPanel getInstance() {
		if(instance == null) {
			instance = new MainPanel();
		}
		return instance;
	}
	
	private MainPanel() {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		cardLayout = new CardLayout();
    	cardPanel = new JPanel(cardLayout);
    	
    	constraints.fill = GridBagConstraints.BOTH;
    	constraints.weightx = 1.0;
    	constraints.weighty = 0.02;
    	constraints.gridx = 0;
    	constraints.gridy = 0;
        MenuBar menuPanel = MenuBar.getInstance();
        add(menuPanel, constraints);

    	constraints.weighty = 1.0;
    	constraints.gridy = 1;
        GamePanel mainCardPanel = GamePanel.getInstance();
        SettingsPanel settingsCardPanel = SettingsPanel.getInstance();
        cardPanel.add(mainCardPanel, "mainCard");
        currentCard = "mainCard";
		JScrollPane scroller = new JScrollPane(settingsCardPanel);
		scroller.setBorder(null);
        cardPanel.add(scroller, "settingsCard");
        add(cardPanel, constraints);
	}
	
	public void showCard(String card) {
		currentCard = card;
		cardLayout.show(cardPanel, currentCard);
	}
	
	public String getCurrentCard() {
		return currentCard;
	}
}
