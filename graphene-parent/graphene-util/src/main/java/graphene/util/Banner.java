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
