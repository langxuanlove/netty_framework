package com.hzw.monitor.mysqlbinlog.event.data;

import java.util.ArrayList;

import com.hzw.monitor.mysqlbinlog.event.EventData;
import com.hzw.monitor.mysqlbinlog.event.EventHeader;

/**
 * 
 * @author gqliu
 * 2016年1月13日
 *
 */
public class RowsQueryEventData implements EventData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8263641770089808861L;
	
	private String query;
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RowsQueryEventData");
        sb.append("{query='").append(query).append('\'');
        sb.append('}');
        return sb.toString();
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
