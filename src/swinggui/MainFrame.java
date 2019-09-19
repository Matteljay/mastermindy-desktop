package swinggui;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.*;

import mmcore.SimpleSettings;

public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = -7203506363637754039L;
	
	public MainFrame() {
    	SimpleSettings.init();
    	URL url = getClass().getResource("res/icon_m.png");
    	ImageIcon img = new ImageIcon(url);
    	setIconImage(img.getImage());
        setSize(retrieveWindowSize());
        if(wasMaximized()) {
        	setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        }
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
        	@Override
        	public void windowOpened(WindowEvent e) {
        		GUIController.getInstance().panelsBuiltEvent();
        		super.windowOpened(e);
        	}
            public void windowClosing(WindowEvent e) {
            	storeWindowSizeAndState();
                System.exit(0);
            }
    	});

        try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {}
        setLocationRelativeTo(null);
        setTitle("MasterMindy");
        JPanel gamePanel = MainPanel.getInstance();
        add(gamePanel);
        setVisible(true);
    }
    
    private Dimension retrieveWindowSize() {
    	String[] parts = SimpleSettings.get("windowSize").split(";");
		return new Dimension(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	}

    private Boolean wasMaximized() {
    	return (SimpleSettings.get("windowState").equals("maximized"));
    }
	private void storeWindowSizeAndState() {
		int winState = getExtendedState();
		if(winState == JFrame.NORMAL) {
			String joined = String.join(";", String.valueOf(getWidth()), String.valueOf(getHeight()));
			SimpleSettings.store("windowSize", joined);
			SimpleSettings.store("windowState", "normal");
		} else if((winState & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
			SimpleSettings.store("windowState", "maximized");
		}
    }
}
