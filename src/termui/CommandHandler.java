package termui;

import java.util.ArrayList;

import mmcore.SimpleSettings;

public class CommandHandler {
	String[] comparts;
	MasterMindyTerminal mmterminal;
	ArrayList<CommandStruct> commandDefs;
	
	public CommandHandler(MasterMindyTerminal parent) {
		this.mmterminal = parent;
		commandDefs = new ArrayList<CommandStruct>();
		commandDefs.add(new CommandStruct(
				"help", "h", "shows help message", new HelpCmd(), null, 0, 0));
		commandDefs.add(new CommandStruct(
				"settings", "s", "shows all settings in compact format", new CompactSettingsCmd(), null, 0, 0));
		commandDefs.add(new CommandStruct(
				"quit", "q", "exits the game", new QuitCmd(), null, 0, 0));
		commandDefs.add(new CommandStruct(
				"restart", "re", "restarts the game", new RestartCmd(), null, 0, 0));
		commandDefs.add(new CommandStruct(
				"numPawns", "np", "number of pawn slots", new RestartCmd(), int.class, 1, 12));
		commandDefs.add(new CommandStruct(
				"assortPawns", "ap", "number of pawn types/colors", new RestartCmd(), int.class, 1, 12));
		commandDefs.add(new CommandStruct(
				"maxTurns", "mt", "maximum turns per game, zero will disable", new MaxTurnsCmd(), int.class, 0, 30));
		commandDefs.add(new CommandStruct(
				"gameTime", "gt", "time per game in minutes, zero will disable", new RestartCmd(), int.class, 0, 99));
		commandDefs.add(new CommandStruct(
				"turnTime", "tt", "time per turn in seconds, zero will disable", new RestartCmd(), int.class, 0, 999));
		commandDefs.add(new CommandStruct(
				"allowCopies", "ac", "allow duplicates in the secret code (0 = no, 1 = yes)", new RestartCmd(), int.class, 0, 1));
		commandDefs.add(new CommandStruct(
				"startupHints", "sh", "show hints at startup (0 = no, 1 = yes)", new RestartCmd(), int.class, 0, 1));
	}
	
	public void process(String command) {
		comparts = command.split(" ");
		String com = comparts[0].toLowerCase();
		if(com.isEmpty()) {
			System.out.println("Cannot process empty command, try !help");
			return;
		}
		for(CommandStruct comStruct: commandDefs) {
			if(com.equalsIgnoreCase(comStruct.name) || com.equals(comStruct.abbrev)) {
				if(comStruct.parameter_unit == null) {
					comStruct.command.execute(null);
				} else {
					if(comparts.length <= 1) {
						System.out.println("Parameter required for " + comStruct.name +
								", current value: " + SimpleSettings.get(comStruct.name));
					} else {
						handleParameter(comStruct, comparts[1]);
					}
				}
				return;
			}
		}
		System.out.println("Unknown command: " + comparts[0]);
	}
	
	private void handleParameter(CommandStruct command, String param) {
		String currentSetting = SimpleSettings.get(command.name);
		int parLim = Integer.MIN_VALUE;
		if(command.parameter_unit == int.class) {
			try {
				parLim = Integer.parseInt(comparts[1]);
			} catch(NumberFormatException e) {
				System.out.println(command.name + " requires a valid number as parameter");
				return;
			}
		} else if(command.parameter_unit == String.class) {
			parLim = comparts[1].length();
		}
		if(parLim < command.parameter_min || parLim > command.parameter_max) {
			System.out.println("Input parameter out of range <" +
					command.parameter_min + "-" + command.parameter_max + ">");
			return;
		}
		if(currentSetting.equals(comparts[1])) {
			System.out.println("No change, " + command.name + " already had value " + currentSetting);
			return;
		}
		SimpleSettings.store(command.name, comparts[1]);
		System.out.println("Changed " + command.name + " to: " + comparts[1]);
		command.command.execute(comparts[1]);
	}
	
	private class HelpCmd implements Command {
		public void execute(String param) {
			System.out.println("You can use '!', '/' or '\\' to send a command");
			for(CommandStruct comStruct: commandDefs) {
				if(comStruct.parameter_unit == null) {
					System.out.println(comStruct.name);
				} else {
					System.out.println(comStruct.name + " <" + comStruct.parameter_min + "-" + comStruct.parameter_max +
							"> current setting: " + SimpleSettings.get(comStruct.name));
				}
				System.out.println("  " + comStruct.description + ", abbreviation: " + comStruct.abbrev);
			}
			System.out.println("");
		}
	}
	
	private class CompactSettingsCmd implements Command {
		public void execute(String param) {
			for(CommandStruct comStruct: commandDefs) {
				if(comStruct.parameter_unit != null) {
					System.out.println(comStruct.name + ": " + SimpleSettings.get(comStruct.name));
				}
			}
		}
	}
	
	private class QuitCmd implements Command {
		public void execute(String param) {
			mmterminal.exitAndCleanup();
		}
	}
	
	private class RestartCmd implements Command {
		public void execute(String param) {
			mmterminal.restartGame();
		}
	}

	private class MaxTurnsCmd implements Command {
		public void execute(String param) {
			int maxTurns = Integer.parseInt(param);
			mmterminal.setMaxTurns(maxTurns);
			mmterminal.handleTurnCountReached();
		}
	}

}
