package cugxg.com.onekeyalarm.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.util.EmergencyUtils;
import cugxg.com.onekeyalarm.util.UiUtils;
import cugxg.com.onekeyalarm.util.UserUtils;

@SuppressLint("ValidFragment")
public class AddReceiveManDialog extends AppCompatDialogFragment {
    private View mContentView;
    private LinearLayout linearLayout;
    public AddReceiveManDialog(Context context){
        mContentView = LayoutInflater.from(context).inflate(R.layout.dialog_add_receiveman, null);
        linearLayout = mContentView.findViewById(R.id.add_receive_layout);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        @SuppressLint("RestrictedApi") AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(UiUtils.getString(R.string.add_receive))
                .setCancelable(false)
                .setView(mContentView, 0, 50, 0, 0)
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel), null)
                .setPositiveButton(UiUtils.getString(R.string.dialog_finish), null)
                .create();
        List<String> ids = UserUtils.getUser().getEmergencyContact();
        for (String id : ids) {
            AVQuery<AVObject> avQuery = new AVQuery<>("EmergencyContact");
            avQuery.getInBackground(id, new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    try {

                    String name = avObject.getString("name");
                    String phone = avObject.getString("phone");

                    ToggleButton toggleButton = new ToggleButton(mContentView.getContext());
                    toggleButton.setText("姓名："+name+",联系电话："+phone);
                    toggleButton.setBackgroundResource(R.drawable.selctor_btn);
                    toggleButton.setTextOn(null);
                    toggleButton.setTextOff(null);
                    toggleButton.setTag(phone);
                    linearLayout.addView(toggleButton);
                    }catch (Exception e1){
                        Log.d("add receive",e1.toString());
                    }

                }
            });

        }
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mCallback == null) return;
                        String res=new String();
                        for(int i=0;i<linearLayout.getChildCount();i++){
                            ToggleButton toggleButton= (ToggleButton) linearLayout.getChildAt(i);
                            if (toggleButton.isChecked()){
                                String phone = (String) toggleButton.getTag();
                                res=res+phone+";";
                            }
                        }
                        mCallback.onAddSuccess(res);
                    }
                });
            }
        });
        return alertDialog;
    }

    public OnAddReceiveCallback mCallback;

    public interface OnAddReceiveCallback{
        void onAddSuccess(String phone);
    }

    public void setOnAddReceiveCallback(OnAddReceiveCallback callback){
        this.mCallback = callback;
    }
}
