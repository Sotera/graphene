/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
