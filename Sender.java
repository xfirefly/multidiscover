package com.gbcom.ccsv3.transport.multidiscover;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * UDP 发现
 * 
 * <p>
 * 
 * @author syz
 *         <p>
 * @date 2015-6-26,下午04:39:06
 *       <p>
 * @version v1.0.0
 *          <p>
 * @see Sender
 */
public interface Sender {

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
	public void send(String msg, InetAddress ip, int port)
			throws UnknownHostException;

}
