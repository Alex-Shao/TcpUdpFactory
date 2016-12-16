package com.moonearly.utils.service;

import android.text.TextUtils;
import android.util.Log;

import com.alfred.customer.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 16/12/16.
 * TCP,UDP 工程类:
 * 功能:
 * 1、启动UDP服务器 { startUdpServer(int index) } index 为主机编号 影响端口,对应停止{ stopUdpServer() };
 * 2、获取主机的ip{getServiceIp(int index, UdpSendCallBack udpSendCallBack)} 首先需要实现功能1;
 * 3、启动TCP服务器 { startTcpServer(JSONCallBack MJsonCallBack) } 对应停止{  stopTcpServer() }
 * 4、对TCP服务器发送数据 { tcpSend(String strMsg), tcpSend(String ip,byte []data)} 若需要取得返回数据 请启动 TCP服务器
 */

public class TcpUdpFactory {
	private static ServerSocket mSocket;
	private static DatagramSocket mDSocket;
	public static final int TCP_SERVER_PORT_DEFAULT = 8800;
	public static final int UDP_SERVER_PORT_DEFAULT = 9900;

	private static int localUdpPort = UDP_SERVER_PORT_DEFAULT;
	private static int localTcpPort = TCP_SERVER_PORT_DEFAULT;

	private static final String UDP_ACK_IP ="Please give me your IP address";
	private static final String UDP_REQUEST_IP ="My IP address is ...";

	public  static String S_ENCODING   = "UTF-8";
	public  static final int CONNECT_TIMEOUT   = 4000;
	public  static final String TAG   = TcpUdpFactory.class.getSimpleName();
	
	public static TcpDaemonRunnable sTcpDaemonTask=null;
	public static UdpDaemonRunnable sUdpDaemonTask=null;

	private static JSONCallBack jsonCallBack;
	private static Map<Integer,String> ipMap = new HashMap<Integer, String>();
	/**
	 * 启动tcp服务器
	 * @param index 主机id
	 * @param MJsonCallBack 服务器接受数据回调
     */
	public static void startTcpServer(int index, JSONCallBack MJsonCallBack) {
		jsonCallBack = MJsonCallBack;
		localTcpPort = TCP_SERVER_PORT_DEFAULT + index;
		try {
			mSocket = new ServerSocket(localTcpPort);
		} catch (IOException e) {
			LogUtil.e(TAG,e.getMessage().toString());
			return;
		}            
		sTcpDaemonTask = new TcpDaemonRunnable();
		Thread recThread = new Thread(sTcpDaemonTask);
        recThread.setName("TCP_server");
        recThread.setPriority(Thread.MAX_PRIORITY-2);
        recThread.start();	
	}


	public static void stopTcpServer()
	{
		if(sTcpDaemonTask != null)
		{
			sTcpDaemonTask.stop();
			sTcpDaemonTask = null;
			jsonCallBack = null;
            try {
				mSocket.close();
			} catch (IOException e) {
				LogUtil.w(TAG, e.getMessage().toString());
			}
		}
	}
	

	/**
	 *启动udp服务器
	 * @param index  主机编号
     */
	public static void startUdpServer(int index) {
		localUdpPort = UDP_SERVER_PORT_DEFAULT + index;
		try {
			if (mDSocket == null) {
				mDSocket = new DatagramSocket(null);
				mDSocket.setReuseAddress(true);
				mDSocket.setBroadcast(true);
				mDSocket.bind(new InetSocketAddress(localUdpPort));
			}
		} catch (IOException e) {
			LogUtil.e(TAG, "new DatagramSocket FAIL"+e.getMessage().toString());
			return;
		}     
		sUdpDaemonTask = new UdpDaemonRunnable();
		Thread udprecThread = new Thread(sUdpDaemonTask);
		udprecThread.setName("UDP_server");
        udprecThread.setPriority(Thread.MAX_PRIORITY-2);
		udprecThread.start();
	}


	public static void stopUdpServer()
	{
		if(sUdpDaemonTask != null)
		{
			sUdpDaemonTask.stop();
			sUdpDaemonTask = null;
            mDSocket.close();
		}
	}

	private static class TcpDaemonRunnable implements Runnable {
		private boolean done=false;
		private final int MAX_BUFFER_LEN = 1024;
		public void stop()
		{
			done=true;
		}
        public void run() {
        	byte  []readbuff = new byte[MAX_BUFFER_LEN];
        	byte  []buffer = new byte[MAX_BUFFER_LEN];
        	int   data_total_len = 0;
        	
            while (!done) {
            	Socket s;
            	
				try {
					s = mSocket.accept();
					InputStream in = s.getInputStream();
					int readlen = in.read(readbuff);						
            		if(readlen < 1)
            		{
            			try {
							Thread.sleep(200);//等待数据传输完成
						} catch (InterruptedException e) {
						}
            			readlen = in.read(readbuff);
            			if(readlen < 1)
                		{
            				continue;
                		}
						LogUtil.i(TAG, String.format("1延时后读取长度=%d", readlen));
            		}
            		String fromip = s.getInetAddress().getHostAddress();
            		String utf8code = new String(readbuff,0,readlen,S_ENCODING);
					if(jsonCallBack != null){
						jsonCallBack.call(utf8code);
					}
				} catch (IOException e1) {
					if(!done) {
						LogUtil.w(TAG, "TcpDaemonTask 异常"+e1.getMessage().toString());
						try {
							mSocket.close();
						} catch (IOException e0) {
							LogUtil.w(TAG, "mSocket.close 异常"+e0.getMessage().toString());
						}
						try {
							mSocket = new ServerSocket(localTcpPort);
						} catch (IOException e2) {
							LogUtil.e(TAG, "new ServerSocket 异常"+e2.getMessage().toString());
						}
					}
				}  
            }
        }
	}
	
