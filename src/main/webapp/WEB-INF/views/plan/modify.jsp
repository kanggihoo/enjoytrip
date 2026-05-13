<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>여행계획 수정 - EnjoyTrip</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container" style="margin-top:30px;">
    <h2>여행계획 수정</h2>

    <form action="${pageContext.request.contextPath}/plan/update" method="post">
        <input type="hidden" name="planId" value="${plan.planId}">
        <table class="form-table">
            <tr>
                <th>제목 *</th>
                <td><input type="text" name="title" value="${plan.title}" required style="width:100%;"></td>
            </tr>
            <tr>
                <th>출발일</th>
                <td><input type="date" name="startDate" value="${plan.startDate}"></td>
            </tr>
            <tr>
                <th>도착일</th>
                <td><input type="date" name="endDate" value="${plan.endDate}"></td>
            </tr>
            <tr>
                <th>예산(원)</th>
                <td><input type="number" name="totalBudget" value="${plan.totalBudget}" min="0"></td>
            </tr>
            <tr>
                <th>메모</th>
                <td><textarea name="memo" rows="4" style="width:100%;">${plan.memo}</textarea></td>
            </tr>
        </table>

        <div style="text-align:center; margin-top:20px;">
            <button type="submit" class="btn btn-primary">수정 완료</button>
            <a href="${pageContext.request.contextPath}/plan/detail?planId=${plan.planId}" class="btn">취소</a>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>
