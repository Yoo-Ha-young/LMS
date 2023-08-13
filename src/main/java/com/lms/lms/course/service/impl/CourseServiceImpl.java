package com.lms.lms.course.service.impl;

import com.lms.lms.course.dto.CourseDto;
import com.lms.lms.course.entity.Course;
import com.lms.lms.course.mapper.CourseMapper;
import com.lms.lms.course.model.CourseInput;
import com.lms.lms.course.model.CourseParam;
import com.lms.lms.course.repository.CourseRepository;
import com.lms.lms.course.service.CourseService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    private LocalDate getLocalDate(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try{
            return LocalDate.parse(value, formatter);
        }catch (Exception e) {

        }

        return null;
    }


    @Override
    public boolean add(CourseInput parameter) {
        Course course = Course.builder()
            .categoryId(parameter.getCategoryId())
            .subject(parameter.getSubject())
            .keyword(parameter.getKeyword())
            .summary(parameter.getSummary())
            .contents(parameter.getContents())
            .price(parameter.getPrice())
            .salePrice(parameter.getSalePrice())
            .saleEndDt(getLocalDate(parameter.getSaleEndDtText()))
            .regDt(LocalDateTime.now())

            .build();
        courseRepository.save(course);
        return true;
    }

    @Override
    public List<CourseDto> list(CourseParam parameter) {

        long totalCount = courseMapper.selectionListCount(parameter);

        List<CourseDto> list = courseMapper.selectList(parameter);
        if(!CollectionUtils.isEmpty(list)) {
            int i = 0;
            for(CourseDto x : list) {
                x.setTotalCount(totalCount);
                x.setSeq(totalCount - parameter.getPageStart() -i);
                i++;
            }
        }

        return list;
    }

    @Override
    public CourseDto getById(long id) {
        return courseRepository.findById(id).map(CourseDto::of).orElse(null);
    }

    @Override
    public boolean set(CourseInput parameter) {
//        LocalDate saleEndDt = getLocalDate(parameter.getSaleEndDtText());
        Optional<Course> optionalCourse = courseRepository.findById(parameter.getId());

        if(!optionalCourse.isPresent()) {
            return false;
        }

        Course course = optionalCourse.get();
        course.setCategoryId(parameter.getCategoryId());
        course.setSubject(parameter.getSubject());
        course.setKeyword(parameter.getKeyword());
        course.setSummary(parameter.getSummary());
        course.setContents(parameter.getContents());
        course.setPrice(parameter.getPrice());
        course.setSalePrice(parameter.getSalePrice());
        course.setSaleEndDt(getLocalDate(parameter.getSaleEndDtText()));
        course.setUdtDt(LocalDateTime.now());
        courseRepository.save(course);

        return true;
    }

    @Override
    public boolean del(String idList) {

        if(idList != null && idList.length() >0) {
            String[] ids = idList.split(",");
            for(String x: ids) {
                long id = 0L;
                try{
                    id = Long.parseLong(x);
                } catch (Exception e) {

                }

                if(id>0) {
                    courseRepository.deleteById(id);
                }
            }
        }

        return true;
    }
}