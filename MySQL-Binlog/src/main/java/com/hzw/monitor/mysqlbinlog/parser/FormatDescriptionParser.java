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
import com.hzw.monitor.mysqlbinlog.event.data.FormatDescriptionEventData;
import com.hzw.monitor.mysqlbinlog.utils.ByteUtils;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class FormatDescriptionParser implements EventDataParser {
	private static final Logger logger = LogManager.getLogger(FormatDescriptionParser.class);

	public EventData parse(ByteBuf msg, ChannelHandlerContext context, int checksumLength) {
		LoggerUtils.debug(logger, "");
		// TODO Auto-generated method stub
		int binlogVersion = ByteUtils.readUnsignedInt(msg, 2);
		String serverVersion = ByteUtils.readSpecifiedLengthString(msg, 50).trim();
		msg.skipBytes(4);
		int headerLength = ByteUtils.readUnsignedByte(msg);// 仅仅1个字节
		FormatDescriptionEventData eventData = new FormatDescriptionEventData();
		eventData.setBinlogVersion(binlogVersion);
		eventData.setServerVersion(serverVersion);
		eventData.setHeaderLength(headerLength);
		return eventData;
	}

}
