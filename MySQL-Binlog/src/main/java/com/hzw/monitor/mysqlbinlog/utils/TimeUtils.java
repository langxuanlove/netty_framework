package com.hzw.monitor.mysqlbinlog.utils;
/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
public class TimeUtils {
	public static void sleepSeconds(long t) {// 睡眠相关秒
		sleepMilliSeconds(t * 1000);
	}

	@SuppressWarnings("static-access")
	public static void sleepMilliSeconds(long t) {// 睡眠相关毫秒
		try {
			Thread.currentThread().sleep(t);
		} catch (Exception e) {
		}
	}

}
