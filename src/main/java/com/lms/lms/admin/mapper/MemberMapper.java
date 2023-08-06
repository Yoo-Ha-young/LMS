package com.lms.lms.admin.mapper;


import com.lms.lms.admin.dto.MemberDto;
import com.lms.lms.admin.model.MemberParam;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    long selectListCount(MemberParam parameter);
    List<MemberDto> selectList(MemberParam parameter);

}
