<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>${hotplace.title} - EnjoyTrip</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoJavascriptKey}"></script>
    <style>
        .detail-layout { display:flex; gap:24px; margin-top:20px; }
        .detail-image  { flex:0 0 340px; }
        .detail-image img { width:100%; border-radius:8px; }
        .detail-image .no-img { width:100%; height:240px; background:#f0f0f0; display:flex;
            align-items:center; justify-content:center; color:#aaa; border-radius:8px; }
        .detail-info  { flex:1; }
        .detail-info table th { text-align:right; padding:8px 14px; color:#555; width:100px; }
        .detail-info table td { padding:8px; }
        #map { width:100%; height:300px; border:1px solid #ccc; border-radius:6px; margin-top:16px; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container" style="margin-top:30px;">
    <h2>${hotplace.title}</h2>

    <div class="detail-layout">
        <!-- 이미지 -->
        <div class="detail-image">
            <c:choose>
                <c:when test="${not empty hotplace.imagePath}">
                    <img src="${pageContext.request.contextPath}/${hotplace.imagePath}" alt="${hotplace.title}">
                </c:when>
                <c:otherwise>
                    <div class="no-img">이미지 없음</div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- 정보 -->
        <div class="detail-info">
            <table>
                <tr><th>작성자</th><td>${hotplace.userId}</td></tr>
                <tr><th>다녀온 날짜</th><td>${hotplace.visitDate}</td></tr>
                <tr><th>장소유형</th><td>${hotplace.placeType}</td></tr>
                <tr><th>등록일</th><td>${hotplace.createdAt}</td></tr>
                <tr>
                    <th>설명</th>
                    <td style="white-space:pre-wrap;">${hotplace.description}</td>
                </tr>
            </table>
        </div>
    </div>

    <!-- 지도 -->
    <h3 style="margin-top:24px;">위치</h3>
    <div id="map"></div>

    <!-- 버튼 -->
    <div style="text-align:right; margin-top:16px;">
        <c:if test="${sessionScope.loginUser eq hotplace.userId}">
            <a href="${pageContext.request.contextPath}/hotplace/modify?hotplaceId=${hotplace.hotplaceId}"
               class="btn btn-primary">수정</a>
            <a href="${pageContext.request.contextPath}/hotplace/delete?hotplaceId=${hotplace.hotplaceId}"
               class="btn" style="background:#dc3545; color:#fff;"
               onclick="return confirm('삭제하시겠습니까?')">삭제</a>
        </c:if>
        <a href="${pageContext.request.contextPath}/hotplace/list" class="btn">목록</a>
    </div>
</div>

<script>
    window.onload = function () {
        const lat = ${hotplace.latitude};
        const lng = ${hotplace.longitude};

        if (!lat || !lng) return;

        const pos = new kakao.maps.LatLng(lat, lng);
        const map = new kakao.maps.Map(document.getElementById('map'), {
            center: pos,
            level: 5
        });
        new kakao.maps.Marker({ map: map, position: pos });

        new kakao.maps.InfoWindow({
            content: '<div style="padding:5px;">${hotplace.title}</div>',
            removable: false
        }).open(map, new kakao.maps.Marker({ map: map, position: pos }));
    };
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>
