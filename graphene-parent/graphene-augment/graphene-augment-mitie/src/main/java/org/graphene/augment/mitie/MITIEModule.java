package org.graphene.augment.mitie;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Symbol;

public class MITIEModule {

	public static final String BASIC_AUTH = "mitie.basic-auth";
	public static final String BASE_URL = "mitie.base-url";

	public void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {

		// MITIE defaults (if no mitie.properties file is provided, or not in
		// catalina.properties, etc)
		configuration.add(BASIC_AUTH, "");// obviously just an
														// example
		configuration.add(BASE_URL, "http://localhost/mitie");// obviously just
																// an example
	}

	public MITIERestAPIConnection buildMITIEConnection(
			@Symbol(BASIC_AUTH) String auth, @Symbol(BASE_URL) String url) {
		MITIERestAPIConnection m = new MITIERestAPIConnectionImpl(url, auth);
		return m;
	}
}
