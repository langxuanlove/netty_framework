package com.hzw.monitor.mysqlbinlog.command;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 * @QQ: 837500869
 */
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hzw.monitor.mysqlbinlog.type.CommandType;
import com.hzw.monitor.mysqlbinlog.utils.ByteUtils;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;

public class CheckBinlogChecksumCommand {
	private static final Logger logger = LogManager.getLogger(CheckBinlogChecksumCommand.class);
	private String sql = null;

	public CheckBinlogChecksumCommand(String s) {
		this.sql = s;
	}

	public void write(ChannelHandlerContext context) {
		LoggerUtils.debug(logger, "write CheckBinlogChecksumCommand...");
		byte[] queryBytes = ByteUtils.writeByte((byte) CommandType.QUERY.ordinal(), 1);
		byte[] sqlBytes = this.sql.getBytes();// 不用带\0,所以不需要使用ByteUtils
		// 准备
		int totalCount = queryBytes.length + sqlBytes.length;
		byte[] totalCountBytes = ByteUtils.writeInt(totalCount, 3);
		byte[] commandTypeBytes = new byte[1];
		commandTypeBytes[0] = 0;
		//
		ByteBuf finalBuf = PooledByteBufAllocator.DEFAULT.directBuffer(totalCount + 4);
		finalBuf.writeBytes(totalCountBytes).writeBytes(commandTypeBytes).writeBytes(queryBytes).writeBytes(sqlBytes);
		context.channel().writeAndFlush(finalBuf);// 缓存清理
		// LoggerUtils.debug(logger, "发送CheckBinlogChecksumCommand succeed...");

	}
}
