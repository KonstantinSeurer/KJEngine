/**
 * 
 */
package de.kjEngine.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import de.kjEngine.io.RL.ProtocolImplementation;

/**
 * @author konst
 *
 */
public class FileProtocolImplementation implements ProtocolImplementation {

	public FileProtocolImplementation() {
	}

	@Override
	public InputStream openInputStream(RL rl) throws ResourceNotFoundException {
		try {
			return new FileInputStream(new File(rl.getPath()));
		} catch (FileNotFoundException e) {
			throw new ResourceNotFoundException(e);
		}
	}

	@Override
	public OutputStream openOutputStream(RL rl) throws ResourceNotFoundException {
		try {
			return new FileOutputStream(new File(rl.getPath()));
		} catch (FileNotFoundException e) {
			throw new ResourceNotFoundException(e);
		}
	}
}
