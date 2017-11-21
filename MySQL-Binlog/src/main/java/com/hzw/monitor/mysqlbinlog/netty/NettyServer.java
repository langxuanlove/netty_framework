package com.hzw.monitor.mysqlbinlog.netty;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hzw.monitor.mysqlbinlog.handlers.ChildChannelHandler;
import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;
import com.hzw.monitor.mysqlbinlog.utils.MyProperties;
import com.hzw.monitor.mysqlbinlog.utils.TimeUtils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class NettyServer {
	private static final Logger logger = LogManager.getLogger(NettyServer.class);
	private static int CPU = Runtime.getRuntime().availableProcessors();
	public static AtomicInteger startUp = new AtomicInteger(0);// 0:初始值 1:成功
																// -1:失败

	public synchronized static void start() {

		if (startUp.get() == 1) {// 之前启动成功了
			return;
		}
		// logger.info("尝试启动Netty线程");
		new Thread(new Runnable() {
			public void run() {

				MyProperties p = MyProperties.getInstance();
				int port = p.getNetty_port();
				int boss = p.getNetty_boss();
				int worker = p.getNetty_worker();
				LoggerUtils.info(logger, "Netty -cpu:" + CPU + " port:" + port + " boss:" + boss + " worker:" + worker);
				EventLoopGroup bossGroup = new NioEventLoopGroup(boss);
				EventLoopGroup workerGroup = new NioEventLoopGroup(worker);
				try {
					ServerBootstrap b = new ServerBootstrap();
					b.group(bossGroup, workerGroup).channel(MyNioServerSocketChannel.class)
							.option(ChannelOption.SO_BACKLOG, 2048).childHandler(new ChildChannelHandler());
					// 绑定端口，同步等待成功
					ChannelFuture f = b.bind(port).sync();
					// 等待服务端监听端口关闭
					LoggerUtils.info(logger, "netty server start ok.");
					startUp.set(1);
					f.channel().closeFuture().sync();
				} catch (Exception e) {
					LoggerUtils.error(logger, e.toString());
					startUp.set(-1);
				} finally {// 优雅退出，释放线程资源
					bossGroup.shutdownGracefully();
					workerGroup.shutdownGracefully();
					LoggerUtils.info(logger, "Netty Server exit...");
				}
			}
		}).start();
		// 等待线程结果
		while (0 == NettyServer.startUp.get()) {// 等待结果
			TimeUtils.sleepMilliSeconds(10);// 睡眠10毫秒
		}
		if (-1 == NettyServer.startUp.get()) {
			// netty启动失败
			System.exit(0);
		} else {
			LoggerUtils.debug(logger, "netty start ok...");
		}
	}
}
