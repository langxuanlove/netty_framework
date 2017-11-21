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

public class RotateEventData implements EventData {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7492611888873768930L;
	private String binlogFilename;
	private long binlogPosition;

	public String getBinlogFilename() {
		return binlogFilename;
	}

	public void setBinlogFilename(String binlogFilename) {
		this.binlogFilename = binlogFilename;
	}

	public long getBinlogPosition() {
		return binlogPosition;
	}

	public void setBinlogPosition(long binlogPosition) {
		this.binlogPosition = binlogPosition;
	}

	public String toString() {
		return "type:ROTATE binlogFilename" + binlogFilename + " binlogPosition:" + binlogPosition;
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
