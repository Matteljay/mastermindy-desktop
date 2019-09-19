package swinggui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import mmcore.SimpleSettings;

public class SettingsPanel extends JPanel {

	private static final long serialVersionUID = -8813637435687128083L;
	private static SettingsPanel instance = null;
	private GridBagConstraints constraints;
	
	public static synchronized SettingsPanel getInstance() {
		if(instance == null) {
			instance = new SettingsPanel();
		}
		return instance;
	}
	
	private SettingsPanel() {
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
    	constraints.weighty = 0.1;
		
		addGUISetting("numPawns", "Pawn field amount", new RestartCommand(),
				new String[]{"3", "4", "5", "6", "7", "8", "10"});
		addGUISetting("assortPawns", "Pawn color assortment", new RestartCommand(),
				new String[]{"3", "4", "5", "6", "8", "9", "10", "12"});
		addGUISetting("maxTurns", "Max turns per game", new UpdateTurnsCommand(),
				new String[]{"none", "4", "6", "8", "12", "20", "30"});
		addGUISetting("gameTime", "Max time per game (minutes)", new RestartCommand(),
				new String[]{"none", "1", "2", "3", "5", "10", "15"});
		addGUISetting("turnTime", "Max time per turn (seconds)", new RestartCommand(),
				new String[]{"none", "5", "10", "20", "30", "60", "120"});
		addGUISetting("allowCopies", "Allow duplicates in the secret", new RestartCommand(),
				null);
		addGUISetting("startupHints", "Show hints at startup", new RestartCommand(),
				null);
		
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridx = 0;
		constraints.weightx = 0.2;
		constraints.weighty = 0;
		constraints.gridwidth = 2;
		JLabel label = new JLabel(" ");
		add(label, constraints);
		label = new JLabel("MasterMindy created by Matteljay@pm.me");
		add(label, constraints);
		label = new JLabel("Version 20190919");
		add(label, constraints);
		label = new JLabel("Motivate more work like this by donating");
		add(label, constraints);
	}
	
	private void addGUISetting(String key, String description, Command command, String[] choices) {
		JLabel label = new JLabel(description);
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridx = 0;
		constraints.weightx = 0.2;
		add(label, constraints);
		
		Component component;
		if(choices == null) {
			component = addChoiceCheckBox(key, command, choices);
		} else {
			component = addChoiceDropDown(key, command, choices);
		}
		label.setLabelFor(component);
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.gridx = 1;
		constraints.weightx = 0.1;
		add(component, constraints);
	}
	
	private Component addChoiceCheckBox(String key, Command command, String[] choices) {
		JCheckBox choiceCheckBox = new JCheckBox();
		choiceCheckBox.setSelected(SimpleSettings.getBool(key));
		choiceCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(SimpleSettings.getBool(key) == choiceCheckBox.isSelected()) {
					return;
				}
				SimpleSettings.store(key, choiceCheckBox.isSelected() ? "1" : "0");
				command.execute(null);
			}
		});
		return choiceCheckBox;
	}
	
	private Component addChoiceDropDown(String key, Command command, String[] choices) {
		JComboBox<String> choiceDropDown = new JComboBox<>(choices);
		String value = SimpleSettings.get(key);
		choiceDropDown.setSelectedItem(value);
		choiceDropDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ItemString = (String) choiceDropDown.getSelectedItem();
				if(SimpleSettings.get(key).equals(ItemString)) {
					return;
				}
				SimpleSettings.store(key, ItemString);
				command.execute(null);
			}
		});
		return choiceDropDown;
	}

	private class RestartCommand implements Command {
		public void execute(String param) {
			GUIController.getInstance().newGameRequested();
			new ToastMessage(instance, "Saved, game restarted");
		}
	}
	
	private class UpdateTurnsCommand implements Command {
		public void execute(String param) {
			GUIController.getInstance().maxTurnsChangedEvent();
			new ToastMessage(instance, "Saved, max turns updated");
		}
	}
}
