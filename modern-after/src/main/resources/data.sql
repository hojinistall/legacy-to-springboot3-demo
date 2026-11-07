-- 데모 데이터: 공개 글 1,000건
INSERT INTO BOARD (ID, TITLE, WRITER, CONTENT, DEL_YN, REG_DATE)
SELECT BOARD_SEQ.NEXTVAL,
       '업무 공지 ' || X,
       CASE MOD(X, 3) WHEN 0 THEN '김대리' WHEN 1 THEN '이과장' ELSE '박사원' END,
       '공지 내용 ' || X,
       'N',
       TIMESTAMP '2026-01-01 09:00:00' + X * INTERVAL '1' HOUR
FROM SYSTEM_RANGE(1, 1000);

-- 삭제 처리된 비공개 글. 레거시에서는 SQL 인젝션으로 이 글이 노출됐다
INSERT INTO BOARD (ID, TITLE, WRITER, CONTENT, DEL_YN)
VALUES (BOARD_SEQ.NEXTVAL, '[비공개] 인사평가 결과', '인사팀', '외부 노출 금지', 'Y');

-- 최근 글 일부에 댓글
INSERT INTO BOARD_COMMENT (ID, BOARD_ID, CONTENT)
SELECT X, 1000 - MOD(X, 20), '확인했습니다 ' || X
FROM SYSTEM_RANGE(1, 60);
