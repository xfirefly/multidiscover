package com.gbcom.ccsv3.transport.multidiscover;

import org.apache.log4j.Logger;

/**
 * 发现服务器
 * 
 * <p>
 * 
 * @author syz
 *         <p>
 * @date 2015-6-26,下午04:23:36
 *       <p>
 * @version v1.0.0
 *          <p>
 * @see com.gbcom.ccsv3.transport.multidiscover.DiscoverServer
 */
public class DiscoverServer {
	private static final Logger LOGGER = Logger.getLogger(DiscoverServer.class);
	
	private static class DiscoverServerHolder{
		private static final DiscoverServer INSTANCE = new DiscoverServer();
	}
	/**
	 * 获取单例
	 * 
	 * @return DiscoverServer
	 */
	public static DiscoverServer getInstance() {
		return DiscoverServerHolder.INSTANCE;
	}

	private boolean started = false;
	private Receiver discover;

	private DiscoverServer() {
		discover = UdpDiscoverFactory.getMultiUdpDiscover();
	}

	/**
	 * 开
	 */
	public void on() {
		started = true;

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					discover.start();
				} catch (Exception e) {
					e.printStackTrace();
					discover = null;
					LOGGER.error("start discover server unknoe host", e);
					started = false;

					// maybe throws new exception.
				}
			}

		});
		t.start();
		LOGGER.info("start discover server  for device success!!!!");
	}

	/**
	 * 关
	 */
	public void off() {
		if (discover != null) {
			discover.stop();
		}
		started = false;
	}

	/**
	 * 是否开启
	 * 
	 * @return started
	 */
	public boolean isStarted() {
		return started;
	}

	static class UdpDiscoverFactory {
		/**
		 * 获取多播发现者
		 * 
		 * @return Receiver
		 */
		public static Receiver getMultiUdpDiscover() {
			return new MultiReceiver();
		}

		/**
		 * 获取单播发现者
		 * 
		 * @return Receiver
		 */
		public static Receiver getUdpDiscover() {
			return new UniReceiver();
		}
	}

}
