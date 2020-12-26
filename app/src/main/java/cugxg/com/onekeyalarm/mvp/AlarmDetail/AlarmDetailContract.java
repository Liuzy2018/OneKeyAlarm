package cugxg.com.onekeyalarm.mvp.AlarmDetail;

import android.content.Context;

import cugxg.com.onekeyalarm.base.BasePresenter;
import cugxg.com.onekeyalarm.base.BaseView;
import cugxg.com.onekeyalarm.model.Case;

public interface AlarmDetailContract {
    interface View extends BaseView<Presenter> {
        public void setPresenter(Presenter presenter);
        public Context getContext();
    }
    interface Presenter extends BasePresenter {
        public void saveCase(Case cas);
    }
}
