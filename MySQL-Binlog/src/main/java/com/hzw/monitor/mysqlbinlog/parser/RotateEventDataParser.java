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
import com.hzw.monitor.mysqlbinlog.event.data.RotateEventData;
import com.hzw.monitor.mysqlbinlog.utils.ByteUtils;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;
import com.hzw.monitor.mysqlbinlog.utils.MyAttributes;
import com.hzw.monitor.mysqlbinlog.utils.MyConstants;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class RotateEventDataParser implements EventDataParser {
	private static final Logger logger = LogManager.getLogger(RotateEventDataParser.class);

	public EventData parse(ByteBuf msg, ChannelHandlerContext context, int checksumLength) {
		// TODO Auto-generated method stub
		LoggerUtils.debug(logger, "available:" + msg.readableBytes());
		long binlogPosition = ByteUtils.readUnsignedLong(msg, 8);
		String binlogFileName = ByteUtils.readSpecifiedLengthString(msg,
				ByteUtils.availableWithChecksumLength(msg, checksumLength));
		// 第一步及时保留到全局对象,供后续处理
		MyAttributes myAttribute = context.channel().attr(MyConstants.MY_CONTEXT_ATTRIBUTES).get();
		myAttribute.updateBinlog(binlogFileName, binlogPosition);
		// Logger
		RotateEventData eventData = new RotateEventData();
		eventData.setBinlogFilename(binlogFileName);
		eventData.setBinlogPosition(binlogPosition);
		return eventData;
	}

}
