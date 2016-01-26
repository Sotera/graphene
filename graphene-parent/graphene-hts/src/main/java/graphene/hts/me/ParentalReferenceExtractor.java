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

package graphene.hts.me;

import static graphene.util.validator.ValidationUtils.isValid;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalTruthValues;
import graphene.model.idl.G_Gender;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.util.Triple;
import graphene.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ParentalReferenceExtractor {

	@Inject
	private G_PropertyKeyTypeAccess propertyKeyTypeAccess;
	FamilyReferenceExtractor fre = new FamilyReferenceExtractor();
	private String context = "";

	private String provenance = "";

	public String getContext() {
		return context;
	}

	/**
	 * Supplied by ingest class
	 * 
	 * @param string
	 * @param father
	 * @param p
	 * @param names
	 * @return
	 */
	private Long getOrCreateNodeId(final String string, final String father, final HashMap<String, Object> p,
			final Object names) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<Triple<Long, G_Gender, String>> getParentNodes(final Object names, final String father,
			final String child, final String[] potentialFields) throws AvroRemoteException {
		final HashMap<String, Object> p = new HashMap<String, Object>();

		final ArrayList<Triple<Long, G_Gender, String>> list = new ArrayList<Triple<Long, G_Gender, String>>();
		if (isValid(father)) {
			// This one is not imputed since it was listed in the data as Father
			p.put(propertyKeyTypeAccess.getPropertyKey(G_CanonicalPropertyType.NAME.name()).getName(), father);
			p.put("FamilyRole", "Parent");// change this, we should assume
											// father,
											// but for certain it's a 'parent'
											// in a
											// parental role
			p.put("Gender", G_Gender.MALE.toString());// change this, we should
														// assume
														// father, but for
														// certain
														// it's
														// a 'parent' in a
														// parental
														// role
			p.put(G_CanonicalPropertyType.CONTEXT.toString(), context);
			p.put(G_CanonicalPropertyType.METRIC_PROVENANCE.toString(), provenance);
			final String unique = father + " Parent of " + child;
			p.put(G_CanonicalPropertyType.LINK.toString(), unique);
			final Long listedFather = getOrCreateNodeId(
					propertyKeyTypeAccess.getPropertyKey(G_CanonicalPropertyType.NAME.name()).getName(), father, p,
					names);
			if (listedFather != null) {
				final Triple<Long, G_Gender, String> retval = new Triple<Long, G_Gender, String>(listedFather,
						(G_Gender) p.get("Gender"), (String) p.get(propertyKeyTypeAccess.getPropertyKey(
								G_CanonicalPropertyType.NAME.name()).getName()));
				list.add(retval);
			}
		}
		for (final String s : potentialFields) {
			Triple<Long, G_Gender, String> t = null;
			if ((t = imputeParent(names, child, s)) != null) {
				list.add(t);
			}
		}
		return list;
	}

	public String getProvenance() {
		return provenance;
	}

	/**
	 * 
	 * @param names
	 * @param child
	 * @param list
	 * @param s
	 * @return
	 * @throws AvroRemoteException
	 */
	private Triple<Long, G_Gender, String> imputeParent(final Object names, final String child, final String s)
			throws AvroRemoteException {
		HashMap<String, Object> p;
		// No clean way to influence the 'child' node--we should create the
		// parent nodes first, and then hang on to all the imputed gender
		// scores.
		final Tuple<String, G_Gender> t = fre.getParentAndImputedChildGender(s);
		if (t != null) {
			p = new HashMap<String, Object>();
			p.put(propertyKeyTypeAccess.getPropertyKey(G_CanonicalPropertyType.NAME.name()).getName(), t.getFirst());
			p.put(G_CanonicalPropertyType.METRIC_IMPUTED.toString(), G_CanonicalTruthValues.TRUE.toString());
			p.put(G_CanonicalPropertyType.METRIC_IMPUTEDFROM.toString(), "Address1");
			p.put(G_CanonicalPropertyType.CONTEXT.toString(), context);
			p.put(G_CanonicalPropertyType.METRIC_PROVENANCE.toString(), provenance);
			final String unique = t.getFirst() + " Parent of " + child;
			p.put(G_CanonicalPropertyType.LINK.toString(), unique);
			final Long fId = getOrCreateNodeId(propertyKeyTypeAccess
					.getPropertyKey(G_CanonicalPropertyType.NAME.name()).getName(), unique, p, names);
			if (fId != null) {
				final Triple<Long, G_Gender, String> retval = new Triple<Long, G_Gender, String>(fId, t.getSecond(),
						t.getFirst());
				return retval;
			}
		}
		return null;
	}

	public void setContext(final String context) {
		this.context = context;
	}

	public void setProvenance(final String provenance) {
		this.provenance = provenance;
	}
}
