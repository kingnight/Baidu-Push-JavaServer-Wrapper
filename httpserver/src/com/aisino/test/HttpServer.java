package com.aisino.test;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//测试
//http://localhost:8004/httpserver/httpserver.action?action=initUser
public class HttpServer extends HttpServlet{
	/**
	 * 提供一个测试服务器环境，模拟客户端向服务器发起请求和返回处理
	 */
	private static final long serialVersionUID = 1L;
	private boolean DEBUG=true;
	
    public boolean isBlank(String str) {
	        if(str == null || str.trim().length() == 0 || "".equals(str)) {
	            return true;
	        }
	        return false;
	}
    
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		doPost(request,response);
	}
	
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		String actionName=request.getParameter("action");
		if(DEBUG)
			System.out.println("actionName----"+actionName);
		response.setContentType("text/plain; charset=UTF-8");
		if(actionName.equals("initUser"))
		{
			String appid=request.getParameter("appid");
			String user_id=request.getParameter("user_id");
			String channel_id=request.getParameter("channel_id");
			String platform=request.getParameter("platform");
			String user_name=request.getParameter("user_name");
			if(DEBUG)
			System.out.println("initUser---"+"appid:"+appid+"\n"
					+"user_id:"+user_id+"\n"+
					"channel_id:"+channel_id+"\n"+
					"platform:"+platform+"\n"+
					"user_name:"+user_name);
			
			PrintWriter out;
			out=response.getWriter();
			
			if(isBlank(appid) || isBlank(user_id) || isBlank(channel_id))
			//if(appid.length()>0 && user_id.length()>0 && channel_id.length()>0)
			{
				out.print("{code:-1}");
			}
			else{
				out.print("{code:0}");			
			}
			out.close();
		}
		else if(actionName.equals("setTag")){
			String user_id=request.getParameter("user_id");
			String tag=request.getParameter("tag");
			System.out.println("setTag---"+"user_id:"+user_id+"tag:"+tag);
			
			PrintWriter out;
			out=response.getWriter();
			if(isBlank(tag) || isBlank(user_id)){
				out.print("{code:-1}");
			}				
			else				
				out.print("{code:0}");
			out.close();
		}
		else if(actionName.equals("delTag")){
			String user_id=request.getParameter("user_id");
			String tag=request.getParameter("tag");
			System.out.println("delTag---"+"user_id:"+user_id+"tag:"+tag);
			
			PrintWriter out;
			out=response.getWriter();
			if(isBlank(tag) || isBlank(user_id))
				out.print("{code:-1}");
			else				
				out.print("{code:0}");
			out.close();
		}	
		else if(actionName.equals("getTagInfo")){
			String appid=request.getParameter("appid");
			PrintWriter out;
			out=response.getWriter();			
			if(isBlank(appid)){
				out.print("{code:-1}");								
			}
			else{
				out.print("{code:0," +
				"res:{ \"id\" : \"zxc_001\" , \"name\" : \"company_news\" , \"description\" : \"introduce news of the company\" , \"isFixed\" : false},{ \"id\" : \"zxc_002\" , \"name\" : \"OA\" , \"description\" : \"workflow of com\" , \"isFixed\" : false}}");				
			}
			out.close();
		}		
		else{
			System.out.println("error");
			PrintWriter out;
			out=response.getWriter();
			out.print("{code:-1}");
			out.close();			
		}
		return ;
	}
}
