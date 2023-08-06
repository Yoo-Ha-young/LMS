package com.lms.lms.admin.mapper;


import lombok.Data;
import lombok.ToString;

@Data
public class MemberInput {

    String userId;
    String userStatus;
    String password;
}
