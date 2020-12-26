package cugxg.com.onekeyalarm.mvp.Refund.ViewRefundProcess;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import java.util.HashMap;

import butterknife.OnClick;
import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseFragment;
import cugxg.com.onekeyalarm.layout.NodeProgressBar;
import cugxg.com.onekeyalarm.model.User;
import cugxg.com.onekeyalarm.mvp.Refund.RefundActivity;
import cugxg.com.onekeyalarm.util.UserUtils;


public class ViewRefundProcessFragment extends BaseFragment {
    private NodeProgressBar ssl;
    private SeekBar seekbar;
    private HashMap<Integer,Integer> processPerNode = new HashMap<>();
    public static ViewRefundProcessFragment newInstance() {
        return new ViewRefundProcessFragment();
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_refund_process, container, false);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        processPerNode.put(1,29);
        processPerNode.put(2,58);
        processPerNode.put(3,87);
        processPerNode.put(4,99);
        ssl=(NodeProgressBar) getActivity().findViewById(R.id.ssl);
        User user = UserUtils.getUser();
        int process = user.getRefundProcess();
        ssl.setProgressByNode(process);
        ssl.setProgressOnly(processPerNode.get(process));
    }

    @OnClick({R.id.btn_reload_file})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reload_file:
                Intent intent = new Intent();
                intent.setClass(mContext,RefundActivity.class);
                startActivity(intent);
                break;
        }
    }

}
