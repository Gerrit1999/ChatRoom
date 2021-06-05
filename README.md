# ChatRoom

# 第1章 系统介绍

## 1.1 概述

​		随着网络的大幅度普及，以及网络技术快速发展，人们通过网络进行交流的方式发生着巨大的变化，人们不再拘泥于移动电话的通话模式，越来越多的人通过网络聊天工具进行交流，网络聊天室便是非常典型的聊天工具之一。聊天室系统的即时交流方式满足了网络中人们同时与多人进行聊天交流的需要,使得较多的人在同一个聊天页面进行交流变得方便，简单。		

​		ChatRoom聊天室是采用B/S结构进行开发的网页聊天室项目，使用socket技术来进行通信，主要功能如下：

​		首先，用户需要注册并登录后才能进入聊天界面，该系统可以创建多个房间，可以给房间设置名字和密码，创建的房间都有一个唯一的房间号，用户能够输入房间号和密码来进入对应的房间，进入房间后也可以退出。每个用户能够创建和加入多个房间。在房间右侧可以看到房间内的所有成员和在线的成员，并能选择其中的一名成员进行私聊。每个房间可以加入多个用户，房间之间相互独立，互不干扰。用户发送信息到房间后，房间内的所有人均能立即收到消息。聊天记录存储在数据库中，在关闭浏览器或退出登录后，下次登录时也能获取到聊天记录。

​		其次，用户可以发送多种类型的消息，例如文本、表情、图片、文件等。文本和表情消息可以选择字体大小、字体风格和字体粗细，图片消息能根据图片的长宽比例在聊天框中显示，文件消息发送后保存到服务器，房间中的其他成员可以接收到发送文件的消息，选择是否下载。



## 1.2 系统的部署

​		使用Maven将项目源代码打包为war放到tomcat的webapps目录下，然后打开tomcat/bin/startup.bat启动服务器，启动完成后浏览器访问 http://localhost:8080/ChatRoom 即可。



## 1.3 功能模块图

​		功能模块图见图1.3。

![Image text](./productShow/images/功能模块图.jpg)

<div align = "center">图 1.3 功能模块图</div>



# 第2章 系统设计

## 2.1 系统运行环境

​		系统选择使用Java作为后台开发语言，jsp作为前台开发页面，使用JavaScript以及jQuery作为脚本语言，使用IDEA作为开发工具，使用Maven来管理项目，使用SSM框架来负责前后端以及数据库数据的交互，使用Spring Security来验证登录，使用Tomcat来搭建服务器。客户端使用Google Chrome和Microsoft Edge作为测试。



## 2.2 前端设计

​		前端部分使用两个页面，主页提供注册和登录的功能，登录成功后会进入到聊天页面。聊天页面的左上角显示用户的头像和用户名，还有菜单按钮可以下拉选择创建、加入房间和退出登录。创建房间和加入房间成功后会进入对应的房间。退出登录后会跳转到登录页面。右侧显示所有已加入的房间，当房间有新消息时会显示未读消息计数的红点提示。页面中央显示当前房间的聊天记录，房间接收到的消息会在这里显示，上方显示房间名和房间号，右侧有退出房间按钮，如果是房主还有关闭和开启房间的按钮。下方是聊天文本框和工具栏，点击发送按钮可以发送文本和图片，点击文件上传按钮则可以发送文件。右侧是公告栏和成员栏，成员栏可以显示房间中的所有成员，并将在线成员置顶并标注。



## 2.3 数据库设计

​		如图2.1所示，其中inner开头的为中间表。![Image text](./productShow/images/数据表.jpg)

<div align = "center">图 2.3 数据表</div>				



## 2.4 数据流图

​		如图2.4所示。

![Image text](./productShow/images/数据流图.jpg)

<div align = "center">图 2.4 数据流图</div>				



## 2.5 后端设计

### 2.5.1 实体类

- 图片信息：

```java
public class Image implements Serializable {
    private Integer id;

    private String url;         // 图片路径

    private Integer width;      // 图片宽度

    private Integer height;     // 图片长度

    private Double proportion;  // 宽长比
}
```

- 文件信息：

