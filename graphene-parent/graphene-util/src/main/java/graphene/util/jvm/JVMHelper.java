package graphene.util.jvm;

public class JVMHelper {
	private static final Runtime runtime = Runtime.getRuntime();

	/**
	 * Aggressively suggest to free memory, then return the amount of free
	 * memory in the system.
	 * 
	 * @return
	 */
	public static long getFreeMem() {
		doGarbage();
		return runtime.freeMemory();
	}

	public static void immolativeShutdown() {
		suggestGC();
		ThreadLocalImmolater i = new ThreadLocalImmolater();
		i.immolate();
	}

	/**
	 * Aggressively suggest to free memory, then returns the total amount of
	 * memory in the Java virtual machine
	 * 
	 * @return
	 */
	public static long getTotalMem() {
		doGarbage();
		return runtime.totalMemory();
	}

	private static void doGarbage() {
		collectGarbage();
		collectGarbage();
	}

	/*
	 * SUGGEST to run the garbage collector. Remember, we can only suggest that
	 * it be called, we can't force it to be done.
	 */
	public static void suggestGC() {
		runtime.gc();
	}

	/**
	 * 
	 */
	static void collectGarbage() {
		try {
			System.gc();
			Thread.sleep(100);
			System.runFinalization();
			Thread.sleep(100);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

	}

}
