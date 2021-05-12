<%--
  Created by IntelliJ IDEA.
  User: Gerrit
  Date: 2021/4/10
  Time: 14:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>聊天室</title>
    <base href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="css/emoji-sprites.css">
    <script type="text/javascript" src="jquery/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="layer/layer.js"></script>
    <style type="text/css">
        .talk_con {
            width: 600px;
            height: 668px;
            /*border: 1px solid #666;*/
            margin: 0 0 0 270px;
            background: #f9f9f9;
        }

        .talk_show {
            width: 580px;
            height: 420px;
            border: 1px #666;
            background: #fff;
            margin: 10px auto 0;
            overflow: auto;
        }

        .talk_input {
            width: 580px;
            margin: 10px auto 0;
        }

        .uTalk {
            margin: 10px;
        }

        .uTalk span {
            overflow-wrap: break-word;
            display: inline-block;
            background: #0181cc;
            border-radius: 10px;
            color: #fff;
            padding: 5px 10px;
        }

        .iTalk {
            margin: 0 10px 10px 10px;
            text-align: right;
        }

        .iTalk span {
            overflow-wrap: break-word;
            display: inline-block;
            background: #ef8201;
            border-radius: 10px;
            color: #fff;
            padding: 5px 10px;
        }

        .roomItem {
            height: 60px;
        }

        .roomItem a {
            height: 50px;
        }

        .redRoundWithNumber {
            width: 20px;
            height: 20px;
            background-color: #F00;
            border-radius: 25px;
            float: right;
            margin-top: 10px;
        }

        .redRoundWithNumber span {
            height: 20px;
            line-height: 20px;
            display: block;
            color: #FFF;
            text-align: center;
        }
    </style>
</head>
<body>
<div class="panel panel-default" style="width: 882px;margin-left: 370px;margin-top: 30px;padding: 5px">
    <div class="bs-example" style="float: left;width: 260px;height: 668px;margin-bottom: 0">
        <ul id="roomList" class="nav nav-pills nav-stacked nav-pills-stacked-example">
            <li role="presentation">
                <div>
                    <div style="float: left;">
                        <img src="images/bg.jpg" alt="头像" height="50px" width="50px">
                        &nbsp;&nbsp;用户名: <span id="username"><security:authentication
                            property="principal.originalUser.username"/></span><br/>
                        <input id="userId" type="hidden"
                               value=<security:authentication property="principal.originalUser.id"/>>
                    </div>
                    <br/><br/><br/>
                </div>
            </li>
            <li role="presentation" class="active"><a href="javascript:createChatRoomShow();">创建房间</a></li>
            <li role="presentation"><a href="javascript:joinChatRoomShow();">加入房间</a></li>
            已加入的房间
        </ul>
    </div>
    <div class="panel panel-default talk_con">
        <div class="panel-heading" style="height: 70px">
            <div style="float: left;margin-top: 3px">
                <h3 class="panel-title" style="font-size: 25px">
                    <span id="roomName"></span>
                </h3>
                <h3 class="panel-title" style="font-size: 15px">
                    id: <span id="roomId">0</span>
                </h3>
            </div>
            <div style="float: right;margin-top: 1px">
                <button id="exitBtn" type="button" class="btn btn-primary btn-lg active" style="margin-left: 20px">
                    退出房间
                </button>
            </div>
        </div>
        <div id="messageShow">
        </div>
        <div class="input-group talk_input">
            <div class="btn-group" role="group" aria-label="..." style="margin-bottom: 8px;margin-right: 8px">
                <div class="dropup" style="float: left">
                    <button class="btn btn-default dropdown-toggle" type="button" id="expressionBtn"
                            data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" style="height: 39px">
                        <i class="glyphicon glyphicon-star-empty"></i>
                    </button>
                    <div class="dropdown-menu" id="expressionMenu" aria-labelledby="expressionBtn" style="padding: 0">
                        <%--导入表情--%>
                        <%@include file="/WEB-INF/pages/emoji.jsp" %>
                    </div>
                </div>
                <button class="btn btn-default" type="button" id="fontSizeMenu"
                        data-toggle="dropdown" aria-haspopup="true" aria-expanded="true" style="height: 39px">
                    <i class="glyphicon glyphicon-text-size"></i>
                </button>
                <ul class="dropdown-menu" aria-labelledby="fontSizeMenu">
                    <li><a onclick="setFontSize(15)">15</a></li>
                    <li><a onclick="setFontSize(30)">30</a></li>
                    <li><a onclick="setFontSize(45)">45</a></li>
                </ul>
                <button id="setBoldBtn" type="button" class="btn btn-default" style="height: 39px;margin-left: -1px"><i
                        class="glyphicon glyphicon-bold"></i></button>
                <button id="setItalicBtn" type="button" class="btn btn-default" style="height: 39px"><i
                        class="glyphicon glyphicon-italic"></i></button>
                <button type="button" id="pictureBtn" class="btn btn-default" style="height: 39px"><i
                        class="glyphicon glyphicon-picture"></i></button>
                <input type="file" name="file" id="inputPicture" onchange="pictureUpload()"
                       style="visibility: hidden;opacity:0;width:40px;height:100%;position:absolute;top:0;left:173px"
                       accept="image/gif,image/jpeg,image/jpg,image/png,image/svg">
                <button type="button" id="clearBtn" class="btn btn-default" style="height: 39px;margin-left: -1px"><i
                        class="glyphicon glyphicon-trash"></i></button>
            </div>
            <div class="btn-group" role="group" aria-label="..." style="margin-bottom: 8px;">
                <input id="file" type="file" class="btn btn-default" style="width: 236px">
                <input id="uploadFile" type="button" value="上传文件" class="btn btn-default" style="height: 39px">
            </div>
            <div class="input-group">
                <textarea id="msg" class="form-control" placeholder="请输入文本..."
                          style="resize: none;height: 100px;font-size: 15px"></textarea>
                <span class="input-group-btn">
                    <button id="sendBtn" class="btn btn-success" type="button"
                            style="width: 85px;height: 100px">发送</button>
                </span>
            </div>
        </div>
    </div>
</div>
</body>

<%@ include file="/WEB-INF/modal/join-chatRoom.jsp" %>
<%@ include file="/WEB-INF/modal/create-chatRoom.jsp" %>
<%@ include file="/WEB-INF/modal/exit-confirm.jsp" %>
<script src="js/chat.js" type="text/javascript"></script>
<script>
    // 监听后退
    $(document).ready(function () {
        if (window.history && window.history.pushState) {
            $(window).on('popstate', function () {
                window.history.pushState('forward', null, './to/main.html');
                window.history.forward(1);
                $("#exitConfirmModal").modal("show");
            });
        }
        window.history.pushState('forward', null, './to/main.html'); //在IE中必须得有这两行
        window.history.forward(1);
    });

    // 页面刷新
    window.onbeforeunload = function (event) {
    };

    // 页面关闭
    $(window).unload(function () {
    });
</script>
</html>