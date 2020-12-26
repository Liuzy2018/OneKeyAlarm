package cugxg.com.onekeyalarm.model.source;

import cugxg.com.onekeyalarm.model.Case;

public interface CaseDataSource {
    public void saveCaseInfo(Case cas, final SaveCaseInfoCallback callback);

    interface SaveCaseInfoCallback{
        void saveSuccess();
        void saveFail(Error e);
    }
}
