<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header style="background:#1a73e8; color:#fff; padding:0 20px; box-shadow:0 2px 4px rgba(0,0,0,.2);">
    <nav style="display:flex; align-items:center; height:56px; max-width:1200px; margin:0 auto; gap:16px;">
        <a href="${pageContext.request.contextPath}/"
           style="font-size:22px; font-weight:bold; color:#fff; text-decoration:none; margin-right:auto;">
            EnjoyTrip
        </a>
        <a href="${pageContext.request.contextPath}/attraction/list" class="nav-link">지역별관광지</a>
        <a href="${pageContext.request.contextPath}/plan/list"       class="nav-link">나의여행계획</a>
        <a href="${pageContext.request.contextPath}/hotplace/list"   class="nav-link">핫플레이스</a>
        <a href="${pageContext.request.contextPath}/board/list?type=notice" class="nav-link">공지사항</a>
        <a href="${pageContext.request.contextPath}/board/list?type=free"   class="nav-link">자유게시판</a>

        <c:choose>
            <c:when test="${not empty sessionScope.loginUser}">
                <span style="color:#fff; font-size:14px;">${not empty sessionScope.loginUserName ? sessionScope.loginUserName : sessionScope.loginUser}님</span>
                <a href="${pageContext.request.contextPath}/user/logout" class="nav-link">로그아웃</a>
                <a href="${pageContext.request.contextPath}/user/mypage" class="nav-link">마이페이지</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/user/join"  class="nav-link">회원가입</a>
                <a href="${pageContext.request.contextPath}/user/login" class="nav-link">로그인</a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>
<style>
    .nav-link { color:#fff; text-decoration:none; font-size:14px; padding:4px 8px; border-radius:4px; }
    .nav-link:hover { background:rgba(255,255,255,.2); }
    /* header의 color:#fff 가 form 요소에 상속되지 않도록 전역 리셋 */
    body { color: #333; }
    input, select, textarea, button { color: #333; }
    select option { color: #333; background-color: #fff; }
</style>
