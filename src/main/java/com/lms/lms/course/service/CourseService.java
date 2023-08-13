package com.lms.lms.course.service;

import com.lms.lms.course.dto.CourseDto;
import com.lms.lms.course.model.CourseInput;
import com.lms.lms.course.model.CourseParam;
import java.util.List;

public interface CourseService {

    boolean add(CourseInput parameter);

    List<CourseDto> list(CourseParam parameter);

    CourseDto getById(long id);

    // 강좌정보 수정
    boolean set(CourseInput parameter);

    // 강좌 내용 삭제
    boolean del(String idList);
}