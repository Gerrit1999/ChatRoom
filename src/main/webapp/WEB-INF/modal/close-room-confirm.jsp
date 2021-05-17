<%--
  Created by IntelliJ IDEA.
  User: Gerrit
  Date: 2021/4/14
  Time: 16:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8" %>
<div id="closeRoomConfirmModal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">系统弹窗</h4>
            </div>
            <div class="modal-body">
                <h3>您确定要关闭房间吗?</h3><br/>
                <input id="closeRoomPswd" class="form-control" placeholder="请输入登录密码..."><br/>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="closeRoomConfirmBtn" type="button" class="btn btn-primary">确定</button>
            </div>
        </div>
    </div>
</div>