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

public class XidEventData implements EventData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1830588105284956838L;
	private long xid;

	public long getXid() {
		return xid;
	}

	public void setXid(long xid) {
		this.xid = xid;
	}

	@Override
	public String toString() {
		return "XidEventData [xid=" + xid + "]";
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
