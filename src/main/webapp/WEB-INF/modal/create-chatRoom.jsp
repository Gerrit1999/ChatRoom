<%@ page language="java" contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8" %>
<div id="createChatRoomModal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">创建聊天室</h4>
            </div>
            <div class="modal-body">
                <input id="setRoomName" class="form-control" placeholder="请输入聊天室名..."><br/>
                <input id="setRoomPassword" class="form-control" placeholder="请输入聊天室密码...">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="createConfirmBtn" type="button" class="btn btn-primary">创建</button>
            </div>
        </div>
    </div>
</div>