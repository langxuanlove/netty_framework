package com.hzw.monitor.mysqlbinlog.connection;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;
import com.hzw.monitor.mysqlbinlog.utils.MyProperties;

public class ConnectionFactory {
	private static final Logger logger = LogManager.getLogger(ConnectionFactory.class);

	public static Connection makeObject() {
		return makeObject(MyProperties.getInstance());
	}

	public static Connection makeObject(MyProperties mp) {
		Connection myConn = null;
		if (null != mp) {
			String ip = mp.getIP();
			int port = mp.getPort();
			if (null != ip && port >= 0) {
				try {
					// 在这里创建具体的对象
					SocketAddress sAddress = new InetSocketAddress(ip, port);
					SocketChannel sChannel = SocketChannel.open(sAddress);
					sChannel.configureBlocking(false);// 非阻塞
					myConn = new Connection(sChannel);
				} catch (UnknownHostException e) {
					LoggerUtils.error(logger, e.toString());
				} catch (IOException e) {
					LoggerUtils.error(logger, e.toString());
				}
			}
		}
		// 无论如何，都返回连接，失敗則返回null
		return myConn;
	}

	// 测试
	public static void main(String[] args) {
		Connection myConn = ConnectionFactory.makeObject();
		LoggerUtils.info(logger, "create connection: " + myConn);
		myConn.close();
	}
}
