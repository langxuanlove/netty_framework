package com.hzw.monitor.mysqlbinlog.event.data;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hzw.monitor.mysqlbinlog.event.EventData;
import com.hzw.monitor.mysqlbinlog.event.EventHeader;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;
import com.hzw.monitor.mysqlbinlog.utils.MyConstants;
import com.hzw.monitor.mysqlbinlog.utils.StringUtils;

public class UpdateRowsEventData implements EventData {
	/**
	 * 
	 */
	private static final Logger logger = LogManager.getLogger(UpdateRowsEventData.class);
	private static final long serialVersionUID = 2891052649804109908L;
	private long tableId;
	// 修改前
	// private BitSet includedColumnsBeforeUpdate;
	private String[] includedColumnsBeforeUpdateColumnNames;
	// 修改后
	// private BitSet includedColumns;
	private String[] includedColumnNames;

	private List<Map.Entry<Serializable[], Serializable[]>> rows;

	public void setIncludedColumnsBeforeUpdate(BitSet includedColumnsBeforeUpdate, HashMap<String, String> mappings) {
		// this.includedColumnsBeforeUpdate = includedColumnsBeforeUpdate;
		this.includedColumnsBeforeUpdateColumnNames = StringUtils.map(includedColumnsBeforeUpdate, mappings);
	}

	public void setIncludedColumns(BitSet includedColumns, HashMap<String, String> mappings) {
		// this.includedColumns = includedColumns;
		this.includedColumnNames = StringUtils.map(includedColumns, mappings);
	}

	public void setRows(List<Map.Entry<Serializable[], Serializable[]>> rows) {
		this.rows = rows;
	}

	public long getTableId() {
		return tableId;
	}

	public void setTableId(long tableId) {
		this.tableId = tableId;
	}

	// public BitSet getIncludedColumns() {
	// return includedColumns;
	// }

	public UpdateRowsEventData() {

	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("UpdateRowsEventData");
		sb.append("{tableId=").append(tableId);
		sb.append(", includedColumnsBeforeUpdate=")
				.append(Arrays.toString(this.includedColumnsBeforeUpdateColumnNames));
		sb.append(", includedColumns=").append(Arrays.toString(this.includedColumnNames));
		sb.append(", rows=[");
		for (Map.Entry<Serializable[], Serializable[]> row : rows) {
			sb.append("\n    ").append("{before=").append(Arrays.toString(row.getKey())).append(", after=")
					.append(Arrays.toString(row.getValue())).append("},");
		}
		if (!rows.isEmpty()) {
			sb.replace(sb.length() - 1, sb.length(), "\n");
		}
		sb.append("]}");
		return sb.toString();
	}

	private EventHeader header;

	public void setEventHeader(EventHeader h) {
		this.header = h;
	}

	private TableMapEventData tableMapEventData;

	public void setTableMapEventData(TableMapEventData t) {
		tableMapEventData = t;
	}

	// 可以开始获取json字符串了
	public ArrayList<String> toJson() {
		ArrayList<String> result = new ArrayList<String>();
		// 准备工作1
		String database = tableMapEventData.getDatabase();
		String table = tableMapEventData.getTable();
		// 准备工作2
		StringBuilder strBuilder;
		String[] columns = this.includedColumnNames;// 修改后的
		int length;
		for (Map.Entry<Serializable[], Serializable[]> doubleRow : rows) {
			// 遍历每一个row
			strBuilder = new StringBuilder();
			strBuilder.append("{");
			strBuilder.append("\"" + MyConstants.DATABASE + "\":\"" + database + "\",");
			strBuilder.append("\"" + MyConstants.TABLE + "\":\"" + table + "\",");
			strBuilder.append("\"" + MyConstants.ACTION_TYPE + "\":\"" + MyConstants.ACTION_UPDATE + "\",");
			strBuilder.append("\"" + MyConstants.ACTION_TIME + "\":\"" + header.getTimestamp() + "\",");

			Serializable[] row = doubleRow.getValue();// 修改之后的数据
			length = row.length;
			for (int index = 0; index < length; index++) {
				strBuilder.append("\"");
				strBuilder.append(columns[index]);
				strBuilder.append("\":\"");
				strBuilder.append(row[index]);
				strBuilder.append("\"");
				if (index + 1 != length) {
					strBuilder.append(",");
				}
			}
			strBuilder.append("}");
			result.add(strBuilder.toString());
			//LoggerUtils.debug(logger, strBuilder.toString());
		}
		return result;
	}
}
