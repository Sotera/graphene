package graphene.util.fs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class SystemCommand {
	public SystemCommand() {
		// TODO Auto-generated constructor stub
	}

	public void doCommand(List<String> command) throws IOException {
		String s = null;

		ProcessBuilder pb = new ProcessBuilder(command);
		Process process = pb.start();

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));

		// read the output from the command
		System.out.println("Here is the standard output of the command:\n");
		while ((s = stdInput.readLine()) != null) {
			System.out.println(s);
		}

		// read any errors from the attempted command
		System.out
				.println("Here is the standard error of the command (if any):\n");
		while ((s = stdError.readLine()) != null) {
			System.out.println(s);
		}
	}
}
