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

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

public class ColorUtil {
	// private static final String DEFAULT_BUNDLE_NAME = "graphene.util.colors";
	// private static final String BUNDLE_NAME = "graphene_optional_colors01";

	public enum Shade {
		pshade0,
		pshade1,
		pshade2,
		pshade3,
		pshade4,
		s1shade0,
		s1shade1,
		s1shade2,
		s1shade3,
		s1shade4,
		s2shade0,
		s2shade1,
		s2shade2,
		s2shade3,
		s2shade4,
		scshade0,
		scshade1,
		scshade2,
		scshade3,
		scshade4;
	}

	@Inject
	@Symbol(value = "pshade0")
	private String pshade0;
	@Inject
	@Symbol(value = "pshade1")
	private String pshade1;
	@Inject
	@Symbol(value = "pshade2")
	private String pshade2;
	@Inject
	@Symbol(value = "pshade3")
	private String pshade3;
	@Inject
	@Symbol(value = "pshade4")
	private String pshade4;

	@Inject
	@Symbol(value = "s1shade0")
	private String s1shade0;
	@Inject
	@Symbol(value = "s1shade1")
	private String s1shade1;
	@Inject
	@Symbol(value = "s1shade2")
	private String s1shade2;
	@Inject
	@Symbol(value = "s1shade3")
	private String s1shade3;
	@Inject
	@Symbol(value = "s1shade4")
	private String s1shade4;

	@Inject
	@Symbol(value = "s2shade0")
	private String s2shade0;
	@Inject
	@Symbol(value = "s2shade1")
	private String s2shade1;
	@Inject
	@Symbol(value = "s2shade2")
	private String s2shade2;
	@Inject
	@Symbol(value = "s2shade3")
	private String s2shade3;
	@Inject
	@Symbol(value = "s2shade4")
	private String s2shade4;

	@Inject
	@Symbol(value = "scshade0")
	private String scshade0;
	@Inject
	@Symbol(value = "scshade1")
	private String scshade1;
	@Inject
	@Symbol(value = "scshade2")
	private String scshade2;
	@Inject
	@Symbol(value = "scshade3")
	private String scshade3;
	@Inject
	@Symbol(value = "scshade4")
	private String scshade4;

	public ColorUtil() {
	}

	/**
	 * @return the pshade0
	 */
	public final String getPshade0() {
		return pshade0;
	}

	/**
	 * @return the pshade1
	 */
	public final String getPshade1() {
		return pshade1;
	}

	/**
	 * @return the pshade2
	 */
	public final String getPshade2() {
		return pshade2;
	}

	/**
	 * @return the pshade3
	 */
	public final String getPshade3() {
		return pshade3;
	}

	/**
	 * @return the pshade4
	 */
	public final String getPshade4() {
		return pshade4;
	}

	/**
	 * @return the s1shade0
	 */
	public final String getS1shade0() {
		return s1shade0;
	}

	/**
	 * @return the s1shade1
	 */
	public final String getS1shade1() {
		return s1shade1;
	}

	/**
	 * @return the s1shade2
	 */
	public final String getS1shade2() {
		return s1shade2;
	}

	/**
	 * @return the s1shade3
	 */
	public final String getS1shade3() {
		return s1shade3;
	}

	/**
	 * @return the s1shade4
	 */
	public final String getS1shade4() {
		return s1shade4;
	}

	/**
	 * @return the s2shade0
	 */
	public final String getS2shade0() {
		return s2shade0;
	}

	/**
	 * @return the s2shade1
	 */
	public final String getS2shade1() {
		return s2shade1;
	}

	/**
	 * @return the s2shade2
	 */
	public final String getS2shade2() {
		return s2shade2;
	}

	/**
	 * @return the s2shade3
	 */
	public final String getS2shade3() {
		return s2shade3;
	}

	/**
	 * @return the s2shade4
	 */
	public final String getS2shade4() {
		return s2shade4;
	}

	/**
	 * @return the scshade0
	 */
	public final String getScshade0() {
		return scshade0;
	}

	/**
	 * @return the scshade1
	 */
	public final String getScshade1() {
		return scshade1;
	}

	/**
	 * @return the scshade2
	 */
	public final String getScshade2() {
		return scshade2;
	}

	/**
	 * @return the scshade3
	 */
	public final String getScshade3() {
		return scshade3;
	}

	/**
	 * @return the scshade4
	 */
	public final String getScshade4() {
		return scshade4;
	}

}
