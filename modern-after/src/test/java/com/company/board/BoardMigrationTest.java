package com.company.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.company.board.domain.PostPage;
import com.company.board.service.PostService;

/** 레거시에서 실제로 장애를 냈던 8가지 문제가 재발하지 않는지 확인하는 회귀 테스트 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BoardMigrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    PostService postService;

    @Test
    @DisplayName("[문제1] SQL 인젝션 검색어로 삭제된 비공개 글이 노출되지 않는다")
    void sqlInjectionIsBlocked() throws Exception {
        mvc.perform(get("/board").param("keyword", "' OR '1'='1"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("인사평가"))));

        assertThat(postService.findPage("' OR '1'='1", 1).totalCount()).isZero();
    }

    @Test
    @DisplayName("[문제2] 목록은 DB에서 10건만 가져오고 페이지 계산이 맞다")
    void pagingIsDoneInDatabase() {
        PostPage first = postService.findPage(null, 1);
        PostPage last = postService.findPage(null, 100);

        assertThat(first.totalCount()).isEqualTo(1000);
        assertThat(first.totalPages()).isEqualTo(100);
        assertThat(first.posts()).hasSize(10);
        assertThat(first.posts().getFirst().id()).isEqualTo(1000);
        assertThat(last.posts().getLast().id()).isEqualTo(1);
    }

    @Test
    @DisplayName("[문제3] 댓글 수가 한 번의 조회로 정확히 계산된다")
    void commentCountsAreJoined() {
        PostPage first = postService.findPage(null, 1);

        assertThat(first.posts().getFirst().commentCount()).isEqualTo(3);
        assertThat(first.posts()).allSatisfy(p -> assertThat(p.commentCount()).isEqualTo(3));
    }

    @Test
    @DisplayName("[문제5] 숫자가 아닌 page 값은 500이 아니라 400 안내 화면")
    void invalidPageReturnsFriendlyError() throws Exception {
        mvc.perform(get("/board").param("page", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("요청 값이 올바르지 않아요")));
    }

    @Test
    @DisplayName("[문제5] 범위를 벗어난 page 값은 마지막 페이지로 보정")
    void outOfRangePageIsClamped() {
        assertThat(postService.findPage(null, 99999).page()).isEqualTo(100);
        assertThat(postService.findPage(null, -3).page()).isEqualTo(1);
    }

    @Test
    @DisplayName("[문제6] 빈 제목은 저장되지 않고 안내 문구가 나온다")
    void emptyTitleIsRejected() throws Exception {
        mvc.perform(post("/board").param("title", " ").param("writer", "김대리").param("content", "내용"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "title"))
                .andExpect(content().string(containsString("제목을 입력해 주세요")));

        assertThat(postService.findPage(null, 1).totalCount()).isEqualTo(1000);
    }

    @Test
    @DisplayName("[문제1·6] 작은따옴표가 든 제목도 정상 저장된다")
    void quoteInTitleIsSaved() throws Exception {
        mvc.perform(post("/board").param("title", "김 대리's 보고서").param("writer", "김대리").param("content", "내용"))
                .andExpect(redirectedUrl("/board"));

        assertThat(postService.findPage("'s 보고서", 1).posts())
                .singleElement()
                .satisfies(p -> assertThat(p.title()).isEqualTo("김 대리's 보고서"));
    }

    @Test
    @DisplayName("[문제6] GET 요청으로는 글이 등록되지 않는다")
    void getCannotWrite() throws Exception {
        mvc.perform(get("/board/write").param("title", "몰래 등록"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("[문제7] 검색어에 넣은 스크립트는 실행되지 않도록 이스케이프된다")
    void xssIsEscaped() throws Exception {
        mvc.perform(get("/board").param("keyword", "<script>alert(1)</script>"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("<script>alert(1)</script>"))))
                .andExpect(content().string(containsString("&lt;script&gt;alert(1)&lt;/script&gt;")));
    }
}
