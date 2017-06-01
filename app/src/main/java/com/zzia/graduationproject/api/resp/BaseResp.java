package com.zzia.graduationproject.api.resp;


public class BaseResp<T> {
    public int resultCode;
    public String message;
    public int action;
    public String id;
    public int status;
    public T data;
    public int allCount;
//    public MomentShare share_app;
//    public String download_link;
    public boolean isSuccess(){
        if (resultCode==200){
            return true;
        }
        return false;
    }
//    public boolean isSuccess() {
//        // token失效
//        if (status == Constants.RespCode.TOKEN_INVALID) {
//            EventBus.getDefault().post(new TokenInvalidEvent());
//        }
//        // 需要更新APP
//        if (status == Constants.RespCode.UPDATE||status == Constants.RespCode.MUST_UPDATE) {
//            EventBus.getDefault().post(new UpdateEvent(message,download_link,status));
//        }
//
//        return status == 200 || status == 1;
//    }


    @Override
    public String toString() {
        return "BaseResp{" +
                "resultCode=" + resultCode +
                ", message='" + message + '\'' +
                ", action=" + action +
                ", status=" + status +
                ", data=" + data +
                ", allCount=" + allCount +
                '}';
    }
}
