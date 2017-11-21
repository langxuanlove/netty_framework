package com.hzw.monitor.mysqlbinlog.parser;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import java.io.IOException;
import java.io.Serializable;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hzw.monitor.mysqlbinlog.event.EventData;
import com.hzw.monitor.mysqlbinlog.event.data.TableMapEventData;
import com.hzw.monitor.mysqlbinlog.event.data.WriteRowsEventData;
import com.hzw.monitor.mysqlbinlog.utils.ByteUtils;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;
import com.hzw.monitor.mysqlbinlog.utils.MyAttributes;
import com.hzw.monitor.mysqlbinlog.utils.MyConstants;
import com.hzw.monitor.mysqlbinlog.utils.StringUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class WriteRowsEventDataParser implements EventDataParser {
	private static final Logger logger = LogManager.getLogger(WriteRowsEventDataParser.class);

	private boolean mayContainExtraInformation = false;

	public WriteRowsEventDataParser(boolean b) {
		this.mayContainExtraInformation = b;
	}

	public EventData parse(ByteBuf msg, ChannelHandlerContext context, int checksumLength) {
		// LoggerUtils.debug(logger, "");
		long tableId = ByteUtils.readUnsignedLong(msg, 6);
		msg.skipBytes(2);
		if (mayContainExtraInformation) {
			int extraInfoLength = ByteUtils.readUnsignedInt(msg, 2);
			// inputStream.readInteger(2);
			msg.skipBytes(extraInfoLength - 2);
		}
		int numberOfColumns = ByteUtils.readVariableNumber(msg).intValue();
		// 获取1
		BitSet includedColumns = null;
		try {
			includedColumns = ByteUtils.readBitSet(msg, numberOfColumns, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 获取2
		List<Serializable[]> rows = new LinkedList<Serializable[]>();
		MyAttributes myAttribute = context.channel().attr(MyConstants.MY_CONTEXT_ATTRIBUTES).get();
		TableMapEventData tableMapEventData = myAttribute.getAndRmoveTableMapEventData(tableId);
		String database = tableMapEventData.getDatabase();
		String table = tableMapEventData.getTable();
		try {
			while (msg.readableBytes() > checksumLength) {// 表明还有内容可取
				rows.add(ByteUtils.deserializeRow(tableMapEventData, includedColumns, msg));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 准备返回
		WriteRowsEventData eventData = new WriteRowsEventData();
		eventData.setTableMapEventData(tableMapEventData);// 保留映射关系:database
															// table等
		eventData.setIncludedColumns(includedColumns, myAttribute.getColumnsMapping(database, table));// 保留列名
		eventData.setRows(rows);
		eventData.setTableId(tableId);
		return eventData;
	}

}
