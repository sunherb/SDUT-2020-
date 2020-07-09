package com.example.viewnews.usermodel;

import org.litepal.crud.LitePalSupport;

public class UserInfo extends LitePalSupport {

    private String userAccount;
    private String nickName;
    private String userPwd;
    private String userSex;
    private String userBirthDay;
    private String userSignature;
    private String imagePath;

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserBirthDay() {
        return userBirthDay;
    }

    public void setUserBirthDay(String userBirthDay) {
        this.userBirthDay = userBirthDay;
    }

    public String getUserSignature() {
        return userSignature;
    }

    public void setUserSignature(String userSignature) {
        this.userSignature = userSignature;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userAccount='" + userAccount + '\'' +
                ", nickName='" + nickName + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", userSex='" + userSex + '\'' +
                ", userBirthDay='" + userBirthDay + '\'' +
                ", userSignature='" + userSignature + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
