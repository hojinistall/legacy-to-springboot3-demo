package com.company.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.board.domain.PostForm;
import com.company.board.domain.PostPage;
import com.company.board.domain.PostSummary;
import com.company.board.mapper.PostMapper;

/**
 * [해결4] 커넥션 열고 닫기를 직접 하지 않고 스프링 트랜잭션에 맡김.
 * 예외가 나도 커넥션이 항상 풀로 반환되어 풀 고갈 장애가 사라진다.
 */
@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostMapper postMapper;
    private final int pageSize;

    public PostService(PostMapper postMapper, @Value("${board.page-size}") int pageSize) {
        this.postMapper = postMapper;
        this.pageSize = pageSize;
    }

    public PostPage findPage(String keyword, int page) {
        String trimmed = keyword == null ? null : keyword.strip();
        long total = postMapper.count(trimmed);
        int totalPages = (int) Math.max(1, (total + pageSize - 1) / pageSize);
        int current = Math.min(Math.max(page, 1), totalPages);

        List<PostSummary> posts = postMapper.findPage(trimmed, (current - 1) * pageSize, pageSize);
        return new PostPage(posts, current, totalPages, total);
    }

    @Transactional
    public void write(PostForm form) {
        postMapper.insert(form);
    }
}
