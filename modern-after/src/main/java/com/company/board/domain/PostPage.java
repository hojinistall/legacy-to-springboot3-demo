package com.company.board.domain;

import java.util.List;

public record PostPage(List<PostSummary> posts, int page, int totalPages, long totalCount) {

    public boolean hasPrevious() {
        return page > 1;
    }

    public boolean hasNext() {
        return page < totalPages;
    }
}
