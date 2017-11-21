package com.hzw.monitor.mysqlbinlog.handlers;

import java.util.ArrayList;
/**
 * 
 * @author zhiqiang.liu
 * @2016年1月14日
 * @837500869
 */
import java.util.HashMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.hzw.monitor.mysqlbinlog.event.EventData;
import com.hzw.monitor.mysqlbinlog.event.EventHeader;
import com.hzw.monitor.mysqlbinlog.event.EventType;
import com.hzw.monitor.mysqlbinlog.parser.DeleteRowsEventDataParser;
import com.hzw.monitor.mysqlbinlog.parser.EventDataParser;
import com.hzw.monitor.mysqlbinlog.parser.FormatDescriptionParser;
import com.hzw.monitor.mysqlbinlog.parser.NullEventDataParser;
import com.hzw.monitor.mysqlbinlog.parser.QueryEventDataParser;
import com.hzw.monitor.mysqlbinlog.parser.RotateEventDataParser;
import com.hzw.monitor.mysqlbinlog.parser.RowsQueryEventDataParser;
import com.hzw.monitor.mysqlbinlog.parser.TableMapEventDataParser;
import com.hzw.monitor.mysqlbinlog.parser.UpdateRowsEventDataParser;
import com.hzw.monitor.mysqlbinlog.parser.WriteRowsEventDataParser;
import com.hzw.monitor.mysqlbinlog.parser.XidEventDataParser;
import com.hzw.monitor.mysqlbinlog.utils.ByteUtils;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;
import com.hzw.monitor.mysqlbinlog.utils.MyAttributes;
import com.hzw.monitor.mysqlbinlog.utils.MyConstants;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class BinlogEventParseHandler extends SimpleChannelInboundHandler<ByteBuf> {
	// http://dev.mysql.com/doc/internals/en/binlog-event.html
	private static final Logger logger = LogManager.getLogger(BinlogEventParseHandler.class);
	private static final EventType[] EVENT_TYPES = EventType.values();
	private boolean enterLargeBlockPacket = false;// 暂时不支持这种情况,TODO
	private HashMap<EventType, EventDataParser> parsers = new HashMap<EventType, EventDataParser>();

	public BinlogEventParseHandler() {
		// null_event
		parsers.put(EventType.UNKNOWN, new NullEventDataParser());
		// rotate
		parsers.put(EventType.ROTATE, new RotateEventDataParser());
		// format_description
		parsers.put(EventType.FORMAT_DESCRIPTION, new FormatDescriptionParser());
		// query
		parsers.put(EventType.QUERY, new QueryEventDataParser());
		// table_map
		parsers.put(EventType.TABLE_MAP, new TableMapEventDataParser());
		// delete_rows
		parsers.put(EventType.DELETE_ROWS, new DeleteRowsEventDataParser(false));
		// ext_delete_rows
		parsers.put(EventType.EXT_DELETE_ROWS, new DeleteRowsEventDataParser(true));
		// xid
		parsers.put(EventType.XID, new XidEventDataParser());
		// write_rows
		parsers.put(EventType.WRITE_ROWS, new WriteRowsEventDataParser(false));
		// ext_write_rows
		parsers.put(EventType.EXT_WRITE_ROWS, new WriteRowsEventDataParser(true));
		// update_rows
		parsers.put(EventType.UPDATE_ROWS, new UpdateRowsEventDataParser(false));
		// ext_update_rows
		parsers.put(EventType.EXT_UPDATE_ROWS, new UpdateRowsEventDataParser(true));
		// rows_query
		parsers.put(EventType.ROWS_QUERY, new RowsQueryEventDataParser());
	}

	private void handle(EventType eventType, EventData eventData) {
		// 保证参数OK
		if (null == eventType || null == eventData) {
			return;
		}
		if (EventType.isValid(eventType)) {// 有效事件才处理
			ArrayList<String> datas = eventData.toJson();
			if (null != datas) {
				for (String data : datas) {
					// MQUtils.sendMqQueue("kmonitor-binlog", data);
					LoggerUtils.debug(logger, data);
				}
			}
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, ByteBuf msg) throws Exception {
		try {
			// LoggerUtils.debug(logger, "进入BinLogEventParseHandler处理器");
			EventHeader header = new EventHeader();
			header.setTimestamp(ByteUtils.readUnsignedLong(msg, 4) * 1000L);
			header.setEventType(EVENT_TYPES[ByteUtils.readUnsignedInt(msg, 1)]);
			header.setServerId(ByteUtils.readUnsignedLong(msg, 4));
			header.setEventLength(ByteUtils.readUnsignedLong(msg, 4));
			header.setNextPosition(ByteUtils.readUnsignedLong(msg, 4));
			header.setFlag(ByteUtils.readUnsignedInt(msg, 2));
			// LoggerUtils.debug(logger, header.toString());
			// 2)获取EventParser
			EventDataParser parser = parsers.get(header.getEventType());
			MyAttributes myAttribute = context.channel().attr(MyConstants.MY_CONTEXT_ATTRIBUTES).get();
			int checksumLength = (int) myAttribute.getChecksumLength();
			EventData eventData = parser.parse(msg, context, checksumLength);
			// LoggerUtils.debug(logger, eventData.toString());
			// 3)关联
			eventData.setEventHeader(header);
			// 4)处理
			handle(header.getEventType(), eventData);
		} catch (Exception e) {
			LoggerUtils.error(logger, e.toString());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		// cause.printStackTrace();//务必要关闭
		LoggerUtils.error(logger, cause.toString());
		ctx.close();
	}

}
