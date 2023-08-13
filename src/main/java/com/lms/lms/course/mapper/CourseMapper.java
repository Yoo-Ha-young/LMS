package com.lms.lms.course.mapper;

import com.lms.lms.course.dto.CourseDto;
import com.lms.lms.course.model.CourseParam;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper {

    long selectionListCount(CourseParam parameter);
    List<CourseDto> selectList(CourseParam parameter);

}
