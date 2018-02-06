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

package com.ntc.entities.address;

/**
 *
 * @author nghiatc
 * @since Aug 11, 2015
 */
public class Country {
	public static final int ACTIVE = 1;
	
	private String isoCode;
	private String name;
	private int status;
	
	public Country() {
		super();
	}	

	public Country(String isoCode, String name) {
		super();
		this.isoCode = isoCode;
		this.name = name;
		this.status = ACTIVE;
	}

	public Country(String isoCode, String name, int status) {
		super();
		this.isoCode = isoCode;
		this.name = name;
		this.status = status;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}	
	
}
