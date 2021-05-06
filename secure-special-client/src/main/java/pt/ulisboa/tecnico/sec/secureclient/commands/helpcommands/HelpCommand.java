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
				System.lineSeparator() + "   * obtainreport <user_id> <epoch>" +
				System.lineSeparator() + "   * usersatlocation <x_location> <y_location> <epoch>" +
				System.lineSeparator() + "   * getproofsofuseratepochs <user_id> <epoch_1> <epoch_2> ... <epoch_n>" +
				System.lineSeparator() + "   * submitreporttest <option>" +
				System.lineSeparator() + "   * gatherproofstest <option>" +
				System.lineSeparator() + "   * obtainreporttest <option>" +
				System.lineSeparator() + "   * help" +
				System.lineSeparator() + "   * clear" +
				System.lineSeparator() + "   * quit" +
				System.lineSeparator() + "## ---------------------------------------------------------- ##" +
				System.lineSeparator() + "   Submit Report Test Cases:" +
				System.lineSeparator() + "	 1. Valid Report" +
				System.lineSeparator() + "	 2. Report with duplicated proofs" +
				System.lineSeparator() + "	 3. Report duplicated in the same epoch" +
				System.lineSeparator() + "	 4. Packet sent to the server with repeated nonce (Replay Attack)" +
				System.lineSeparator() + "	 5. Report with message stealing" +
				System.lineSeparator() + "	 6. Report with invalid digital signature" +
				System.lineSeparator() + "	 7. Report with less proofs that necessary" +
				System.lineSeparator() + "	 8. Report Proofs in different epochs" +
				System.lineSeparator() + "## ---------------------------------------------------------- ##" +
				System.lineSeparator() + "   Submit Gather Proofs Test Cases:" +
				System.lineSeparator() + "	 1. Ask proof out of range" +
				System.lineSeparator() + "	 2. Ask proof with Replay Attack" +
				System.lineSeparator() + "	 3. Ask proof with Invalid Digital Signature" +
				System.lineSeparator() + "## ---------------------------------------------------------- ##" + 
				System.lineSeparator() + "   Obtain Reports Test Cases:" + 
				System.lineSeparator() + "   1. Obtain Report from invalid user" + 
				System.lineSeparator() + "   2. Obtain Report from another user - priviledge exception" + 
				System.lineSeparator() + "   3. Obtain Report with invalid signature" + 
				System.lineSeparator() + "   4. Obtain Report with invalid epoch" +
				System.lineSeparator() + "   5. Obtain Report with repeated nonce (Replay Attack)"
		);
	}

}
