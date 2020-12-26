package cugxg.com.onekeyalarm.model;

import android.os.Parcelable;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

import java.util.ArrayList;
import java.util.List;

@AVClassName("Case")
public class Case extends AVObject {
    public static final Parcelable.Creator CREATOR = AVObject.AVObjectCreator.instance;


    public static String[] list_type = {"乘坐黑车","非法囚禁","非法交易","拦路抢劫","聚众赌博","其他"};
    private boolean isDetail;
    private ArrayList<String> recipient;//收件人
    private String venue;//案发地
    private String describe;
    private double latitude;//维度
    private double longitude;//经度
    private String type;//类型：
    private List<String>soundUrl;
    private List<String> photoUrl;
    private String userId;
    private boolean isEnableToPhone;

    public List<String> getEvidenceUrl() {
        return getList("evidenceUrl");
    }

    public void setEvidenceUrl(ArrayList<String> evidenceUrl) {
        put("evidenceUrl",evidenceUrl);
        this.evidenceUrl = evidenceUrl;
    }

    private List<String> evidenceUrl;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String[] toArray(){
        return new String[]{this.getVenue(),this.getTime()};
    }
    private String time;
    public String getType() {
        return getString("type");
    }

    public void setType(String type) {
        put("type",type);
        this.type = type;
    }

    public double getLatitude() {
        return getDouble("latitude");
    }

    public void setLatitude(double latitude) {
        put("latitude",latitude);
        this.latitude = latitude;
    }

    public double getLongitude() {
        return getDouble("longitude");
    }

    public void setLongitude(double longitude) {
        put("longitude",longitude);
        this.longitude = longitude;
    }

    public String getUserId() {
        return getString("userId");
    }

    public void setUserId(String userId) {
        put("userId",userId);
        this.userId = userId;
    }


    public Case(String theClassName) {
        super(theClassName);
    }

    public boolean isDetail() {
        return getBoolean("isDetail");
    }

    public void setDetail(boolean detail) {
        put("isDetail",detail);
        isDetail = detail;
    }

    public List<String> getRecipient() {
        return getList("recipient");
    }

    public void setRecipient(ArrayList<String> recipient) {
        put("recipient",recipient);
        this.recipient = recipient;
    }

    public String getVenue() {
        return getString("venue");
    }

    public void setVenue(String venue) {
        put("venue",venue);
        this.venue = venue;
    }

    public String getDescribe() {
        return getString("describe");
    }

    public void setDescribe(String describe) {
        put("describe",describe);
        this.describe = describe;
    }

    public List<String> getSoundUrl() {
        return getList("soundUrl");
    }

    public void setSoundUrl(ArrayList<String> soundUrl) {
        put("soundUrl",soundUrl);
        this.soundUrl = soundUrl;
    }

    public List<String> getPhotoUrl() {
        return getList("photoUrl");
    }

    public void setPhotoUrl(ArrayList<String> photoUrl) {
        put("photoUrl",photoUrl);
        this.photoUrl = photoUrl;
    }

    public boolean isEnableToPhone() {
        return getBoolean("isEnableToPhone");
    }

    public void setEnableToPhone(boolean enableToPhone) {
        put("isEnableToPhone",enableToPhone);
        this.isEnableToPhone = enableToPhone;
    }
}
