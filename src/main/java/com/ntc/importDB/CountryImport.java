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

package com.ntc.importDB;

import com.ntc.dao.address.CountryDAO;
import com.ntc.entities.address.Country;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author nghiatc
 * @since Aug 20, 2015
 */
public class CountryImport {

	public static void main(String[] args) {
		HashMap<String, String> lstZone = CSVParser.readzone_code(true);
		HashMap<String, String> lstCode = CSVParser.readzone_code(false);
		List<Country> lstCountry = new ArrayList<Country>();
		for (String countryName : lstZone.keySet()) {
			String countryCode = lstCode.get(countryName) != null ? lstCode.get(countryName) : countryName;
			if (countryName.contains("\"")) {
				countryName = countryName.replaceAll("\"", "");
				countryName = countryName.replaceAll("\\.", ",");
			}
			Country country = new Country(countryCode, countryName);
			lstCountry.add(country);
		}
		//add US
		lstCountry.add(new Country("US", "United States"));
		CountryDAO.getInstance().insertBatch(lstCountry);
		System.out.println("Done");
		System.out.println(CountryDAO.getInstance().getList().size());
	}

}
