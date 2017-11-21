package com.hzw.monitor.mysqlbinlog.server;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import java.io.IOException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hzw.monitor.mysqlbinlog.connection.Connection;
import com.hzw.monitor.mysqlbinlog.connection.ConnectionFactory;
import com.hzw.monitor.mysqlbinlog.netty.MonitorQueue;
import com.hzw.monitor.mysqlbinlog.netty.NettyServer;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;

public class MyServer {
	private static final Logger logger = LogManager.getLogger(MyServer.class);
	private static final String name = "com.mysql.jdbc.Driver";

	static {// 加载1次就可以了,启动时加载mysql驱动程序，免得后面耗时
		try {
			Class.forName(name);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			LoggerUtils.error(logger, e.toString());
		}
	}

	public static void main(String[] args) throws IOException {
		LoggerUtils.debug(logger, "system begins...");
		// 0启动Netty TCP服务器线程
		NettyServer.start();

		// 1 创建连接并抛到Netty队列里
		LoggerUtils.debug(logger, "try to create connection to mysql...");
		Connection conn = ConnectionFactory.makeObject();
		if (null == conn) {
			LoggerUtils.error(logger, "create socket  failed");
			System.exit(-1);
		} else {
			LoggerUtils.debug(logger, "create socket succeed: " + conn.getSocketChannel());
		}
		// 尝试纳入netty的管理范围
		MonitorQueue.addObject(conn.getSocketChannel());
	}

}
