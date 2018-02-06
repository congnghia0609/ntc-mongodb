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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author nghiatc
 * @since Aug 20, 2015
 */
public class CSVParser {

	private static final String FEE_SHIP = "file/feeship2016.csv";
	private static final String COUNTRY_CODE = "file/countryCode.csv";
	private static final String ZONE = "file/zone.csv";
	private static final String STATE = "file/state.csv";
	private static final int STATENUM = 3532;
	
	public static String[][] readState() {
		Pattern statePattern = Pattern.compile("^\"(.*)?\",\"(.*)?\",\"(.*)?\"$");
		String[][] lstState = new String[STATENUM][3];
		BufferedReader br = null;
		String line = "";
		int idx = 0;
		try {
			br = new BufferedReader(new FileReader(STATE));
			while ((line = br.readLine()) != null) {
				// preprocess
				Matcher matcher = statePattern.matcher(line);
                if (matcher.find()) {
                    if (idx >= STATENUM){
                    	break;
                    }
                    lstState[idx][0] = matcher.group(1).trim();
                    lstState[idx][1] = matcher.group(2).trim();
                    lstState[idx++][2] = matcher.group(3).trim();
                }
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return lstState;
	}
	
	public static HashMap<String, double[]> readFee() {
		HashMap<String, double[]> lstFS = new HashMap<>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try {
			br = new BufferedReader(new FileReader(FEE_SHIP));
			while ((line = br.readLine()) != null) {
				// preprocess
				for (int i = 0; i <= 9; i++)
					line = line.replaceAll("," + i, "" + i);
				String[] arr = line.split(cvsSplitBy);
				// Read zone
				String zone = arr[1];
				// Read fee of zone
				double[] lstFee = null;
				if (arr.length >= 3) {
					lstFee = new double[arr.length - 2];
					for (int i = 3; i < arr.length; i++) {
						if (arr[i].contains("$"))
							try {
								if (arr[i].contains("\"")) {
									lstFee[i - 3] = Double.parseDouble(arr[i].substring(2, arr[i].length() - 1));
								} else {
									lstFee[i - 3] = Double.parseDouble(arr[i].substring(1));
								}
							} catch (Exception e) {
								System.out.println(arr[i]);
							}
					}
				}
				lstFS.put(zone, lstFee);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		for (int i = 0; i < lstFS.get("E").length; i++) {
			System.out.print(lstFS.get("E")[i] + " ");
		}
		return lstFS;
	}

	public static HashMap<String, String> readzone_code(boolean isZone) {
		HashMap<String, String> result = new HashMap<>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try {
			if (isZone)
				br = new BufferedReader(new FileReader(ZONE));
			else
				br = new BufferedReader(new FileReader(COUNTRY_CODE));
			while ((line = br.readLine()) != null) {
				// preprocess
				line = line.replaceAll(", ", ". ");
				String[] arr = line.split(cvsSplitBy);
				for (int i = 0; i < arr.length - 1; i = i + 2) {
					if (arr[i] != "") {
						// Read key
						String key = arr[i];
						// Read value
						String value = arr[i + 1];
						result.put(key, value);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	
	public static void main(String[] args) {
		String[][] test = readState();
		HashMap<String, double[]> fee = readFee();
//		for (String key : test.keySet()) {
//			System.out.println(key);
//		}
		System.out.println("\n" + fee.get("E").length);

	}
	
	

}
