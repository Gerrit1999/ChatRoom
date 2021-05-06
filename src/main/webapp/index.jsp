<%--
  Created by IntelliJ IDEA.
  User: Gerrit
  Date: 2021/5/3
  Time: 21:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="zh-CN">

<head>
    <meta charset="utf-8">
    <title>聊天室</title>
    <link rel="stylesheet" href="css/style.css">
</head>

<body>
<div class="content">
    <div class="form sign-in">
        <h2>欢迎回来</h2>
        <p>${SPRING_SECURITY_LAST_EXCEPTION.message}</p>
        <form method="post" action="security/do/login.html">
            <%--            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">--%>
            <label>
                <span>用户名</span>
                <input type="text" name="username" required="required"/>
            </label>
            <label>
                <span>密码</span>
                <input type="password" name="password" required="required"/>
            </label>
            <button type="submit" class="submit">登 录</button>
        </form>
    </div>
    <div class="sub-cont">
        <div class="img">
            <div class="img__text m--up">
                <h2>还未注册？</h2>
                <p>点击下方注册吧！</p>
            </div>
            <div class="img__text m--in">
                <h2>已有帐号？</h2>
                <p>有帐号就登录吧，好久不见了！</p>
            </div>
            <div class="img__btn">
                <span class="m--up">注 册</span>
                <span class="m--in">登 录</span>
            </div>
        </div>
        <div class="form sign-up">
            <h2>立即注册</h2>
            <%--            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">--%>
            <form onsubmit="register()" target="rfFrame">
                <iframe id="rfFrame" name="rfFrame" src="about:blank" style="display:none;"></iframe>
                <label>
                    <span>用户名</span>
                    <input type="text" id="username" required="required"/>
                </label>
                <label>
                    <span>用户名</span>
                    <input type="email" id="email" required="required"/>
                </label>
                <label>
                    <span>密码</span>
                    <input type="password" id="password" required="required"/>
                </label>
                <button class="submit" type="submit">注册</button>
            </form>
        </div>
    </div>
</div>

<script src="js/script.js"></script>
<script src="jquery/jquery-2.1.1.min.js"></script>
<script type="text/javascript" src="layer/layer.js"></script>

<script>

    function register() {
        const username = $('#username').val();
        const email = $('#email').val();
        const password = $('#password').val();
        const data = JSON.stringify({
            "username": username,
            "email": email,
            "password": password
        });
        $.ajax({
            url: "user/do/register.json",
            type: "post",
            contentType: "application/json;charset=utf-8",
            data: data,
            dataType: "json",
            async: false,
            success: function (response) {
                const result = response.result;
                if (result === "SUCCESS") {
                    layer.msg("注册成功! 3s后跳转到登录页面");
                    setTimeout(function () {
                        $(location).attr("href", "index.jsp");
                    }, 3000);
                } else if (result === "FAILED") {
                    layer.msg("注册失败! " + response.message);
                }
            },
            error: function (response) {
                console.log(1)
                layer.msg("注册失败! " + response.status + " " + response.statusText);
            }
        });
    }

</script>


</body>

</html>