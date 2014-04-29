package graphene.util;


// >>>> Temporary solution - needs renaming at least 


@SuppressWarnings("serial")
public class UtilRuntimeException extends RuntimeException {

	/**
	 * @param arg0
	 * @param arg1
	 */
	public UtilRuntimeException(String string, Throwable throwable) {
		super(string, throwable);
	}

	/**
	 * @param arg0
	 */
	public UtilRuntimeException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param string
	 */
	public UtilRuntimeException(String string) {
		super(string);
	}
	
}
