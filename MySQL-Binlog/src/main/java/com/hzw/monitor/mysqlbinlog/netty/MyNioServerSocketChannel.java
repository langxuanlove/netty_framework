package com.hzw.monitor.mysqlbinlog.netty;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import java.nio.channels.SocketChannel;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;

import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MyNioServerSocketChannel extends NioServerSocketChannel {
	private static final Logger logger = LogManager.getLogger(MyNioServerSocketChannel.class);

	// 继承已经有的类，用于干预连接
	@Override
	protected int doReadMessages(List<Object> buf) throws Exception {
		// logger.debug("\ndoReadMessages(...) enter....\n触发了新的连接...开始准备2阶段提取");
		// logger.debug("buf :" + buf);
		// LoggerUtils.debug(logger, new Exception().toString());
		// 原始部分,直接关闭
		try {
			SocketChannel tempCh = javaChannel().accept();
			tempCh.close();
		} catch (Exception e) {

		}

		// 移花接木
		SocketChannel ch = MonitorQueue.getObject();
		try {
			if (ch != null) {
				buf.add(new NioSocketChannel(this, ch));
				LoggerUtils.debug(logger, "数据库连接如下: " + ch);
				// logger.debug("buf :" + buf);
				return 1;
			}
		} catch (Throwable t) {
			LoggerUtils.info(logger, "Failed to create a new channel from an accepted socket." + t);
			try {
				ch.close();
			} catch (Throwable t2) {
				LoggerUtils.info(logger, "Failed to close a socket." + t2);
			}
		}

		return 0;

	}
}
