package com.gbcom.ccsv3.transport.multidiscover;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 多播发现者
 * 
 * * 组播 通信端口 1107 单播 通信端口1108
 * 
 * <p>
 * 
 * @author syz
 *         <p>
 * @date 2015-6-26,下午04:25:33
 *       <p>
 * @version v1.0.0
 *          <p>
 * @see com.gbcom.ccsv3.transport.multidiscover.MultiReceiver
 */
public class MultiReceiver implements Receiver {
	private static final Logger LOGGER = Logger.getLogger(MultiReceiver.class);

	/**
	 * 多播ip
	 */
	public static final String MULTI_GROUP_IP = "224.7.11.3";
	/**
	 * 多播端口
	 */
	public static final int MULTI_GROUP_PORT = 1107;
	private MulticastSocket msr = null;
	private InetAddress group = null;

	private boolean started = false;

	/**
	 * 开始
	 * 
	 * @throws UnknownHostException
	 *             Exception
	 */
	@Override
	public void start() throws UnknownHostException {
		// 创建多播socket
		// 接收报文
		this.group = InetAddress.getByName(MULTI_GROUP_IP);// 组播地址

		try {
			msr = new MulticastSocket(MULTI_GROUP_PORT); // server bind port
			//java.net.SocketException: No such device
//	        at java.net.PlainDatagramSocketImpl.join(Native Method)
//	        at java.net.PlainDatagramSocketImpl.join(PlainDatagramSocketImpl.java:181)
//	        at java.net.MulticastSocket.joinGroup(MulticastSocket.java:277)
//	        at com.gbcom.ccsv3.transport.multidiscover.MultiReceiver.start(MultiReceiver.java:56)
//	        at com.gbcom.ccsv3.transport.multidiscover.DiscoverServer$1.run(DiscoverServer.java:50)
//	        at java.lang.Thread.run(Thread.java:662)
			msr.joinGroup(group);// 加入连接
			byte[] buffer = new byte[50];
			LOGGER.info("Thread=" + Thread.currentThread()
					+ " ; MultiReceiver started!!! (启动时间: " + new Date() + ")");
			started = true;
			while (true) {
				try {
					// 建立一个指定缓冲区大小的数据包
					DatagramPacket dp = new DatagramPacket(buffer,
							buffer.length);
					msr.receive(dp);
					DpDispatcher.getInstance().addDp(dp);
				} catch (Exception e) {
					LOGGER.error("receiver is error , continue", e);
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("MultiDiscover --- start -- error", e);
		} finally {
			if (msr != null) {
				try {
					msr.leaveGroup(group);
					msr.close();
				} catch (Exception e) {
					LOGGER.error("MultiDiscover --- start finall -- error", e);
				}
			}
		}

	}

	/**
	 * 停止
	 */
	@Override
	public void stop() {
		if (msr != null) {
			try {
				msr.leaveGroup(group);
				msr.close();
			} catch (Exception e) {
				LOGGER.error("MultiDiscover --- start finall -- error", e);
			}
		}
		started = false;

	}

	/**
	 * 是否开启
	 * 
	 * @return started
	 */
	@Override
	public boolean isStarted() {
		return started;
	}

}
