<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>나의 여행계획 - EnjoyTrip</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container" style="margin-top:30px;">
    <h2>나의 여행계획</h2>

    <div style="text-align:right; margin-bottom:12px;">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/plan/write">+ 새 계획 만들기</a>
    </div>

    <c:choose>
        <c:when test="${empty plans}">
            <p style="text-align:center; padding:40px; color:#888;">등록된 여행 계획이 없습니다.</p>
        </c:when>
        <c:otherwise>
            <table class="table">
                <thead>
                    <tr>
                        <th>번호</th>
                        <th>제목</th>
                        <th>출발일</th>
                        <th>도착일</th>
                        <th>예산(원)</th>
                        <th>등록일</th>
                        <th>관리</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="plan" items="${plans}" varStatus="status">
                        <tr>
                            <td>${status.count}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/plan/detail?planId=${plan.planId}">
                                    ${plan.title}
                                </a>
                            </td>
                            <td>${plan.startDate}</td>
                            <td>${plan.endDate}</td>
                            <td>${plan.totalBudget}</td>
                            <td>${plan.createdAt}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/plan/modify?planId=${plan.planId}">수정</a>
                                &nbsp;|&nbsp;
                                <a href="${pageContext.request.contextPath}/plan/delete?planId=${plan.planId}"
                                   onclick="return confirm('삭제하시겠습니까?')">삭제</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>
