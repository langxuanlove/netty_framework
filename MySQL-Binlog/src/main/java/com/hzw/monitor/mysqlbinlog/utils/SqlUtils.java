package com.hzw.monitor.mysqlbinlog.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SqlUtils {
	private static final Logger logger = LogManager.getLogger(SqlUtils.class);

	// private static final String TABLE_SCHEMA = "TABLE_SCHEMA";
	// private static final String TABLE_NAME = "TABLE_NAME";
	private static final String COLUMN_NAME = "COLUMN_NAME";
	private static final String ORDINAL_POSITION = "ORDINAL_POSITION";

	public static HashMap<String, String> getDatabaseTableColumnsMapping(String ip, int port, String username,
			String password, String database, String table) {

		// 通过查询数据库来获取消息
		// LoggerUtils.debug(logger, "-----------------------------begin");
		HashMap<String, String> mappings = new HashMap<String, String>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		try {
			String url = "jdbc:mysql://" + ip + ":" + port + "/" + database;
			conn = DriverManager.getConnection(url, username, password);// 获取连接
			stmt = conn.createStatement();
			// 构造sql
			String sql = "select COLUMN_NAME, ORDINAL_POSITION ";
			sql += " from INFORMATION_SCHEMA.COLUMNS ";
			sql += "where TABLE_SCHEMA='" + database + "' and TABLE_NAME='" + table + "'";
			//LoggerUtils.debug(logger, "sql:" + sql);
			resultSet = stmt.executeQuery(sql);
			String columnName;
			int position;
			while (null != resultSet && resultSet.next()) {
				// 存在下一行数据
				// String tableSchema = resultSet.getString(TABLE_SCHEMA);
				// String tableSchema = resultSet.getString(TABLE_SCHEMA);
				columnName = resultSet.getString(COLUMN_NAME);
				position = resultSet.getInt(ORDINAL_POSITION);
				// LoggerUtils.debug(logger, "" + columnName + " " + position);
				mappings.put("" + (position - 1), columnName);
			}
		} catch (Exception e) {
			LoggerUtils.error(logger, e.toString());
		} finally {
			// 必须分开关闭
			// 关闭resultset
			if (null != resultSet) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// 关闭stmt
			if (null != stmt) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// 关闭conn
			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// LoggerUtils.debug(logger, "-----------------------------query mapping
		// end");

		return mappings;
	}

	public static boolean isAlterTableSql(String sql) {
		sql = sql.toUpperCase();
		return sql.startsWith("ALTER TABLE");
	}

	public static boolean isDropTableSql(String sql) {
		sql = sql.toUpperCase();
		return sql.startsWith("DROP TABLE");
	}

	public static String getAlterTableName(String sql) {
		String table = null;
		try {
			int firstPosition = -1;
			int secondPosition = -1;
			byte[] data = sql.getBytes();
			int length = data.length;
			for (int index = 0; index < length; index++) {
				if (0x60 == data[index]) {
					if (-1 == firstPosition) {
						firstPosition = index;
					} else if (-1 == secondPosition) {
						secondPosition = index;
						break;// 必须跳出
					}
				}
			}
			firstPosition++;
			table = new String(data, firstPosition, secondPosition - firstPosition);
		} catch (Exception e) {
			table = null;
		}
		return table;
	}

	public static String getDropTableName(String sql) {
		String table = null;
		try {
			int firstPosition = -1;
			int secondPosition = -1;
			byte[] data = sql.getBytes();
			int length = data.length;
			for (int index = 0; index < length; index++) {
				if (0x60 == data[index]) {
					if (-1 == firstPosition) {
						firstPosition = index;
					} else if (-1 == secondPosition) {
						secondPosition = index;
						break;// 必须跳出
					}
				}
			}
			firstPosition++;
			table = new String(data, firstPosition, secondPosition - firstPosition);
		} catch (Exception e) {
			table = null;
		}
		return table;
	}

	public static void main(String[] args) {
		HashMap<String, String> result = getDatabaseTableColumnsMapping("172.172.177.25", 3306, "root", ".", "skyeye",
				"product");
		LoggerUtils.debug(logger, result.toString());
	}
}
