package com.aisino.pushserver;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;


import org.bson.types.ObjectId;

public class mongoDriver {
	
	private static DB dbClient=null;
	//初始化数据库，设置索引
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
	
	//get tag detail information 
	//output：
	//{ "id" : "zxc_001" , "name" : "company_news" , "description" : "introduce news of the company" ,
	//"isFixed" : false},{ "id" : "zxc_002" , "name" : "OA" , 
	//"description" : "workflow of com" , "isFixed" : false}
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
	
	
	//保存已经推送成功的各类信息----------------------------------------	
	//由于系统_id索引包含时间戳，不需要额外添加字段

	//保存推送tag信息
	public boolean saveTagInfo(String tagVal,String msg){
		DBCollection pushmsg = dbClient.getCollection("pushmsg");
		BasicDBObject doc = new BasicDBObject("type", "tag").
                append("name",tagVal).
                append("msg", msg);
		pushmsg.insert(doc);		
		return false;		
	}
	//保存推送的广播信息
	public boolean saveBroadcastInfo(String msg){
		DBCollection pushmsg = dbClient.getCollection("pushmsg");
		BasicDBObject doc = new BasicDBObject("type","broadcast").
                append("msg", msg);
		pushmsg.insert(doc);		
		return false;	
	}
	//保存推送的个人信息
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
	//查询已经推送成功的信息，输入参数按年，月，日，此日期之后的信息
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
	//获得最后一条推送的信息
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

	//test
	public static void main(String[] args){
		String DBname ="pushtest001";
		String app_id ="222223455555";
		String user_id ="123456789012345678";
		String channel_id ="4923859573096872165";
		
		try {
			mongoDriver driver=new mongoDriver();
			driver.initDBdriver(DBname);
			
			//insert user info
			//driver.insertUsersInfo(app_id,user_id,channel_id);
						
			//insert one tag info
			/*
			String id ="zxc_001";
			String name ="company_news";
			String description ="introduce news of the company";
			boolean isFixed =false;
			driver.setTagInfo(app_id,id,name,description,isFixed);

			id ="zxc_002";
			name ="OA";
			description ="workflow of com";
			isFixed =false;
			driver.setTagInfo(app_id,id,name,description,isFixed);
			
			//add user tag
			String[] tags={"news4","news5"};
			driver.setUserTag(user_id,tags);
			String[] tags2={"news6"};
			driver.setUserTag(user_id,tags2);
			*/
			
			//get detail tag info
			String taginfo=driver.getTagInfo(app_id);
			System.out.println("Res:"+taginfo);
			
			
			//save 
			/*driver.saveTagInfo("news","a tag message");
			driver.saveBroadcastInfo("broadcast message");
			driver.saveSingleInfo(user_id,channel_id,"single message");
			
			//
			String queryMsg=driver.QueryMsgByDate(2013,10,17);
			System.out.println("queryMsg:"+queryMsg);
			
			String ql=driver.QueryLastMsg();
			System.out.println("QueryLastMsg:"+ql);*/

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}