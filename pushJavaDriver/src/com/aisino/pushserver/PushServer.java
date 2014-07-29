/*
 * Push Server 
 * push notification to mobile phone use baidu push and apple APNS
 * 2013 Aisino 
 * 
 * ver 1.0.1
 * 2014-01-22
 * add parameter to make sound for iOS
 * 
 * */

package com.aisino.pushserver;
import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;
import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushBroadcastMessageRequest;
import com.baidu.yun.channel.model.PushBroadcastMessageResponse;
import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;

import com.baidu.yun.channel.model.PushTagMessageRequest;
import com.baidu.yun.channel.model.PushTagMessageResponse;

import com.baidu.yun.channel.model.PushUnicastMessageRequest;
import com.baidu.yun.channel.model.PushUnicastMessageResponse;

import com.baidu.yun.channel.model.QueryBindListRequest;
import com.baidu.yun.channel.model.QueryBindListResponse;
import com.baidu.yun.channel.model.BindInfo;
import java.util.List;

public class PushServer {
	private static String apiKey;
	private static String secretKey;
	private static String sound=",\"aps\":{\"sound\":\"default\"}";
	
	public enum MsgType{BroadcastMessage,BroadcastNotice,TagMessage,TagNotice,SingleMessage,SingleNotice}
	//enum DeviceType{Android,iOS};
	public enum DeployStatus {Developer,Production};

//初始化Key参数,其中apiKey, secretKey是在推送开发者网站中创建新应用时获取到的。
	public void setKey(String _apiKey,String _secretKey){
		setApiKey(_apiKey);
		setSecretKey(_secretKey);
	}
	
	public static String getApiKey() {
		return apiKey;
	}
	
	public void setApiKey(String apiKey) {
		PushServer.apiKey = apiKey;
	}
	
	public static String getSecretKey() {
		return secretKey;
	}
	
	public void setSecretKey(String secretKey) {
		PushServer.secretKey = secretKey;
	}
	
//Android平台接口--------------------------------------------------------

//推送广播消息
/*
**输入：**

MsgType type ：指定推送消息类型，枚举类型
> 消息：MsgType.BroadcastMessage
> 
> 通知：MsgType.BroadcastNotice 

String msg ：推送内容，字符串类型

String title ：推送标题（可选参数），字符串类型，只在发送`通知`时需指定

**输出：**

boolean 布尔类型，推送成功返回true，失败返回false
*/	
	public boolean PushBroadcastMsg(MsgType type ,String msg,String...title){
		
		if(MsgType.BroadcastMessage!=type && MsgType.BroadcastNotice!=type){
			System.out.println("Input MsgType Error! ");
			return false;
		}
		//String SendTitle = "";
	    //for (String str : title)
	    //	SendTitle =SendTitle+str;
	    
	    
		ChannelKeyPair pair = new ChannelKeyPair(getApiKey(), getSecretKey());
		// 2. 创建BaiduChannelClient对象实例
		BaiduChannelClient channelClient = new BaiduChannelClient(pair);
		
		// 3. 若要了解交互细节，请注册YunLogHandler类
		channelClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) {
				System.out.println(event.getMessage());
			}
		});
		//android -----------------------------------------------------
		try {
			
			// 4. 创建请求类对象
			PushBroadcastMessageRequest request = new PushBroadcastMessageRequest();
			request.setDeviceType(3);	// device_type => 1: web 2: pc 3:android 4:ios 5:wp	
			
			//消息
			if(MsgType.BroadcastMessage==type)
				request.setMessage(msg);
			//通知
			else
			{
				request.setMessageType(1);
				request.setMessage("{\"title\":\""+title[0]+"\",\"description\":\""+msg+"\"}");				
			}

 			
			// 5. 调用pushMessage接口
			PushBroadcastMessageResponse response = channelClient.pushBroadcastMessage(request);
				
			// 6. 认证推送成功
			System.out.println("push Android amount : " + response.getSuccessAmount()); 
			
		} catch (ChannelClientException e) {
			// 处理客户端错误异常
			e.printStackTrace();
			return false;
		} catch (ChannelServerException e) {
			// 处理服务端错误异常
			System.out.println(
					String.format("request_id: %d, error_code: %d, error_message: %s" , 
						e.getRequestId(), e.getErrorCode(), e.getErrorMsg()
						)
					);
			return false;
		}
		

		
		return true;
		
	}
	
	
