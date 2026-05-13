<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>${plan.title} - EnjoyTrip</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${initParam.kakaoJavascriptKey}"></script>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container" style="margin-top:30px;">
    <h2>${plan.title}</h2>

    <table class="form-table">
        <tr><th>출발일</th><td>${plan.startDate}</td></tr>
        <tr><th>도착일</th><td>${plan.endDate}</td></tr>
        <tr><th>예산</th><td>${plan.totalBudget}원</td></tr>
        <tr><th>메모</th><td>${plan.memo}</td></tr>
        <tr><th>등록일</th><td>${plan.createdAt}</td></tr>
    </table>

    <hr style="margin:20px 0;">

    <div style="display:flex; gap:20px;">
        <!-- 방문지 목록 -->
        <div style="width:250px; flex-shrink:0;">
            <h3>여행지 목록</h3>
            <ul id="placeList" style="list-style:none; padding:0;">
                <c:forEach var="detail" items="${plan.details}" varStatus="s">
                    <li id="detail-${detail.detailId}"
                        draggable="true"
                        data-detail-id="${detail.detailId}"
                        data-lat="${detail.latitude}"
                        data-lng="${detail.longitude}"
                        style="padding:8px 10px; margin:4px 0; background:#e8f4fd; border-radius:4px; cursor:grab;">
                        <strong>${s.count}.</strong> ${detail.title}
                        <br><small>${detail.visitDate}</small>
                    </li>
                </c:forEach>
            </ul>

            <!-- 방문지 추가 폼 -->
            <c:if test="${sessionScope.loginUser eq plan.userId}">
                <h4 style="margin-top:15px;">방문지 추가</h4>
                <form action="${pageContext.request.contextPath}/plan/addDetail" method="post">
                    <input type="hidden" name="planId" value="${plan.planId}">
                    <input type="hidden" name="contentId" id="addContentId" value="0">
                    <input type="text"   name="title"     id="addTitle"     placeholder="장소명" style="width:100%; margin-bottom:5px;" required>
                    <input type="hidden" name="latitude"  id="addLat"  value="0">
                    <input type="hidden" name="longitude" id="addLng"  value="0">
                    <input type="date"   name="visitDate" style="width:100%; margin-bottom:5px;">
                    <textarea name="memo" placeholder="메모" style="width:100%; height:60px;"></textarea>
                    <button type="submit" style="width:100%; margin-top:5px;">추가</button>
                </form>
            </c:if>
        </div>

        <!-- 지도 -->
        <div style="flex:1;">
            <div id="map" style="width:100%; height:500px; border:1px solid #ccc;"></div>
        </div>
    </div>

    <div style="text-align:right; margin-top:15px;">
        <c:if test="${sessionScope.loginUser eq plan.userId}">
            <a href="${pageContext.request.contextPath}/plan/modify?planId=${plan.planId}" class="btn btn-primary">수정</a>
            <a href="${pageContext.request.contextPath}/plan/delete?planId=${plan.planId}"
               class="btn" onclick="return confirm('삭제하시겠습니까?')" style="background:#dc3545; color:#fff;">삭제</a>
        </c:if>
        <a href="${pageContext.request.contextPath}/plan/list" class="btn">목록</a>
    </div>
</div>

<script>
    const CONTEXT = '${pageContext.request.contextPath}';
    let map;

    window.onload = function () {
        map = new kakao.maps.Map(document.getElementById('map'), {
            center: new kakao.maps.LatLng(36.5, 127.5),
            level: 12
        });

        const details = [
            <c:forEach var="d" items="${plan.details}" varStatus="s">
                { detailId: ${d.detailId}, title: '${d.title}', lat: ${d.latitude}, lng: ${d.longitude} }<c:if test="${!s.last}">,</c:if>
            </c:forEach>
        ];

        if (details.length === 0) return;

        const bounds = new kakao.maps.LatLngBounds();
        const positions = [];

        details.forEach(function(d, idx) {
            const pos = new kakao.maps.LatLng(d.lat, d.lng);
            bounds.extend(pos);
            positions.push(pos);

            const marker = new kakao.maps.Marker({ map: map, position: pos });
            new kakao.maps.InfoWindow({
                content: '<div style="padding:5px;">' + (idx + 1) + '. ' + d.title + '</div>',
                removable: false
            }).open(map, marker);
        });

        // 방문 경로 선 그리기
        if (positions.length > 1) {
            new kakao.maps.Polyline({
                map: map,
                path: positions,
                strokeWeight: 3,
                strokeColor: '#5B8FF9',
                strokeOpacity: 0.8
            });
        }
        map.setBounds(bounds);

        // 드래그앤드롭 순서 변경 → AJAX
        initDragDrop();
    };

    function initDragDrop() {
        const list = document.getElementById('placeList');
        let dragged = null;

        list.querySelectorAll('li').forEach(function(li) {
            li.addEventListener('dragstart', function() { dragged = this; });
            li.addEventListener('dragover',  function(e) { e.preventDefault(); });
            li.addEventListener('drop', function() {
                if (dragged === this) return;
                const allItems = [...list.querySelectorAll('li')];
                const toIdx = allItems.indexOf(this);
                list.insertBefore(dragged, this);

                // 새 순서를 서버에 반영
                list.querySelectorAll('li').forEach(function(item, newOrder) {
                    fetch(CONTEXT + '/plan/updateOrder', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body: 'detailId=' + item.dataset.detailId + '&visitOrder=' + (newOrder + 1)
                    });
                });
            });
        });
    }
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>
