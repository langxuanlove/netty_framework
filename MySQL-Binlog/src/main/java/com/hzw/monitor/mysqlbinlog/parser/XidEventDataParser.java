package com.hzw.monitor.mysqlbinlog.parser;
/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hzw.monitor.mysqlbinlog.event.EventData;
import com.hzw.monitor.mysqlbinlog.event.data.XidEventData;
import com.hzw.monitor.mysqlbinlog.utils.ByteUtils;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class XidEventDataParser implements EventDataParser {// 提交数据
	private static final Logger logger = LogManager.getLogger(XidEventData.class);

	public EventData parse(ByteBuf msg, ChannelHandlerContext context, int checksumLength) {
		//LoggerUtils.debug(logger, "");
		long xid = ByteUtils.readUnsignedLong(msg, 8);
		XidEventData eventData = new XidEventData();
		eventData.setXid(xid);
		return eventData;
	}

}
