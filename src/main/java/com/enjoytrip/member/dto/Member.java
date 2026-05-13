package com.enjoytrip.member.dto;

public class Member {

    private String userId;
    private String userPw;
    private String userName;
    private String email;
    private String joinDate;

    public Member() {}

    public Member(String userId, String userPw, String userName, String email) {
        this.userId   = userId;
        this.userPw   = userPw;
        this.userName = userName;
        this.email    = email;
    }

    public String getUserId()               { return userId; }
    public void setUserId(String userId)    { this.userId = userId; }

    public String getUserPw()               { return userPw; }
    public void setUserPw(String userPw)    { this.userPw = userPw; }

    public String getUserName()             { return userName; }
    public void setUserName(String userName){ this.userName = userName; }

    public String getEmail()                { return email; }
    public void setEmail(String email)      { this.email = email; }

    public String getJoinDate()             { return joinDate; }
    public void setJoinDate(String joinDate){ this.joinDate = joinDate; }

    @Override
    public String toString() {
        return "Member{userId='" + userId + "', userName='" + userName + "'}";
    }
}
