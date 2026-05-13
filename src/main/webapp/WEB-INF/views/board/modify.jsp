<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>글 수정 - EnjoyTrip</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f5f5; }
        .container { max-width: 800px; margin: 40px auto; padding: 0 16px; }
        h2 { color: #1a73e8; margin-bottom: 24px; }
        .form-card { background: #fff; border-radius: 8px; box-shadow: 0 1px 6px rgba(0,0,0,.08); padding: 32px; }
        .form-group { margin-bottom: 18px; }
        label { display: block; font-size: 14px; font-weight: 600; color: #444; margin-bottom: 6px; }
        input[type=text], textarea { width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 15px; color: #333; }
        input[type=text]:focus, textarea:focus { outline: none; border-color: #1a73e8; }
        textarea { min-height: 260px; resize: vertical; font-family: inherit; }
        .btn-row { display: flex; gap: 10px; justify-content: flex-end; margin-top: 20px; }
        .btn { padding: 10px 24px; border-radius: 6px; font-size: 14px; cursor: pointer; text-decoration: none; text-align: center; border: none; }
        .btn-primary { background: #1a73e8; color: #fff; }
        .btn-secondary { background: #eee; color: #333; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="container">
    <h2>글 수정</h2>
    <div class="form-card">
        <form method="post" action="${pageContext.request.contextPath}/board/update">
            <input type="hidden" name="boardId" value="${board.boardId}">
            <input type="hidden" name="type" value="${type}">
            <div class="form-group">
                <label>제목 *</label>
                <input type="text" name="title" value="${board.title}" required maxlength="300">
            </div>
            <div class="form-group">
                <label>내용 *</label>
                <textarea name="content" required>${board.content}</textarea>
            </div>
            <div class="btn-row">
                <a href="${pageContext.request.contextPath}/board/detail?boardId=${board.boardId}&type=${type}" class="btn btn-secondary">취소</a>
                <button type="submit" class="btn btn-primary">저장</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>
