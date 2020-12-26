package cugxg.com.onekeyalarm.model.source;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import cugxg.com.onekeyalarm.model.Case;
import cugxg.com.onekeyalarm.model.User;
import cugxg.com.onekeyalarm.util.ToastUtils;
import cugxg.com.onekeyalarm.util.UserUtils;

public class CaseRepository implements CaseDataSource{
    @Override
    public void saveCaseInfo(final Case cas, final SaveCaseInfoCallback callback) {
        cas.saveEventually(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    User user =UserUtils.getUser();
                    List<String> curcases = user.getCases();
                    if(curcases==null)
                        curcases=new ArrayList<>();
                    String id = cas.getObjectId();
                    curcases.add(id);
                    user.setCases((ArrayList<String>)curcases);
                    user.saveInBackground();
                    callback.saveSuccess();
                }else{
                    e.printStackTrace();
                    callback.saveFail(new Error(e));
                }
            }
        });
    }
}
