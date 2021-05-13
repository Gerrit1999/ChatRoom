/*$(document).keyup(function (event) {
        if (event.keyCode === 13) {// 按下回车
            sendMsg();
        }
    });*/

// 退出登录
function logout() {
    $("#logoutConfirmModal").modal("show");
}

// 加入聊天室
function createChatRoomShow() {
    $("#createChatRoomModal").modal("show");
}

// 加入聊天室
function joinChatRoomShow() {
    $("#joinChatRoomModal").modal("show");
}

// 加入房间
function joinChatRoom(roomId, roomPassword) {
    const userId = $("#userId").val();
    $.ajax({
        url: "chatRoom/join/chatRoom.json",
        type: "post",
        data: {
            "userId": userId,
            "roomId": roomId,
            "password": roomPassword
        },
        dataType: "json",
        success: function (response) {
            const result = response.result;
            if (result === "SUCCESS") {
                layer.msg("加入成功!");
                const room = response.data;
                appendRoomItem(room);
                gotoChatRoom(room.id, room.name);
            } else if (result === "FAILED") {
                layer.msg("加入失败! " + response.message);
            }
        },
        error: function (response) {
            layer.msg("加入失败! " + response.status + " " + response.statusText)
        }
    })
}

// 添加到房间列表
function appendRoomItem(roomItem) {
    const href = "javascript:gotoChatRoom(" + roomItem.id + ",'" + roomItem.name + "')";
    $('#roomList').append(
        '<li role="presentation" class="roomItem" id="room_' + roomItem.id + '">' +
        '<span id="" class="badge" style="float: right;margin:10px 10px 0 0;display: none">0</span>' +
        '<a style="padding: 0" href=' + href + '>' +
        '<div style="float: left;">' +
        '<img src="images/bg.jpg" alt="' + roomItem.id + '" width="50px" height="50px"></div>' +
        '<div style="height: 50px;line-height: 50px;float:left">&nbsp;&nbsp;' + roomItem.name + '</div>' +
        '</a></li>'
    );
    $('#messageShow').append('<div class="panel-body talk_show" id="words_' + roomItem.id + '"' + ' style="display: none"></div>')
    // 获取未读信息数
    const userId = $("#userId").val();
    $.ajax({
        url: "chatRoom/get/unread.json",
        type: "post",
        async: false,
        data: {
            "roomId": roomItem.id,
            "userId": userId
        },
        dataType: "json",
        success: function (response) {
            const unread = response.data;
            if (unread > 0) {
                const span = $('#room_' + roomItem.id).children('span');
                span.show();
                if (unread > 99) {
                    span.text('99+');
                } else {
                    span.text(unread);
                }
            }
        },
        error: function (response) {
            layer.msg(response.status + " " + response.statusText)
        }
    })

    // 接收消息
    recvMsg(roomItem.id);
}

// 进入房间
function gotoChatRoom(roomId, roomName) {
    const beforeRoomId = $('#roomId').text();
    const userId = $("#userId").val();
    if (beforeRoomId !== 0) {
        $('#words_' + beforeRoomId).hide();
    }
    $('#words_' + roomId).show();

    // 清除未读信息
    const span = $('#room_' + roomId).children('span');
    if (span.text() != 0) {
        $.ajax({
            url: "chatRoom/update/unread.json",
            type: "post",
            data: {
                "userId": userId,
                "roomId": roomId,
                "unread": 0
            },
            dataType: "json",
            error: function (response) {
                layer.msg(response.status + " " + response.statusText)
            }
        })
    }
    span.text(0);
    span.hide();

    // 更新房间信息
    $('#roomId').text(roomId);
    $('#roomName').text(roomName);
    // 更新在线列表
    updateUserList(roomId);
}

