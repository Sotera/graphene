package graphene.util;

import java.io.PrintWriter;
import java.io.StringWriter;

// This is a very simple interpreter of exception message.
// For a more comprehensive solution see JumpStart's BusinessServiceExceptionInterpreter.

public class ExceptionUtil {

	static public String getMessage(Throwable t) {
		String m = t.getMessage();
		return m == null ? t.getClass().getSimpleName() : m;
	}

	static public String getRootCauseMessage(Throwable t) {
		Throwable rc = getRootCause(t);
		return getMessage(rc);
	}

	static public Throwable getRootCause(Throwable t) {
		Throwable cause = t;
		Throwable subCause = cause.getCause();
		while (subCause != null && !subCause.equals(cause)) {
			cause = subCause;
			subCause = cause.getCause();
		}
		return cause;
	}

	static public String printStackTrace(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter, false);
		t.printStackTrace(printWriter);
		printWriter.close();
		String s = stringWriter.getBuffer().toString();
		return s;
	}

}
