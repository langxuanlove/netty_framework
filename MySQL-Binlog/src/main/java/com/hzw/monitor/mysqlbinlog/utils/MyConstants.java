package com.hzw.monitor.mysqlbinlog.utils;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import io.netty.util.AttributeKey;

public class MyConstants {
	//// https://dev.mysql.com/doc/internals/en/sending-more-than-16mbyte.html
	public static final int MAX_PACKET_LENGTH = 16777215;

	// 解析配置文件使用
	public static String CONFIG_FILE = "mysqlbinlog.properties";
	public static String MYSQL_CLIENT_ID = "clientid";
	public static String MYSQL_IP = "ip";
	public static String MYSQL_PORT = "port";
	public static String MYSQL_USERNAME = "username";
	public static String MYSQL_PASSWORD = "password";

	// netty使用
	public static String NETTY_PORT = "netty_server_port";
	public static String NETTY_BOSS = "netty_boss_number";
	public static String NETTY_WORKER = "netty_worker_number";

	// handler使用
	public static String FIXED_LENGTH_HANDLER = "FIXED_LENGTH_HANDLER";
	public static String FIXED_LENGTH_HANDLER_V2 = "FIXED_LENGTH_HANDLER_V2";
	public static String Greeting_Packet_Handler = "Greeting_Packet_Handler";
	public static String Authen_Result_handler = "Authen_Result_handler";
	public static String Fetch_Binlog_NamePosition_Result_Handler = "Fetch_Binlog_NamePosition_Result_Handler";
	public static String Fetch_Binlog_CheckSum_ResultHandler = "Fetch_Binlog_CheckSum_ResultHandler";
	public static String Log_Event_Parse_Handler = "Log_Event_Parse_Handler";

	// 我自己的
	@SuppressWarnings("deprecation")
	public static AttributeKey<MyAttributes> MY_CONTEXT_ATTRIBUTES = new AttributeKey<MyAttributes>(
			"MY_CONTEXT_ATTRIBUTES");
	// toJson时使用
	public static String DATABASE = "databaseName";
	public static String TABLE = "tableName";
	public static String ACTION_TYPE = "optType";
	public static String ACTION_WRITE = "INSERT";
	public static String ACTION_UPDATE = "UPDATE";
	public static String ACTION_DELETE = "DELETE";
	public static String ACTION_TIME = "optTime";

}