// 获取已加入的房间
function getAllChatRooms() {
    const userId = $('#userId').val();
    $.ajax({
        url: "chatRoom/get/allChatRooms.json",
        type: "post",
        data: {
            "userId": userId,
        },
        dataType: "json",
        success: function (response) {
            const n = response.length;
            for (let i = 0; i < n; i++) {
                const roomItem = response[i];
                appendRoomItem(roomItem);
                getHistoryMsg(roomItem.id, userId);
                if (i === 0) {
                    gotoChatRoom(roomItem.id, roomItem.name);
                }
            }
        },
        error: function (response) {
            layer.msg("加入失败! " + response.status + " " + response.statusText)
        }
    })
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

// 发送文字信息
function sendMsg(message) {
    const textarea = $("#msg");
    const id = $('#userId').val();
    const username = $('#username').text();
    const roomId = $('#roomId').text();
    let fontSize = textarea.css("font-size");
    fontSize = fontSize.substr(0, fontSize.indexOf('px'));
    const fontWeight = textarea.css("font-weight");
    const fontStyle = textarea.css("font-style");
    const sender = {
        "id": id,
        "username": username
    }
    const data = JSON.stringify({
        "message": message,
        "sender": sender,
        "roomId": roomId,
        "fontSize": fontSize,
        "fontWeight": fontWeight,
        "fontStyle": fontStyle
    });
    $.ajax({
        url: "chat/send/message.json",
        type: "post",
        contentType: "application/json;charset=utf-8",
        data: data,
        dataType: "json",
        success: function (response) {
            const result = response.result;
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

// 添加消息
function appendMsg(roomId, userId, msg) {
    const date = msg.date;
    let message = msg.message;
    const sender = msg.sender;
    const file = msg.file;
    const words = $('#words_' + roomId);
    if (sender.id == userId) {// 是我发的消息
        words.append('<div style="text-align: right;margin-bottom: 5px;"><span>' + date + '</span></div>')
        if (file !== null && file.image !== null) {// 是图片
            const image = file.image;
            words.append('<div class="iTalk"><span><img width="' + 160 * image.proportion + '" height="160" src="' + image.url + '"></span></div>')
        } else {// 是文本
            let re = new RegExp("\n", "g");// 匹配所有的\n, g表示全部global
            message = message.replace(re, "<br/>");// html中解析换行用<br/>
            words.append('<div class="iTalk"><span style="font-size:' + msg.fontSize + ';font-weight:' + msg.fontWeight + ';font-style:' + msg.fontStyle + '">' + message + '</span></div>');
        }
    } else {// 是其他人发的消息
        words.append('<div style="text-align: left;margin-bottom: 5px;"><span>' + date + '  [ From: ' + sender.username + ' ]</span></div>')
        if (file == null) {// 是文本
            let re = new RegExp("\n", "g");// 匹配所有的\n, g表示全部global
            message = message.replace(re, "<br/>");// html中解析换行用<br/>
            words.append('<div class="utalk"><span style="font-size:' + msg.fontSize + ';font-weight:' + msg.fontWeight + ';font-style:' + msg.fontStyle + '">' + message + '</span></div>');
        } else if (file.image == null) {// 是文件
            words.append('<div class="uTalk">' +
                '<span>' + message + '</span><br/>' +
                '<span style="margin-top: 5px;">' + '<form action="chat/download/file.html" method="post" style="margin: 0">' +
                '<input name="path" type="hidden" value="' + file.path + '">' +
                '<input name="fileName" type="hidden" value="' + file.name + '">' +
                '<input type="submit" value="下载" style="background: #0181cc;color: white;padding: 0;border: 0"></form>' +
                '</span></div>');
        } else {// 是图片
            const image = file.image;
            words.append('<div class="uTalk"><span><img width="' + 160 * image.proportion + '" height="160" src="' + image.url + '"></span></div>')
        }
    }
    // 自动滚动到底部
    words[0].scrollTop = words[0].scrollHeight;
}

// 长轮询接收消息
function recvMsg(roomId) {
    const userId = $('#userId').val();
    $.ajax({
        url: "chat/recv/message.json",
        type: "post",
        data: {
            "userId": userId,
            "roomId": roomId
        },
        dataType: "json",
        timeout: 30000, // 超时时间要大于后台的超时时间
        success: function (response) {
            const result = response.result;
            if (result === "SUCCESS") {
                const msg = response.data;
                appendMsg(roomId, userId, msg);
                // 判断是否在当前房间页面
                const curRoomId = $('#roomId').text();
                if (roomId != curRoomId) {
                    // 更新未读红点
                    const span = $('#room_' + roomId).children('span');
                    span.show();
                    let unread = span.text();
                    if (unread++ < 100) {
                        // 保存未读消息
                        $.ajax({
                            url: "chatRoom/update/unread.json",
                            type: "post",
                            data: {
                                "userId": userId,
                                "roomId": roomId,
                                "unread": unread
                            },
                            dataType: "json",
                            error: function (response) {
                                layer.msg(response.status + " " + response.statusText)
                            }
                        })
                        span.text(unread > 99 ? '99+' : unread);
                    } else {
                        span.text('99+');
                    }
                }
            }
            recvMsg(roomId);
        },
        error: function (response) {
            layer.msg("接收消息失败! 请刷新页面重试 " + response.status + " " + response.statusText)
        }
    })
}

// 获取历史消息
function getHistoryMsg(roomId, userId) {
    $.ajax({
        url: "chat/get/history/message.json",
        type: "post",
        data: {
            "userId": userId,
            "roomId": roomId
        },
        dataType: "json",
        success: function (response) {
            const messages = response.data;
            for (let i = 0, n = messages.length; i < n; i++) {
                appendMsg(roomId, userId, messages[i]);
            }
        }
    })
}

// 更新在线日期
function updateRecentActiveTime() {
    const roomId = $('#roomId').text();
    if (roomId === 0) {
        return;
    }
    const userId = $('#userId').val();
    $.ajax({
        url: "user/update/recentActiveTime.json",
        type: "post",
        data: {
            "userId": userId
        },
        dataType: "json",
        success: function () {
        }
    })
}

// 更新在线列表
function updateUserList(roomId) {
    $.ajax({
        url: "user/get/activeList.json",
        type: "post",
        async: false,
        data: {
            "roomId": roomId
        },
        dataType: "json",
        success: function (response) {
            const result = response.result;
            if (result === "SUCCESS") {
                const userList = $('#userList');
                userList.empty();
                for (let i = 0, n = response.data.length; i < n; i++) {
                    const user = response.data[i];
                    userList.append(
                        '<li role="presentation">' +
                        '<span>' + user.username + ' 在线</span>' +
                        '</li>'
                    )
                }
            } else if (result === "FAILED") {
                layer.msg("更新房间内用户列表失败! " + response.message);
            }
        }
    })
    $.ajax({
        url: "user/get/notActiveList.json",
        type: "post",
        async: false,
        data: {
            "roomId": roomId
        },
        dataType: "json",
        success: function (response) {
            const result = response.result;
            if (result === "SUCCESS") {
                const userList = $('#userList');
                for (let i = 0, n = response.data.length; i < n; i++) {
                    const user = response.data[i];
                    userList.append(
                        '<li role="presentation">' +
                        '<span>' + user.username + ' 离线</span>' +
                        '</li>'
                    )
                }
            } else if (result === "FAILED") {
                layer.msg("更新房间内用户列表失败! " + response.message);
            }
        }
    })
}

$(function () {
    function update() {
        const roomId = $('#roomId').text();
        // 更新在线列表
        updateUserList(roomId);
        //每30s更新活跃时间
        updateRecentActiveTime();
    }
    update();
    setInterval(update, 10 * 1000);

// 退出登录确认
    $('#logoutConfirmBtn').click(function () {
        window.location.href = "./security/do/logout.html";
    })

// 创建聊天室确认按钮
    $("#createConfirmBtn").click(function () {
        const userId = $("#userId").val();
        const roomName = $("#setRoomName").val();
        const roomPassword = $("#setRoomPassword").val();
        $("#roomName").val("");
        $("#roomPassword").val("");
        $("#createChatRoomModal").modal("hide");
        const data = JSON.stringify({
            hostId: userId,
            name: roomName,
            password: roomPassword
        });
        $.ajax({
            url: "chatRoom/create/chatRoom.json",
            type: "post",
            contentType: "application/json;charset=utf-8",
            data: data,
            dataType: "json",
            success: function (response) {
                const result = response.result;
                if (result === "SUCCESS") {
                    joinChatRoom(response.data, roomPassword)
                } else if (result === "FAILED") {
                    layer.msg("创建失败! " + response.message);
                }
            },
            error: function (response) {
                layer.msg("创建失败! " + response.status + " " + response.statusText)
            }
        })
        $("#setRoomName").val("");
        $("#setRoomPassword").val("");
        $("#createChatRoomModal").modal("hide");
    })

// 加入聊天室确认按钮
    $("#joinConfirmBtn").click(function () {
        const roomId = $("#inputRoomId").val();
        const roomPassword = $("#inputRoomPassword").val();
        $("#inputRoomId").val("");
        $("#inputRoomPassword").val("");
        $("#joinChatRoomModal").modal("hide");
        joinChatRoom(roomId, roomPassword);
    })

// 发送点击事件
    $("#sendBtn").click(function () {
        const image = $("#inputPicture").val();
        if (image === "") {
            sendMsg($("#msg").val());
        } else {// 发送图片
            const formData = new FormData();
            const userId = $('#userId').val();
            const roomId = $('#roomId').text();
            formData.append("image", $("#inputPicture")[0].files[0]);
            formData.append("userId", userId);
            formData.append("roomId", roomId);
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
        const userId = $("#userId").val();
        const roomId = $("#roomId").text();
        $.ajax({
            url: "chatRoom/do/exit.json",
            type: "post",
            data: {
                "userId": userId,
                "roomId": roomId
            },
            dataType: "json",
            success: function (response) {
                const result = response.result;
                if (result === "SUCCESS") {
                    $("#exitConfirmModal").modal("hide");
                    $('#room_' + roomId).remove();
                    $('#words_' + roomId).remove();
                    if ($('#roomList li').length > 0) {
                        $('#roomList li:eq(0)').children('a')[0].click();
                    } else {
                        $("#roomId").text(0);
                    }
                    layer.msg("退出成功!");
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
        const userId = $('#userId').val();
        const roomId = $('#roomId').text();
        formData.append("multipartFile", $("#file")[0].files[0]);
        formData.append("userId", userId);
        formData.append("roomId", roomId);
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
        const roomId = $('#roomId').text();
        $("#words_" + roomId).empty();
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

// 页面加载成功后获取所有加入的房间
    getAllChatRooms();
})