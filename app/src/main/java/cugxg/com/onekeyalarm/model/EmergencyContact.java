package cugxg.com.onekeyalarm.model;

import android.os.Parcel;

import com.avos.avoscloud.AVObject;

public class EmergencyContact extends AVObject {
    String name;
    String phone;

    public EmergencyContact(String theClassName) {
        super(theClassName);
    }
    public EmergencyContact() {
        super();
    }
    public EmergencyContact(Parcel in){super(in);}

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        this.put("name",name);
        this.name = name;
    }

    public String getPhone() {
        return getString("phone");
    }

    public void setPhone(String phone) {
        this.put("phone",phone);
        this.phone = phone;
    }
}
