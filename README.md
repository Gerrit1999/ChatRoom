# ChatRoom

# 第1章 系统介绍

## 1.1 概述

​		随着网络的大幅度普及，以及网络技术快速发展，人们通过网络进行交流的方式发生着巨大的变化，人们不再拘泥于移动电话的通话模式，越来越多的人通过网络聊天工具进行交流，网络聊天室便是非常典型的聊天工具之一。聊天室系统的即时交流方式满足了网络中人们同时与多人进行聊天交流的需要,使得较多的人在同一个聊天页面进行交流变得方便，简单。		

​		ChatRoom聊天室是采用B/S结构进行开发的网页聊天室项目，使用socket技术来进行通信，主要功能如下：

​		首先，该系统可以创建多个房间，创建的房间都有一个唯一的房间号，用户能够输入房间号来进入对应的房间，进入房间后也可以退出。每个用户能够创建和加入多个房间。房间中的其他人都能获取到每个用户进入房间和退出房间的消息。每个房间可以加入多个用户，房间之间相互独立，互不干扰。用户发送信息到房间后，房间内的所有人均能立即收到消息。

​		其次，用户可以发送多种类型的消息，例如文本、表情、图片、文件等。文本和表情消息可以选择字体大小、字体风格和字体粗细，图片消息能根据图片的长宽比例在聊天框中显示，文件消息发送后保存到服务器，房间中的其他成员可以接收到发送文件的消息，选择是否下载。

## 1.2 系统的部署

​		使用Maven将项目源代码打包为war放到tomcat的wabapps目录下，然后打开tomcat/bin/startup.bat启动服务器，启动完成后浏览器访问 http://localhost:8080/ChatRoom 即可。

## 1.3 功能模块图

功能模块图见图1.3。

![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img1.3.jpg)

<div align = "center">图 1.3 功能模块图</div>



# 第2章 系统设计

## 2.1 系统运行环境

​		系统选择使用Java作为后台开发语言，jsp作为前台开发页面，使用JavaScript以及Jquery作为脚本语言，使用IDEA作为开发工具，使用Maven来管理项目，使用SpringMVC框架来负责前后端数据的交互，使用Tomcat来搭建服务器。客户端使用Google Chrome和Microsoft Edge作为测试。

## 2.2 前端设计

​		前端部分使用两个页面，主页负责创建房间和加入房间的功能，另一个页面则是房间内的聊天功能。当在主页点击创建房间或加入房间后，向后台获取房间信息，并重定向到聊天页面。聊天页面需将用户发送的消息传给后台，由后台进行数据的处理和转发。还需要监听后台传过来的房间中其他人发送的数据，并根据数据内容在页面上显示。

## 2.3 后端设计

​		后端部分，每个用户要实现创建多个房间，加入多个房间，房间相互独立，退出房间这些功能，需要创建一个ChatRoom类，用来保存房间的socket和该房间内的所有连接的socket，提供新增socket，删除socket，接收消息并转发的方法。当用户加入房间时，将用户的socket放入对应的房间，退出房间后删除该socket。当用户发送消息时，先找到该用户发送消息时所在的房间，然后将消息转发给该房间中的所有成员。要实现不同类型消息的传输，需要创建一个Message类来保存不同数据类型的内容，之后使用Message来作为发送和接收的对象。

​		在与前端的交互中，为了数据格式的统一，需要创建一个ResultEntity结果类，将操作是否成功、操作返回的数据、出错原因等内容封装后再发送给前端。

​		此外，为了便于用户发送接收消息时获取所在房间和其socket，但该系统没有使用到数据库，因此使用map来保存所有创建的房间和用户的socket。保存房间的map封装在ChatRoomMap类，用房间号作为Key。保存用户socket的map封装在SocketMap类，用浏览器的sessionId和用户socket本地端口号作为Key。

## 2.4 架构图

![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img2.1.jpg)

<div align = "center">图 2.1 架构图</div>



# 第3章 系统实现

## 3.1 创建房间

​		用户点击创建房间按钮后，前台使用ajax向后台发送请求，后台通过serverSocket创建一个服务器套接字，绑定服务器的ip地址，并获取自动生成的端口号，然后将服务器套接字封装到ChatRoom对象中，开启该房间的线程，用来监听客户端的连接。然后将该房间保存到ChatRoomMap中。将房间号存入session域中，前台跳转到聊天界面时就可以从session中获取到房间号从而显示在聊天框中。如图3.1所示。

创建房间成功后，前端将调用加入房间的函数，让当前用户进入房间。

![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img3.1.png)

<div align = "center">图 3.1</div>



## 3.2 加入房间

