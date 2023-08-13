package com.lms.lms.course.controller;


import com.lms.lms.BaseController;
import com.lms.lms.admin.dto.MemberDto;
import com.lms.lms.admin.service.CategoryService;
import com.lms.lms.course.dto.CourseDto;
import com.lms.lms.course.model.CourseInput;
import com.lms.lms.course.model.CourseParam;
import com.lms.lms.course.service.CourseService;
import com.lms.lms.util.PageUtil;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AdminCourseController extends BaseController {
    private final CategoryService categoryService;
    private final CourseService courseService;


    @GetMapping("/admin/course/list.do")
    public String list(Model model, CourseParam parameter) {

        parameter.init();
        List<CourseDto> courseList = courseService.list(parameter);

        long totalCount = 0;
        if (!CollectionUtils.isEmpty(courseList)) {
            totalCount = courseList.get(0).getTotalCount();
        }
        String queryString = parameter.getQueryString();

        String pagerHtml = getPaperHtml(totalCount, parameter.getPageSize(),
            parameter.getPageIndex(), queryString);

        model.addAttribute("list", courseList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pager", pagerHtml);

        return "/admin/course/list";
    }

    @GetMapping(value = {"/admin/course/add.do", "/admin/course/edit.do"})
    public String add(Model model, HttpServletRequest request, CourseInput parameter) {

        // 카테고리 정보 내림
        model.addAttribute("category", categoryService.list());

        boolean editMode = request.getRequestURI().contains("/edit.do");
        CourseDto detail = new CourseDto();

        if(editMode) {
            long id = parameter.getId();
            CourseDto existCourse = courseService.getById(id);
            if(existCourse == null) {
                // error 처리
                model.addAttribute("message", "강좌정보가 존재하지 않습니다.");
                return "common/error";
            }
            detail = existCourse;
        }
        //todo

        model.addAttribute("editMode", editMode);
        model.addAttribute("detail", detail);

        return "admin/course/add";
    }

     @PostMapping(value = {"/admin/course/add.do", "/admin/course/edit.do"})
    public String addSubmit(Model model, HttpServletRequest request, CourseInput parameter) {

         boolean editMode = request.getRequestURI().contains("/edit.do");

         if(editMode) {
             long id = parameter.getId();
             CourseDto existCourse = courseService.getById(id);
             if(existCourse == null) {
                 // error 처리
                 model.addAttribute("message", "강좌정보가 존재하지 않습니다.");
                 return "common/error";
             }
             boolean result = courseService.set(parameter);

         } else {
             boolean result = courseService.add(parameter);

         }

        return "redirect:/admin/course/list.do";
    }

    @PostMapping("/admin/course/delete.do")
    public String del(Model model, HttpServletRequest request, CourseInput parameter) {

        boolean result = courseService.del(parameter.getIdList());


        return "redirect:/admin/course/list.do";
    }
}
