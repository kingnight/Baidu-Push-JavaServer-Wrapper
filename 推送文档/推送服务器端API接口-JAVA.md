# 推送服务器端API接口-JAVA#

## 1.通用接口 ##

### 1.1 初始化Key参数 ###

		PushServer pushserver= new PushServer();
		pushserver.setKey(apiKey, secretKey);

其中apiKey, secretKey是在推送开发者网站中创建新应用时获取到的。

### 1.2 查询应用绑定设备信息 ###

	public String QueryBindList(String UserId)	

**输入：**

String UserId：应用的用户ID

**输出：**

String 查询结果，JSON格式数据，包含绑定设备的信息。

    {"channel_id":"4444772434926321234","user_id":"822808065592337474"bind_name":"Nexus 7","bind_time":"1382081790"device_type":"3","deviceId":"44E515A988BC70C6A742512345A5F447"timestamp":"1382332288","expire":"1382418688"}

## 2.Android平台接口 ##

### 2.1 推送广播消息 ###

	public boolean PushBroadcastMsg(MsgType type ,String msg,String...title)

**输入：**

MsgType type ：指定推送消息类型，枚举类型
> 消息：MsgType.BroadcastMessage
> 
> 通知：MsgType.BroadcastNotice 

String msg ：推送内容，字符串类型

String title ：推送标题（可选参数），字符串类型，只在发送`通知`时需指定

**输出：**

boolean 布尔类型，推送成功返回true，失败返回false


**示例：**

    //消息
    boolean res=pushserver.PushBroadcastMsg(MsgType.BroadcastMessage,"send from code");
    if(!res)
    System.out.println("PushBroadcastMsg Error !");

    //通知
    boolean res2=pushserver.PushBroadcastMsg(MsgType.BroadcastNotice,"send from code","title");
    if(!res2)
    System.out.println("PushBroadcastMsg Error !");

### 2.2 推送标签消息 ###

    public boolean PushTagMsg(MsgType type ,String tag,String msg,String...title)

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

**示例：**

    //消息
    boolean res=pushserver.PushTagMsg(MsgType.TagMessage,"aisino","test message from aisino");
    if(!res)
    System.out.println("PushTagMsg Error !");	
	
    //通知
    boolean res=pushserver.PushTagMsg(MsgType.TagNotice,"aisino","test message from aisino","aisino title");
    if(!res)
    System.out.println("PushTagMsg Error !");	

### 2.3 推送某个特定用户消息 ###

    public boolean PushSingleMsg(MsgType type,Long ChannelId,String UserId,String msg,String...title)
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

**示例：**

		Long ChannelId=111111111111L;
		String UserId="22222222222222";
		//消息
		boolean res=pushserver.PushSingleMsg(MsgType.SingleMessage,ChannelId,UserId,"single message");
		if(!res)
			System.out.println("PushSingleMsg Error !");			
		//通知
		boolean res=pushserver.PushSingleMsg(MsgType.SingleNotice,ChannelId,UserId,"single message","single title");
		if(!res)
			System.out.println("PushSingleMsg Error !");


# 3.iOS平台接口 #

>注：iOS平台只能推送`通知`消息

### 3.1 推送广播消息 ###
    public boolean iOSPushBroadcastMsg(DeployStatus status,String msg,String...title)

**输入：**

DeployStatus status ：枚举类型,指定推送开发模式状态，不同状态需要终端采用不同的开发者证书
> 开发状态：DeployStatus.Developer
> 
> 发布状态：DeployStatus.Production 

String msg ：推送内容，字符串类型

String title ：推送标题（可选参数），字符串类型

**输出：**

boolean 布尔类型，推送成功返回true，失败返回false


### 3.2 推送标签消息 ###
    public boolean iOSPushTagMsg(DeployStatus status ,String tag,String msg,String...title)
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

### 3.3 推送某个特定用户消息 ###

    public boolean iOSPushSingleMsg(DeployStatus status ,Long ChannelId,String UserId,String msg,String...title)
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
