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

import com.mongodb.client.MongoDatabase;
import com.ntc.configer.NConfig;

/**
 *
 * @author nghiatc
 * @since Feb 7, 2018
 */
public abstract class CommonDAO {
    private final MDBConnect connect;
	protected MongoDatabase dbSource;

	public CommonDAO() {
		String configName = "ntc";
		connect = MDBConnect.getInstance(configName);
		if(connect != null) {
			dbSource = connect.getClient().getDatabase(NConfig.getConfig().getString(configName + ".mongodb.db"));
		}
	}

	public CommonDAO(String configName) {
		connect = MDBConnect.getInstance(configName);
		if(connect != null) {
			dbSource = connect.getClient().getDatabase(NConfig.getConfig().getString(configName + ".mongodb.db"));
		}
	}

	public MDBConnect getConnect() {
		return connect;
	}

	public MongoDatabase getDbSource() {
		return dbSource;
	}
}
