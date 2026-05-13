<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>핫플레이스 수정 - EnjoyTrip</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoJavascriptKey}&libraries=services"></script>
    <style>
        #map { width:100%; height:350px; border:1px solid #ccc; border-radius:4px; }
        .form-table th { width:120px; text-align:right; padding:8px 12px; }
        .form-table td { padding:8px; }
        .preview-img { max-width:200px; max-height:150px; margin-top:8px; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container" style="margin-top:30px;">
    <h2>핫플레이스 수정</h2>

    <form action="${pageContext.request.contextPath}/hotplace/update"
          method="post" enctype="multipart/form-data">

        <input type="hidden" name="hotplaceId" value="${hotplace.hotplaceId}">
        <input type="hidden" name="existingImage" value="${hotplace.imagePath}">

        <!-- 지도 -->
        <div style="margin-bottom:10px; display:flex; gap:8px;">
            <input type="text" id="searchKeyword" placeholder="장소 재검색" style="flex:1; padding:8px;">
            <button type="button" onclick="searchPlace()">검색</button>
        </div>
        <div id="map" style="margin-bottom:12px;"></div>
        <p id="selectedAddress" style="font-size:13px; color:#555; margin-bottom:10px;">
            현재 위치: (${hotplace.latitude}, ${hotplace.longitude})
        </p>

        <input type="hidden" name="latitude"  id="latitude"  value="${hotplace.latitude}">
        <input type="hidden" name="longitude" id="longitude" value="${hotplace.longitude}">

        <table class="form-table" style="width:100%;">
            <tr>
                <th>핫플 이름 *</th>
                <td><input type="text" name="title" value="${hotplace.title}" required style="width:100%;"></td>
            </tr>
            <tr>
                <th>다녀온 날짜</th>
                <td><input type="date" name="visitDate" value="${hotplace.visitDate}"></td>
            </tr>
            <tr>
                <th>장소유형</th>
                <td>
                    <select name="placeType" style="padding:6px;">
                        <option value="">-- 선택 --</option>
                        <option value="관광지"   <c:if test="${hotplace.placeType eq '관광지'}">selected</c:if>>관광지</option>
                        <option value="음식점"   <c:if test="${hotplace.placeType eq '음식점'}">selected</c:if>>음식점</option>
                        <option value="카페"     <c:if test="${hotplace.placeType eq '카페'}">selected</c:if>>카페</option>
                        <option value="숙박"     <c:if test="${hotplace.placeType eq '숙박'}">selected</c:if>>숙박</option>
                        <option value="쇼핑"     <c:if test="${hotplace.placeType eq '쇼핑'}">selected</c:if>>쇼핑</option>
                        <option value="문화시설" <c:if test="${hotplace.placeType eq '문화시설'}">selected</c:if>>문화시설</option>
                        <option value="기타"     <c:if test="${hotplace.placeType eq '기타'}">selected</c:if>>기타</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>핫플 설명</th>
                <td><textarea name="description" rows="4" style="width:100%;">${hotplace.description}</textarea></td>
            </tr>
            <tr>
                <th>현재 사진</th>
                <td>
                    <c:if test="${not empty hotplace.imagePath}">
                        <img src="${pageContext.request.contextPath}/${hotplace.imagePath}"
                             class="preview-img" alt="현재 이미지">
                    </c:if>
                </td>
            </tr>
            <tr>
                <th>새 사진 선택</th>
                <td>
                    <input type="file" name="image" id="imageInput" accept="image/*"
                           onchange="previewImage(this)">
                    <img id="newPreview" style="max-width:200px; max-height:150px; margin-top:8px; display:none;" alt="새 이미지">
                    <p style="font-size:12px; color:#888;">선택하지 않으면 기존 사진이 유지됩니다.</p>
                </td>
            </tr>
        </table>

        <div style="text-align:center; margin-top:20px;">
            <button type="submit" class="btn btn-primary">수정 완료</button>
            <a href="${pageContext.request.contextPath}/hotplace/detail?hotplaceId=${hotplace.hotplaceId}" class="btn">취소</a>
        </div>
    </form>
</div>

<script>
    let map, selectedMarker;

    window.onload = function () {
        const initLat = ${hotplace.latitude != 0 ? hotplace.latitude : 37.5665};
        const initLng = ${hotplace.longitude != 0 ? hotplace.longitude : 126.9780};
        const pos = new kakao.maps.LatLng(initLat, initLng);

        map = new kakao.maps.Map(document.getElementById('map'), {
            center: pos, level: 5
        });

        // 기존 위치에 마커 표시
        selectedMarker = new kakao.maps.Marker({ map: map, position: pos });

        kakao.maps.event.addListener(map, 'click', function(e) {
            placeMarker(e.latLng);
        });
    };

    function searchPlace() {
        const keyword = document.getElementById('searchKeyword').value.trim();
        if (!keyword) { alert('검색어를 입력하세요.'); return; }
        const ps = new kakao.maps.services.Places();
        ps.keywordSearch(keyword, function(data, status) {
            if (status !== kakao.maps.services.Status.OK) {
                alert('검색 결과가 없습니다.'); return;
            }
            const latlng = new kakao.maps.LatLng(data[0].y, data[0].x);
            map.setCenter(latlng);
            placeMarker(latlng);
            document.getElementById('selectedAddress').textContent = '선택된 장소: ' + data[0].place_name;
        });
    }

    function placeMarker(latlng) {
        if (selectedMarker) selectedMarker.setMap(null);
        selectedMarker = new kakao.maps.Marker({ map: map, position: latlng });
        document.getElementById('latitude').value  = latlng.getLat();
        document.getElementById('longitude').value = latlng.getLng();
        document.getElementById('selectedAddress').textContent =
            '선택된 위치: (' + latlng.getLat().toFixed(6) + ', ' + latlng.getLng().toFixed(6) + ')';
    }

    function previewImage(input) {
        const img = document.getElementById('newPreview');
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) { img.src = e.target.result; img.style.display = 'block'; };
            reader.readAsDataURL(input.files[0]);
        }
    }
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>
