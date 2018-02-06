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

import com.ntc.dao.address.StateDAO;
import com.ntc.entities.address.State;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nghiatc
 * @since Aug 20, 2015
 */
public class StateImport {

	public static void main(String[] args) {
		List<State> lstState = new ArrayList<State>();
		String[][] stateData = CSVParser.readState();
		for (int idx = 0; idx < stateData.length; idx++) {
			State state = new State(stateData[idx][0], stateData[idx][2], stateData[idx][1]);
			lstState.add(state);
		}
		StateDAO.getInstance().insertBatch(lstState);
		System.out.println("Done");
		System.out.println(StateDAO.getInstance().getListByCountry("US").size());
		System.out.println(StateDAO.getInstance().getListByCountry("GB").size());
	}

}
