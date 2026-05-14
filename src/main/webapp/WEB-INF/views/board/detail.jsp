<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>${board.title} - EnjoyTrip</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f5f5; }
        .container { max-width: 800px; margin: 40px auto; padding: 0 16px; }
        .card { background: #fff; border-radius: 8px; box-shadow: 0 1px 6px rgba(0,0,0,.08); overflow: hidden; }
        .post-header { padding: 24px 28px; border-bottom: 1px solid #f0f0f0; }
        .post-title { font-size: 22px; font-weight: 700; color: #222; margin-bottom: 10px; }
        .post-meta { font-size: 13px; color: #aaa; display: flex; gap: 16px; }
        .post-content { padding: 28px; font-size: 15px; color: #444; line-height: 1.8; white-space: pre-wrap; min-height: 200px; }
        .btn-row { padding: 20px 28px; display: flex; gap: 10px; justify-content: space-between; border-top: 1px solid #f0f0f0; }
        .btn { padding: 9px 22px; border-radius: 6px; font-size: 14px; text-decoration: none; cursor: pointer; border: none; display: inline-block; }
        .btn-secondary { background: #eee; color: #333; }
        .btn-edit { background: #1a73e8; color: #fff; }
        .btn-delete { background: #d32f2f; color: #fff; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="container">
    <div class="card">
        <div class="post-header">
            <div class="post-title">${board.title}</div>
            <div class="post-meta">
                <span>작성자: ${board.userId}</span>
                <span>작성일: ${board.createdAt.length() >= 10 ? board.createdAt.substring(0,10) : board.createdAt}</span>
                <span>조회: ${board.views}</span>
            </div>
        </div>
        <div class="post-content">${board.content}</div>
        <div class="btn-row">
            <a href="${pageContext.request.contextPath}/board/list?type=${type}" class="btn btn-secondary">&larr; 목록</a>
            <c:if test="${sessionScope.loginUser == board.userId}">
                <div style="display:flex; gap:8px;">
                    <a href="${pageContext.request.contextPath}/board/modify?boardId=${board.boardId}&type=${type}" class="btn btn-edit">수정</a>
                    <form method="post" action="${pageContext.request.contextPath}/boards/${board.boardId}/delete"
                          style="margin:0;"
                          onsubmit="return confirm('삭제하시겠습니까?')">
                        <input type="hidden" name="type" value="${type}">
                        <button type="submit" class="btn btn-delete">삭제</button>
                    </form>
                </div>
            </c:if>
        </div>
    </div>
</div>
</body>
</html>
