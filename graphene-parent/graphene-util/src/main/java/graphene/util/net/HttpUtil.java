package graphene.util.net;

public class HttpUtil {

	public static String getAuthorizationEncoding(String username,
			char[] password) {
		String encoding = org.apache.commons.codec.binary.Base64
				.encodeBase64String((username + ":" + password).getBytes());
		return encoding;
	}
	public static String getAuthorizationEncoding(String userpassword) {
		String encoding = org.apache.commons.codec.binary.Base64
				.encodeBase64String(userpassword.getBytes());
		return encoding;
	}
}