//推送标签消息
/*
**输入：**

MsgType type： 指定推送消息类型，枚举类型
> 消息：MsgType.TagMessage
> 
> 通知：MsgType.TagNotice 

String msg ：推送内容，字符串类型

String tag ：标签消息，字符串类型，可用于分组

String title： 推送标题（可选参数），字符串类型，只在发送`通知`时需指定

**输出：**

boolean 布尔类型，推送成功返回true，失败返回false
*/	
	public boolean PushTagMsg(MsgType type ,String tag,String msg,String...title){
		
		if(MsgType.TagMessage!=type && MsgType.TagNotice!=type){
			System.out.println("Input MsgType Error! ");
			return false;
		}
		
		ChannelKeyPair pair = new ChannelKeyPair(getApiKey(), getSecretKey());
		// 2. 创建BaiduChannelClient对象实例
		BaiduChannelClient channelClient = new BaiduChannelClient(pair);
		
		// 3. 若要了解交互细节，请注册YunLogHandler类
		channelClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) {
				System.out.println(event.getMessage());
			}
		});

		try {
			
			// 4. 创建请求类对象
			PushTagMessageRequest request = new PushTagMessageRequest();
			request.setDeviceType(3);	// device_type => 1: web 2: pc 3:android 4:ios 5:wp	
			
			request.setTagName(tag);
			
			//消息
			if(MsgType.TagMessage==type)
				request.setMessage(msg);
			//通知
			else{
				request.setMessageType(1);
				request.setMessage("{\"title\":\""+title[0]+"\",\"description\":\""+msg+"\"}");				
			}

			// 5. 调用pushMessage接口
			PushTagMessageResponse response = channelClient.pushTagMessage(request);
				
			// 6. 认证推送成功
			System.out.println("push Android amount : " + response.getSuccessAmount()); 
			
		} catch (ChannelClientException e) {
			// 处理客户端错误异常
			e.printStackTrace();
			return false;
		} catch (ChannelServerException e) {
			// 处理服务端错误异常
			System.out.println(
					String.format("request_id: %d, error_code: %d, error_message: %s" , 
						e.getRequestId(), e.getErrorCode(), e.getErrorMsg()
						)
					);
			return false;
		}
				
		return true;		
	}

//推送某个特定用户消息
/*
**输入：**

MsgType type： 指定推送消息类型，枚举类型
> 消息：MsgType.SingleMessage
> 
> 通知：MsgType.SingleNotice 

Long ChannelId： 推送通道ID

String UserId：应用的用户ID

String msg： 推送内容，字符串类型

String title ：推送标题（可选参数），字符串类型，只在发送`通知`时需指定

**输出：**

boolean 布尔类型，推送成功返回true，失败返回false
*/	
	public boolean PushSingleMsg(MsgType type,Long ChannelId,String UserId,String msg,String...title){
		
		if(MsgType.SingleMessage!=type && MsgType.SingleNotice!=type){
			System.out.println("Input MsgType Error! ");
			return false;
		}
		
		ChannelKeyPair pair = new ChannelKeyPair(getApiKey(), getSecretKey());
		// 2. 创建BaiduChannelClient对象实例
		BaiduChannelClient channelClient = new BaiduChannelClient(pair);
		
		// 3. 若要了解交互细节，请注册YunLogHandler类
		channelClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) {
				System.out.println(event.getMessage());
			}
		});
		//android -----------------------------------------------------
		try {
			
			// 4. 创建请求类对象
			//		手机端的ChannelId， 手机端的UserId， 先用1111111111111代替，用户需替换为自己的
			PushUnicastMessageRequest request = new PushUnicastMessageRequest();
			request.setDeviceType(3);	// device_type => 1: web 2: pc 3:android 4:ios 5:wp		
			request.setChannelId(ChannelId);	
			request.setUserId(UserId);	 
			
			//消息
			if(MsgType.SingleMessage==type)
				request.setMessage(msg);
			//通知
			else{
				request.setMessageType(1);
				request.setMessage("{\"title\":\""+title[0]+"\",\"description\":\""+msg+"\"}");				
			}
			
			// 5. 调用pushMessage接口
			PushUnicastMessageResponse response = channelClient.pushUnicastMessage(request);
				
			// 6. 认证推送成功
			System.out.println("push amount : " + response.getSuccessAmount()); 
			
		} catch (ChannelClientException e) {
			// 处理客户端错误异常
			e.printStackTrace();
			return false;
		} catch (ChannelServerException e) {
			// 处理服务端错误异常
			System.out.println(
					String.format("request_id: %d, error_code: %d, error_message: %s" , 
						e.getRequestId(), e.getErrorCode(), e.getErrorMsg()
						)
					);
			return false;
		}		
		return true;
		
	}


