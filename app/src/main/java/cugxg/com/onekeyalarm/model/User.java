package cugxg.com.onekeyalarm.model;

import android.os.Parcel;

import com.avos.avoscloud.AVUser;

import java.util.ArrayList;
import java.util.List;

import cugxg.com.onekeyalarm.constants.Api;

public class User extends AVUser{
    private String name;
    public String password;
    /** 性别，0：未设置、1：男 、2：女 */
    private int sex = 0;

    //0：没有申请退款；1:正在退款；2：提交材料完成；3：正在审查；4：材料有误；5：退款成功
    private int refundProcess;

    private String cardID;
    private String address;
    public String phone;
    private ArrayList<String> emergencyContacts;
    private ArrayList<String> Cases;
    private double money;

    public User() {
        super();
    }
    public User(Parcel in){super(in);}
    public static final Creator CREATOR = AVObjectCreator.instance;

    public User(String name, String password, int sex, String cardID, String address, String mobilePhoneNumber, ArrayList<String> emergencyContacts) {
        this.name = name;
        this.password = password;
        this.sex = sex;
        this.cardID = cardID;
        this.address = address;
        this.phone = mobilePhoneNumber;
        this.emergencyContacts = emergencyContacts;
    }

    public double getMoney() {
        return getDouble("money");
    }

    public void setMoney(double money) {
        put("money",money);
        this.money = money;
    }

    public String getName() {
        return getString(Api.NAME);
    }

    public void setName(String name) {
        put(Api.NAME,name);
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSex() {
        return getInt(Api.SEX);
    }

    public void setSex(int sex) {
        put(Api.SEX,sex);
        this.sex = sex;
    }

    public int getRefundProcess() {
        return getInt("refundProcess");
    }

    public void setRefundProcess(int refundProcess) {
        put("refundProcess",refundProcess);
        this.refundProcess = refundProcess;
    }

    public String getCardID() {
        return getString(Api.CARDID);
    }

    public void setCardID(String cardID) {
        put(Api.CARDID,cardID);
        this.cardID = cardID;
    }

    public String getAddress() {
        return getString(Api.ADDRESS);
    }

    public void setAddress(String address) {
        put(Api.ADDRESS,address);
        this.address = address;
    }

    public String getPhone() {
        return getString(Api.PHONE);
    }

    public void setPhone(String mobilePhoneNumber) {
        this.phone = mobilePhoneNumber;
    }

    public List<String> getEmergencyContact() {
        return getList("emergencyContacts");
    }

    public void setEmergencyContact(ArrayList<String> emergencyContacts) {
        put("emergencyContacts",emergencyContacts);
        this.emergencyContacts = emergencyContacts;
    }

    public List<String> getCases() {
        return getList("caseID");
    }

    public void setCases(ArrayList<String> cases) {
        put("caseID",cases);
        this.Cases = cases;
    }

}
