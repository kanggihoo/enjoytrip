<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>마이페이지 - EnjoyTrip</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f5f5; }
        .container { max-width: 600px; margin: 60px auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,.1); padding: 40px; }
        h2 { margin-bottom: 28px; color: #1a73e8; border-bottom: 2px solid #e8f0fe; padding-bottom: 12px; }
        .info-row { display: flex; padding: 14px 0; border-bottom: 1px solid #f0f0f0; }
        .info-label { width: 120px; font-weight: 600; color: #555; font-size: 14px; }
        .info-value { flex: 1; color: #222; font-size: 15px; }
        .btn-group { display: flex; gap: 12px; margin-top: 28px; }
        .btn { padding: 10px 24px; border: none; border-radius: 6px; font-size: 14px; cursor: pointer; text-decoration: none; text-align: center; }
        .btn-primary { background: #1a73e8; color: #fff; }
        .btn-danger  { background: #d32f2f; color: #fff; }
        .btn:hover { opacity: .85; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<div class="container">
    <h2>마이페이지</h2>
    <div class="info-row">
        <div class="info-label">아이디</div>
        <div class="info-value">${member.userId}</div>
    </div>
    <div class="info-row">
        <div class="info-label">이름</div>
        <div class="info-value">${member.userName}</div>
    </div>
    <div class="info-row">
        <div class="info-label">이메일</div>
        <div class="info-value">${not empty member.email ? member.email : '-'}</div>
    </div>
    <div class="info-row">
        <div class="info-label">가입일</div>
        <div class="info-value">${member.joinDate}</div>
    </div>
    <div class="btn-group">
        <a href="${pageContext.request.contextPath}/user/modify" class="btn btn-primary">정보 수정</a>
        <a href="${pageContext.request.contextPath}/plans" class="btn btn-primary">내 여행계획</a>
        <button class="btn btn-danger" onclick="confirmDelete()">회원 탈퇴</button>
    </div>
</div>
<form id="deleteForm" method="post" action="${pageContext.request.contextPath}/user/delete" style="display:none;"></form>
<script>
function confirmDelete() {
    if (confirm('정말 탈퇴하시겠습니까? 모든 데이터가 삭제됩니다.')) {
        document.getElementById('deleteForm').submit();
    }
}
</script>
</body>
</html>
