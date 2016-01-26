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

#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

/**
 * This package holds classes for a particular type of document.  The document/record is read in via a DAO implementation and then these classes get populated either manually or automatically through deserialization (i.e. Json->Java)
 * 
 * A best practice is to separate related classes by the realm of data that they deal with - i.e. All Instagram related classes go in one package, and Facebook ones go in another, etc.  This is important in case the format of the data changes in one and not the other, or if the realms have a different convention for the same terms.
 * @author djue
 *
 */
package ${package}.model.media;