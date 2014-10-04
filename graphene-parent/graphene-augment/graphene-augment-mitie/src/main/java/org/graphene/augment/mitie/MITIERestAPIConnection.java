package org.graphene.augment.mitie;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;

import graphene.business.commons.exception.DataAccessException;

public interface MITIERestAPIConnection {

	public abstract String performQuery(String inputText)
			throws DataAccessException, UnsupportedEncodingException,
			ClientProtocolException, IOException;

}