	private static String mOldServerIp = "";

	private static class UdpDaemonRunnable implements Runnable {
		private boolean done = false;

		public void stop() {
			done = true;
		}

		public void run() {
			byte data[] = new byte[8192];
			int reConnect = 1;
			//创建一个空的DatagramPacket对象
			DatagramPacket packet = new DatagramPacket(data, data.length);
			while (!done) {
				try {
					//使用receive方法接收客户端所发送的数据
					mDSocket.receive(packet);
					if (packet.getLength() < 1) {
						continue;
					}
					String fromip = packet.getAddress().getHostAddress();
					String msg = new String(packet.getData(),0,packet.getLength());
					int index = packet.getPort() - UDP_SERVER_PORT_DEFAULT;
					if(msg.contains(UDP_ACK_IP)) {
						ipMap.put(index, fromip);
						//终端请求我的IP地址
						Log.d(TAG, String.format("主机编号:%d,ip:%s,请求我的Ip,正在回复...", index, fromip));
						udpSend(packet.getAddress().getHostAddress(), packet.getPort(), UDP_REQUEST_IP.getBytes(), null);
					}else if(msg.contains(UDP_REQUEST_IP)){
						ipMap.put(index, fromip);
						Log.d(TAG, String.format("主机编号:%d,告诉我他的ip为:%s,正在回复...", index, fromip));
					}
				} catch (IOException e1) {
					if (!done) {
						LogUtil.w(TAG, "发生异常" + e1.getMessage().toString()+",正在尝试重新启动第"+reConnect);
						if (reConnect++ < 10) {
							try {
								mDSocket.close();
								mDSocket = new DatagramSocket(localUdpPort);
								mDSocket.setBroadcast(true);
							} catch (IOException e2) {
								LogUtil.e(TAG, "重新启动发生异常" + e2.getMessage().toString());
							}
						}
					}
				}
			}
		}
	}
	private static void udpSend(final String ip,final int port,final byte[]data, final UdpSendCallBack udpSendCallBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//创建一个InetAddree
					InetAddress serverAddress = InetAddress.getByName(ip);
					//创建一个DatagramPacket对象，并指定要讲这个数据包发送到网络当中的哪个地址，以及端口号
					DatagramPacket packet = new DatagramPacket(data,data.length,serverAddress,port);
					mDSocket.send(packet);
					if(udpSendCallBack != null)
						udpSendCallBack.call(true);
				}catch (Exception e) {
					LogUtil.w(TAG, "udpSend 异常"+e.getMessage().toString());
					udpSendCallBack.call(false);
				}
			}
		}).start();

	}


	public static void tcpSend(final int index, final byte []data, final TcpSendCallBack tcpSendCallBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String ip = ipMap.get(index);
				if(TextUtils.isEmpty(ip)){
					Log.d(TAG, "不存在当前主机,请先获取主机ip");
					return;
				}
				try {
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(ip,TCP_SERVER_PORT_DEFAULT + index), CONNECT_TIMEOUT);

					OutputStream out = socket.getOutputStream();
					out.write(data);
					out.flush();
					socket.close();
					if(tcpSendCallBack != null){
						tcpSendCallBack.call(true);
					}
				} catch (IOException e) {
					LogUtil.w(TAG, "tcpSend fail"+e.getMessage().toString());
					if(tcpSendCallBack != null){
						tcpSendCallBack.call(false);
					}
				}
			}
		}).start();
	}


	private static void tcpSend(final int index, final String strMsg, final TcpSendCallBack tcpSendCallBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String ip = ipMap.get(index);
					if(TextUtils.isEmpty(ip)){
						Log.d(TAG, "不存在当前主机,请先获取主机ip");
						return;
					}
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(ip,TCP_SERVER_PORT_DEFAULT + index), CONNECT_TIMEOUT);
					OutputStream out = socket.getOutputStream();
					out.write(strMsg.getBytes(S_ENCODING));
					out.flush();
					socket.close();
					if(tcpSendCallBack != null){
						tcpSendCallBack.call(true);
					}
				} catch (IOException e) {
					if(tcpSendCallBack != null){
						tcpSendCallBack.call(false);
					}
				}
			}
		}).start();
	}

	public static void getServiceIp(int index, UdpSendCallBack udpSendCallBack){
		udpSend("255.255.255.255", UDP_SERVER_PORT_DEFAULT + index, UDP_ACK_IP.getBytes(), udpSendCallBack);
	}

}
