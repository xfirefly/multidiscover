package com.gbcom.ccsv3.transport.multidiscover;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import com.gbcom.omc.si.common.Const;

/**
 * 转发器，接收dp
 * 
 * <p>
 * 
 * @author syz
 *         <p>
 * @date 2015-6-26,下午04:29:42
 *       <p>
 * @version v1.0.0
 *          <p>
 * @see com.gbcom.ccsv3.transport.multidiscover.DpDispatcher
 */
public class DpDispatcher {
	private static final Logger LOG = Logger.getLogger(DpDispatcher.class);
	private static final int THREAD_NUM = 1;
	private static final int BLOCK_QUEUE_MAX_SIZE = 10000;
	private static final int BLOCK_QUEUE_CLEAR_SIZE = 10000;

	/**
	 * 线程的执行器
	 */
	private ExecutorService executor = null;

	private boolean isRunning = false;
	/**
	 * 上报Trap消息的队列 :SIZE
	 */
	private BlockingQueue<DatagramPacket> dpQueue = new LinkedBlockingQueue<DatagramPacket>(
			BLOCK_QUEUE_MAX_SIZE);

	private static class DpDispatcherHolder {
		private static final DpDispatcher INSTANCE = new DpDispatcher();
	}

	/**
	 * 获取单例对象
	 * 
	 * @return TaskDispatcher
	 */
	public static DpDispatcher getInstance() {
		return DpDispatcherHolder.INSTANCE;
	}

	private DpDispatcher() {
		init();
		start();
	}

	private void init() {
		isRunning = false;
	}

	/**
	 * 添加数据包
	 * 
	 * @param dp
	 *            DatagramPacket
	 */
	public void addDp(DatagramPacket dp) {

		if (!isRunning) {
			LOG
					.error("UdpDispatcher  is not running, the Task below may not process");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("add DatagramPacket to Queue，， Address="
					+ dp.getAddress());
		}
		try {
			if (dpQueue.size() >= BLOCK_QUEUE_CLEAR_SIZE) {
				LOG
						.info(" *****cleart request Task***** trap queue size is more than "
								+ BLOCK_QUEUE_CLEAR_SIZE
								+ ";;  CLEAR BlockingQueue");
				dpQueue.clear();
			}
			dpQueue.put(dp);
		} catch (InterruptedException e) {
			LOG.info("/******* add dp InterruptedException*********/");
			LOG.error("add dp to queue interrupted", e);
			LOG.info("/******* add dp InterruptedException  *********/");
		} catch (Exception e) {
			LOG.error("Other Exception  ", e);
		}

	}

	/**
	 * 停止
	 */
	public void stop() {
		executor.shutdownNow();
		isRunning = false;
	}

	/**
	 * 开始
	 */
	public void start() {

		executor = Executors.newCachedThreadPool();
		for (int i = 0; i < THREAD_NUM; i++) {
			executor.execute(new DispatcherTask());
		}
		isRunning = true;
		LOG.info("do Dispatcher task start  , current thread size =  "
				+ THREAD_NUM);

	}

	class DispatcherTask implements Runnable {

		/**
		 * 线程执行方法
		 */
		@Override
		public void run() {

			DatagramPacket dp = null;
			while (!Thread.currentThread().isInterrupted()) {
				try {
					long begin = System.currentTimeMillis();
					dp = dpQueue.take();

					String s = new String(dp.getData(), 0, dp.getLength());
					LOG.info("discover receiver dp , msg=" + s
							+ ",dpQueue size=" + dpQueue.size());
					if (s.equalsIgnoreCase("who")) {
						/*
						 * TransportMapping mapping
						 * =SnmpSingleton.getTransportMapping(); if(mapping
						 * instanceof DefaultUdpTransportMapping){ String ip =
						 * ((DefaultUdpTransportMapping)mapping).getAddress().
						 * getInetAddress().toString();
						 * SenderFactory.getMultiSender().send(ip); }
						 */

						String ip = "NULL";
						int port = 162;
						if (Const.sourceSnmpIp == null
								|| Const.sourceSnmpIp.trim().equals("")) {
							ip = InetAddress.getLocalHost().getHostAddress()
									.toString();
						} else {
							String[] udpSrc = (Const.sourceSnmpIp.trim())
									.split("/");
							if (udpSrc.length < 1 || udpSrc.length > 2) {
								ip = InetAddress.getLocalHost()
										.getHostAddress().toString();
							} else {
								ip = udpSrc[0];
								port = (udpSrc.length == 2) ? Integer
										.parseInt(udpSrc[1]) : 162;
							}
						}
						String msg = "IP:" + ip + "," + "PORT:" + port;
						// InetAddress addr =
						// InetAddress.getByName(MultiSender.MULTI_GROUP_IP);
						// SenderFactory.getMultiSender().send(msg,MultiSender.MULTI_GROUP_IP,MultiSender.MULTI_GROUP_PORT);

						SenderFactory.getUniSender().send(msg, dp.getAddress(),
								dp.getPort());
					} else {
						// LOG.error("OTHER INFOR---"+s);
					}

					if (LOG.isDebugEnabled()) {

						LOG.info("process Task  success, thread="
								+ Thread.currentThread().getName()
								+ "  ;spend time ：total= "
								+ ((System.currentTimeMillis() - begin) / 1000)
								+ "s  || the queue size is not actually:"
								+ dpQueue.size());
					}
				} catch (InterruptedException e) {
					LOG
							.info("/******* DP Dispatcher  InterruptedException*********/");
					LOG.error("DP Dispatcher thread interrupted ;; tread = "
							+ Thread.currentThread().getName(), e);
					LOG
							.info("/******* DP Dispatcher  InterruptedException*********/");
					Thread.currentThread().interrupt();
					break;
				} catch (Exception e) {
					LOG.error("DP Dispatcher thread exception", e);
					continue;
				}
			}

		}
	}

	public static class SenderFactory {
		/**
		 * 获取多播发送者
		 * 
		 * @return Sender
		 */
		public static Sender getMultiSender() {
			return MultiSender.getInstance();
		}

		/**
		 * 获取单播发送者
		 * 
		 * @return UniSender
		 */
		public static Sender getUniSender() {
			return UniSender.getInstance();
		}

	}

}