```java
public class File implements Serializable {
    private Integer id;

    private String name;    // 文件名

    private String path;    // 文件路径

    private Long size;      // 文件大小

    private Image image;    // 包含的图片
}
```

- 用户信息：

```java
public class User implements Serializable {
    private Integer id;

    private String username;        // 用户名

    private String password;        // 密码

    private String email;           // 邮箱

    private Date recentActiveTime;  // 最后活跃时间
}
```

- 房间信息：

```java
public class Room {
    private Integer id;

    private String name;        // 房间名

    private String password;    // 房间密码

    private Integer hostId;     // 房主id

    private Boolean enable;     // 是否启用
}
```

- 封装聊天数据：

```java
public class Message implements Serializable {
    private Integer id;

    private Date date;			// 发送时间

    private Integer roomId;		// 消息所在房间id

    private User sender;    	// 发送方

    private User receiver;  	// 接收方

    private File file;			// 包含的文件

    private String message;		// 消息内容

    private Integer fontSize;	// 字体大小

    private Integer fontWeight;	// 字体粗细

    private String fontStyle;	// 字体
}
```



### 2.5.2 工具类

- Server类负责接收和转发消息：

```java
public class Server {
    private static ServerSocket serverSocket;   // 服务端套接字

    public static String hostAddress;           // 服务器ip

    public static Integer localPort;            // 服务器端口号
}
```

- 使用Map保存连接到Server的socket、Server接收到的Socket和输入输出流：

```java
public class SocketMap {
    private static final Map<Integer, Socket> acceptSocket = new ConcurrentHashMap<>();
    private static final Map<Integer, Socket> connectSocket = new ConcurrentHashMap<>();
    private static final Map<Socket, ObjectOutputStream> outputMap = new ConcurrentHashMap<>();
    private static final Map<Socket, ObjectInputStream> inputMap = new ConcurrentHashMap<>();
}
```

- 在与前端的交互中，为了数据格式的统一，将操作是否成功、操作返回的数据、出错原因等内容封装后再发送给前端。

```java
public class ResultEntity<T> {
    public enum ResultType {
        SUCCESS,
        FAILED;
    }

    private ResultType result;  // 当前请求处理结果

    private String message;     // 错误消息

    private T data;             // 返回的数据
}
```

- 存放常量的工具类

```java
public class CustomConstant {
    public static final String MESSAGE_STRING_INVALIDATE = "字符串不合法! 请不要传入空字符串!";

    public static final String MESSAGE_SYSTEM_ERROR_IO_EXCEPTION = "系统错误: IO异常!";
   
   	......
}
```



# 第3章 系统实现

## 3.1 注册和登录

​		用户输入用户名、邮箱和密码后即可注册，其中用户名要求不能重复。点击注册后，后台会验证用户名是否存在，如果不存在则将密码加密后封装User对象保存到数据库：

```java
public boolean addUser(User user) {
        String password = user.getPassword();
        if (password == null) {
            return false;
        }
        password = passwordEncoder.encode(password);
        user.setPassword(password);
        int cnt = userMapper.insertSelective(user);
        return cnt == 1;
}
```

​		登录使用Spring Security进行验证：

```java
public class CrowdUserDetailsService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查找User对象
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        // 给Admin设置角色权限信息
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new SecurityUser(user, authorities);
    }
}
```

```java
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;  // 使用BCryptPasswordEncoder加密

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
        ;
    }
}
```



## 3.1 创建房间

​		用户点击创建房间，页面会弹出一个模态框，用户填写房间名和密码后，前台使用ajax向后台发送请求，后台通过@RequestBody封装为Room对象，密码加密后保存到数据库。操作成功后返回房间id，前端将根据房间id调用加入房间的函数，让当前用户进入房间。



## 3.2 加入房间

​		用户点击加入房间按钮后，页面会弹出一个模态框，用户输入房间号和密码，点击进入后，前台将数据发送给后台，后台验证成功后，发送“xxx已进入房间”的消息到该房间，之后返回一个Room对象给前台，之后前台通过该room对象跳转到该房间，并向后台拉取该房间的聊天记录。



## 3.3 消息发送

