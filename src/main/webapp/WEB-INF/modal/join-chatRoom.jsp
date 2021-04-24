<%@ page language="java" contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8" %>
<div id="joinChatRoomModal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">加入聊天室</h4>
            </div>
            <div class="modal-body">
                <input id="roomPortInput" class="form-control" placeholder="请输入聊天室房间号...">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="joinConfirmBtn" type="button" class="btn btn-primary">进入</button>
            </div>
        </div>
    </div>
</div>