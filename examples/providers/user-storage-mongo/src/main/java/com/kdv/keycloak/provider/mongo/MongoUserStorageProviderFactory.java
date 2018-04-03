/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kdv.keycloak.provider.mongo;

import com.mongodb.MongoClient;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.common.util.EnvUtil;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MongoUserStorageProviderFactory implements UserStorageProviderFactory<MongoUserStorageProvider> {

    private static final Logger logger = Logger.getLogger(MongoUserStorageProviderFactory.class);

    public static final String PROVIDER_NAME = "kdv-mongo";

    //    protected Properties properties = new Properties();
    protected MongoClient mongoClient;

    @Override
    public String getId() {
        return PROVIDER_NAME;
    }

    protected static final List<ProviderConfigProperty> configMetadata;

    static {
        configMetadata = ProviderConfigurationBuilder.create()
                .property().name("mongo-host")
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("MongoDB server host")
                .defaultValue("localhost")
                .helpText("MongoDB server host (default localhost)")
                .add()
                .property().name("mongo-port")
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("MongoDB server port")
                .defaultValue("27017")
                .helpText("MongoDB server port (default 27017)")
                .add()
                .build();
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configMetadata;
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
        String host = config.getConfig().getFirst("mongo-host");
        logger.info("mongo-host = " + host); //TODO
        if (host == null) throw new ComponentValidationException("MongoDB server host must be specified");
        String port = config.getConfig().getFirst("mongo-port");
        logger.info("mongo-port = " + port); //TODO
        if (port == null) throw new ComponentValidationException("MongoDB server port must be specified");
        if (!(port.matches("^[0-9]+$"))) throw new ComponentValidationException("MongoDB server port must be a number");
        int portNumber = Integer.parseInt(port);
        if (!(portNumber < 65536))
            throw new ComponentValidationException("MongoDB server port must be a valid port number (< 65536)");

        try {
            mongoClient = new MongoClient(host, portNumber);
        } catch (Exception e) {
            throw new ComponentValidationException("MongoDB connection failed", e);
        }
    }

/*
    @Override
    public void init(Config.Scope config) {
        InputStream is = getClass().getClassLoader().getResourceAsStream("/users.properties");

        if (is == null) {
            logger.warn("Could not find users.properties in classpath");
        } else {
            try {
                properties.load(is);
            } catch (IOException ex) {
                logger.error("Failed to load users.properties file", ex);
            }
        }
    }
*/

    @Override
    public MongoUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new MongoUserStorageProvider(session, model, mongoClient);
    }

}
