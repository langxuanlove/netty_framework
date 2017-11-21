package com.hzw.monitor.mysqlbinlog.connection;
/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Connection {
	private static final Logger logger = LogManager.getLogger(Connection.class);
	private SocketChannel socketChannel;

	public Connection(SocketChannel s) {
		this.socketChannel = s;
	}

	public SocketChannel getSocketChannel() {
		return this.socketChannel;
	}

	public void close() {
		if (null != socketChannel) {
			try {
				socketChannel.close();
				logger.debug("close socket: " + socketChannel);
			} catch (IOException e) {
			}
			socketChannel = null;
		}

	}
}
