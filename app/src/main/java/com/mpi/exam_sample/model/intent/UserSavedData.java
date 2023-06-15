package com.mpi.exam_sample.model.intent;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserSavedData implements Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("mail_address")
    private String mailAddress;

    public UserSavedData() {
        this.name = "";
        this.mailAddress = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }
}
