package com.company.board.domain;

import java.time.LocalDateTime;

/** 목록 한 줄. Map 대신 타입이 있는 레코드로 바꿔 오타로 인한 null 화면을 없앴다. */
public record PostSummary(long id, String title, String writer, LocalDateTime regDate, int commentCount) {
}