​		用户在文本框中输入消息点击发送后，前端获取文本框的内容、字体大小、字体风格、字体粗细等参数，通过ajax发送给后端，后台使用Socket套接字进行用户之间的通信。首先新建一个Server类来接收和转发所有消息，当用户登录成功后，创建套接字，发送连接信息连接到Server，并通过哈希表保存该套接字：

```java
 // 获取socket
 Socket socket = new Socket(Server.hostAddress, Server.localPort);
 // 设置超时时间
 socket.setSoTimeout(CustomConstant.acceptTimeOut);
 // 保存socket
 SocketMap.addConnectSocket(userId, socket);
```

​		使用ObjectOutputStream来发送Message对象：

```java
// 获取输出流
ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
// 连接到server
oos.writeObject(new Message(null, null, new User(userId), null));
```

​		Server使用ObjectInputStream收到连接信息（Message对象）后，通过哈希表保存接收到的套接字。

```java
Socket socket = serverSocket.accept();
// 获取输入流
ObjectInputStream ois = SocketMap.getObjectInputStream(socket);
// 获取连接信息
Message linkMsg = (Message) ois.readObject();
// 保存
SocketMap.addAcceptSocket(linkMsg.getSender().getId(), socket);
```

​		创建连接成功后，Server会开启一个子线程用来与该用户进行通信。用户发送信息时会将消息封装为Message对象，通过已保存的输出流发送给Server，同时保存数据库：

```
// 获取输出流
ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
// 发送数据
oos.writeObject(message);
// 保存数据库
messageService.addMessage(message);
```

​		Server通过已保存的输入流读取封装好的消息，从中获取发送者，接收者，房间号等信息，找到符合条件的接收者并转发。

```java
Integer senderId = message.getSender().getId();
User receiver = message.getReceiver();
Integer receiverId = receiver == null ? 0 : message.getReceiver().getId();
// -----转发-----
// 获取活跃的用户
List<User> users = userService.getUsersByRoomIdActive(message.getRoomId(), CustomConstant.activeTime);
for (User user : users) {
	if (receiverId != 0 && !senderId.equals(user.getId()) && !receiverId.equals(user.getId())) {
		continue;
	}
	Socket member = SocketMap.getAcceptSocket(user.getId());
    if (member != null) {
        // 获取输出流
        ObjectOutputStream oos = SocketMap.getObjectOutputStream(member);
        // 发送数据
        assert oos != null;
        oos.writeObject(message);
   	}
}
```



## 3.4 发送文件

​		用户上传文件后，前端将文件放入formData中作为ajax的data参数的数据。后台使用MultipartFile来接收文件，使用前需要将MultipartFile放入Spring的IOC容器中，并设置最大上传文件大小，本系统设置为200MB。通过其getOriginalFilename方法获取文件名，为了防止同名，需要拼接上一个随机的uuid，设置保存路径为：\upload\room_房间号\user\_用户id\，使用transferTo将文件保存到服务器：

```java
private com.example.entity.File saveFile(Integer roomId, Integer userId, MultipartFile multipartFile, HttpSession session) throws IOException {
    String basePath = session.getServletContext().getRealPath("upload") + "\\room_" + roomId + "\\user_" + userId;
    String fileName = multipartFile.getOriginalFilename();
    if (fileName == null) {
        throw new RuntimeException(CustomConstant.MESSAGE_SYSTEM_ERROR_NULL_POINTER_EXCEPTION);
    }
    int p = fileName.lastIndexOf('.');
    String prefix = fileName.substring(0, p);// 文件名
    String suffix = fileName.substring(p + 1);// 文件后缀
    String filePath = basePath + '\\' + prefix + '-' + UUID.randomUUID().toString().replace("-", "") + '.' + suffix;// 文件路径

    File desFile = new File(filePath);
    if (!desFile.getParentFile().exists()) {// 如果文件夹不存在则创建
        if (!desFile.mkdirs()) {
            throw new RuntimeException(CustomConstant.MESSAGE_SYSTEM_ERROR_MKDIRS);
        }
    }
    // 保存文件
    multipartFile.transferTo(desFile);

    // 文件大小
    long size = multipartFile.getSize();
    // 封装到file
    return new com.example.entity.File(fileName, filePath, size);
}
```



## 3.5 下载文件