//查询应用绑定设备信息
/*
**输入：**

String UserId：应用的用户ID

**输出：**

String 查询结果，JSON格式数据，包含绑定设备的信息。
*/
	public String QueryBindList(String UserId){
		String bindlist="";
		ChannelKeyPair pair = new ChannelKeyPair(getApiKey(), getSecretKey());		
		// 2. 创建BaiduChannelClient对象实例
		BaiduChannelClient channelClient = new BaiduChannelClient(pair);
		
		// 3. 若要了解交互细节，请注册YunLogHandler类
		channelClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) {
				// TODO Auto-generated method stub
				System.out.println(event.getMessage());
			}
		});
		
		try {
			// 4. 创建请求类对象
			//		手机端的UserId， 先用1111111111111代替，用户需替换为自己的
			QueryBindListRequest request = new QueryBindListRequest();
			request.setUserId(UserId);
			
			// 5. 调用queryBindList接口
			QueryBindListResponse response = channelClient.queryBindList(request);
			
			// 6. 对返回的结果对象进行操作
			List<BindInfo> bindInfos = response.getBinds();
			for ( BindInfo bindInfo : bindInfos ) {
				long channelId = bindInfo.getChannelId();
				String userId = bindInfo.getUserId();
				int status = bindInfo.getBindStatus();
				//System.out.println("channel_id:" + channelId + ", user_id: " + userId + ", status: " + status);

				String bindName = bindInfo.getBindName();
				long bindTime = bindInfo.getBindTime();
				String deviceId = bindInfo.getDeviceId();
				int deviceType = bindInfo.getDeviceType();
				long timestamp = bindInfo.getOnlineTimestamp();
				long expire = bindInfo.getOnlineExpires();
				
				/*System.out.println("bind_name:" + bindName + "\t" + "bind_time:" + bindTime);
				System.out.println("device_type:" + deviceType + "\tdeviceId" + deviceId);
				System.out.println(String.format("timestamp: %d, expire: %d", timestamp, expire));
				*/
				bindlist+="{\"channel_id\":\""+channelId+"\",\"user_id\":\""+userId+
						"\"bind_name\":\""+bindName+"\",\"bind_time\":\""+bindTime+
						"\"device_type\":\""+deviceType+"\",\"deviceId\":\""+deviceId+
						"\"timestamp\":\""+timestamp+"\",\"expire\":\""+expire+
						"\"}";
			}
			
		} catch (ChannelClientException e) {
			// 处理客户端错误异常
			e.printStackTrace();
		} catch (ChannelServerException e) {
			// 处理服务端错误异常
			System.out.println(
					String.format("request_id: %d, error_code: %d, error_message: %s" , 
						e.getRequestId(), e.getErrorCode(), e.getErrorMsg()
						)
					);
		}		
		return bindlist;
		
	}


//iOS平台接口------------------------------------
//>注：iOS平台只能推送`通知`消息

