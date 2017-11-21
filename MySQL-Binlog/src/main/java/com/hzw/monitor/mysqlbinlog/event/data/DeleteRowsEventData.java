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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hzw.monitor.mysqlbinlog.event.EventData;
import com.hzw.monitor.mysqlbinlog.event.EventHeader;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;
import com.hzw.monitor.mysqlbinlog.utils.MyConstants;
import com.hzw.monitor.mysqlbinlog.utils.StringUtils;

public class DeleteRowsEventData implements EventData {
	/**
	 * 
	 */
	private static final Logger logger = LogManager.getLogger(DeleteRowsEventData.class);
	private static final long serialVersionUID = -3478409831005413055L;
	private long tableId;
	// private BitSet includedColumns;
	private String[] includedColumnNames;
	private List<Serializable[]> rows;

	public long getTableId() {
		return tableId;
	}

	public void setTableId(long tableId) {
		this.tableId = tableId;
	}

	public void setIncludedColumns(BitSet includedColumns, HashMap<String, String> mappings) {
		// this.includedColumns = includedColumns;
		this.includedColumnNames = StringUtils.map(includedColumns, mappings);
	}

	public List<Serializable[]> getRows() {
		return rows;
	}

	public void setRows(List<Serializable[]> rows) {
		this.rows = rows;
	}

	public DeleteRowsEventData() {

	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("DeleteRowsEventData");
		sb.append("{tableId=").append(tableId);
		sb.append(", includedColumns=").append(Arrays.toString(includedColumnNames));
		sb.append(", rows=[");
		for (Object[] row : rows) {
			sb.append("\n    ").append(Arrays.toString(row)).append(",");
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

	public ArrayList<String> toJson() {
		ArrayList<String> result = new ArrayList<String>();
		// 准备工作1
		String database = tableMapEventData.getDatabase();
		String table = tableMapEventData.getTable();
		// 准备工作2
		StringBuilder strBuilder;
		String[] columns = this.includedColumnNames;
		int length;
		for (Serializable[] row : rows) {
			// 遍历每一个row
			strBuilder = new StringBuilder();
			strBuilder.append("{");
			strBuilder.append("\"" + MyConstants.DATABASE + "\":\"" + database + "\",");
			strBuilder.append("\"" + MyConstants.TABLE + "\":\"" + table + "\",");
			strBuilder.append("\"" + MyConstants.ACTION_TYPE + "\":\"" + MyConstants.ACTION_DELETE + "\",");
			strBuilder.append("\"" + MyConstants.ACTION_TIME + "\":\"" + header.getTimestamp() + "\",");
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
			LoggerUtils.debug(logger, strBuilder.toString());
		}
		return result;
	}
}
