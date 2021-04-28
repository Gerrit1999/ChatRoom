<%--
  Created by IntelliJ IDEA.
  User: Gerrit
  Date: 2021/4/10
  Time: 14:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html lang="en">
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
            margin: 30px auto auto 450px;
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
    </style>
</head>
<body>
<div>
    <%--<div class="panel panel-default" style="float: left;margin-left: 350px;height: 670px">
        <div class="panel-heading" style="text-align: center">在线列表</div>
        <div class="list-group" style="overflow:auto;height: 626px">
            <button href="#" class="list-group-item" style="width: 200px">Dapibus ac facilisis in</button>
        </div>
    </div>--%>
    <div class="panel panel-default talk_con">
        <div class="panel-heading" style="height: 70px">
            <div style="float: left;margin-top: 3px"><h3 class="panel-title" style="font-size: 25px">
                房间号: ${sessionScope.port}</h3>
                我的地址: ${sessionScope.localAddress}:${sessionScope.localPort}</h4>
            </div>
            <div style="float: right;margin-top: 1px">
                <button id="exitBtn" type="button" class="btn btn-primary btn-lg active" style="margin-left: 20px">
                    退出房间
                </button>
            </div>
        </div>
        <div class="panel-body talk_show" id="words"></div>
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

<%@ include file="/WEB-INF/modal/exit-confirm.jsp" %>
<script>
    /*$(document).keyup(function (event) {
        if (event.keyCode === 13) {// 按下回车
            sendMsg();
        }
    });*/

    $(function () {
        // 发送点击事件
        $("#sendBtn").click(function () {
            const image = $("#inputPicture").val();
            if (image === "") {
                sendMsg();
            } else {// 发送图片
                const formData = new FormData();
                formData.append("image", $("#inputPicture")[0].files[0]);
                formData.append("localAddress", "${sessionScope.localAddress}");
                formData.append("localPort", ${sessionScope.localPort});
                $.ajax({
                    type: "post",
                    url: "chat/send/image.json",
                    async: false,
                    data: formData,
                    cache: false,
                    processData: false,
                    contentType: false,
                    mimeType: "multipart/form-data",
                    success: function () {
                        layer.msg("发送成功!");
                    },
                    error: function (response) {
                        layer.msg("发送失败! " + response.status + " " + response.statusText);

                    }
                })
                $("#msg").val("");
                $("#inputPicture").val("");
            }
        });

        // 退出房间点击事件
        $("#exitBtn").click(function () {
            $("#exitConfirmModal").modal("show");
        })

        // 退出房间确认
        $("#exitConfirmBtn").click(function () {
            var port = ${sessionScope.port};
            var localAddress = "${sessionScope.localAddress}";
            var localPort = ${sessionScope.localPort};
            $.ajax({
                url: "chatRoom/do/exit.json",
                type: "post",
                data: {
                    "port": port,
                    "localAddress": localAddress,
                    "localPort": localPort
                },
                dataType: "json",
                success: function (response) {
                    var result = response.result;
                    if (result === "SUCCESS") {
                        // 回到主页
                        $(location).attr("href", "");
                    } else if (result === "FAILED") {
                        layer.msg("退出失败! " + response.message);
                    }
                },
                error: function (response) {
                    layer.msg("退出失败! " + response.status + " " + response.statusText);
                }
            })
        })

        // 发送文件
        $("#uploadFile").click(function () {
            const formData = new FormData();
            formData.append("files", $("#file")[0].files[0]);
            formData.append("localAddress", "${sessionScope.localAddress}");
            formData.append("localPort", ${sessionScope.localPort});
            $.ajax({
                type: "post",
                url: "chat/upload/file.json",
                data: formData,
                cache: false,
                processData: false,
                contentType: false,
                mimeType: "multipart/form-data",
                success: function () {
                    layer.msg("发送成功!");
                },
                error: function (response) {
                    if (response.status === 0) {
                        layer.msg("发送失败! 文件大小不能大于200MB!");
                    } else {
                        layer.msg("发送失败! " + response.status + " " + response.statusText);
                    }
                }
            })
            $("#file").val("");
        })

        // 清屏
        $("#clearBtn").click(function () {
            $("#words").empty();
            layer.msg("清屏成功!");
        })

        // 点击图片按钮选择文件
        $("#pictureBtn").click(function () {
            $("#inputPicture").click();
        })

        // 设置加粗
        $("#setBoldBtn").click(function () {
            $(this).toggleClass("active");
            if ($(this).hasClass("active")) {
                $("#msg").css("font-weight", "bold");
            } else {
                $("#msg").css("font-weight", "normal");
            }
        })

        // 设置斜体
        $("#setItalicBtn").click(function () {
            $(this).toggleClass("active");
            if ($(this).hasClass("active")) {
                $("#msg").css("font-style", "italic");
            } else {
                $("#msg").css("font-style", "normal");
            }
        })

        // 页面加载成功后立即获取消息
        recvMsg();
    })

    // 发送文字信息
    function sendMsg() {
        const textarea = $("#msg");
        const msg = textarea.val();
        const fontSize = textarea.css("font-size");
        const fontWeight = textarea.css("font-weight");
        const fontStyle = textarea.css("font-style");
        const localAddress = "${sessionScope.localAddress}";
        const localPort = ${sessionScope.localPort};
        const data = JSON.stringify({
            "msg": msg,
            "fontSize": fontSize,
            "fontWeight": fontWeight,
            "fontStyle": fontStyle,
            "sourceAddress": localAddress,
            "sourcePort": localPort
        });
        $.ajax({
            url: "chat/send/message.json",
            type: "post",
            contentType: "application/json;charset=utf-8",
            data: data,
            dataType: "json",
            success: function (response) {
                var result = response.result;
                if (result === "SUCCESS") {
                    layer.msg("发送成功!");
                } else if (result === "FAILED") {
                    layer.msg("发送失败! " + response.message);
                }
            },
            error: function (response) {
                layer.msg("发送失败! " + response.status + " " + response.statusText);
            }
        })
        textarea.val("");
    }

    // 设置字体大小
    function setFontSize(fontSize) {
        $("#msg").css("font-size", fontSize);
    }

    // 选择图片后回显到input中
    function pictureUpload() {
        var image = $("#inputPicture").val();
        $("#msg").val(image);
    }

    /**
     *长轮询接收消息
     */
    function recvMsg() {
        const localAddress = "${sessionScope.localAddress}";
        var localPort = ${sessionScope.localPort};
        $.ajax({
            url: "chat/recv/message.json",
            type: "post",
            data: {
                "localPort": localPort
            },
            dataType: "json",
            timeout: 30000, // 超时时间要大于后台的超时时间
            success: function (response) {
                const result = response.result;
                if (result === "SUCCESS") {
                    const data = response.data;
                    const date = data.date;
                    let msg = data.msg;
                    const sourceAddress = data.sourceAddress;
                    const sourcePort = data.sourcePort;
                    if (sourceAddress === localAddress && sourcePort === localPort) {// 是我发的消息
                        $("#words").append('<div style="text-align: right;margin-bottom: 5px;"><span>' + date + '</span></div>')
                        if (data.image !== null) {// 是图片
                            let image = data.image;
                            $("#words").append('<div class="iTalk"><span><img width="' + 160 * image.proportion + '" height="160" src="' + image.contextPath + '"></span></div>')
                        } else {// 是文本
                            let re = new RegExp("\n", "g");// 匹配所有的\n, g表示全部global
                            msg = msg.replace(re, "<br/>");// html中解析换行用<br/>
                            $("#words").append('<div class="iTalk"><span style="font-size:' + data.fontSize + ';font-weight:' + data.fontWeight + ';font-style:' + data.fontStyle + '">' + msg + '</span></div>');
                        }
                    } else {// 是其他人发的消息
                        $("#words").append('<div style="text-align: left;margin-bottom: 5px;"><span>' + date + '  [ From: ' + sourceAddress + ':' + sourcePort + ' ]</span></div>')
                        const file = data.file;
                        if (file !== null) {// 是文件
                            $("#words").append('<div class="uTalk">' +
                                '<span>' + msg + '</span><br/>' +
                                '<span style="margin-top: 5px;">' + '<form action="chat/download/file.html" method="post" style="margin: 0">' +
                                '<input name="file" type="hidden" value="' + file + '">' +
                                '<input type="submit" value="下载" style="background: #0181cc;color: white;padding: 0;border: 0"></form>' +
                                '</span></div>');
                        } else if (data.image !== null) {// 是图片
                            let image = data.image;
                            $("#words").append('<div class="uTalk"><span><img width="' + 160 * image.proportion + '" height="160" src="' + image.contextPath + '"></span></div>')
                        } else {// 是文本
                            let re = new RegExp("\n", "g");// 匹配所有的\n, g表示全部global
                            msg = msg.replace(re, "<br/>");// html中解析换行用<br/>
                            $("#words").append('<div class="utalk"><span style="font-size:' + data.fontSize + ';font-weight:' + data.fontWeight + ';font-style:' + data.fontStyle + '">' + msg + '</span></div>');
                        }
                    }
                    // 自动滚动到底部
                    let div = document.getElementById('words');
                    div.scrollTop = div.scrollHeight;
                }
                recvMsg();
            },
            error: function () {
                recvMsg();
            }
        })
    }

    // 监听后退
    $(document).ready(function () {
        if (window.history && window.history.pushState) {
            $(window).on('popstate', function () {
                window.history.pushState('forward', null, './to/chat.html');
                window.history.forward(1);
                $("#exitConfirmModal").modal("show");
            });
        }
        window.history.pushState('forward', null, './to/chat.html'); //在IE中必须得有这两行
        window.history.forward(1);
    });

    // 监听页面关闭
    /*$(window).bind('beforeunload', function () {
        return false;
    });*/
</script>
</html>