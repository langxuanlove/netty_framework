package com.hzw.monitor.mysqlbinlog.handlers;
/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hzw.monitor.mysqlbinlog.utils.MyConstants;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
	private static final Logger logger = LogManager.getLogger(ChildChannelHandler.class);

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		// 要在这里加上所有的处理句柄
		logger.debug("initChannel invoked...");
		ChannelPipeline cp = channel.pipeline();// 下面的次序不能变
		cp.addLast(MyConstants.FIXED_LENGTH_HANDLER, new FixedLengthHandler());
		cp.addLast(MyConstants.Greeting_Packet_Handler, new GreetingPacketResultHandler());
		cp.addLast(MyConstants.Authen_Result_handler, new AuthenticateResultHandler());
		cp.addLast(MyConstants.Fetch_Binlog_NamePosition_Result_Handler, new FetchBinlogNamePositionResultHandler());
		cp.addLast(MyConstants.Fetch_Binlog_CheckSum_ResultHandler, new FetchBinlogChecksumResultHandler());
		cp.addLast(MyConstants.Log_Event_Parse_Handler, new BinlogEventParseHandler());
		//这里用自己的一些机制存放一些属性
		
	}

}
