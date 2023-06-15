package com.mpi.exam_sample.model.api;

import com.mpi.exam_sample.enums.UserInfoState;

import java.util.List;

public class LiveUserInfoData {
    private UserInfoState userInfoState;
    private List<UserInfoResponse> userInfoResponseList;

    private String errorMessage;

    public LiveUserInfoData(UserInfoState userInfoState) {
        this.userInfoState = userInfoState;
    }

    public LiveUserInfoData(UserInfoState userInfoState, List<UserInfoResponse> userInfoResponseList) {
        this.userInfoState = userInfoState;
        this.userInfoResponseList = userInfoResponseList;
    }

    public LiveUserInfoData(UserInfoState userInfoState, String errorMessage) {
        this.userInfoState = userInfoState;
        this.errorMessage = errorMessage;
    }

    public UserInfoState getUserInfoState() {
        return userInfoState;
    }

    public void setUserInfoState(UserInfoState userInfoState) {
        this.userInfoState = userInfoState;
    }

    public List<UserInfoResponse> getUserInfoResponseList() {
        return userInfoResponseList;
    }

    public void setUserInfoResponseList(List<UserInfoResponse> userInfoResponseList) {
        this.userInfoResponseList = userInfoResponseList;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
