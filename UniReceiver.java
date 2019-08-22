package com.gbcom.ccsv3.transport.multidiscover;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 单播发现者
 * 
 * <p>
 * 
 * @author syz
 *         <p>
 * @date 2015-6-26,下午04:25:33
 *       <p>
 * @version v1.0.0
 *          <p>
 * @see UniReceiver
 */
public class UniReceiver implements Receiver {
	private static final Logger LOGGER = Logger.getLogger(UniReceiver.class);

	/**
	 * 多播ip
	 */
	public static final String MULTI_GROUP_IP = "224.0.0.2";
	/**
	 * 多播端口
	 */
	public static final int MULTI_GROUP_PORT = 1107;
	private DatagramSocket dgsocket = null;
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
		@SuppressWarnings("unused")
		InetAddress group = InetAddress.getByName(MULTI_GROUP_IP);// 组播地址
		try {
			dgsocket = new DatagramSocket(MULTI_GROUP_PORT);
			byte[] buffer = new byte[50];
			LOGGER
					.info("Thread=" + Thread.currentThread()
							+ " ; UniReceiver started收数据包启动！(启动时间: "
							+ new Date() + ")");
			while (true) {
				// 建立一个指定缓冲区大小的数据包
				DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
				dgsocket.receive(dp);
				DpDispatcher.getInstance().addDp(dp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("MultiDiscover --- start -- error", e);
		} finally {
			if (dgsocket != null) {
				try {
					dgsocket.close();
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
		if (dgsocket != null) {
			try {
				dgsocket.close();
			} catch (Exception e) {
				LOGGER.error("MultiDiscover --- start finall -- error", e);
			}
		}
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
