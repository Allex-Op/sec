package pt.ulisboa.tecnico.sec.secureclient.commands.helpcommands;

import java.util.List;

import pt.ulisboa.tecnico.sec.secureclient.commands.Command;
import pt.ulisboa.tecnico.sec.secureclient.exceptions.NotSufficientArgumentsException;

public class HelpCommand extends Command {
	
	public static final int EXPECTED_ARGUMENTS = 0;

	@Override
	public void execute(List<String> arguments) throws NotSufficientArgumentsException {
		verifyNumberOfArguments(arguments.size(), EXPECTED_ARGUMENTS);
		
		System.out.println(
				System.lineSeparator() + "## ---------------------------------------------------------- ##" + 
				System.lineSeparator() + "   Supported Commands are:" +
				System.lineSeparator() + "   1. obtainreport <user_id> <epoch>" + 
				System.lineSeparator() + "   2. usersatlocation <x_location> <y_location> <epoch>" +
				System.lineSeparator() + "   3. help" + 
				System.lineSeparator() + "   4. clear" + 
				System.lineSeparator() + "   5. quit" +
				System.lineSeparator() + "## ---------------------------------------------------------- ##"
		);
	}

}
