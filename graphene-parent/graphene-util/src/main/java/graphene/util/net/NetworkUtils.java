package graphene.util.net;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(NetworkUtils.class);

	/**
	 * 
	 * @param hostnameOrIP
	 * @return
	 */
	public static boolean isServerAlive(String hostnameOrIP, int port) {
		Socket socket = null;
		boolean reachable = false;
		try {
			socket = new Socket(hostnameOrIP, port);
			reachable = true;
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
				}
		}
		return reachable;
	}

	public static boolean isServerAliveOn80(String hostnameOrIP) {
		return isServerAlive(hostnameOrIP, 80);
	}

	public static boolean pingable(String host) {
		int returnVal = 0;
		try {
			boolean isWindows = System.getProperty("os.name").toLowerCase()
					.contains("win");

			ProcessBuilder processBuilder = new ProcessBuilder("ping",
					isWindows ? "-n" : "-c", "1", host);
			Process proc;

			proc = processBuilder.start();
			returnVal = proc.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return returnVal == 0;
	}

//	public static boolean pingable(String hostnameOrIP) {
//		try {
//			// create the ping command as a list of strings
//			SystemCommand ping = new SystemCommand();
//			List<String> commands = new ArrayList<String>();
//			commands.add("ping");
//			commands.add("-c");
//			commands.add("5");
//			commands.add(hostnameOrIP);
//			ping.doCommand(commands);
//			return true;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//	}

}
