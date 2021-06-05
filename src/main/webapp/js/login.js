document.querySelector('.img__btn').addEventListener('click', function () {
    document.querySelector('.content').classList.toggle('s--signup')
    $('#registerFormClearBtn').click()
    $('#loginFormClearBtn').click()
})

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
                layer.msg("注册成功! 正在跳转到登录页面");
                setTimeout(function () {
                    $(location).attr("href", "index.jsp");
                }, 1000);
            } else if (result === "FAILED") {
                layer.msg("注册失败! " + response.message);
            }
        },
        error: function (response) {
            layer.msg("注册失败! " + response.status + " " + response.statusText);
        }
    });
}