​		后台收到下载文件请求后，需要设置response的消息头，通过response对象获取OutputStream流，通过File对象获取获取要下载的文件输入流，将文件输入流循环写入到缓冲区，再将缓冲区的数据输出到客户端浏览器。此时聊天页面就会开始下载了。

```java
request.setCharacterEncoding("utf-8");
// 下载文件需要设置消息头
response.addHeader("content-Type", "application/octet-stream");	// MIME类型:二进制文件(任意文件)
response.addHeader("content-Disposition", "attachment;filename=" + UriUtils.encode(fileName, "utf-8"));	// filename包含后缀

osw = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);

File file = new File(path);
// 获取要下载的文件输入流
in = new FileInputStream(file);
// 如果不填setContentLength，不会报错，但是下载的时候会显示大小未知
response.setContentLength((int) file.length());

int len;
// 创建数据缓冲区
byte[] buffer = new byte[1024];
// 通过response对象获取OutputStream流
os = response.getOutputStream();
// 将FileInputStream流写入到buffer缓冲区
while ((len = in.read(buffer)) > 0) {
    // 将缓冲区的数据输出到客户端浏览器
    os.write(buffer, 0, len);
}
```



## 3.6 发送图片

​		发送图片和发送文件类似，但需要创建一个Image对象来保存图片的属性，便于前端的显示。之后将Image对象封装到File中，保存到Message对象中一起发送即可。



## 3.7 发送表情

​		这里使用emoji表情，因为emoji表情可以直接以文本方式显示而不是图片，可以同文本一起发送。具体实现是通过雪碧图把一张含有多个表情的大图分割成一个个表情，这样每个表情都能设置为一个CSS的类，用许多个div通过类选择器来指定样式，然后给每个表情绑定点击事件，在文本框中拼接上对应的表情即可。



## 3.8 接收消息

​		由于web服务器不能主动向客户端推送消息，这里使用长轮询的方式。客户端从服务器请求数据，当服务器没有可以立即返回给客户端的数据时，不会立刻返回一个空结果，而是保持这个请求等待数据到来，之后将数据作为结果返回给客户端。收到消息后根据消息内容分别进行处理。长轮询的优点是在无消息的情况下不会频繁的请求，耗费资源小，但服务器保持住连接会消耗资源。

​		具体实现步骤是前台定义一个recvMsg函数用来接收消息，同样使用ajax向后台请求数据，不过这里需要设置一个超时时间30s，这个超时时间需大于后台的超时时间，在3.3中我们设置了socket的超时时间为20s，所以后台有20s的时间来等待数据到来，收到消息或是超时后，后台都会通知前台，如果收到消息前台显示。前台只要收到后台的通知，不管是收到消息还是超时，就递归调用recvMsg函数进行下一轮的请求。

​		收到消息后，前端根据消息的来源和消息的内容进行处理，如果消息来源是用户自己，则显示在右侧，否则显示在左侧。如果是图片则使用img标签，由于图片保存在服务器上，将src设置为图片路径即可。如果是其他人发送的文件，在消息下方添加一个下载按钮用来下载文件。

```java
@ResponseBody
@RequestMapping("/recv/message.json")
public ResultEntity<Message> recvMessage(@RequestParam("userId") Integer userId) {
    Socket socket = SocketMap.getConnectSocket(userId);    // 获取对应的socket
    if (socket == null) {
        return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SOCKET_NOT_FOUND, null);
    }
    try {
        // 获取输入流
        ObjectInputStream ois = SocketMap.getObjectInputStream(socket);
        // 获取Message对象
        Message message = null;
        try {
            message = (Message) ois.readObject();// socket的超时时间已经设置为20s
        } catch (IOException ignored) {
        }
        if (message != null) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, message);
        } else {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_RECEIVE_TIMEOUT, null);
        }
    } catch (ClassNotFoundException | IOException e) {
        return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
    }
}
```



## 3.9 获取聊天记录

​		数据库中聊天记录表中包含了发送时间，可以根据用户id和房间id获取一段时间内的聊天记录，封装为List列表返回给前台。本系统默认获取7天内的聊天记录。

