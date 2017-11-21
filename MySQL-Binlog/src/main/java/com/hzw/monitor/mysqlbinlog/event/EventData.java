package com.hzw.monitor.mysqlbinlog.event;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import java.io.Serializable;
import java.util.ArrayList;

import com.hzw.monitor.mysqlbinlog.event.data.TableMapEventData;

public interface EventData extends Serializable {
	public void setEventHeader(EventHeader header);

	public void setTableMapEventData(TableMapEventData tableMapEventData);

	public ArrayList<String> toJson();
}
