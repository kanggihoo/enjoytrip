<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>핫플레이스 - EnjoyTrip</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoJavascriptKey}"></script>
    <style>
        .hotplace-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:20px; margin-top:20px; }
        .hotplace-card { border:1px solid #ddd; border-radius:8px; overflow:hidden; transition:box-shadow .2s; }
        .hotplace-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,.15); }
        .hotplace-card img { width:100%; height:180px; object-fit:cover; background:#eee; }
        .hotplace-card .info { padding:12px; }
        .hotplace-card .info h4 { margin:0 0 6px; }
        .hotplace-card .info .meta { font-size:12px; color:#888; }
        #map { width:100%; height:400px; border:1px solid #ccc; border-radius:4px; margin-bottom:20px; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container" style="margin-top:30px;">
    <div style="display:flex; justify-content:space-between; align-items:center;">
        <h2>나만의 핫플레이스</h2>
        <c:if test="${not empty sessionScope.loginUser}">
            <a href="${pageContext.request.contextPath}/hotplaces/new" class="btn btn-primary">+ 핫플레이스 등록</a>
        </c:if>
    </div>

    <!-- 전체 핫플레이스를 지도에 표시 -->
    <div id="map"></div>

    <!-- 카드 그리드 -->
    <c:choose>
        <c:when test="${empty hotplaces}">
            <p style="text-align:center; padding:40px; color:#888;">등록된 핫플레이스가 없습니다.</p>
        </c:when>
        <c:otherwise>
            <div class="hotplace-grid">
                <c:forEach var="h" items="${hotplaces}">
                    <div class="hotplace-card">
                        <a href="${pageContext.request.contextPath}/hotplaces/${h.hotplaceId}">
                            <c:choose>
                                <c:when test="${not empty h.imagePath}">
                                    <img src="${pageContext.request.contextPath}/${h.imagePath}" alt="${h.title}">
                                </c:when>
                                <c:otherwise>
                                    <div style="width:100%;height:180px;background:#f0f0f0;display:flex;align-items:center;justify-content:center;color:#aaa;">이미지 없음</div>
                                </c:otherwise>
                            </c:choose>
                        </a>
                        <div class="info">
                            <h4>
                                <a href="${pageContext.request.contextPath}/hotplaces/${h.hotplaceId}"
                                   style="text-decoration:none; color:#333;">${h.title}</a>
                            </h4>
                            <p class="meta">
                                ${h.placeType} &nbsp;|&nbsp; ${h.visitDate}<br>
                                작성자: ${h.userId}
                            </p>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script>
    window.onload = function () {
        const map = new kakao.maps.Map(document.getElementById('map'), {
            center: new kakao.maps.LatLng(36.5, 127.5),
            level: 12
        });

        const places = [
            <c:forEach var="h" items="${hotplaces}" varStatus="s">
                { id: ${h.hotplaceId}, title: '${h.title}', lat: ${h.latitude}, lng: ${h.longitude} }<c:if test="${!s.last}">,</c:if>
            </c:forEach>
        ];

        const bounds = new kakao.maps.LatLngBounds();
        const CONTEXT = '${pageContext.request.contextPath}';

        places.forEach(function(p) {
            if (!p.lat || !p.lng) return;
            const pos    = new kakao.maps.LatLng(p.lat, p.lng);
            const marker = new kakao.maps.Marker({ map: map, position: pos });
            bounds.extend(pos);

            const iw = new kakao.maps.InfoWindow({
                content: '<div style="padding:5px; font-size:13px;"><a href="' + CONTEXT +
                         '/hotplaces/' + p.id + '">' + p.title + '</a></div>'
            });
            kakao.maps.event.addListener(marker, 'click', function() { iw.open(map, marker); });
        });

        if (places.length > 0) map.setBounds(bounds);
    };
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>
