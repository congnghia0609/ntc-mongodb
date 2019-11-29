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

/**
 *
 * @author nghiatc
 * @since Feb 7, 2018
 */
public class MongoErrorCode {
    public static final int SUCCESS = 0;
	public static final int NOT_CONNECT = -1;
    public static final int ID_INVALID = -2;
    public static final int WRITE_ERROR = -3;
}