//推送广播消息
/*
**输入：**

DeployStatus status ：枚举类型,指定推送开发模式状态，不同状态需要终端采用不同的开发者证书
> 开发状态：DeployStatus.Developer
> 
> 发布状态：DeployStatus.Production 

String msg ：推送内容，字符串类型

String title ：推送标题（可选参数），字符串类型

**输出：**

boolean 布尔类型，推送成功返回true，失败返回false
*/	
	public boolean iOSPushBroadcastMsg(DeployStatus status,String msg,String...title){
		ChannelKeyPair pair = new ChannelKeyPair(getApiKey(), getSecretKey());
		// 2. 创建BaiduChannelClient对象实例
		BaiduChannelClient channelClient = new BaiduChannelClient(pair);
		
		// 3. 若要了解交互细节，请注册YunLogHandler类
		channelClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) {
				System.out.println(event.getMessage());
			}
		});		

		try {
			
			// 4. 创建请求类对象
			PushBroadcastMessageRequest request = new PushBroadcastMessageRequest();
			request.setDeviceType(4);	// device_type => 1: web 2: pc 3:android 4:ios 5:wp	
			request.setMessageType(1);  //通知
			if(status == DeployStatus.Developer)
				request.setDeployStatus(1); // DeployStatus => 1: Developer 2: Production
			else
				request.setDeployStatus(2); // DeployStatus => 1: Developer 2: Production
			//System.out.println("{\"title\":\""+title[0]+"\",\"description\":\""+msg+"\""+sound+"}");
			request.setMessage("{\"title\":\""+title[0]+"\",\"description\":\""+msg+"\""+sound+"}");	
			
			// 5. 调用pushMessage接口
			PushBroadcastMessageResponse response = channelClient.pushBroadcastMessage(request);
				
			// 6. 认证推送成功
			System.out.println("push iOS amount : " + response.getSuccessAmount()); 
			
		} catch (ChannelClientException e) {
			// 处理客户端错误异常
			e.printStackTrace();
		} catch (ChannelServerException e) {
			// 处理服务端错误异常
			System.out.println(
					String.format("request_id: %d, error_code: %d, error_message: %s" , 
						e.getRequestId(), e.getErrorCode(), e.getErrorMsg()
						)
					);
		}
		
		return true;
		
	}

//推送标签消息
/*
**输入：**

DeployStatus status ：枚举类型,指定推送开发模式状态，不同状态需要终端采用不同的开发者证书
> 开发状态：DeployStatus.Developer
> 
> 发布状态：DeployStatus.Production 

String msg ：推送内容，字符串类型

String tag ：标签消息，字符串类型，可用于分组

String title ：推送标题（可选参数），字符串类型

**输出：**

boolean 布尔类型，推送成功返回true，失败返回false
*/
	public boolean iOSPushTagMsg(DeployStatus status ,String tag,String msg,String...title){
		ChannelKeyPair pair = new ChannelKeyPair(getApiKey(), getSecretKey());
		// 2. 创建BaiduChannelClient对象实例
		BaiduChannelClient channelClient = new BaiduChannelClient(pair);
		
		// 3. 若要了解交互细节，请注册YunLogHandler类
		channelClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) {
				System.out.println(event.getMessage());
			}
		});		

		try {
			
			// 4. 创建请求类对象
			PushTagMessageRequest request = new PushTagMessageRequest();
			request.setDeviceType(4);	// device_type => 1: web 2: pc 3:android 4:ios 5:wp	
			request.setMessageType(1);  //通知
			request.setTagName(tag);
			if(status == DeployStatus.Developer)
				request.setDeployStatus(1); // DeployStatus => 1: Developer 2: Production
			else
				request.setDeployStatus(2); // DeployStatus => 1: Developer 2: Production
			
			request.setMessage("{\"title\":\""+title[0]+"\",\"description\":\""+msg+"\""+sound+"}");	
			
			// 5. 调用pushMessage接口
			PushTagMessageResponse response = channelClient.pushTagMessage(request);
				
			// 6. 认证推送成功
			System.out.println("push iOS amount : " + response.getSuccessAmount()); 
			
		} catch (ChannelClientException e) {
			// 处理客户端错误异常
			e.printStackTrace();
		} catch (ChannelServerException e) {
			// 处理服务端错误异常
			System.out.println(
					String.format("request_id: %d, error_code: %d, error_message: %s" , 
						e.getRequestId(), e.getErrorCode(), e.getErrorMsg()
						)
					);
		}
		
		return true;
		
	}

