package com.gbcom.ccsv3.transport.multidiscover;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

/**
 * 多播发送者,单利 和 静态类 * 组播 通信端口 1107 单播 通信端口1108
 * 
 * <p>
 * 
 * @author syz
 *         <p>
 * @date 2015-6-26,下午04:25:33
 *       <p>
 * @version v1.0.0
 *          <p>
 * @see MultiSender
 */
public final class MultiSender implements Sender {
	private static final Logger LOGGER = Logger.getLogger(MultiSender.class);

	/**
	 * 多播ip
	 */
	public static final String MULTI_GROUP_IP = "224.7.11.3";
	/**
	 *多播端口
	 */
	public static final int MULTI_GROUP_PORT = 1108;
	private static final MultiSender INSTANCE = new MultiSender();

	/**
	 * 单例模式，获取单例
	 * 
	 * @return MultiSender
	 */
	public static MultiSender getInstance() {
		return INSTANCE;
	}

	private MultiSender() {

	}

	/**
	 * @param msg
	 *            String
	 * @param ip
	 *            InetAddress
	 * @param port
	 *            int
	 * @throws UnknownHostException
	 *             Exception
	 */
	@Override
	public void send(String msg, InetAddress ip, int port)
			throws UnknownHostException {

		InetAddress group = InetAddress.getByName(MULTI_GROUP_IP);// 组播地址
		MulticastSocket mss = null;
		try {
			// mss = new MulticastSocket(MULTI_GROUP_PORT);
			mss = new MulticastSocket(); // 随机端口 client
			mss.joinGroup(group);

			byte[] buffer = msg.getBytes();
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length,
					group, MULTI_GROUP_PORT);
			mss.send(dp);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("MultiSender -- send", e);
		} finally {
			try {
				if (mss != null) {
					mss.leaveGroup(group);
					mss.close();
				}
			} catch (Exception e) {
				LOGGER.error("MultiSender -- send -- final", e);
			}
		}

	}

}
