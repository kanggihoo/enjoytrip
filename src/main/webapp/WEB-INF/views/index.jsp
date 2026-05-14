<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EnjoyTrip - 즐거운 여행의 시작</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: "Segoe UI", sans-serif; background: #f5f5f5; }

        .hero {
            background: linear-gradient(135deg, #1a73e8 0%, #0d47a1 100%);
            color: #fff;
            padding: 80px 20px;
            text-align: center;
        }

        .hero h1 {
            font-size: 48px;
            font-weight: 800;
            margin-bottom: 16px;
            letter-spacing: -1px;
        }

        .hero p {
            font-size: 20px;
            opacity: 0.85;
            margin-bottom: 36px;
        }

        .hero-btn {
            display: inline-block;
            padding: 14px 36px;
            background: #fff;
            color: #1a73e8;
            border-radius: 30px;
            font-size: 16px;
            font-weight: 700;
            text-decoration: none;
            box-shadow: 0 4px 14px rgba(0, 0, 0, 0.2);
        }

        .hero-btn:hover {
            transform: translateY(-2px);
            transition: 0.2s;
        }

        .features {
            max-width: 1100px;
            margin: 60px auto;
            padding: 0 20px;
        }

        .features h2 {
            text-align: center;
            font-size: 28px;
            color: #333;
            margin-bottom: 40px;
        }

        .cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 24px;
        }

        .feat-card {
            display: block;
            background: #fff;
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
            padding: 32px 24px;
            text-align: center;
            transition: transform 0.2s, box-shadow 0.2s;
            text-decoration: none;
            color: inherit;
        }

        .feat-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
        }

        .feat-icon {
            font-size: 48px;
            margin-bottom: 16px;
        }

        .feat-card h3 {
            font-size: 18px;
            color: #222;
            margin-bottom: 10px;
        }

        .feat-card p {
            font-size: 14px;
            color: #777;
            line-height: 1.6;
        }

        .welcome {
            background: #e8f0fe;
            border-radius: 12px;
            padding: 24px 32px;
            max-width: 700px;
            margin: 0 auto 60px;
            text-align: center;
        }

        .welcome h3 {
            color: #1a73e8;
            font-size: 20px;
            margin-bottom: 8px;
        }

        .welcome p {
            color: #555;
        }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="hero">
    <h1>EnjoyTrip</h1>
    <p>대한민국 구석구석, 즐거운 여행을 계획해보세요.</p>
    <a href="${pageContext.request.contextPath}/attractions" class="hero-btn">관광지 둘러보기</a>
</div>

<div class="features">
    <c:if test="${not empty sessionScope.loginUser}">
        <div class="welcome">
            <h3>${sessionScope.loginUserName}님, 환영합니다.</h3>
            <p>오늘도 즐거운 여행 계획을 세워보세요.</p>
        </div>
    </c:if>

    <h2>EnjoyTrip 서비스</h2>
    <div class="cards">
        <a href="${pageContext.request.contextPath}/attractions" class="feat-card">
            <div class="feat-icon">&#127981;</div>
            <h3>지역별 관광지</h3>
            <p>시/도, 구/군별로 관광지를 검색하고 지도에서 위치를 확인하세요.</p>
        </a>

        <a href="${pageContext.request.contextPath}/plans" class="feat-card">
            <div class="feat-icon">&#128204;</div>
            <h3>나의 여행계획</h3>
            <p>방문할 관광지를 선택하고 나만의 여행 루트를 만들어보세요.</p>
        </a>

        <a href="${pageContext.request.contextPath}/hotplaces" class="feat-card">
            <div class="feat-icon">&#128293;</div>
            <h3>핫플레이스</h3>
            <p>직접 방문한 장소를 사진과 함께 다른 사람과 공유해보세요.</p>
        </a>

        <c:choose>
            <c:when test="${not empty sessionScope.loginUser}">
                <a href="${pageContext.request.contextPath}/user/mypage" class="feat-card">
                    <div class="feat-icon">&#128100;</div>
                    <h3>마이페이지</h3>
                    <p>내 정보를 관리하고 여행 계획을 한눈에 확인하세요.</p>
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/user/join" class="feat-card">
                    <div class="feat-icon">&#128100;</div>
                    <h3>회원가입</h3>
                    <p>지금 가입하고 나만의 여행계획을 저장하고 관리하세요.</p>
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
