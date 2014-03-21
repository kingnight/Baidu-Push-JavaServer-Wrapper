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


import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

public class Push2DBdriver {
	private static String apiKey;
	private static String secretKey;
	private static DB dbClient=null;
	
	enum MsgType{BroadcastMessage,BroadcastNotice,TagMessage,TagNotice,SingleMessage,SingleNotice}
	public void setKey(String _apiKey,String _secretKey){
		setApiKey(_apiKey);
		setSecretKey(_secretKey);
	}
	
	public static String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		Push2DBdriver.apiKey = apiKey;
	}
	
	public static String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		Push2DBdriver.secretKey = secretKey;
	}
	
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
		
		//ios -------------------------------------------------------------------
		/*try {
			
			// 4. 创建请求类对象
			PushBroadcastMessageRequest request = new PushBroadcastMessageRequest();
			request.setDeviceType(4);	// device_type => 1: web 2: pc 3:android 4:ios 5:wp	
			
			request.setMessage(msg);
			// 若要通知，
			//			request.setMessageType(1);
			//			request.setMessage("{\"title\":\"Notify_title_danbo\",\"description\":\"Notify_description_content\"}");
 			
			// 5. 调用pushMessage接口
			PushBroadcastMessageResponse response = channelClient.pushBroadcastMessage(request);
				
			// 6. 认证推送成功
			System.out.println("push Android amount : " + response.getSuccessAmount()); 
			
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
		*/
		
		return true;
		
	}
	
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
		//android -----------------------------------------------------
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

		
	public void initDBdriver(String DBname) throws UnknownHostException{
		
		if(dbClient==null){
			MongoClient mongoClient = new MongoClient();
			dbClient = mongoClient.getDB( DBname );
			
			DBCollection users = dbClient.getCollection("users");
			users.ensureIndex( new BasicDBObject("appid",1),new BasicDBObject("unique", true));	
			users.ensureIndex( new BasicDBObject("user_id",1));	
			
			DBCollection subtaginfo = dbClient.getCollection("subtaginfo");
			subtaginfo.ensureIndex(new BasicDBObject("appid",1));
			subtaginfo.ensureIndex(new BasicDBObject("id",1));
			
			DBCollection pushmsg = dbClient.getCollection("pushmsg");
			pushmsg.ensureIndex(new BasicDBObject("timestamp",1));
			pushmsg.ensureIndex(new BasicDBObject("type",1));
		}
	}
	
	//insert user data
	public void insertUsersInfo(String appid,String user_id,String channel_id){
		DBCollection users = dbClient.getCollection("users");
		//app_id
		//user_id
		//channel_id
		BasicDBObject doc = new BasicDBObject("appid", appid).
                append("user_id",user_id).
                append("channel_id", channel_id);
		users.insert(doc);
	}
	
	//set user tag
	public boolean setUserTag(String user_id, String[] tags){
		
		DBCollection users = dbClient.getCollection("users");	
		BasicDBObject tag = new BasicDBObject();
		for(int i=0 ; i<tags.length;i++){
			tag.put("tag", tags[i]);
			users.update(new BasicDBObject("user_id", user_id) , 
					new BasicDBObject("$addToSet", tag));
		}		
		return false;		
	}
	
	//get all tag detail info 
	public String getTagInfo(String app_id){
		String query = null;
		DBCollection subtaginfo = dbClient.getCollection("subtaginfo");
		
		DBCursor cursor= subtaginfo.find(new BasicDBObject("app_id", app_id),new BasicDBObject("app_id", 0).append("_id", 0));
		try {
			   while(cursor.hasNext()) {
				   if(query==null)
					   query=cursor.next().toString();
				   else											   
					   query=query+","+cursor.next().toString();
				   
			       System.out.println("getTagInfo:"+query);
			   }
			} finally {
			   cursor.close();
		}
		return query;
		
		
	}
	
	//set tag detail information
	public void setTagInfo(String app_id,String id,String name,String description,boolean isFixed){
		DBCollection subtaginfo = dbClient.getCollection("subtaginfo");
		
		BasicDBObject doc = new BasicDBObject("app_id", app_id).
				append("id",id).
                append("name",name).
                append("description", description).
                append("isFixed", isFixed);
		subtaginfo.insert(doc);
	}
	
	//由于系统_id索引包含时间戳，不需要额外添加字段
	//save
	public boolean saveTagInfo(String tagVal,String msg){
		DBCollection pushmsg = dbClient.getCollection("pushmsg");
		BasicDBObject doc = new BasicDBObject("type", "tag").
                append("name",tagVal).
                append("msg", msg);
		pushmsg.insert(doc);		
		return false;		
	}
	
	public boolean saveBroadcastInfo(String msg){
		DBCollection pushmsg = dbClient.getCollection("pushmsg");
		BasicDBObject doc = new BasicDBObject("type","broadcast").
                append("msg", msg);
		pushmsg.insert(doc);		
		return false;	
	}
	
	public boolean saveSingleInfo(String user_id,String channel_id, String msg){
		DBCollection pushmsg = dbClient.getCollection("pushmsg");
		BasicDBObject doc = new BasicDBObject("type","single").
                append("user_id",user_id).
                append("channel_id",channel_id).
                append("msg", msg);
		pushmsg.insert(doc);			
		return false;		
	}
	//get date equal or older than objectId 
	public String QueryMsgByDate(int year,int month,int day){
		if(year <1970 || year > 2100 || month <=0 || month >12 || day <=0 || day>31)
		{
			System.out.println("input Date Error");
			return null;
		}
		Calendar c=Calendar.getInstance();
		c.set(year,month-1,day,0,0,0);
		Date d=c.getTime();
		//System.out.println("date:"+d);
		
		String query = null;
		DBCollection subtaginfo = dbClient.getCollection("pushmsg");
		//System.out.println("ObjectId:"+new ObjectId(d));
		DBCursor cursor= subtaginfo.find(new BasicDBObject("_id", new BasicDBObject("$gte",new ObjectId(d))),new BasicDBObject("_id", 0));
		try {
			   while(cursor.hasNext()) {
				   if(query==null)
					   query=cursor.next().toString();
				   else											   
					   query=query+","+cursor.next().toString();
				   
			       //System.out.println("QueryMsgByDate:"+query);
			   }
			} finally {
			   cursor.close();
		}
		return query;		
	}
	
	//get last insert msg
	public String QueryLastMsg(){
		String query = null;
		DBCollection subtaginfo = dbClient.getCollection("pushmsg");

		DBCursor cursor= subtaginfo.find().sort(new BasicDBObject("$natural", -1)).limit(1);
		try {
			   while(cursor.hasNext()) {
				   if(query==null)
					   query=cursor.next().toString();
				   else											   
					   query=query+","+cursor.next().toString();
				   
			       //System.out.println("QueryLastMsg:"+query);
			   }
			} finally {
			   cursor.close();
		}
		return query;					
	}
	
	
	public static void main(String[] args){
		// 1. 设置developer平台的ApiKey/SecretKey
		String apiKey = "PmdxEvYG5HatSGXzCYzqWahT";
		String secretKey = "zmPT9TOHAGBMgd6KOctYHAT8CCbA9A1g";
		Push2DBdriver pushserver= new Push2DBdriver();
		pushserver.setKey(apiKey, secretKey);	
		
		
	}
}


