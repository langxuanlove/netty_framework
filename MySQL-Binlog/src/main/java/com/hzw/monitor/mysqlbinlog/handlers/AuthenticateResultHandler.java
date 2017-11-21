package com.hzw.monitor.mysqlbinlog.handlers;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hzw.monitor.mysqlbinlog.command.FetchBinlogNamePositionCommand;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuthenticateResultHandler extends SimpleChannelInboundHandler<ByteBuf> {
	private static final Logger logger = LogManager.getLogger(AuthenticateResultHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext context, ByteBuf msg) throws Exception {
		try {
			// 验证结果，既然能到这里，说明是正确的
			// 前面验证了肯定不是-1(0xFF),必须为0x00才是正确的
			// 不需要做任何验证
			// :)
			// 再发送一个命令,前提是自己没有设置指定的binlogname & binlogPosition
			new FetchBinlogNamePositionCommand("show master status").write(context);
			context.pipeline().remove(this);// 完成使命，退出历史舞台
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
