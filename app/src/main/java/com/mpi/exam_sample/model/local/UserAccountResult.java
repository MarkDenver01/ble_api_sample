package com.mpi.exam_sample.model.local;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserAccountResult implements Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("mail_address")
    private String mailAddress;

    public UserAccountResult() {
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
