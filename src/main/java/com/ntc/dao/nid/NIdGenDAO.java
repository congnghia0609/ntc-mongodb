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

package com.ntc.dao.nid;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.ntc.mongodb.CommonDAO;
import com.ntc.mongodb.MongoDBParse;
import com.ntc.entities.nid.NIdGen;

/**
 *
 * @author nghiatc
 * @since Aug 11, 2015
 */
public class NIdGenDAO extends CommonDAO {
    private final Logger logger = LoggerFactory.getLogger(NIdGenDAO.class);
    private static Map<String, NIdGenDAO> mapNID = new ConcurrentHashMap<String, NIdGenDAO>();
	private static Lock lock = new ReentrantLock();
    private String name;

	private final String TABLE_NAME = "nidgen";

	public static NIdGenDAO getInstance(String name) {
        if(name == null || name.isEmpty()){
            return null;
        }
        NIdGenDAO instance = mapNID.containsKey(name) ? mapNID.get(name) : null;
		if(instance == null) {
			lock.lock();
			try {
                instance = mapNID.containsKey(name) ? mapNID.get(name) : null;
				if(instance == null) {
					instance = new NIdGenDAO(name);
                    mapNID.put(name, instance);
				}
			} finally {
				lock.unlock();
			}
		}

		return instance;
	}

	private NIdGenDAO() {
	}
    
    private NIdGenDAO(String name) {
        this.name = name;
	}

    public String getName() {
        return name;
    }
    
    public int getNext(){
        int ret = -1;
        NIdGen mid = null;
        if(dbSource != null) {
            try {
                MongoMapper mapper = new MongoMapper();
                Document filter = new Document("_id", name);
                Document update = new Document("$inc", new Document("seq", 1));
                FindOneAndUpdateOptions opt = new FindOneAndUpdateOptions().upsert(true);
                Document doc = dbSource.getCollection(TABLE_NAME).findOneAndUpdate(filter, update, opt);
                if(doc != null) {
					mid = mapper.parseObject(doc);
                    ret = mid.getSeq() + 1;
				} else{
                    ret = 1;
                }
            } catch(final Exception e) {
				logger.error("getNext ", e);
			}
        }
        return ret;
    }
    
    public int getMaxId(){
        int ret = -1;
        NIdGen mid = null;
        if(dbSource != null) {
            try {
                MongoMapper mapper = new MongoMapper();
                Document filter = new Document("_id", name);
                Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
                if(doc != null) {
					mid = mapper.parseObject(doc);
                    ret = mid.getSeq();
				} else {
                    ret = 0;
                }
            } catch(final Exception e) {
				logger.error("getMaxId ", e);
			}
        }
        return ret;
    }
    
    public void reset(){
        if(dbSource != null) {
            try {
                Document filter = new Document("_id", name);
                Document update = new Document("$set", new Document("seq", 0));
                FindOneAndUpdateOptions opt = new FindOneAndUpdateOptions().upsert(true);
                Document doc = dbSource.getCollection(TABLE_NAME).findOneAndUpdate(filter, update, opt);
            } catch(final Exception e) {
				logger.error("reset ", e);
			}
        }
    }
    
    private class MongoMapper extends MongoDBParse<NIdGen> {

		@Override
		public NIdGen parseObject(Document doc) {
			NIdGen account = new NIdGen(doc.getString("_id"), doc.getInteger("seq"));
			return account;
		}

		@Override
		public Document toDocument(NIdGen obj) {
			Document doc = new Document("_id", obj.getName()).append("seq", obj.getSeq());
			return doc;
		}
	}
    
//    public static void main(String[] args) {
//        MIdGenDAO nid = MIdGenDAO.getInstance("nghiaabc");
//        int maxId = nid.getMaxId();
//        System.out.println("===============maxId: " + maxId);
//        
//        int nextId = nid.getNext();
//        System.out.println("===============nextId: " + nextId);
//    }
}
