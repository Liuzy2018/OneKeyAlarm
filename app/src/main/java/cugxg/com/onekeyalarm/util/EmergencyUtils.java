package cugxg.com.onekeyalarm.util;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.GetCallback;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cugxg.com.onekeyalarm.model.EmergencyContact;


public class EmergencyUtils {
    public static boolean isExist(String name,String phone){
        final AVQuery<AVObject> priorityQuery = new AVQuery<>("EmergencyContact");
        priorityQuery.whereEqualTo("name", name);

        final AVQuery<AVObject> statusQuery = new AVQuery<>("EmergencyContact");
        statusQuery.whereEqualTo("phone", phone);

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(priorityQuery, statusQuery));
        try {
            List<AVObject> templist = query.find();
            if(templist.size()!=0)
                return true;
        } catch (AVException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void saveEmergency(String name,String phone){
        EmergencyContact emergencyContact = new EmergencyContact("EmergencyContact");
        emergencyContact.setName(name);
        emergencyContact.setPhone(phone);
        emergencyContact.saveInBackground();
    }
    public static String findIdByNamePhone(String name,String phone){
        final AVQuery<AVObject> priorityQuery = new AVQuery<>("EmergencyContact");
        priorityQuery.whereEqualTo("name", name);

        final AVQuery<AVObject> statusQuery = new AVQuery<>("EmergencyContact");
        statusQuery.whereEqualTo("phone", phone);

        AVQuery<AVObject> query = AVQuery.or(Arrays.asList(priorityQuery, statusQuery));
        try {
            List<AVObject> templist = query.find();
            if (templist.size()!=0)
                return templist.get(0).getObjectId();
        } catch (AVException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void deleteEmergency(String id){

        AVQuery.doCloudQueryInBackground("delete from EmergencyContact where objectId='"+id+"'", new CloudQueryCallback<AVCloudQueryResult>() {
            @Override
            public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                if (e!=null)
                    e.toString();
            }
        });
    }
    public static HashMap<String,String> findNamePhoneById(String id){
        final HashMap<String,String> res = new HashMap<>();
        AVQuery<AVObject> avQuery = new AVQuery<>("EmergencyContact");
        avQuery.getInBackground(id, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                String name = avObject.getString("name");
                String phone = avObject.getString("phone");
                res.put(avObject.getString("name"), avObject.getString("phone"));
            }
        });
        return res;
    }

}
