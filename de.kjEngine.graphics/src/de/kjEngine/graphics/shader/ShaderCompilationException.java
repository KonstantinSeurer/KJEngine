/**
 * 
 */
package de.kjEngine.graphics.shader;

/**
 * @author konst
 *
 */
public class ShaderCompilationException extends Exception {
	private static final long serialVersionUID = 811482444119483948L;
	
	private int characterIndex;

	public ShaderCompilationException(int characterIndex) {
		this.characterIndex = characterIndex;
	}

	/**
	 * @param message
	 */
	public ShaderCompilationException(String message, int characterIndex) {
		super(message);
		this.characterIndex = characterIndex;
	}

	/**
	 * @param cause
	 */
	public ShaderCompilationException(Throwable cause, int characterIndex) {
		super(cause);
		this.characterIndex = characterIndex;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ShaderCompilationException(String message, Throwable cause, int characterIndex) {
		super(message, cause);
		this.characterIndex = characterIndex;
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ShaderCompilationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int characterIndex) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.characterIndex = characterIndex;
	}

	/**
	 * @return the characterIndex
	 */
	public int getCharacterIndex() {
		return characterIndex;
	}

	/**
	 * @param characterIndex the characterIndex to set
	 */
	public void setCharacterIndex(int characterIndex) {
		this.characterIndex = characterIndex;
	}
}
