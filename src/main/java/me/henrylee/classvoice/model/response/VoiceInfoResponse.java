package me.henrylee.classvoice.model.response;

public class VoiceInfoResponse extends Response {
    private UserVoiceInfo voiceInfo;

    public VoiceInfoResponse(ErrMsg errMsg) {
        super(errMsg);
    }

    public VoiceInfoResponse(ErrMsg errMsg, UserVoiceInfo voiceInfo) {
        super(errMsg);
        this.voiceInfo = voiceInfo;
    }

    public UserVoiceInfo getVoiceInfo() {
        return voiceInfo;
    }

    public void setVoiceInfo(UserVoiceInfo voiceInfo) {
        this.voiceInfo = voiceInfo;
    }
}