​		用户点击加入房间按钮后，页面会弹出一个模态框，用户输入房间号方可加入房间，点击进入后，前台会将session域中的房间号同用户输入的房间号进行比较，若相同，则直接跳转到聊天界面，否则调用进入房间的函数。

​		进入房间的函数也是通过ajax发送请求给后台，后台根据房间号到ChatRoomMap中查找，如果查找到了就获取用户的sessionId，用来标识客户端，然后创建socket套接字连接到该房间，从中获取ip和端口号，并和房间号一同保存到session域中。由于后面接收消息的需要，这里给socket设置一个超时时间20s。接着将该socket保存到SocketMap中。使用ObjectOutputStream输出流的writeObject方法，发送用Message对象封装的“ip地址:端口号已进入房间”消息给房间服务器，房间服务器会将该消息转发出去。后台操作成功后，前台跳转到聊天页面。

## 3.3 发送消息

​		用户在文本框中输入消息点击发送后，前端获取文本框的内容、字体大小、字体风格、字体粗细等参数，通过ajax发送给后端，后端使用@requestBody注解将这些参数封装成Message对象，再根据前台传来用户信息来使用对应的socket发送数据给对应的房间服务器。

## 3.4 发送文件

​		用户上传文件后，前端将文件放入formData中作为ajax的data参数的数据。后台使用MultipartFile来接收文件，使用前需要将MultipartFile放入SpringMVC的IOC容器中，并设置最大上传文件大小，本系统设置为200MB。通过其getOriginalFilename方法获取文件名，为了防止同名，需要拼接上一个随机的uuid，设置保存路径为项目根目录\用户IP\用户端口号\文件名，使用transferTo将文件保存到服务器。初始化一个File对象用来保存文件路径，接着将File对象和文件大小封装在Message对象中发送给房间服务器，接收方通过File对象就可以获取文件的路径，选择接收后，后台通过文件路径下载文件。

## 3.5 下载文件

​		后台收到下载文件请求后，需要设置response的消息头，通过response对象获取OutputStream流，通过File对象获取获取要下载的文件输入流，将文件输入流循环写入到缓冲区，再将缓冲区的数据输出到客户端浏览器。此时聊天页面就会开始下载了。

## 3.6 发送图片

​		发送图片和发送文件类似，但需要创建一个Image类来保存图片的长宽比，便于前端的显示。之后将Image对象保存到Message对象中一起发送即可。

## 3.7 发送表情

​		这里使用emoji表情，因为emoji表情可以直接以文本方式显示而不是图片，可以同文本一起发送。具体实现是通过雪碧图把一张含有多个表情的大图分割成一个个表情，这样每个表情都能设置为一个css的类，用许多个div通过类选择器来指定样式，然后给每个表情绑定点击事件，在文本框中拼接上对应的表情即可。

## 3.8 转发消息

​		在3.1中，刚创建房间时会开启一个线程来监听人员加入。每次有用户加入房间时，会将该用户的socket加入到sockets集合中，再开启一个线程用来与该用户进行通信。当收到用户发来的消息时，会从集合遍历房间中所有用户的socket，使用ObjectOutputStream输出流的writeObject方法来转发给对应的用户。

## 3.9 接收消息

​		由于web服务器不能主动向客户端推送消息，这里使用长轮询的方式。客户端从服务器请求数据，当服务器没有可以立即返回给客户端的数据时，不会立刻返回一个空结果，而是保持这个请求等待数据到来，之后将数据作为结果返回给客户端。收到消息后根据消息内容分别进行处理。长轮询的优点是在无消息的情况下不会频繁的请求，耗费资源小，但服务器保持住连接会消耗资源。

​		具体实现步骤是前台定义一个recvMsg函数用来接收消息，同样使用ajax向后台请求数据，不过这里需要设置一个超时时间30s，这个超时时间需大于后台的超时时间，在3.2中我们设置了socket的超时时间为20s，所以后台有20s的时间来等待数据到来，收到消息或是超时后，后台都会通知前台，如果收到消息前台显示。前台只要收到后台的通知，不管是收到消息还是超时，就递归调用recvMsg函数进行下一轮的请求。

​		收到消息后，前端根据消息的来源和消息的内容进行处理，如果消息来源是用户自己，则显示在右侧，否则显示在左侧。如果是图片则使用img标签，由于图片保存在服务器上，将src设置为图片路径即可。如果是其他人发送的文件，在消息下方添加一个下载按钮用来下载文件。

## 3.10 退出房间

​		用户点击浏览器的返回或是聊天页面的退出房间按钮后，会显示退出房间确认模态框，当用户点击确认后，前台将用户信息和房间号发送给后台，后台通过用户信息获取其socket，并将该socket从房间中和socketMap中移除，并发送消息通知房间中的其他人。特别地，如果房间中没有socket连接，后端将关闭该房间的线程，从ChatRoomMap中移除，回收资源。



