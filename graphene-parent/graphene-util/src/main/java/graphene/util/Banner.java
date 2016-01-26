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

import java.io.PrintStream;

public class Banner {
	private static final String[] BANNER = { "   __________  ___    ____  __  _________   ________",
			"  / ____/ __ \\/   |  / __ \\/ / / / ____/ | / / ____/",
			" / / __/ /_/ / /| | / /_/ / /_/ / __/ /  |/ / __/   ",
			"/ /_/ / _, _/ ___ |/ ____/ __  / /___/ /|  / /___   ",
			"\\____/_/ |_/_/  |_/_/   /_/ /_/_____/_/ |_/_____/   ",
			"                                                    " };
	private static final int STRAP_LINE_SIZE = 53;

	public void printBanner(final Class<?> sourceClass, final PrintStream printStream) {
		for (final String line : BANNER) {
			printStream.println(line);
		}

	}
}
