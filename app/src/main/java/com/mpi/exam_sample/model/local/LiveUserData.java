package com.mpi.exam_sample.model.local;

import com.mpi.exam_sample.enums.UserState;

public class LiveUserData {
    private UserState userState;
    private UserAccountResult userAccountResult;

    public LiveUserData(UserState userState, UserAccountResult userAccountResult) {
        this.userState = userState;
        this.userAccountResult = userAccountResult;
    }

    public LiveUserData(UserState userState) {
        this.userState = userState;
    }

    public UserState getUserState() {
        return userState;
    }

    public UserAccountResult getUserAccountResult() {
        return userAccountResult;
    }
}