//推送某个特定用户消息
/*
**输入：**

DeployStatus status ：枚举类型,指定推送开发模式状态，不同状态需要终端采用不同的开发者证书
> 开发状态：DeployStatus.Developer
> 
> 发布状态：DeployStatus.Production 

String msg ：推送内容，字符串类型

Long ChannelId： 推送通道ID

String UserId：应用的用户ID

String title ：推送标题（可选参数），字符串类型

**输出：**

boolean 布尔类型，推送成功返回true，失败返回false
*/
	public boolean iOSPushSingleMsg(DeployStatus status ,Long ChannelId,String UserId,String msg,String...title){
		ChannelKeyPair pair = new ChannelKeyPair(getApiKey(), getSecretKey());
		// 2. 创建BaiduChannelClient对象实例
		BaiduChannelClient channelClient = new BaiduChannelClient(pair);
		
		// 3. 若要了解交互细节，请注册YunLogHandler类
		channelClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) {
				System.out.println(event.getMessage());
			}
		});		

		try {
			
			// 4. 创建请求类对象
			PushUnicastMessageRequest request = new PushUnicastMessageRequest();
			request.setDeviceType(4);	// device_type => 1: web 2: pc 3:android 4:ios 5:wp	
			request.setMessageType(1);  //通知
			request.setChannelId(ChannelId);	
			request.setUserId(UserId);	 
			if(status == DeployStatus.Developer)
				request.setDeployStatus(1); // DeployStatus => 1: Developer 2: Production
			else
				request.setDeployStatus(2); // DeployStatus => 1: Developer 2: Production
			
			request.setMessage("{\"title\":\""+title[0]+"\",\"description\":\""+msg+"\""+sound+"}");	
			
			// 5. 调用pushMessage接口
			PushUnicastMessageResponse response = channelClient.pushUnicastMessage(request);
				
			// 6. 认证推送成功
			System.out.println("push iOS amount : " + response.getSuccessAmount()); 
			
		} catch (ChannelClientException e) {
			// 处理客户端错误异常
			e.printStackTrace();
		} catch (ChannelServerException e) {
			// 处理服务端错误异常
			System.out.println(
					String.format("request_id: %d, error_code: %d, error_message: %s" , 
						e.getRequestId(), e.getErrorCode(), e.getErrorMsg()
						)
					);
		}
		
		return true;
		
	}


//测试代码
	public static void main(String[] args){
		
		// 1. 设置developer平台的ApiKey/SecretKey
		String apiKey = "8DFC5qWxSe7hgMxT7Pdt4TCV";
		String secretKey = "1C2crPeCP1z1xZ1iNWgPiUlckqfuRxbt";
		PushServer pushserver= new PushServer();
		pushserver.setKey(apiKey, secretKey);	
		
	    //消息
	    /*boolean res=pushserver.PushBroadcastMsg(MsgType.BroadcastMessage,"然后通过fs.read()读取时，逐步从磁盘中将字节拷贝到Buffer中，完成一次读取，则从这个Buffer中slice出读取的部分为一个小Buffer对象");
	    if(!res)
	    System.out.println("PushBroadcastMsg Error !");

	    //通知
	     * 
	     */
	    //boolean res2=pushserver.PushBroadcastMsg(MsgType.BroadcastNotice,"HttpServlet首先必须读取Http请求的内容。Servlet容器负责创建HttpServlet对象，并把Http请求直接封装到HttpServlet对象中","李立明");
	    //if(!res2)
	    //System.out.println("PushBroadcastMsg Error !");
	    /*
		String UserId="822808065592337373";
		String res=pushserver.QueryBindList(UserId);
		if(res != null)
			System.out.println("res:"+res);*/
		//pushserver.iOSPushTagMsg(DeployStatus.Developer,"ttt","soundsounds1111111111","TITLE");
		//pushserver.iOSPushTagMsg(DeployStatus.Developer,"def","def tag test","TITLE");
		pushserver.iOSPushBroadcastMsg(DeployStatus.Developer,"2222深入开展形式多样的实践教育活动。","活动背景");
		
		//pushserver.iOSPushSingleMsg(DeployStatus.Developer,5477073329945604746L,"1018481786240868713","single","single");
		//pushserver.QueryBindList("1018481786240868713");
//		String userId="984011212117826789";
//		Long channelId=4353107090483623703L;
//		pushserver.PushSingleMsg(MsgType.SingleNotice, channelId, userId, "jinkai","jinkai");
	}
}


