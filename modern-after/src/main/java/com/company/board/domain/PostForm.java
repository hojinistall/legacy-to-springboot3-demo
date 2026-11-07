package com.company.board.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 글쓰기 입력값. 레거시에는 검증이 없어 빈 제목, 1만 자 제목이 그대로 저장됐다. */
public record PostForm(
        @NotBlank(message = "제목을 입력해 주세요")
        @Size(max = 200, message = "제목은 200자까지 입력할 수 있어요")
        String title,

        @NotBlank(message = "작성자를 입력해 주세요")
        @Size(max = 50, message = "작성자는 50자까지 입력할 수 있어요")
        String writer,

        @Size(max = 10000, message = "내용은 1만 자까지 입력할 수 있어요")
        String content) {

    public static PostForm empty() {
        return new PostForm("", "", "");
    }
}
