package com.company.board.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * 2012년 작성. 담당자 퇴사 후 아무도 손대지 못한 DAO.
 * 이 파일에서 실제 장애로 이어진 문제를 [문제N] 주석으로 표시했다.
 */
public class BoardDAO {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Map<String, Object>> getList(String keyword, int page) throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        // [문제1] 검색어를 SQL에 그대로 이어 붙임 → SQL 인젝션
        //         검색창에 ' OR '1'='1 을 넣으면 비공개 글까지 전부 조회된다
        String sql = "SELECT * FROM BOARD WHERE DEL_YN = 'N'";
        if (keyword != null && !keyword.equals("")) {
            sql += " AND TITLE LIKE '%" + keyword + "%'";
        }
        sql += " ORDER BY ID DESC";

        // [문제2] 전체 글을 메모리로 다 읽은 뒤 자바에서 10개만 잘라냄
        //         글이 수십만 건이 되자 목록 화면이 10초 이상 걸리고 서버 메모리가 부족해졌다
        ResultSet rs = stmt.executeQuery(sql);
        List<Map<String, Object>> all = new ArrayList<Map<String, Object>>();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<String, Object>();
            row.put("id", rs.getLong("ID"));
            row.put("title", rs.getString("TITLE"));
            row.put("writer", rs.getString("WRITER"));
            row.put("regDate", rs.getTimestamp("REG_DATE"));
            all.add(row);
        }

        int from = (page - 1) * 10;
        int to = Math.min(from + 10, all.size());
        List<Map<String, Object>> list = all.subList(from, to);

        // [문제3] 글마다 댓글 수를 따로 조회 (N+1 쿼리) → 목록 한 번에 쿼리 11번
        for (Map<String, Object> row : list) {
            ResultSet crs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM BOARD_COMMENT WHERE BOARD_ID = " + row.get("id"));
            crs.next();
            row.put("commentCount", crs.getInt(1));
        }

        // [문제4] 예외가 나면 close()까지 가지 못해 커넥션이 반환되지 않음
        //         하루 몇 번 오류가 쌓이면 커넥션 풀이 고갈되어 사이트 전체가 멈췄다
        rs.close();
        stmt.close();
        conn.close();
        return list;
    }

    public void insert(String title, String writer, String content) throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        // [문제1] 등록도 같은 방식. 제목에 작은따옴표(')만 들어가도 500 에러
        stmt.executeUpdate("INSERT INTO BOARD (ID, TITLE, WRITER, CONTENT, DEL_YN, REG_DATE) VALUES ("
                + "BOARD_SEQ.NEXTVAL, '" + title + "', '" + writer + "', '" + content + "', 'N', SYSDATE)");
        stmt.close();
        conn.close();
    }
}
