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

/**
 * Use this module when you have your Authentication and Authorization being done by an external source, such as a Tomcat hooked up to an LDAP, etc.
 * 
 * The underlying assumption is that in this scenario, no user can even reach our application until they have been Authenticated and Authorized by the container.  Thus, our application doesn't need to worry about passwords, etc.
 * 
 * Since we don't manage user accounts' registration, etc, we auto-create a new user account if it doesn't exist.
 * 
 * We also expect the username to be the 'user principal' or 'remoteUser' specified in each request header.
 * 
 * @author djue
 *
 */
package graphene.security.tomcat.preaa;