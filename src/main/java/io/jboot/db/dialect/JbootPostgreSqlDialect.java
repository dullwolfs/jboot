/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.db.dialect;

import com.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;
import io.jboot.db.model.Column;
import io.jboot.db.model.Or;
import io.jboot.exception.JbootException;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.StrUtil;

import java.util.List;


public class JbootPostgreSqlDialect extends PostgreSqlDialect implements IJbootModelDialect {


    @Override
    public String forFindByColumns(String table, String loadColumns, List<Column> columns, String orderBy, Object limit) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT ");
        sqlBuilder.append(loadColumns)
                .append(" FROM  \"")
                .append(table).append("\" ");

        appIfNotEmpty(columns, sqlBuilder);


        if (StrUtil.isNotBlank(orderBy)) {
            sqlBuilder.append(" ORDER BY ").append(orderBy);
        }

        if (limit == null) {
            return sqlBuilder.toString();
        }

        if (limit instanceof Number) {
            sqlBuilder.append(" limit ").append(limit).append(" offset ").append(0);
            return sqlBuilder.toString();
        } else if (limit instanceof String && limit.toString().contains(",")) {
            String[] startAndEnd = limit.toString().split(",");
            String start = startAndEnd[0];
            String end = startAndEnd[1];

            sqlBuilder.append(" limit ").append(end).append(" offset ").append(start);
            return sqlBuilder.toString();
        } else {
            throw new JbootException("sql limit is error!,limit must is Number of String like \"0,10\"");
        }
    }


    @Override
    public String forPaginateSelect(String loadColumns) {
        return "SELECT " + loadColumns;
    }


    @Override
    public String forPaginateFrom(String table, List<Column> columns, String orderBy) {
        StringBuilder sqlBuilder = new StringBuilder(" FROM \"").append(table).append("\"");

        appIfNotEmpty(columns, sqlBuilder);

        if (StrUtil.isNotBlank(orderBy)) {
            sqlBuilder.append(" ORDER BY ").append(orderBy);
        }

        return sqlBuilder.toString();
    }


    private void appIfNotEmpty(List<Column> columns, StringBuilder sqlBuilder) {
        if (ArrayUtil.isNotEmpty(columns)) {
            sqlBuilder.append(" WHERE ");

            int index = 0;
            for (Column column : columns) {

                if (column instanceof Or) {
                    // delete last " AND " str
                    sqlBuilder.delete(sqlBuilder.length() - 5,sqlBuilder.length())
                            .append(" OR ");
                }else {
                    sqlBuilder.append(" \"")
                            .append(column.getName())
                            .append("\" ")
                            .append(column.getLogic());


                    if (column.isMustNeedValue()) {
                        sqlBuilder.append(" ? ");
                    }

                    if (index != columns.size() - 1) {
                        sqlBuilder.append(" AND ");
                    }
                }

                index++;
            }
        }
    }

}