# 第4章 系统测试

## 4.1 创建并加入房间

​		创建房间后会自动进入聊天室，并发送提示信息到群聊。如图4.1-1所示。

![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.1-1.png)

<div align = "center">图 4.1-1</div>



​		打开另外一个浏览器，进入到刚才创建的房间。如图4.1-2所示。

![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.1-2.png)

<div align = "center">图 4.1-2</div>



​		点击进入后，跳转到聊天界面，如图4.1-3所示，此时第一个浏览器显示有用户加入了房间的消息。如图4.1-4所示。

![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.1-3.png)

<div align = "center">图 4.1-3</div>



![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.1-4.png)

<div align = "center">图 4.1-4</div>



## 4.2 发送和接收消息

​		在4.1的基础上，我们再加入了一个成员，此时房间有三名成员。为了体现房间的独立性，我们再创建一个房间。然后我们在4.1创建的房间中发送消息。如图4.2-1、4.2-2、4.2-3所示，三名成员均收到了该消息。而新创建的房间则接收不到该消息。如图4.2-4所示。

![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.2-1.png)

<div align = "center">图 4.2-1</div>

 

![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.2-2.png)

<div align = "center">图 4.2-2</div>



![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.2-3.png)

<div align = "center">图 4.2-3</div>



![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.2-4.png)

<div align = "center">图 4.2-4</div>



## 4.3 发送和接收图片

​		发送图片后，其他成员同样可以接收到，图片的比例也没有发生改变，如图4.3-1、4.3-2、4.3-3所示。

![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.3-1.png)

<div align = "center">图 4.3-1</div>



![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.3-2.png) 

<div align = "center">图 4.3-2</div>



![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.3-3.png)

<div align = "center">图 4.3-3</div>



## 4.4 文件的发送和接收

​		上传文件成功后，其他成员能收到带有发送者、文件名和文件大小的信息，并在下方有一个下载按钮，如图4.4-1所示。点击下载后，浏览器便会弹出下载框自动下载。如图4.4-2所示。

![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.4-1.png)

<div align = "center">图 4.4-1</div>

 

![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.4-2.png)

<div align = "center">图 4.4-2</div>



## 4.5 退出房间

​		用户点击退出房间后，会显示模态框，如图4.5-1所示。点击确定后将回到主页，同时房间中的其他成员显示提示信息，如图4.5-2所示。

![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.5-1.png)

<div align = "center">图 4.5-1</div>



![Image text](https://gitee.com/Gerrit1999/ChatRoom/raw/master/img/productShow/img4.5-2.png)

<div align = "center">图 4.5-2</div>

 

# 结论

​		本文主要介绍web聊天室的功能，对系统设计和系统实现进行分析，最后对整个系统进行运行及测试。具体完成了以下功能:

​		1）房间的创建和加入；

​		2）同一房间的群聊功能；

​		3）文件和图片的上传和下载；

​		4）用Ajax实现消息实时更新。

​		虽然该系统实现了基本的群聊功能，但还是有很多方面可以做到更细致更人性化，适用范围更广，比如：

​		1）可以增加注册登录的功能，来代替用IP加端口号的方式显示用户名，这对用户来说会更加直观，同时也保证系统的安全性。

​		2）在（1）的基础上实现私聊功能会更加方便直观。

​		3）使用数据库保存房间信息、用户信息，更加高效、可靠，便于管理。

​		4）在（3）的基础上就可以设计一个管理员系统来对房间和用户进行管理。

​		5）由于刷新网页时聊天记录也会被清空，所以可以将聊天信息保存到本地，页面加载时从本地获取聊天记录，用户体验会更好一些。等等。

​		我在设计这个系统时恰好刚学习了Java web开发的相关内容，心里想能不能将它和网络编程结合起来开发一个项目，抱着练练手的心态开始编写这个系统。在这个过程中充满了酸甜苦辣。当一个问题想了好久也无法解决时，我感到沮丧与无助。但当经过努力解决了一个程序上的难题时，我也获得了小小的成就感。虽然在开发中遇到了很多困难，但系统最终还是顺利完成了。不过这个系统还有很多不足的地方，我将在以后的时间好好完善它，以求能达到一个更加高的水平，同时也能从中获得更多宝贵的经验。

​		最后，对指导老师和所有帮助我的同学表示忠心的感谢！

 

# 参考文献

​		[1]  方巍.Java EE架构设计与开发实践.第一版.清华大学出版社,2017:49-155

​		[2]  唐四薪.TCP/IP网络编程项目式教程.第一版.清华大学出版社,2019:46-175

​		[3]  孤剑.Web通信之长连接、长轮询（long polling）.博客园,2014 https://www.cnblogs.com/AloneSword/p/3517463.html