package cugxg.com.onekeyalarm.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.apm.test.tool.APMFragment;
import com.apm.test.tool.APMFragmentv4;
import com.apm.test.tool.FragmentProxy;

import butterknife.ButterKnife;

/**
 * Created by zhouyou on 2016/6/27.
 * Class desc: fragment base class
 */
public abstract class BaseFragment extends Fragment {

    private View mView;
    private Activity mActivity;
    protected Context mContext;
    private AppCompatActivity mCompatActivity;
    protected LayoutInflater mLayoutInflater;

    // 是否可见
    protected boolean mIsVisiable;
    // 是否已经调用了 onCreateView
    protected boolean mIsViewCreate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentProxy.callonCreate(this);

        mActivity = getActivity();
        mContext = mActivity;
        mCompatActivity = (AppCompatActivity) mActivity;
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayoutInflater = getActivity().getLayoutInflater();
        mView = getLayout(inflater, container, savedInstanceState);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIsViewCreate = true;
        // 绑定依赖注入框架
        ButterKnife.bind(this, mView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCreateFragment(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentProxy.callonResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        FragmentProxy.callonPause(this);
    }

    /**
     * 返回事件，默认退出当前 activity
     */
    protected void onBack(){
        finish();
    }

    /**
     * 销毁当前挂载的 Activity
     */
    protected void finish(){
        mActivity.finish();
    }

    /**
     * 查找当前控件
     */
    protected View findViewById(int id){
        return mView.findViewById(id);
    }

    /**
     * 获取 Layout 布局
     */
    public abstract View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisiable = isVisibleToUser;
        if(mIsVisiable && mIsViewCreate){
            onLazyLoadData();
        }
    }

    protected void onLazyLoadData(){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //初始化fragment
    public abstract void onCreateFragment(@Nullable Bundle savedInstanceState);
}
