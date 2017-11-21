package com.hzw.monitor.mysqlbinlog.utils;
/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
public class MyProperties {
	private static final Logger logger = LogManager.getLogger(MyProperties.class);

	// 以下为全局需要

	private static MyProperties myProperties = null;// 全局单例变量，一开始就存在

	static {// 静态块里，只加载一次

		Properties props = new Properties();
		try {
			InputStream in = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(MyConstants.CONFIG_FILE);
			props.load(in);
			in.close();
		} catch (Exception e) {
			// logger.error(e.toString());
			LoggerUtils.error(logger, "fail to read config file");
			System.exit(-1);
		}
		// 读取值mysql
		long clientid = Long.parseLong(props.getProperty(MyConstants.MYSQL_CLIENT_ID));
		String ip = props.getProperty(MyConstants.MYSQL_IP, null);
		int port = Integer.parseInt(props.getProperty(MyConstants.MYSQL_PORT, "3306"));
		String username = props.getProperty(MyConstants.MYSQL_USERNAME, null);
		String password = props.getProperty(MyConstants.MYSQL_PASSWORD, null);
		// netty
		int netty_port = Integer.parseInt(props.getProperty(MyConstants.NETTY_PORT, "10000"));
		int netty_boss = Integer.parseInt(props.getProperty(MyConstants.NETTY_BOSS, "1"));
		int netty_worker = Runtime.getRuntime().availableProcessors()
				* Integer.parseInt(props.getProperty(MyConstants.NETTY_WORKER, "2").trim());// 2倍cpu
		props = null;
		// 构造新的对象
		myProperties = new MyProperties(clientid, ip, port, username, password, netty_port, netty_boss, netty_worker);

	}

	public static MyProperties getInstance() {
		return myProperties;
	}

	// 私有属性开始//////////////////////////////////////////////////////////////////
	private long mysql_client_id = 0;
	private String mysql_ip;
	private int mysql_port;
	private String mysql_username;
	private String mysql_password;
	// netty
	private int netty_port;
	private int netty_boss;
	private int netty_worker;

	private MyProperties() {// 私有方法，保证单例

	}

	private MyProperties(long clientid, String ip, int port, String username, String password, int np, int nboss,
			int nworker) {
		// used by mysql
		this.mysql_client_id = clientid;
		this.mysql_ip = ip;
		this.mysql_port = port;
		this.mysql_username = username;
		this.mysql_password = password;
		// used by netty
		this.netty_port = np;
		this.netty_boss = nboss;
		this.netty_worker = nworker;

	}

	public long getClientID() {
		return this.mysql_client_id;
	}

	public String getIP() {
		return this.mysql_ip;
	}

	public int getPort() {
		return this.mysql_port;
	}

	public String getUsername() {
		return this.mysql_username;
	}

	public String getPassword() {
		return this.mysql_password;
	}

	public int getNetty_port() {
		return netty_port;
	}

	public int getNetty_boss() {
		return netty_boss;
	}

	public int getNetty_worker() {
		return netty_worker;
	}

	public String toString() {
		StringBuilder strBuilder = new StringBuilder("\n");
		strBuilder.append(MyConstants.MYSQL_IP).append(": ").append(mysql_ip).append(" ");
		strBuilder.append(MyConstants.MYSQL_PORT).append(": ").append(mysql_port).append(" ");
		strBuilder.append(MyConstants.MYSQL_USERNAME).append(": ").append(mysql_username).append(" ");
		strBuilder.append(MyConstants.MYSQL_PASSWORD).append(": ").append(mysql_password).append("\n");
		strBuilder.append(MyConstants.NETTY_PORT).append(": ").append(netty_port).append(" ");
		strBuilder.append(MyConstants.NETTY_BOSS).append(": ").append(netty_boss).append(" ");
		strBuilder.append(MyConstants.NETTY_WORKER).append(": ").append(netty_worker).append("\n");
		return strBuilder.toString();
	}

	// 测试
	public static void main(String[] args) {
		// just for test
		MyProperties property = MyProperties.getInstance();
		logger.debug(property.toString());
	}
}
