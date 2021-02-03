/**
 * 
 */
package de.kjEngine.graphics.shader;

/**
 * @author konst
 *
 */
public class UnexpectedValueException extends ShaderCompilationException {
	private static final long serialVersionUID = 7986642663876201846L;
	
	private String value;

	public UnexpectedValueException(int characterIndex, String value) {
		super(characterIndex);
		this.value = value;
	}

	/**
	 * @param message
	 */
	public UnexpectedValueException(String message, int characterIndex, String value) {
		super(message, characterIndex);
		this.value = value;
	}

	/**
	 * @param cause
	 */
	public UnexpectedValueException(Throwable cause, int characterIndex, String value) {
		super(cause, characterIndex);
		this.value = value;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnexpectedValueException(String message, Throwable cause, int characterIndex, String value) {
		super(message, cause, characterIndex);
		this.value = value;
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public UnexpectedValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int characterIndex, String value) {
		super(message, cause, enableSuppression, writableStackTrace, characterIndex);
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
