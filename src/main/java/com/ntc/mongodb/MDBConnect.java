/*
 * Copyright 2018 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntc.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.ntc.configer.NConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Feb 7, 2018
 */
public class MDBConnect {
    private static final Logger logger = LoggerFactory.getLogger(MDBConnect.class);
	private static final ConcurrentHashMap<String, MDBConnect> instanceMap = new ConcurrentHashMap<String, MDBConnect>(16, 0.9f, 16);

	private static Lock lock = new ReentrantLock();
	private MongoClient client;

	public static MDBConnect getInstance(String configName) {
		MDBConnect instance = instanceMap.get(configName);
		if(instance == null) {
			lock.lock();
			try {
				instance = instanceMap.get(configName);
				if(instance == null) {
					try {
						instance = new MDBConnect(configName);
						instanceMap.put(configName, instance);
					} catch(Exception e) {
						logger.error("error ", e);
					}
				}
			} finally {
				lock.unlock();
			}
		}

		return instance;
	}

	private MDBConnect() {
		client = null;
	}

	private MDBConnect(String configName) throws Exception {
		client = null;
        boolean strict = NConfig.getConfig().getBoolean(configName + ".mongodb.strict", false);
		List<MongoCredential> credential = new ArrayList<MongoCredential>();
		List<ServerAddress> servers = new ArrayList<ServerAddress>();
		final String hosts = NConfig.getConfig().getString(configName + ".mongodb.host");
		if(hosts != null && !hosts.isEmpty()) {
			String[] hostArr = hosts.split(";");
			for(String host : hostArr) {
				String[] hostPort = host.split(":");
				if(hostPort.length >= 2) {
					servers.add(new ServerAddress(hostPort[0], NumberUtils.toInt(hostPort[1])));
				}
			}
		}

		if(!servers.isEmpty()) {
			String mongCredential = NConfig.getConfig().getString(configName + ".mongodb.users");
			String[] cred = mongCredential.split(";");
			for(String c: cred) {
				String[] usp = c.split(":");
				if(usp.length >= 3) {
					credential.add(MongoCredential.createCredential(usp[1], usp[0], usp[2].toCharArray()));
				}
			}
            int maxConnection = NConfig.getConfig().getInt(configName + ".mongodb.max_connection", 50);
            System.out.println(configName + ".mongodb.max_connection: " + maxConnection);
			MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder().connectionsPerHost(maxConnection)
                    .maxConnectionIdleTime(60000).connectTimeout(60000).sslEnabled(false).retryWrites(true)
                    .writeConcern(WriteConcern.JOURNALED);
            if (strict) {
                optionsBuilder.readPreference(ReadPreference.primaryPreferred());
            } else {
                optionsBuilder.readPreference(ReadPreference.secondaryPreferred());
            }
			
			String keyFile = NConfig.getConfig().getString(configName + ".mongodb.keyfile");
			String keyPass = NConfig.getConfig().getString(configName + ".mongodb.keypass");

			if (keyFile != null && keyPass != null) {
                System.out.println(configName + ".mongodb.keyfile: " + keyFile);
                System.out.println(configName + ".mongodb.keypass: " + keyPass);
				//optionsBuilder.sslEnabled(true).socketFactory(SSLContextUtil.createDefaultSSLContext(keyFile, keyPass).getSocketFactory());
                optionsBuilder.sslEnabled(true).sslInvalidHostNameAllowed(true).socketFactory(SSLContextUtil.createDefaultSSLContext(keyFile, keyPass).getSocketFactory());
			}
			
			MongoClientOptions options = optionsBuilder.build();
			client = new MongoClient(servers, credential, options);
			client.setWriteConcern(WriteConcern.JOURNALED);
			if (strict) {
                client.setReadPreference(ReadPreference.primaryPreferred());
            } else {
                client.setReadPreference(ReadPreference.secondaryPreferred());
            }
		}

		if(client == null) {
			logger.error("Don't found mongoDB");
		}
	}

	public MongoClient getClient() {
		return client;
	}
}
