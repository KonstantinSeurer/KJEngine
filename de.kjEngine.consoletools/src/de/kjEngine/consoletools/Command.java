/**
 * 
 */
package de.kjEngine.consoletools;

import java.io.BufferedReader;

/**
 * @author konst
 *
 */
public interface Command {

	public void run(BufferedReader in, String[] args);
	
	public String getSyntax();
}
