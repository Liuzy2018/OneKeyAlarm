package cugxg.com.onekeyalarm.mvp.UserInfo;


import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseFragment;
import cugxg.com.onekeyalarm.model.EmergencyContact;
import cugxg.com.onekeyalarm.model.User;
import cugxg.com.onekeyalarm.util.EmergencyUtils;
import cugxg.com.onekeyalarm.util.ToastUtils;
import cugxg.com.onekeyalarm.util.UiUtils;
import cugxg.com.onekeyalarm.util.UserUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoFragment extends BaseFragment implements UserInfoContract.View {
    @BindView(R.id.txt_name)
    EditText edt_name;
    @BindView(R.id.radiogroup)
    RadioGroup radio_sex;
    @BindView(R.id.radioman)
    RadioButton radioButton_man;
    @BindView(R.id.radiowoman)
    RadioButton radioButton_woman;
    @BindView(R.id.txt_address)
    EditText edt_address;
    @BindView(R.id.txt_id_number)
    EditText edt_idnumber;
    @BindView(R.id.EmergencyLists)
    TableLayout emergencyLists;

    @BindView(R.id.btn_save)
    Button save;

    private UserInfoContract.Presenter mPresenter;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    public static UserInfoFragment newInstance() {
        return new UserInfoFragment();
    }


    //    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        fragmentManager=getActivity().getSupportFragmentManager();
//        transaction=fragmentManager.beginTransaction();
//        Button btnEditInfo = (Button) getActivity().findViewById(R.id.btn_edit_info);
//        btnEditInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                transaction.replace(R.id.content, new RegisterFragment());
//                transaction.commit();
//            }
//        });
//
//    }


//    @OnClick({R.id.btn_edit_info})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btn_edit_info:
//                Intent intent = new Intent(getActivity(), RegisterActivity.class);
//                startActivity(intent);
//                break;
//        }
//    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_info, container, false);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setUserInfo();
        save.setClickable(false);
    }

    private void setUserInfo() {
        User user = UserUtils.getUser();
        edt_name.setText(user.getName());
        edt_idnumber.setText(user.getCardID());
        edt_address.setText(user.getAddress());
        if (user.getSex() == 1)
            radio_sex.check(radioButton_man.getId());
        else if (user.getSex() == 2)
            radio_sex.check(radioButton_woman.getId());
        else
            radio_sex.check(0);
        emergencyLists.removeViews(1, emergencyLists.getChildCount() - 1);

        try {
            List<String> ids = user.getEmergencyContact();
            for (String id : ids) {
                AVQuery<AVObject> avQuery = new AVQuery<>("EmergencyContact");

                avQuery.getInBackground(id, new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException e) {
                        addLine(avObject.getString("name"), avObject.getString("phone"));
                    }
                });
            }

        } catch (Exception e) {
            ToastUtils.show(mContext, e.toString());
        }
    }

    @OnTextChanged(value = {R.id.txt_id_number, R.id.txt_name, R.id.txt_address},
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable s) {
        save.setClickable(true);
    }


    @OnClick({R.id.btn_save, R.id.btn_add_emergency})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                save(view);
                break;
            case R.id.btn_add_emergency:
                save.setClickable(true);
                addLineBlank();
                break;

        }
    }

    private void save(View view) {
        String name = edt_name.getText().toString();
        String address = edt_address.getText().toString();
        String cardID = edt_idnumber.getText().toString();

        int sex = 0;
        if (radio_sex.getCheckedRadioButtonId() == R.id.radioman)
            sex = 1;
        else if (radio_sex.getCheckedRadioButtonId() == R.id.radiowoman)
            sex = 2;
        List<String> ids = saveAndRetEmgIds();

        User user = UserUtils.getUser();
        user.setAddress(address);
        user.setName(name);
        user.setCardID(cardID);
        user.setSex(sex);
        user.setEmergencyContact((ArrayList<String>) ids);
        mPresenter.saveUserInfo(user);
    }

    private void addLineBlank() {
        TableRow r = new TableRow(mContext);
        EditText edtname = new EditText(mContext);
        EditText edtphone = new EditText(mContext);
        Button button = new Button(mContext);
        button.setText(R.string.delete);
        button.setTag(emergencyLists.getChildCount());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick(v);
            }
        });
        r.addView(edtname);
        r.addView(edtphone);
        r.addView(button);
        emergencyLists.addView(r);
    }

    private void addLine(String name, String phone) {
        TableRow r = new TableRow(mContext);
        EditText edtname = new EditText(mContext);
        edtname.setText(name);
        EditText edtphone = new EditText(mContext);
        edtphone.setText(phone);
        Button button = new Button(mContext);
        button.setTag(emergencyLists.getChildCount());
        button.setText(R.string.delete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick(v);
            }
        });
        r.addView(edtname);
        r.addView(edtphone);
        r.addView(button);
        emergencyLists.addView(r);
    }

    private List<String> saveAndRetEmgIds() {
        List<String> ids = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> phones = new ArrayList<>();
        int i;
        for (i = 1; i < emergencyLists.getChildCount(); i++) {
            TableRow row = (TableRow) emergencyLists.getChildAt(i);
            TextView view;
            view = (TextView) row.getChildAt(0);
            String name = view.getText().toString();
            view = (TextView) row.getChildAt(1);
            String phone = view.getText().toString();
            names.add(name);
            phones.add(phone);
            if (EmergencyUtils.isExist(name, phone))
                continue;
            EmergencyUtils.saveEmergency(name, phone);
        }
        for (i = 0; i < names.size(); i++) {
            ids.add(EmergencyUtils.findIdByNamePhone(names.get(i), phones.get(i)));
        }
        return ids;
    }

    public void onDeleteClick(View v) {
        int t = (int) v.getTag();
        TableRow row = (TableRow) emergencyLists.getChildAt(t);
        EditText editText = (EditText) row.getChildAt(0);
        String name = editText.getText().toString();
        editText = (EditText) row.getChildAt(1);
        String phone = editText.getText().toString();
        EmergencyUtils.deleteEmergency(EmergencyUtils.findIdByNamePhone(name,phone));
        emergencyLists.removeViewAt(t);
    }


    @Override
    public void saveUserInfoSuccess() {
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_update_success));
        setUserInfo();
        save.setClickable(false);
    }

    @Override
    public void saveUserInfoFail(Error e) {
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void setPresenter(UserInfoContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
