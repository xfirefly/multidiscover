package com.gbcom.ccsv3.transport.multidiscover;

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
 * @see com.gbcom.ccsv3.transport.multidiscover.Receiver
 */
public interface Receiver {

	/**
	 * 开始
	 * 
	 * @throws UnknownHostException
	 *             Exception
	 */
	public void start() throws UnknownHostException;

	/**
	 * 停止
	 */
	public void stop();

	/**
	 * 是否开启
	 * 
	 * @return Boolean
	 */
	public boolean isStarted();

}
