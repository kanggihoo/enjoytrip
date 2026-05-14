<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>핫플레이스 등록 - EnjoyTrip</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoJavascriptKey}&libraries=services"></script>
    <style>
        #map { width:100%; height:400px; border:1px solid #ccc; border-radius:4px; }
        .preview-img { max-width:200px; max-height:150px; margin-top:8px; display:none; }
        .form-table th { width:120px; text-align:right; padding:8px 12px; vertical-align:top; }
        .form-table td { padding:8px; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container" style="margin-top:30px;">
    <h2>나만의 핫플레이스 등록</h2>

    <form action="${pageContext.request.contextPath}/hotplaces"
          method="post" enctype="multipart/form-data" id="hotplaceForm">

        <!-- 지도 + 검색 -->
        <div style="margin-bottom:12px; display:flex; gap:8px;">
            <input type="text" id="searchKeyword" placeholder="장소 검색" style="flex:1; padding:8px;">
            <button type="button" onclick="searchPlace()">검색</button>
        </div>
        <div id="map" style="margin-bottom:15px;"></div>
        <p id="selectedAddress" style="color:#555; font-size:13px; margin-bottom:10px;">
            지도를 클릭하거나 검색하여 위치를 선택하세요.
        </p>

        <!-- 좌표 (숨김 필드) -->
        <input type="hidden" name="latitude"  id="latitude"  value="0">
        <input type="hidden" name="longitude" id="longitude" value="0">

        <table class="form-table" style="width:100%;">
            <tr>
                <th>핫플 이름 *</th>
                <td><input type="text" name="title" required placeholder="장소 이름" style="width:100%;"></td>
            </tr>
            <tr>
                <th>다녀온 날짜</th>
                <td><input type="date" name="visitDate"></td>
            </tr>
            <tr>
                <th>장소유형</th>
                <td>
                    <select name="placeType" style="padding:6px;">
                        <option value="">-- 선택 --</option>
                        <option value="관광지">관광지</option>
                        <option value="음식점">음식점</option>
                        <option value="카페">카페</option>
                        <option value="숙박">숙박</option>
                        <option value="쇼핑">쇼핑</option>
                        <option value="문화시설">문화시설</option>
                        <option value="기타">기타</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>핫플 설명</th>
                <td><textarea name="description" rows="4" style="width:100%;" placeholder="이 장소를 소개해 주세요."></textarea></td>
            </tr>
            <tr>
                <th>사진 선택</th>
                <td>
                    <input type="file" name="image" id="imageInput" accept="image/*"
                           onchange="previewImage(this)">
                    <img id="previewImg" class="preview-img" alt="미리보기">
                </td>
            </tr>
        </table>

        <div style="text-align:center; margin-top:20px;">
            <button type="submit" class="btn btn-primary">등록</button>
            <a href="${pageContext.request.contextPath}/hotplaces" class="btn">취소</a>
        </div>
    </form>
</div>

<script>
    let map, selectedMarker, geocoder;

    window.onload = function () {
        map = new kakao.maps.Map(document.getElementById('map'), {
            center: new kakao.maps.LatLng(37.5665, 126.9780),
            level: 7
        });
        geocoder = new kakao.maps.services.Geocoder();

        // 지도 클릭으로 위치 선택
        kakao.maps.event.addListener(map, 'click', function(e) {
            placeMarker(e.latLng);
            reverseGeocode(e.latLng);
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
            const first = data[0];
            const latlng = new kakao.maps.LatLng(first.y, first.x);
            map.setCenter(latlng);
            placeMarker(latlng);
            document.getElementById('selectedAddress').textContent = '선택된 장소: ' + first.place_name;
            document.getElementById('latitude').value  = first.y;
            document.getElementById('longitude').value = first.x;
        });
    }

    function placeMarker(latlng) {
        if (selectedMarker) selectedMarker.setMap(null);
        selectedMarker = new kakao.maps.Marker({ map: map, position: latlng });
        document.getElementById('latitude').value  = latlng.getLat();
        document.getElementById('longitude').value = latlng.getLng();
    }

    function reverseGeocode(latlng) {
        geocoder.coord2Address(latlng.getLng(), latlng.getLat(), function(result, status) {
            if (status === kakao.maps.services.Status.OK) {
                const addr = result[0].road_address
                    ? result[0].road_address.address_name
                    : result[0].address.address_name;
                document.getElementById('selectedAddress').textContent = '선택된 위치: ' + addr;
            }
        });
    }

    function previewImage(input) {
        const img = document.getElementById('previewImg');
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                img.src = e.target.result;
                img.style.display = 'block';
            };
            reader.readAsDataURL(input.files[0]);
        }
    }
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>
