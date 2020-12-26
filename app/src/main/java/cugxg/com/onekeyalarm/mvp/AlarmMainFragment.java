package cugxg.com.onekeyalarm.mvp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseFragment;
import cugxg.com.onekeyalarm.mvp.AlarmSimple.AlarmSimpleActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmMainFragment extends BaseFragment {

    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;

    public AlarmMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarm_main, container, false);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {

    }

    @OnClick({R.id.btn_onekeyalarm_main})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.btn_onekeyalarm_main:
//                fragmentManager=getActivity().getSupportFragmentManager();
//                transaction=fragmentManager.beginTransaction();
//                transaction.replace(R.id.content, new AlarmSimpleFragment());
//                transaction.commit();
                Intent intent = new Intent(getActivity(),AlarmSimpleActivity.class);
                startActivity(intent);
                break;

        }
    }


}
