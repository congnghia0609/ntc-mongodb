/*
 * Copyright 2015 nghiatc.
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

package com.ntc.dao.address;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.FindIterable;
import com.mongodb.client.result.UpdateResult;
import com.ntc.entities.address.Country;
import com.ntc.mongodb.CommonDAO;
import com.ntc.mongodb.MongoDBParse;
import com.ntc.mongodb.MongoErrorCode;

/**
 *
 * @author nghiatc
 * @since Aug 11, 2015
 */
public class CountryDAO extends CommonDAO {
	private final Logger logger = LoggerFactory.getLogger(CountryDAO.class);
	private static Lock lock = new ReentrantLock();
	private static CountryDAO instance;

	private String TABLE_NAME = "country";

	public static CountryDAO getInstance() {
		if(instance == null) {
			lock.lock();
			try {
				if(instance == null) {
					instance = new CountryDAO();
				}
			} finally {
				lock.unlock();
			}
		}

		return instance;
	}
	
	public List<Country> getList() {
		List<Country> countries = null;
		if(dbSource != null) {
			try {
				Document objFinder = new Document("status", new Document("$gt", 0));
				Document sort = new Document("name", 1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(objFinder).sort(sort);
				if(doc != null) {
					countries = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				logger.error("getList ", e);
			}
		}

		return countries;
	}
	
	public Country getByName(String name) {
		Country rs = null;
		if (dbSource != null) {
			try {
				Document filter = new Document ("name", name)
						.append("status", new Document("$gt", 0));
				Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
				if (doc != null) {
					rs = new MongoMapper().parseObject(doc);
				}
			} catch (final Exception e) {
				logger.error("getByName ", e);
			}
		}
		
		return rs;
	}
	
	public Country getByCode(String code) {
		Country rs = null;
		if (dbSource != null) {
			try {
				Document filter = new Document ("_id", code)
						.append("status", new Document("$gt", 0));
				Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
				if (doc != null) {
					rs = new MongoMapper().parseObject(doc);
				}
			} catch (final Exception e) {
				logger.error("getByCode ", e);
			}
		}
		
		return rs;
	}
    
	public int insert(Country country) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				Document obj = mapper.toDocument(country);
				dbSource.getCollection(TABLE_NAME).insertOne(obj);
				rs = MongoErrorCode.SUCCESS;
			} catch(final Exception e) {
				logger.error("insert ", e);
			}
		}

		return rs;
	}
	
	public int insertBatch(List<Country> countries) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				List<Document> objs = new LinkedList<Document>();
				for (Country country: countries) {
					Document obj = mapper.toDocument(country);
					obj = mapper.buildInsertTime(obj);
					objs.add(obj);
				}
				dbSource.getCollection(TABLE_NAME).insertMany(objs);
				rs = MongoErrorCode.SUCCESS;
			} catch(final Exception e) {
				logger.error("insertBatch ", e);
			}
		}

		return rs;
	}

	public int update(Country country) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				Document filter = new Document("_id", country.getIsoCode());
				Document obj = new Document("$set", mapper.toDocument(country));
				obj = mapper.buildUpdateTime(obj);
				UpdateResult qRs = dbSource.getCollection(TABLE_NAME).updateOne(filter, obj);
				rs = (int) qRs.getModifiedCount();
			} catch(final Exception e) {
				logger.error("update ", e);
			}
		}

		return rs;
	}

	public int delete(long id) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				Document filter = new Document("_id", id);
				Document obj = new Document("$set", new Document("status", 0));
				obj = mapper.buildUpdateTime(obj);
				UpdateResult qRs = dbSource.getCollection(TABLE_NAME).updateOne(filter, obj);
				rs = (int) qRs.getModifiedCount();
			} catch(final Exception e) {
				logger.error("update ", e);
			}
		}

		return rs;
	}
	
	public int addField(String field, Object defaultValue) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				Document filter = new Document();
				Document obj = new Document("$set", new Document(field, defaultValue));
				UpdateResult qRs = dbSource.getCollection(TABLE_NAME).updateMany(filter, obj);
				rs = (int) qRs.getModifiedCount();
			} catch (final Exception e) {
				logger.error("addField ", e);
			}
		}

		return rs;
	}

	private class MongoMapper extends MongoDBParse<Country> {

		@Override
		public Country parseObject(Document doc) {
			Country account = new Country(doc.getString("_id"), doc.getString("name"), doc.getInteger("status"));
			return account;
		}

		@Override
		public Document toDocument(Country obj) {
			Document doc = new Document("_id", obj.getIsoCode())
					.append("name", obj.getName())
					.append("status", obj.getStatus());
			return doc;
		}

	}
	
//	public static void main(String[] args) {
//		System.out.println(JsonUtils.Instance.toJson(CountryDAO.getInstance().getList()));
//	}
}
