<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>로그인 - EnjoyTrip</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f5f5; }
        .container { max-width: 420px; margin: 80px auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,.1); padding: 40px; }
        h2 { text-align: center; margin-bottom: 28px; color: #1a73e8; }
        .form-group { margin-bottom: 16px; }
        label { display: block; margin-bottom: 6px; font-weight: 600; font-size: 14px; color: #333; }
        input { width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 15px; }
        input:focus { outline: none; border-color: #1a73e8; }
        .btn { width: 100%; padding: 12px; background: #1a73e8; color: #fff; border: none; border-radius: 6px; font-size: 16px; cursor: pointer; margin-top: 8px; }
        .btn:hover { background: #1557b0; }
        .error { color: #d32f2f; background: #fdecea; padding: 10px; border-radius: 6px; margin-bottom: 16px; font-size: 14px; }
        .links { text-align: center; margin-top: 16px; font-size: 14px; }
        .links a { color: #1a73e8; text-decoration: none; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="container">
    <h2>로그인</h2>
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/user/login">
        <div class="form-group">
            <label>아이디</label>
            <input type="text" name="userId" placeholder="아이디 입력" required autofocus>
        </div>
        <div class="form-group">
            <label>비밀번호</label>
            <input type="password" name="userPw" placeholder="비밀번호 입력" required>
        </div>
        <button type="submit" class="btn">로그인</button>
    </form>
    <div class="links">
        <a href="${pageContext.request.contextPath}/user/join">회원가입</a>
    </div>
</div>
</body>
</html>
