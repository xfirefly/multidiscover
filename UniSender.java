package com.gbcom.ccsv3.transport.multidiscover;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

/**
 * 多播发送者,单利 和 静态类一样。
 * 
 * 组播 通信端口 1107 单播 通信端口1108
 * 
 * <p>
 * 
 * @author syz
 *         <p>
 * @date 2015-6-26,下午04:25:33
 *       <p>
 * @version v1.0.0
 *          <p>
 * @see UniSender
 */
public final class UniSender implements Sender {
	private static final Logger LOGGER = Logger.getLogger(UniSender.class);

	/**
	 * 多播ip
	 */
	public static final String MULTI_GROUP_IP = "224.0.0.2";
	/**
	 * 多播端口
	 */
	public static final int MULTI_GROUP_PORT = 1107;
	private static final UniSender INSTANCE = new UniSender();

	/**
	 * 单例模式，获取单例
	 * 
	 * @return UniSender
	 */
	public static UniSender getInstance() {
		return INSTANCE;
	}

	private UniSender() {

	}

	/**
	 * 发送消息。 添加ip 和port 兼容单播处理，作为单播的 端口和地址，
	 * 
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

		DatagramSocket dgSocket = null;
		try {
			dgSocket = new DatagramSocket(); // client 随机端口。

			byte[] buffer = msg.getBytes();
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length, ip,
					port);
			dgSocket.send(dp);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("UniSender -- send", e);
		} finally {
			try {
				if (dgSocket != null) {
					dgSocket.close();
				}
			} catch (Exception e) {
				LOGGER.error("UniSender -- send -- final", e);
			}
		}

	}

}
