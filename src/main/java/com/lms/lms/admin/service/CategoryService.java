package com.lms.lms.admin.service;

import com.lms.lms.admin.dto.CategoryDto;
import com.lms.lms.admin.model.CategoryInput;
import java.util.List;
import org.springframework.stereotype.Service;

public interface CategoryService {
    List<CategoryDto> list();
    /**
     * 카테고리 신규 추가
     * */
    boolean add(String categoryName);
    /**
     * 카테고리 수정
     * */
    boolean update(CategoryInput parameter);
    /**
     * 카테고리 삭제
     * */
    boolean del(long id);
}
