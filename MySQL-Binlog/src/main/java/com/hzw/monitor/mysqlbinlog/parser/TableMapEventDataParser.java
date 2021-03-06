package com.hzw.monitor.mysqlbinlog.parser;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 * @qq:837500869
 */
import java.io.IOException;
import java.util.BitSet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hzw.monitor.mysqlbinlog.event.EventData;
import com.hzw.monitor.mysqlbinlog.event.data.TableMapEventData;
import com.hzw.monitor.mysqlbinlog.utils.ByteUtils;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;
import com.hzw.monitor.mysqlbinlog.utils.MyAttributes;
import com.hzw.monitor.mysqlbinlog.utils.MyConstants;
import com.hzw.monitor.mysqlbinlog.utils.StringUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class TableMapEventDataParser implements EventDataParser {
	private static final Logger logger = LogManager.getLogger(TableMapEventDataParser.class);

	public EventData parse(ByteBuf msg, ChannelHandlerContext context, int checksumLength) {
		// LoggerUtils.debug(logger, "enter TableMapEventDataParser.parse...");
		long tableId = ByteUtils.readUnsignedLong(msg, 6);
		msg.skipBytes(3);// 2 bytes reserved for future use + 1 for the length
							// of database name
		String database = ByteUtils.readZeroTerminatedString(msg);
		msg.skipBytes(1);// table name
		String table = ByteUtils.readZeroTerminatedString(msg);
		Number columnsNumber = ByteUtils.readVariableNumber(msg);
		byte[] columnTypes = ByteUtils.readSpecifiedLengthBytes(msg, columnsNumber.intValue());
		ByteUtils.readVariableNumber(msg);// 不使用,// metadata length
		// 接下来比较复杂
		int[] columnMetadata = null;
		BitSet columnNullability = null;
		try {
			columnMetadata = ByteUtils.readMetadata(msg, columnTypes);
			columnNullability = ByteUtils.readBitSet(msg, columnsNumber.intValue(), true);
		} catch (IOException e) {
			LoggerUtils.error(logger, e.toString());
		}
		TableMapEventData eventData = new TableMapEventData();
		eventData.setTableId(tableId);
		eventData.setDatabase(database);
		eventData.setTable(table);
		eventData.setColumnTypes(columnTypes);
		eventData.setColumnMetadata(columnMetadata);
		eventData.setColumnNullability(columnNullability);
		// 返回前保留到上下文中,供后续事件使用
		MyAttributes myAttribute = context.channel().attr(MyConstants.MY_CONTEXT_ATTRIBUTES).get();
		myAttribute.putTableMapEventData(tableId, eventData);
		// 如果此时没有[database-table-column]的映射关系，也没用,所以要拉取一次
		myAttribute.ensureDatabaseTableColumnsMappingExist(database, table, false);// 非强制更新
		return eventData;
	}

}
