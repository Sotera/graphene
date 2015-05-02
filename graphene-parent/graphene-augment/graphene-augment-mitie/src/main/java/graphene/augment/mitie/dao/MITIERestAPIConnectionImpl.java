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
	private final String url;

	public MITIERestAPIConnectionImpl(final String url, final String basicAuth) {
		this.url = url;
		this.basicAuth = basicAuth;
	}

	private String createCleanUrl(@Nullable final String basicAuth, final String baseUrl) {
		if (ValidationUtils.isValid(basicAuth)) {
			logger.debug("Auth information provided, using auth info.");
			final String cleanAuth = basicAuth.replaceAll("@", "%40");
			final String cleanURL = baseUrl.replace("http://", "http://" + cleanAuth + "@");
			return cleanURL;
		}
		logger.debug("No auth information provided, using without auth info.");
		return baseUrl;
	}

	@Override
	public String performQuery(final String inputText) throws DataAccessException, ClientProtocolException, IOException {
		final HttpClient client = HttpClientBuilder.create().build();

		final HttpPost post = new HttpPost(url);
		post.setHeader("Authorization", "Basic " + HttpUtil.getAuthorizationEncoding(basicAuth));
		final JSONObject obj = new JSONObject();
		obj.put("text", inputText);

		final StringEntity entity = new StringEntity(obj.toString());
		post.setEntity(entity);
		final HttpResponse response = client.execute(post);

		final BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		final StringBuffer sb = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();

		return sb.toString();
	}
}