```java
public List<Message> getIntervalMessage(Integer roomId, Integer userId, Integer intervalDays) {
	List<Message> messages = messageMapper.selectIntervalMessage(roomId, userId, intervalDays);
    for (Message message : messages) {
    	User sender = userService.getSenderByMessageId(message.getId());
        User receiver = userService.getReceiverByMessageId(message.getId());
        message.setSender(sender);
        message.setReceiver(receiver);
        com.example.entity.File file = fileService.getFileByMessageId(message.getId());
        message.setFile(file);
    }
    return messages;
}
```



## 3.10 获取房间内用户

​		前台每隔一段时间向后台请求更新活跃时间、获取房间内活跃用户、获取房间内不活跃用户三个请求，后台通过数据库用户表更新当前用户的最后活跃时间，获取房间中活跃的用户和不活跃用户，分别封装为List列表返回给前台，前台根据用户是否活跃设置显示样式。本系统默认10s更新一次最后活跃时间，所以10s内没有更新即为不活跃用户。

```JAVA
@ResponseBody
@RequestMapping("/update/recentActiveTime.json")
public ResultEntity<Object> updateRecentActiveTime(@RequestParam("userId") Integer userId) {
	userService.updateRecentActiveTime(userId);
	return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
}

@ResponseBody
@RequestMapping("/get/activeList.json")
public ResultEntity<List<User>> getActiveList(@RequestParam("roomId") Integer roomId) {
	List<User> users = userService.getUsersByRoomIdActive(roomId, CustomConstant.activeTime);
    return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, users);
}

@ResponseBody
@RequestMapping("/get/notActiveList.json")
public ResultEntity<List<User>> getNotActiveList(@RequestParam("roomId") Integer roomId) {
	List<User> users = userService.getUsersByRoomIdNotActive(roomId, CustomConstant.activeTime);
	return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, users);
}
```



## 3.11 关闭房间

​		房主拥有关闭房间的权利。当房主点击关闭房间时，房间将不能发送消息，点击开启房间后即可恢复。具体实现是修改数据库中该房间的enable字段。



## 3.12 退出房间

​		用户点击浏览器的返回或是聊天页面的退出房间按钮后，会显示退出房间确认模态框，当用户点击确认后，前台将用户信息和房间号发送给后台，后台操作数据库中房间和用户的关联表，移除该用户。



# 第4章 系统测试

## 4.1 注册并登录

​		注册页面如图4.1-1所示![Image text](./productShow/images/注册.jpg)

<div align = "center">图 4.1-1</div>

​		登录页面如图4.1-2所示

![Image text](./productShow/images/登录.jpg)

<div align = "center">图 4.1-2</div>

登录成功后会跳转到主页面。如图4.1-3所示。

![Image text](./productShow/images/主页.jpg)

<div align = "center">图 4.1-3</div>



## 4.2 创建和加入房间

​		点击菜单键弹出选项，点击创建房间后如图4.2-1所示，输入房间名和密码。

![Image text](./productShow/images/创建聊天室.jpg)	

<div align = "center">图 4.2-1</div>

​		创建成功后如图4.2-2所示。

​		![Image text](./productShow/images/创建成功.jpg)

<div align = "center">图 4.2-2</div>

​		打开另外一个浏览器，登录后加入到刚才创建的房间。如图4.2-3所示。

![Image text](./productShow/images/加入聊天室.jpg)

<div align = "center">图 4.2-3</div>

​		点击进入后，跳转到聊天界面，如图4.2-4所示，此时第一个浏览器显示有用户加入了房间的消息。如图4.2-5所示。

![Image text](./productShow/images/加入成功.jpg)

<div align = "center">图 4.2-4</div>

![Image text](./productShow/images/有用户加入.jpg)

<div align = "center">图 4.2-5</div>



## 4.3 发送和接收消息

​		在4.2的基础上，我们再加入了一个成员，此时房间有三名成员。为了体现房间的独立性，我们再创建一个房间。然后我们在4.2创建的房间中发送消息。如图4.3-1、4.3-2、4.3-3所示，三名成员均收到了该消息。而新创建的房间则接收不到该消息。如图4.3-4所示。

![Image text](./productShow/images/img4.3-1.jpg)

<div align = "center">图 4.3-1</div>

![Image text](./productShow/images/img4.3-2.jpg)

<div align = "center">图 4.3-2</div>

