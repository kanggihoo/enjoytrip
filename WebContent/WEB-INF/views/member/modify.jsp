<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>정보 수정 - EnjoyTrip</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f5f5; }
        .container { max-width: 480px; margin: 60px auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,.1); padding: 40px; }
        h2 { text-align: center; margin-bottom: 28px; color: #1a73e8; }
        .form-group { margin-bottom: 16px; }
        label { display: block; margin-bottom: 6px; font-weight: 600; font-size: 14px; color: #333; }
        input { width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 15px; }
        input:read-only { background: #f9f9f9; color: #888; }
        input:focus:not([readonly]) { outline: none; border-color: #1a73e8; }
        .btn-group { display: flex; gap: 12px; margin-top: 24px; }
        .btn { flex: 1; padding: 12px; border: none; border-radius: 6px; font-size: 15px; cursor: pointer; text-decoration: none; text-align: center; }
        .btn-primary { background: #1a73e8; color: #fff; }
        .btn-secondary { background: #eee; color: #333; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="container">
    <h2>정보 수정</h2>
    <form method="post" action="${pageContext.request.contextPath}/user/modify">
        <div class="form-group">
            <label>아이디</label>
            <input type="text" value="${member.userId}" readonly>
        </div>
        <div class="form-group">
            <label>새 비밀번호</label>
            <input type="password" name="userPw" placeholder="변경할 비밀번호 입력" required>
        </div>
        <div class="form-group">
            <label>이름</label>
            <input type="text" name="userName" value="${member.userName}" required>
        </div>
        <div class="form-group">
            <label>이메일</label>
            <input type="email" name="email" value="${member.email}">
        </div>
        <div class="btn-group">
            <button type="submit" class="btn btn-primary">저장</button>
            <a href="${pageContext.request.contextPath}/user/mypage" class="btn btn-secondary">취소</a>
        </div>
    </form>
</div>
</body>
</html>
