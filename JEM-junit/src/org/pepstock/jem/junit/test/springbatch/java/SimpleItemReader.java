/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pepstock.jem.junit.test.springbatch.java;

import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * Example of Item Reader to use in spring batch for chunck processing
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class SimpleItemReader implements ItemReader<String> {

	private List<String> itemList;

	int count = 0;

	@Override
	public String read() throws Exception, UnexpectedInputException,
			ParseException {
		if (itemList != null && itemList.size() > 0) {
			if (count < itemList.size()) {
				String item = itemList.get(count);
				count++;
				return item;
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param itemList
	 */
	public void setItemList(List<String> itemList) {
		this.itemList = itemList;
	}

}
