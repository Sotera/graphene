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

package graphene.dao.sql.guice;

import java.lang.reflect.Method;
import java.sql.Connection;

import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class TransactionInterceptor implements MethodInterceptor {
    @Inject
    private ConnectionContext context;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Transactional annotation = method.getAnnotation(Transactional.class);
        if (annotation == null || context.getConnection() != null) {
            return invocation.proceed();
        }
        Connection connection = context.getConnection(true);
        connection.setAutoCommit(false);
        try {
            Object rv = invocation.proceed();
            connection.commit();
            return rv;
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.close();
            context.removeConnection();
        }
    }
}
