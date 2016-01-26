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

package graphene.model.funnels;

/**
 * Funnel classes are for converting model pojos into a view object. Instantiate
 * one of these when you want to convert between a customer specific datatype
 * and one of the core models.
 * 
 * The intent is that you have a separate funnel for each pair of items you want
 * to convert between. Most of the time you would only want to convert one way.
 * 
 * These can also be used in coercers that are contributed by Tapestry.
 * 
 * Note that the ordering is usually FROM your domain specific object TO the IDL/core/generic version
 * 
 * @author djue
 * 
 */
public interface Funnel<FROM, TO> {
	TO from(FROM f);

	FROM to(TO f);
}
