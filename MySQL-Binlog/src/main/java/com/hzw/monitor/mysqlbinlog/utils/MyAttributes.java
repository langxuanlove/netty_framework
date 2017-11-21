package com.hzw.monitor.mysqlbinlog.utils;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hzw.monitor.mysqlbinlog.event.data.TableMapEventData;
import com.hzw.monitor.mysqlbinlog.type.ChecksumType;

public class MyAttributes {// 每个连接一个MyAttributes对象
	private static final Logger logger = LogManager.getLogger(MyAttributes.class);
	// 0)ip & port
	private String ip;
	private int port;
	private String username;
	private String password;

	public MyAttributes(String i, int p, String u, String pw) {
		ip = i;
		port = p;
		username = u;
		password = pw;
	}

	// 1)file & position
	private String binlogFileName = null;
	private long binlogPosition = 4;

	public String getBinlogFileName() {
		return binlogFileName;
	}

	public void setBinlogFileName(String binlogFileName) {
		this.binlogFileName = binlogFileName;
	}

	public long getBinlogPosition() {
		return binlogPosition;
	}

	public void setBinlogPosition(long binlogPosition) {
		this.binlogPosition = binlogPosition;
	}

	public void updateBinlog(String name, long position) {
		this.binlogFileName = name;
		this.binlogPosition = position;
	}

	// 2) checksum
	private ChecksumType checksumType = ChecksumType.NONE;

	public ChecksumType getChecksumType() {
		return checksumType;
	}

	public long getChecksumLength() {
		return this.checksumType.getLength();
	}

	public void setChecksumType(ChecksumType checksumType) {
		this.checksumType = checksumType;
	}

	// 3)tableMapEventDatas
	// 临时性的事件处理,用完了应该立刻删除,防止内存占用过多
	private HashMap<Long, TableMapEventData> tableMapEventDatas = new HashMap<Long, TableMapEventData>();

	public void putTableMapEventData(long tableId, TableMapEventData data) {
		tableMapEventDatas.put(tableId, data);
	}

	public TableMapEventData getAndRmoveTableMapEventData(long tableId) {
		// 直接顺带删除了
		return tableMapEventDatas.remove(tableId);
	}

	// 4)保留本连接对应的数据库-表-列名的关系// 处理database-table-columns映射关系
	private HashMap<String, HashMap<String, String>> databaseTableColumnsMapping = new HashMap<String, HashMap<String, String>>();

	public void ensureDatabaseTableColumnsMappingDeleted(String database, String table) {
		String key = StringUtils.union(database, table);
		this.databaseTableColumnsMapping.remove(key);
	}

	public void ensureDatabaseTableColumnsMappingExist(String database, String table, boolean forceUpdate) {
		// 因为比较耗时，所幸,并不是经常改数据表结构
		// 如果之前拉过一次，大部分情况后面不需要再重新拉取
		String key = StringUtils.union(database, table);
		if (false == forceUpdate) {// 不强制更新，有就行了
			if (null != databaseTableColumnsMapping.get(key)) {
				// 已经有了，不用做其它操作
				LoggerUtils.debug(logger, "mappings已经存在，不用更新");
			} else {
				HashMap<String, String> mappings = SqlUtils.getDatabaseTableColumnsMapping(ip, port, username, password,
						database, table);
				LoggerUtils.debug(logger, "非强制更新" + mappings);
				databaseTableColumnsMapping.put(key, mappings);
			}
		} else {
			// 强制更新,拉取强制更新,不管有没有，一律强制更新
			// 比如修改了表结构[这种情况也不多,没事修改表结构干嘛。。。:)]
			HashMap<String, String> mappings = SqlUtils.getDatabaseTableColumnsMapping(ip, port, username, password,
					database, table);
			LoggerUtils.debug(logger, "强制更新" + mappings);
			databaseTableColumnsMapping.put(key, mappings);
		}
	}

	public HashMap<String, String> getColumnsMapping(String database, String table) {
		return databaseTableColumnsMapping.get(StringUtils.union(database, table));
	}

}
