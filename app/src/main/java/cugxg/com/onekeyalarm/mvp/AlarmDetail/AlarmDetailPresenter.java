package cugxg.com.onekeyalarm.mvp.AlarmDetail;

import cugxg.com.onekeyalarm.model.Case;
import cugxg.com.onekeyalarm.model.source.CaseDataSource;
import cugxg.com.onekeyalarm.model.source.CaseRepository;

public class AlarmDetailPresenter implements AlarmDetailContract.Presenter{
    private final AlarmDetailContract.View mView;
    private final CaseRepository mRepository;

    public AlarmDetailPresenter(AlarmDetailContract.View mView, CaseRepository mRepository) {
        this.mView = mView;
        mView.setPresenter(this);

        this.mRepository = mRepository;
    }

    @Override
    public void start() {

    }

    @Override
    public void saveCase(Case cas) {
        mRepository.saveCaseInfo(cas, new CaseDataSource.SaveCaseInfoCallback() {
            @Override
            public void saveSuccess() {

            }

            @Override
            public void saveFail(Error e) {

            }
        });
    }
}
