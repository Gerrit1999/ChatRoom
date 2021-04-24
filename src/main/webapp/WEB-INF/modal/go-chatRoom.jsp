<%--
  Created by IntelliJ IDEA.
  User: Gerrit
  Date: 2021/4/11
  Time: 20:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8" isELIgnored="false" %>
<div id="goChatRoomModal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">进入聊天室</h4>
            </div>
            <div class="modal-body">
                <span>是否进入房间号为: ${sessionScope.port}的房间</span>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="goConfirmBtn" type="button" class="btn btn-primary">进入</button>
            </div>
        </div>
    </div>
</div>