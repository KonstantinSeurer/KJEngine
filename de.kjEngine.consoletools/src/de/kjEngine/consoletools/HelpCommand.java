/**
 * 
 */
package de.kjEngine.consoletools;

import java.io.BufferedReader;

/**
 * @author konst
 *
 */
public class HelpCommand implements Command {

	@Override
	public void run(BufferedReader in, String[] args) {
		for (var command : Main.commands.entrySet()) {
			System.out.println(command.getKey() + ": " + command.getValue().getSyntax());
		}
	}

	@Override
	public String getSyntax() {
		return "";
	}
}
