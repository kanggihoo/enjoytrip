<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원가입 - EnjoyTrip</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f5f5; }
        .container { max-width: 480px; margin: 60px auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,.1); padding: 40px; }
        h2 { text-align: center; margin-bottom: 28px; color: #1a73e8; }
        .form-group { margin-bottom: 16px; }
        label { display: block; margin-bottom: 6px; font-weight: 600; font-size: 14px; color: #333; }
        input { width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 15px; }
        input:focus { outline: none; border-color: #1a73e8; }
        .btn { width: 100%; padding: 12px; background: #1a73e8; color: #fff; border: none; border-radius: 6px; font-size: 16px; cursor: pointer; margin-top: 8px; }
        .btn:hover { background: #1557b0; }
        .error { color: #d32f2f; background: #fdecea; padding: 10px; border-radius: 6px; margin-bottom: 16px; font-size: 14px; }
        .hint { font-size: 12px; color: #888; margin-top: 4px; }
        .links { text-align: center; margin-top: 16px; font-size: 14px; }
        .links a { color: #1a73e8; text-decoration: none; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="container">
    <h2>회원가입</h2>
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/user/join">
        <div class="form-group">
            <label>아이디 *</label>
            <input type="text" name="userId" placeholder="4~20자 영문, 숫자" required maxlength="20">
        </div>
        <div class="form-group">
            <label>비밀번호 *</label>
            <input type="password" name="userPw" id="pw1" placeholder="6자 이상" required>
        </div>
        <div class="form-group">
            <label>비밀번호 확인 *</label>
            <input type="password" id="pw2" placeholder="비밀번호 재입력" required>
            <div class="hint" id="pwHint"></div>
        </div>
        <div class="form-group">
            <label>이름 *</label>
            <input type="text" name="userName" placeholder="이름 입력" required>
        </div>
        <div class="form-group">
            <label>이메일</label>
            <input type="email" name="email" placeholder="example@email.com">
        </div>
        <button type="submit" class="btn">가입하기</button>
    </form>
    <div class="links">
        이미 계정이 있으신가요? <a href="${pageContext.request.contextPath}/user/login">로그인</a>
    </div>
</div>
<script>
    document.querySelector('form').addEventListener('submit', function(e) {
        if (document.getElementById('pw1').value !== document.getElementById('pw2').value) {
            e.preventDefault();
            document.getElementById('pwHint').style.color = '#d32f2f';
            document.getElementById('pwHint').textContent = '비밀번호가 일치하지 않습니다.';
        }
    });
    document.getElementById('pw2').addEventListener('input', function() {
        const hint = document.getElementById('pwHint');
        if (this.value === document.getElementById('pw1').value) {
            hint.style.color = '#2e7d32';
            hint.textContent = '비밀번호가 일치합니다.';
        } else {
            hint.style.color = '#d32f2f';
            hint.textContent = '비밀번호가 일치하지 않습니다.';
        }
    });
</script>
</body>
</html>
