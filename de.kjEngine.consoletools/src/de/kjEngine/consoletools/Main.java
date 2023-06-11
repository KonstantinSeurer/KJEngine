/**
 * 
 */
package de.kjEngine.consoletools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author konst
 *
 */
public class Main {

	public static Map<String, Command> commands = new HashMap<>();

	public static void main(String[] args) {
		commands.put("help", new HelpCommand());
		commands.put("exit", new ExitCommand());
		commands.put("convertToKJT", new ConvertToKJTCommand());
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		String line;
		while (true) {
			System.out.println("Waiting for commands... Syntax: commandName [arg0] [arg1] ...");

			try {
				line = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			String[] segments = line.split(" ");

			if (segments.length == 0) {
				continue;
			}

			Command command = commands.get(segments[0]);

			if (command == null) {
				System.out.println("Unknown command! Enter \"help\" for a list of commands.");
				continue;
			}

			if (segments.length == 1) {
				command.run(in, new String[0]);
			} else {
				command.run(in, Arrays.copyOfRange(segments, 1, segments.length));
			}
		}
	}
}
