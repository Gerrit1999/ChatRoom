<%--
  Created by IntelliJ IDEA.
  User: Gerrit
  Date: 2021/5/3
  Time: 21:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="zh-CN">

<head>
    <meta charset="utf-8">
    <title>聊天室</title>
    <link rel="stylesheet" href="css/login.css">
</head>

<body>
<div class="content">
    <div class="form sign-in">
        <h2>欢迎回来</h2>
        <form method="post" action="security/do/login.html">
            <%--            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">--%>
            <label>
                <span>用户名</span>
                <input type="text" name="username" required="required" oninvalid="setCustomValidity('请输入用户名')"
                       oninput="setCustomValidity('')"/>
            </label>
            <label>
                <span>密码</span>
                <input type="password" name="password" required="required" oninvalid="setCustomValidity('请输入密码')"
                       oninput="setCustomValidity('')"/>
            </label>
            <button type="submit" class="submit">登 录</button>
            <button id="loginFormClearBtn" type="reset" style="display: none"></button>
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
                    <input type="text" id="username" required="required" oninvalid="setCustomValidity('请输入用户名')"
                           oninput="setCustomValidity('')"/>
                </label>
                <label>
                    <span>邮箱</span>
                    <input type="email" id="email" required="required" oninvalid="setCustomValidity('请输入邮箱')"
                           oninput="setCustomValidity('')"/>
                </label>
                <label>
                    <span>密码</span>
                    <input type="password" id="password" required="required" oninvalid="setCustomValidity('请输入密码')"
                           oninput="setCustomValidity('')"/>
                </label>
                <button class="submit" type="submit">注册</button>
                <button id="registerFormClearBtn" type="reset" style="display: none"></button>
            </form>
        </div>
    </div>
</div>

<script src="jquery/jquery-2.1.1.min.js"></script>
<script src="js/login.js"></script>
<script type="text/javascript" src="layer/layer.js"></script>

<c:if test="${SPRING_SECURITY_LAST_EXCEPTION.message eq 'Bad credentials'}">
    <script>
        layer.msg("登录失败! 请检查用户名和密码是否正确!", {
            time: 2000, //2s后自动关闭
        });
    </script>
    <c:remove var="SPRING_SECURITY_LAST_EXCEPTION" scope="session"/>
</c:if>

<script>
    $(function () {
        if ("${pageContext.request.userPrincipal.name}" !== "") {
            // 已经登录, 跳转到聊天界面
            window.location.href = "./to/main.html";
        }
    })
</script>

</body>

</html>