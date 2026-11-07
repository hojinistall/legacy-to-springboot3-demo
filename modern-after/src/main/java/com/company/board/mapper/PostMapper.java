package com.company.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.company.board.domain.PostForm;
import com.company.board.domain.PostSummary;

@Mapper
public interface PostMapper {

    List<PostSummary> findPage(@Param("keyword") String keyword,
                               @Param("offset") int offset,
                               @Param("size") int size);

    long count(@Param("keyword") String keyword);

    void insert(PostForm form);
}
