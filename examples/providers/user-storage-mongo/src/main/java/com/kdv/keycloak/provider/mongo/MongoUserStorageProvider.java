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
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.ReadOnlyException;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;

import java.util.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MongoUserStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        CredentialInputValidator/*,
        CredentialInputUpdater*/
{
    protected KeycloakSession session;
    protected MongoClient mongoClient;
    protected ComponentModel model;
    // map of loaded users in this transaction
//    protected Map<String, UserModel> loadedUsers = new HashMap<>();

    public MongoUserStorageProvider(KeycloakSession session, ComponentModel model, MongoClient mongoClient) {
        this.session = session;
        this.model = model;
        this.mongoClient = mongoClient;
    }

    // UserLookupProvider methods

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
//        UserModel adapter = loadedUsers.get(username);
//        if (adapter == null) {
//            String password = properties.getProperty(username);
//            if (password != null) {
//                adapter = createAdapter(realm, username);
//                loadedUsers.put(username, adapter);
//            }
//        }
//        return adapter;
        return createAdapter(realm, username); //TODO
    }

    protected UserModel createAdapter(RealmModel realm, String username) {
        return new AbstractUserAdapter(session, realm, model) {
            @Override
            public String getUsername() {
                return username;
            }
        };
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();
        return getUserByUsername(username, realm);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        return null;
    }


    // CredentialInputValidator methods

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
//        String password = properties.getProperty(user.getUsername());
//        return credentialType.equals(CredentialModel.PASSWORD) && password != null;
        return credentialType.equals(CredentialModel.PASSWORD) && user.getUsername() == "user1"; //TODO
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return credentialType.equals(CredentialModel.PASSWORD);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) return false;

        UserCredentialModel cred = (UserCredentialModel)input;
//        String password = properties.getProperty(user.getUsername());
        String password = "pwd1"; //TODO
        if (password == null) return false;
        return password.equals(cred.getValue());
    }

    // CredentialInputUpdater methods

/*
    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (input.getType().equals(CredentialModel.PASSWORD)) throw new ReadOnlyException("user is read only for this update");

        return false;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {

    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return Collections.EMPTY_SET;
    }
*/


    @Override
    public void close() {

    }
}
