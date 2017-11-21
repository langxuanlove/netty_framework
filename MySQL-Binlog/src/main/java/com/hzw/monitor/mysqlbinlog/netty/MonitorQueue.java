package com.hzw.monitor.mysqlbinlog.netty;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;
import com.hzw.monitor.mysqlbinlog.utils.MyProperties;
import com.hzw.monitor.mysqlbinlog.utils.TimeUtils;

/**
 * 无业务逻辑的入hdfs
 * 
 * @author zhiqiang.liu 2015年11月13日
 *
 */
public class MonitorQueue {
	// poll: 若队列为空，返回null。
	// remove:若队列为空，抛出NoSuchElementException异常。
	// take:若队列为空，发生阻塞，等待有元素。

	// put---无空间会等待
	// add--- 满时,立即返回,会抛出异常
	// offer---满时,立即返回,不抛异常
	// private static final Logger logger =
	// LoggerFactory.getLogger(MonitorQueue.class);
	private static final Logger logger = LogManager.getLogger(MonitorQueue.class);
	public static BlockingQueue<SocketChannel> objectQueue = new LinkedBlockingQueue<SocketChannel>();

	public static void addObject(SocketChannel obj) {
		objectQueue.offer(obj);
		new Thread(new Runnable() {// 启动一个线程去触发...
			public void run() {
				// TODO Auto-generated method stub
				MyProperties p = MyProperties.getInstance();
				if (null != p) {// 连接本机的netty服务器
					String ip = "127.0.0.1";
					int port = p.getNetty_port();
					if (null != ip && port >= 0) {
						Socket socket = null;
						try {
							socket = new Socket();
							System.out.println("ip: " + ip + " port:" + port);
							socket.connect(new InetSocketAddress(ip, port), 6 * 1000);// 还是给一个连接超时6秒
							TimeUtils.sleepSeconds(6);// 睡眠6秒再退出,足够的时间给netty处理连接了
						} catch (Exception e) {
							LoggerUtils.error(logger, e.toString());
						} finally {
							if (null != socket) {// 主动关闭连接
								try {
									socket.close();
								} catch (IOException e) {
								}
							}
						} // try...catch...finally结束
					} // if结束
				}
			}
		}).start();

	};

	public static SocketChannel getObject() {
		return objectQueue.poll();
	}

}