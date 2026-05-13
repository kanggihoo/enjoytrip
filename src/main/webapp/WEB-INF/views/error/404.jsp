<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>404 오류</title>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main style="max-width:800px;margin:60px auto;padding:24px;">
    <h1>${errorCode}</h1>
    <p>${message}</p>
    <a href="${pageContext.request.contextPath}/">홈으로</a>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>
