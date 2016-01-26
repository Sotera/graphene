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

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.H2Templates;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/jdbc.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Entry<Object, Object> entry : properties.entrySet()) {
            bind(String.class).annotatedWith(Names.named((String)entry.getKey()))
                    .toInstance((String) entry.getValue());
        }
        
        bind(ConnectionContext.class).in(Scopes.SINGLETON);
//        bind(TweetRepository.class).in(Scopes.SINGLETON);
//        bind(UserRepository.class).in(Scopes.SINGLETON);

        TransactionInterceptor interceptor = new TransactionInterceptor();
        requestInjection(interceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), interceptor);        
    }

    @Provides
    @Singleton
    public Configuration configuration() {
        return new Configuration(new H2Templates());
    }

//    @Provides
//    @Singleton
//    public DataSource dataSource(@Named("jdbc.user") String user,
//                                 @Named("jdbc.password") String password,
//                                 @Named("jdbc.url") String url,
//                                 @Named("jdbc.driver") String driver) {
//        try {
//            Class.forName(driver);
//            return DataSources.pooledDataSource(DataSources.unpooledDataSource(
//                    url, user, password));
//
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
