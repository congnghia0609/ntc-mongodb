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
import com.ntc.entities.nid.NIdGenLong;

/**
 *
 * @author nghiatc
 * @since Aug 20, 2015
 */
public class NIdGenLongDAO extends CommonDAO {

    private final Logger logger = LoggerFactory.getLogger(NIdGenLongDAO.class);
    private static Map<String, NIdGenLongDAO> mapNLID = new ConcurrentHashMap<String, NIdGenLongDAO>();
    private static Lock lock = new ReentrantLock();
    private String name;

    private final String TABLE_NAME = "nlidgen";

    public static NIdGenLongDAO getInstance(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        NIdGenLongDAO instance = mapNLID.containsKey(name) ? mapNLID.get(name) : null;
        if (instance == null) {
            lock.lock();
            try {
                instance = mapNLID.containsKey(name) ? mapNLID.get(name) : null;
                if (instance == null) {
                    instance = new NIdGenLongDAO(name);
                    mapNLID.put(name, instance);
                }
            } finally {
                lock.unlock();
            }
        }

        return instance;
    }

    public static NIdGenLongDAO getInstance(String db, String name) {
        if (db == null || db.isEmpty() || name == null || name.isEmpty()) {
            return null;
        }
        NIdGenLongDAO instance = mapNLID.containsKey(name) ? mapNLID.get(name) : null;
        if (instance == null) {
            lock.lock();
            try {
                instance = mapNLID.containsKey(name) ? mapNLID.get(name) : null;
                if (instance == null) {
                    instance = new NIdGenLongDAO(db, name);
                    mapNLID.put(name, instance);
                }
            } finally {
                lock.unlock();
            }
        }

        return instance;
    }

    private NIdGenLongDAO() {
    }

    private NIdGenLongDAO(String name) {
        this.name = name;
    }

    private NIdGenLongDAO(String db, String name) {
        super(db);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getNext() {
        long ret = -1;
        NIdGenLong mid = null;
        if (dbSource != null) {
            try {
                MongoMapper mapper = new MongoMapper();
                Document filter = new Document("_id", name);
                Document update = new Document("$inc", new Document("seq", 1L));
                FindOneAndUpdateOptions opt = new FindOneAndUpdateOptions().upsert(true);
                Document doc = dbSource.getCollection(TABLE_NAME).findOneAndUpdate(filter, update, opt);
                if (doc != null) {
                    mid = mapper.parseObject(doc);
                    ret = mid.getSeq() + 1;
                } else {
                    ret = 1;
                }
            } catch (final Exception e) {
                logger.error("getNext ", e);
            }
        }
        return ret;
    }

    public long getMaxId() {
        long ret = -1;
        NIdGenLong mid = null;
        if (dbSource != null) {
            try {
                MongoMapper mapper = new MongoMapper();
                Document filter = new Document("_id", name);
                Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
                if (doc != null) {
                    mid = mapper.parseObject(doc);
                    ret = mid.getSeq();
                } else {
                    ret = 0;
                }
            } catch (final Exception e) {
                logger.error("getNext ", e);
            }
        }
        return ret;
    }

    public void reset() {
        if (dbSource != null) {
            try {
                Document filter = new Document("_id", name);
                Document update = new Document("$set", new Document("seq", 0L));
                FindOneAndUpdateOptions opt = new FindOneAndUpdateOptions().upsert(true);
                Document doc = dbSource.getCollection(TABLE_NAME).findOneAndUpdate(filter, update, opt);
            } catch (final Exception e) {
                logger.error("reset ", e);
            }
        }
    }

    private class MongoMapper extends MongoDBParse<NIdGenLong> {

        @Override
        public NIdGenLong parseObject(Document doc) {
            NIdGenLong account = new NIdGenLong(doc.getString("_id"), doc.getLong("seq"));
            return account;
        }

        @Override
        public Document toDocument(NIdGenLong obj) {
            Document doc = new Document("_id", obj.getName()).append("seq", obj.getSeq());
            return doc;
        }
    }

//    public static void main(String[] args) {
//        NIdGenLongDAO nid = NIdGenLongDAO.getInstance("nghiaid");
//        long maxId = nid.getMaxId();
//        System.out.println("===============maxId: " + maxId);
//        
//        long nextId = nid.getNext();
//        System.out.println("===============nextId: " + nextId);
//        
//        long maxId2 = nid.getMaxId();
//        System.out.println("===============maxId: " + maxId2);
//    }
}
