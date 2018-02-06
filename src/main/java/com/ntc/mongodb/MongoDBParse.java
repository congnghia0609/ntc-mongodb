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

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bson.Document;

/**
 *
 * @author nghiatc
 * @since Feb 7, 2018
 */
public abstract class MongoDBParse<T> {
    public List<T> parseList(FindIterable<Document> results) {
		List<T> data = new LinkedList<T>();
		Document doc = null;
		MongoCursor<Document> cursor = results.iterator();
		while(cursor != null && cursor.hasNext() && (doc = cursor.next()) != null) {
			T item = parseObject(doc);
			data.add(item);
		}

		return data;
	}

	public List<T> parseList(List<Document> results) {
		List<T> data = new LinkedList<T>();
		if (results != null) {
			Document doc = null;
			Iterator<Document> cursor = results.iterator();
			while(cursor != null && cursor.hasNext() && (doc = cursor.next()) != null) {
				T item = parseObject(doc);
				data.add(item);
			}
		}

		return data;
	}

	public abstract T parseObject(Document doc);

	public abstract Document toDocument(T obj);
	
	public List<Document> toDocumentList(List<T> objs) {
		List<Document> docs = new LinkedList<Document>();
		
		for (T obj: objs) {
			docs.add(toDocument(obj));
		}
		
		return docs;
	}

	public Document buildInsertTime(Map<String, Object> map) {
		Document document = new Document(map);
		document.append("createTime", new Date());
		document.append("updateTime", new Date());
		return document;
	}

	public Document buildUpdateTime(Map<String, Object> map) {
		Document document = new Document(map);
		document.append("$currentDate", new Document("updateTime", true));
		return document;
	}
}
