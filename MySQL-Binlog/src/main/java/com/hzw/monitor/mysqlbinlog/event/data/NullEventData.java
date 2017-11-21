package com.hzw.monitor.mysqlbinlog.event.data;

import java.util.ArrayList;

import com.hzw.monitor.mysqlbinlog.event.EventData;
import com.hzw.monitor.mysqlbinlog.event.EventHeader;

public class NullEventData implements EventData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7022857492822395831L;

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
