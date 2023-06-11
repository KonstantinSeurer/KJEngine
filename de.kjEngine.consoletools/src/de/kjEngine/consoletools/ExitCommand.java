/**
 * 
 */
package de.kjEngine.consoletools;

import java.io.BufferedReader;

/**
 * @author konst
 *
 */
public class ExitCommand implements Command {

	@Override
	public void run(BufferedReader in, String[] args) {
		System.exit(0);
	}

	@Override
	public String getSyntax() {
		return "";
	}
}
