<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*" %>
<html>
<head><title>사내 게시판</title></head>
<body>
<h2>사내 게시판</h2>

<%-- [문제7] 검색어를 그대로 출력 → XSS. 검색어에 스크립트를 넣으면 다른 직원 브라우저에서 실행된다 --%>
<p>검색어: <%= request.getParameter("keyword") %></p>

<table border="1">
    <tr><th>번호</th><th>제목</th><th>작성자</th><th>댓글</th></tr>
<%
    // [문제8] 화면(JSP) 안에 자바 코드(스크립틀릿)가 섞여 있어 수정할 때마다 화면이 깨진다
    List<Map<String, Object>> list = (List<Map<String, Object>>) request.getAttribute("list");
    for (Map<String, Object> row : list) {
%>
    <tr>
        <td><%= row.get("id") %></td>
        <td><%= row.get("title") %></td>
        <td><%= row.get("writer") %></td>
        <td><%= row.get("commentCount") %></td>
    </tr>
<%
    }
%>
</table>
</body>
</html>