![Image text](./productShow/images/img4.3-3.jpg)

<div align = "center">图 4.3-3</div>

![Image text](./productShow/images/img4.3-4.jpg)

<div align = "center">图 4.3-4</div>



## 4.4 发送和接收图片

​		发送图片后，其他成员同样可以接收到，图片的比例也没有发生改变，如图4.4-1、4.4-2、4.4-3所示。

![Image text](./productShow/images/img4.4-1.jpg)

<div align = "center">图 4.4-1</div>

![Image text](./productShow/images/img4.4-2.jpg) 

<div align = "center">图 4.4-2</div>

![Image text](./productShow/images/img4.4-3.jpg)

<div align = "center">图 4.4-3</div>



## 4.5 文件的发送和接收

​		上传文件成功后，其他成员能收到带有发送者、文件名和文件大小的信息，并在下方有一个下载按钮，如图4.5-1所示。点击下载后，浏览器便会弹出下载框自动下载。如图4.5-2所示。

![Image text](./productShow/images/img4.5-1.jpg)

<div align = "center">图 4.5-1</div>

![Image text](./productShow/images/img4.5-2.jpg)

<div align = "center">图 4.5-2</div>



## 4.6 私聊

​		将鼠标悬浮在房间内活跃用户上会显示私聊按钮，如图4.6-1所示。

![Image text](./productShow/images/img4.6-1.jpg)

<div align = "center">图 4.6-1</div>

​		点击私聊后，发送消息只有对方能收到，如图4.6-2、4.6-3、4.6-4所示。

![Image text](./productShow/images/img4.6-2.jpg)

<div align = "center">图 4.6-2</div>

![Image text](./productShow/images/img4.6-3.jpg)

<div align = "center">图 4.6-3</div>

![Image text](./productShow/images/img4.6-4.jpg)

<div align = "center">图 4.6-4</div>



## 4.7 关闭房间

​		房主关闭房间后，成员们将无法发送消息，如图4.7所示。

![Image text](./productShow/images/img4.7.jpg)

<div align = "center">图 4.7</div>



## 4.8 退出房间

​		用户点击退出房间后，将回到主页，同时房间中的其他成员显示提示信息，如图4.8所示。

![Image text](./productShow/images/img4.8.jpg)

<div align = "center">图 4.8</div>



# 结论

​		本文主要介绍web聊天室的功能，对系统设计和系统实现进行分析，最后对整个系统进行运行及测试。具体完成了以下功能:

​		1）用户的注册和登录；		

​		2）房间的创建和加入；

​		3）同一房间的群聊和私聊功能；

​		4）文件和图片的上传和下载；

​		5）用Ajax实现消息实时更新；

​		6）房间的关闭和退出。

​		虽然该系统实现了基本的群聊功能，但还是有很多方面可以做到更细致更人性化，适用范围更广，比如：

​		1）可以增加用户个人信息管理的功能。

​		2）由于重新登录需要获取很多聊天记录，可以将聊天记录缓存到Redis中，减小MySQL的压力。

​		3）完善用户头像和房间头像，在聊天界面能够更直观地显示发送者。

​		4）完成公告功能。等等。

​		我在设计这个系统时恰好刚学习了Java web开发的相关内容，心里想能不能将它和网络编程结合起来开发一个项目，抱着练练手的心态开始编写这个系统。在这个过程中充满了酸甜苦辣。当一个问题想了好久也无法解决时，我感到沮丧与无助。但当经过努力解决了一个程序上的难题时，我也获得了小小的成就感。虽然在开发中遇到了很多困难，但系统最终还是顺利完成了。不过这个系统还有很多不足的地方，我将在以后的时间好好完善它，以求能达到一个更加高的水平，同时也能从中获得更多宝贵的经验。

​		最后，对指导老师和所有帮助我的同学表示忠心的感谢！

 

# 参考文献

​		[1]  方巍.Java EE架构设计与开发实践.第一版.清华大学出版社,2017:49-155

​		[2]  唐四薪.TCP/IP网络编程项目式教程.第一版.清华大学出版社,2019:46-175

​		[3]  孤剑.Web通信之长连接、长轮询（long polling）.博客园,2014 https://www.cnblogs.com/AloneSword/p/3517463.html