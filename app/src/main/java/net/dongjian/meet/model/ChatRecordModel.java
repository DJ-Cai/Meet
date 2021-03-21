package net.dongjian.meet.model;

/**
 * FileName: ChatRecordModel
 * Founder: LiuGuiLin
 * Profile: 会话管理的数据模型---聊天记录里的recyclerView的view
 */
public class ChatRecordModel {

    //头像、昵称、最后的消息、时间、未读消息数量
    private String userId;
    private String url;
    private String nickName;
    private String endMsg;
    private String time;
    private int unReadSize;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEndMsg() {
        return endMsg;
    }

    public void setEndMsg(String endMsg) {
        this.endMsg = endMsg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getUnReadSize() {
        return unReadSize;
    }

    public void setUnReadSize(int unReadSize) {
        this.unReadSize = unReadSize;
    }
}
