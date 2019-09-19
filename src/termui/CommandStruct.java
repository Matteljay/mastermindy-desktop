package termui;

public class CommandStruct {
	public String name;
	public String abbrev;
	public String description;
	public Command command;
	public Class<?> parameter_unit;
	public int parameter_min;
	public int parameter_max;
	public CommandStruct(String name, String abbrev, String description, Command command,
			Class<?> parameter_unit, int parameter_min, int parameter_max) {
		this.name = name;
		this.abbrev = abbrev;
		this.description = description;
		this.command = command;
		this.parameter_unit = parameter_unit;
		this.parameter_min = parameter_min;
		this.parameter_max = parameter_max;
	}

}
