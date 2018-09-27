package me.henrylee.classvoice.model.response;

import java.util.List;

public class VoiceInfoListResponse extends Response {
    private List<UserVoiceInfo> voiceInfos;

    public VoiceInfoListResponse(ErrMsg errMsg) {
        super(errMsg);
    }

    public VoiceInfoListResponse(ErrMsg errMsg, List<UserVoiceInfo> voiceInfos) {
        super(errMsg);
        this.voiceInfos = voiceInfos;
    }

    public List<UserVoiceInfo> getVoiceInfos() {
        return voiceInfos;
    }

    public void setVoiceInfos(List<UserVoiceInfo> voiceInfos) {
        this.voiceInfos = voiceInfos;
    }
}
