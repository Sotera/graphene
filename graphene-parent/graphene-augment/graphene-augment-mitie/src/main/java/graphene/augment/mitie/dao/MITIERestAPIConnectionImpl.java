package graphene.augment.mitie.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.util.net.HttpUtil;
import graphene.util.validator.ValidationUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.Nullable;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.slf4j.Logger;

public class MITIERestAPIConnectionImpl implements MITIERestAPIConnection {
	@Inject
	private Logger logger;
	String basicAuth;
	private String url;

	public MITIERestAPIConnectionImpl(String url, String basicAuth) {
		this.url = url;
		this.basicAuth = basicAuth;
	}

	private String createCleanUrl(@Nullable String basicAuth, String baseUrl) {
		if (ValidationUtils.isValid(basicAuth)) {
			logger.debug("Auth information provided, using auth info.");
			String cleanAuth = basicAuth.replaceAll("@", "%40");
			String cleanURL = baseUrl.replace("http://", "http://" + cleanAuth
					+ "@");
			return cleanURL;
		}
		logger.debug("No auth information provided, using without auth info.");
		return baseUrl;
	}

	@Override
	public String performQuery(String inputText) throws DataAccessException,
			ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost post = new HttpPost(url);
		post.setHeader("Authorization",
				"Basic " + HttpUtil.getAuthorizationEncoding(basicAuth));
		JSONObject obj = new JSONObject();
		obj.put("text", inputText);

		StringEntity entity = new StringEntity(obj.toString());
		post.setEntity(entity);
		HttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}
}
