package com.hzw.monitor.mysqlbinlog.event.data;

import java.util.ArrayList;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import com.hzw.monitor.mysqlbinlog.event.EventData;
import com.hzw.monitor.mysqlbinlog.event.EventHeader;

public class QueryEventData implements EventData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 836735581862808872L;
	private long threadId;
	private long executeTime;
	private int errorCode;
	private String database;
	private String sql;

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public long getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(long executeTime) {
		this.executeTime = executeTime;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
	public String toString() {
		return "QueryEventData [threadId=" + threadId + ", executeTime=" + executeTime + ", errorCode=" + errorCode
				+ ", database=" + database + ", sql=" + sql + "]";
	}

	private EventHeader header;

	public void setEventHeader(EventHeader h) {
		this.header = h;
	}

	public ArrayList<String> toJson() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTableMapEventData(TableMapEventData tableMapEventData) {
		// TODO Auto-generated method stub
		
	}

}
