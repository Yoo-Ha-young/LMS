package com.lms.lms.member.model;


import lombok.Data;


@Data
public class ResetPasswordInput {

    private String userId;
    private String userName;
    private String password;
    private String id;
}
