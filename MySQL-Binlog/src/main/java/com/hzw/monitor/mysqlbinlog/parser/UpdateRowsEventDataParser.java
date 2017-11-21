package com.hzw.monitor.mysqlbinlog.parser;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.hzw.monitor.mysqlbinlog.event.EventData;
import com.hzw.monitor.mysqlbinlog.event.data.TableMapEventData;
import com.hzw.monitor.mysqlbinlog.event.data.UpdateRowsEventData;
import com.hzw.monitor.mysqlbinlog.utils.ByteUtils;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;
import com.hzw.monitor.mysqlbinlog.utils.MyAttributes;
import com.hzw.monitor.mysqlbinlog.utils.MyConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class UpdateRowsEventDataParser implements EventDataParser {
	private static final Logger logger = LogManager.getLogger(UpdateRowsEventDataParser.class);

	private boolean mayContainExtraInformation = false;

	public UpdateRowsEventDataParser(boolean b) {
		mayContainExtraInformation = b;
	}

	public EventData parse(ByteBuf msg, ChannelHandlerContext context, int checksumLength) {
		// LoggerUtils.debug(logger, "");
		// LoggerUtils.debug(logger,
		// "enter UpdateRowsEventDataParser,mayContainExtraInformation:" +
		// mayContainExtraInformation);
		// LoggerUtils.debug(logger, "enter
		// UpdateRowsEventDataParser.parse...");
		long tableId = ByteUtils.readUnsignedLong(msg, 6);
		msg.skipBytes(2);
		if (mayContainExtraInformation) {
			int extraInfoLength = ByteUtils.readUnsignedInt(msg, 2);
			msg.skipBytes(extraInfoLength - 2);
		}
		// 继续处理
		int numberOfColumns = ByteUtils.readVariableNumber(msg).intValue();
		BitSet includedColumnsBeforeUdpate = null;
		try {
			includedColumnsBeforeUdpate = ByteUtils.readBitSet(msg, numberOfColumns, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BitSet includedColumns = null;
		try {
			includedColumns = ByteUtils.readBitSet(msg, numberOfColumns, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// rows
		List<Map.Entry<Serializable[], Serializable[]>> rows = new ArrayList<Map.Entry<Serializable[], Serializable[]>>();
		MyAttributes myAttribute = context.channel().attr(MyConstants.MY_CONTEXT_ATTRIBUTES).get();
		TableMapEventData tableMapEventData = myAttribute.getAndRmoveTableMapEventData(tableId);
		String database = tableMapEventData.getDatabase();
		String table = tableMapEventData.getTable();
		try {
			while (msg.readableBytes() > checksumLength) {// 表明还有内容可取
				// LoggerUtils.debug(logger, "还有数据...");
				rows.add(new AbstractMap.SimpleEntry<Serializable[], Serializable[]>(
						ByteUtils.deserializeRow(tableMapEventData, includedColumnsBeforeUdpate, msg),
						ByteUtils.deserializeRow(tableMapEventData, includedColumns, msg)));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 构造
		UpdateRowsEventData eventData = new UpdateRowsEventData();
		eventData.setTableId(tableId);
		eventData.setTableMapEventData(tableMapEventData);// 为了获取database&table
		HashMap<String, String> mappings = myAttribute.getColumnsMapping(database, table);
		eventData.setIncludedColumnsBeforeUpdate(includedColumnsBeforeUdpate, mappings);
		eventData.setIncludedColumns(includedColumns, mappings);
		eventData.setRows(rows);
		return eventData;
	}

}
