<%--
  sessionIdd by IntelliJ IDEA.
  User: Gerrit
  Date: 2021/4/9
  Time: 22:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <title>主页</title>
    <base href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
    <script type="text/javascript" src="jquery/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="layer/layer.js"></script>
</head>
<body>
<div>
    <button id="createBtn" type="button" class="btn btn-primary btn-lg btn-block"
            style="display:block;margin:150px auto 0;width: 200px;height: 100px;font-size: 27px">
        创建聊天室
    </button>
    <button id="joinBtn" type="button" class="btn btn-default btn-lg btn-block"
            style="display:block;margin:30px auto;width: 200px;height: 100px;font-size: 27px">
        加入聊天室
    </button>
    <div id="tip" class="alert alert-info"
         style="display:block;margin:30px auto;width: 240px;height: 50px;font-size: 18px;padding-top: 13px;visibility: hidden"
         role="alert">
    </div>
</div>
</body>

<%@ include file="WEB-INF/modal/join-chatRoom.jsp" %>
<script type="text/javascript">
    $(function () {
        // 创建聊天室
        $("#createBtn").click(function () {
            $.ajax({
                url: "chatRoom/create/chatRoom.json",
                type: "post",
                dataType: "json",
                success: function (response) {
                    var result = response.result;
                    if (result === "SUCCESS") {
                        toChatRoom(response.data)
                    } else if (result === "FAILED") {
                        layer.msg("创建失败! " + response.message);
                    }
                },
                error: function (response) {
                    layer.msg("创建失败! " + response.status + " " + response.statusText)
                }
            })
        })

        // 加入聊天室
        $("#joinBtn").click(function () {
            $("#joinChatRoomModal").modal("show");
        })

        // 加入聊天室确认按钮
        $("#joinConfirmBtn").click(function () {
            var port = $("#roomPortInput").val();
            $("#joinRoomNameInput").val("");
            $("#joinChatRoomModal").modal("hide");
            if (port === "${sessionScope.port}") {
                $(location).attr("href", "to/chat.html");
            } else {
                toChatRoom(port)
            }
        })
    })

    function toChatRoom(port) {
        $.ajax({
            url: "chatRoom/join/chatRoom.json",
            type: "post",
            data: {
                "port": port
            },
            dataType: "json",
            success: function (response) {
                var result = response.result;
                if (result === "SUCCESS") {
                    $(location).attr("href", "to/chat.html");
                } else if (result === "FAILED") {
                    layer.msg("加入失败! " + response.message);
                }
            },
            error: function (response) {
                layer.msg("加入失败! " + response.status + " " + response.statusText)
            }
        })
    }

    window.setInterval(function () {
        if (${!empty sessionScope.port}) {
            $("#tip").css("visibility", "visible");
            $("#tip").empty();
            $("#tip").append('上次进入的聊天室: ' + "${sessionScope.port}");
        } else {
            $("#tip").css("visibility", "hidden");
        }
    }, 1000);
</script>
</html>
