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
import com.hzw.monitor.mysqlbinlog.event.data.QueryEventData;
import com.hzw.monitor.mysqlbinlog.utils.ByteUtils;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;
import com.hzw.monitor.mysqlbinlog.utils.MyAttributes;
import com.hzw.monitor.mysqlbinlog.utils.MyConstants;
import com.hzw.monitor.mysqlbinlog.utils.SqlUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class QueryEventDataParser implements EventDataParser {
	private static final Logger logger = LogManager.getLogger(QueryEventDataParser.class);

	public EventData parse(ByteBuf msg, ChannelHandlerContext context, int checksumLength) {
		// TODO Auto-generated method stub
		// LoggerUtils.debug(logger, "enter QueryEventDataParser.parse(...)");
		long threadId = ByteUtils.readUnsignedLong(msg, 4);
		long executeTime = ByteUtils.readUnsignedLong(msg, 4);
		msg.skipBytes(1);// 忽略1个字节
		int errorCode = ByteUtils.readUnsignedInt(msg, 2);
		msg.skipBytes(ByteUtils.readUnsignedInt(msg, 2));
		String database = ByteUtils.readZeroTerminatedString(msg);
		String sql = ByteUtils.readSpecifiedLengthString(msg,
				ByteUtils.availableWithChecksumLength(msg, checksumLength));
		QueryEventData eventData = new QueryEventData();
		eventData.setThreadId(threadId);
		eventData.setExecuteTime(executeTime);
		eventData.setErrorCode(errorCode);
		eventData.setDatabase(database);
		eventData.setSql(sql);
		// 0)创建表,不强拉，留到产生数据时来做
		// 1) 修改表结构，需要强制拉取数据
		if (SqlUtils.isAlterTableSql(sql)) {
			String table = SqlUtils.getAlterTableName(sql);
			LoggerUtils.debug(logger, "altered table name is :" + table);
			if (null != table) {
				MyAttributes myAttribute = context.channel().attr(MyConstants.MY_CONTEXT_ATTRIBUTES).get();
				myAttribute.ensureDatabaseTableColumnsMappingExist(database, table, true);// 强制更新
			}
		} else if (SqlUtils.isDropTableSql(sql)) {
			String table = SqlUtils.getDropTableName(sql);
			LoggerUtils.debug(logger, "dropped table name is :" + table);
			if (null != table) {// 强制删除此表的映射关系
				MyAttributes myAttribute = context.channel().attr(MyConstants.MY_CONTEXT_ATTRIBUTES).get();
				myAttribute.ensureDatabaseTableColumnsMappingDeleted(database, table);
			}
		}
		return eventData;
	}

}
