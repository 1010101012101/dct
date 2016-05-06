/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 * 
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */
package cn.vlabs.duckling.vwb.service.backup.pagenation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;


public class PaginationHelper<E> {
	
	public CurrentPage<E> fetchPage(final JdbcTemplate jt,
			final String sqlCountRows, final String sqlFetchRows,
			final Object args[], final int pageNo, final int pageSize,
			final ParameterizedRowMapper<E> rowMapper) {
		
		// determine how many rows are available
		final int rowCount = jt.queryForInt(sqlCountRows, args);
		
		// calculate the number of pages
		int pageCount = rowCount / pageSize;
		if (rowCount > pageSize * pageCount) {
			pageCount++;
		}
		
		// create the page object
		final CurrentPage<E> page = new CurrentPage<E>();
		page.setPageNumber(pageNo);
		page.setPagesAvailable(pageCount);
		
		// fetch a single page of results
		final int startRow = (pageNo - 1) * pageSize;
		jt.query(sqlFetchRows, args, new ResultSetExtractor<CurrentPage<E>>() {
			public CurrentPage<E> extractData(ResultSet rs) throws SQLException, DataAccessException {
				final List<E> pageItems = page.getPageItems();
				int currentRow = 0;
				while (rs.next() && currentRow < startRow + pageSize) {
					if (currentRow >= startRow) {
						pageItems.add(rowMapper.mapRow(rs, currentRow));
					}
					currentRow++;
				}
				return page;
			}
		});
		return page;
	}
}