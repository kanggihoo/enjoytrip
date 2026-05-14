<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>관광지 상세 - EnjoyTrip</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f5f5; }
        .container { max-width: 900px; margin: 40px auto; padding: 0 16px; }

        /* 로딩 */
        #loading { text-align: center; padding: 80px 0; color: #888; }
        .spinner {
            display: inline-block; width: 40px; height: 40px;
            border: 4px solid #e0e0e0; border-top-color: #1a73e8;
            border-radius: 50%; animation: spin .8s linear infinite; margin-bottom: 16px;
        }
        @keyframes spin { to { transform: rotate(360deg); } }

        /* 카드 */
        #detail-card { display: none; background: #fff; border-radius: 8px; box-shadow: 0 1px 8px rgba(0,0,0,.1); overflow: hidden; }
        .hero { width: 100%; max-height: 340px; object-fit: cover; display: block; }
        .no-hero { width: 100%; height: 180px; background: #e8f0fe; display: flex; align-items: center; justify-content: center; font-size: 70px; }
        .body { padding: 32px; }
        h1 { font-size: 26px; color: #222; margin-bottom: 14px; }
        .meta { display: flex; flex-wrap: wrap; gap: 14px; font-size: 14px; color: #777; margin-bottom: 24px; }
        .overview { font-size: 15px; color: #444; line-height: 1.8; margin-bottom: 28px; white-space: pre-wrap; }
        #map { width: 100%; height: 360px; border-radius: 8px; margin-bottom: 24px; background: #eee; }
        .btn-back { display: inline-block; padding: 10px 24px; background: #1a73e8; color: #fff; border-radius: 6px; text-decoration: none; font-size: 14px; }
        .btn-back:hover { background: #1557b0; }

        /* 오류 */
        #error-msg { display: none; text-align: center; padding: 80px; color: #aaa; font-size: 16px; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container">
    <div id="loading">
        <div class="spinner"></div>
        <p>관광지 정보를 불러오는 중...</p>
    </div>

    <div id="error-msg">
        관광지 정보를 찾을 수 없습니다.<br><br>
        <a href="${pageContext.request.contextPath}/attractions" class="btn-back">&larr; 목록으로</a>
    </div>

    <div id="detail-card">
        <div id="heroArea"></div>
        <div class="body">
            <h1 id="d-title"></h1>
            <div class="meta" id="d-meta"></div>
            <div class="overview" id="d-overview"></div>
            <div id="map"></div>
            <a href="${pageContext.request.contextPath}/attractions" class="btn-back">&larr; 목록으로</a>
        </div>
    </div>
</div>

<script type="text/javascript"
    src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoJavascriptKey}">
</script>
<script>
const PROXY = "${pageContext.request.contextPath}/api/attractions";

// URL에서 contentId 추출
const contentId = '${contentId}' || new URLSearchParams(window.location.search).get('contentId');

(async () => {
    if (!contentId) { showError(); return; }

    try {
        const res = await fetch(PROXY + "/" + encodeURIComponent(contentId));
        const json = await res.json();

        const body  = json.response.body;
        if (!body || !body.items || !body.items.item) { showError(); return; }

        const item = Array.isArray(body.items.item) ? body.items.item[0] : body.items.item;
        renderDetail(item);
    } catch (e) {
        console.error(e);
        showError();
    }
})();

function renderDetail(item) {
    document.getElementById('loading').style.display = 'none';
    document.getElementById('detail-card').style.display = 'block';
    document.title = (item.title || '관광지') + ' - EnjoyTrip';

    // Build image HTML with string concatenation to avoid JSP EL parsing.
    var heroArea = document.getElementById('heroArea');
    if (item.firstimage) {
        heroArea.innerHTML = '<img class="hero" src="' + item.firstimage + '" alt="' + (item.title || '') + '">';
    } else {
        heroArea.innerHTML = '<div class="no-hero">&#128205;</div>';
    }

    document.getElementById('d-title').textContent = item.title || '';

    // 메타 정보
    var meta = [];
    if (item.addr1) meta.push('&#128205; ' + item.addr1 + ' ' + (item.addr2 || ''));
    if (item.tel)   meta.push('&#128222; ' + item.tel);
    document.getElementById('d-meta').innerHTML = meta.map(function(m) {
        return '<span>' + m + '</span>';
    }).join('');

    // 개요
    if (item.overview) {
        var tmp = document.createElement('div');
        tmp.innerHTML = item.overview;
        document.getElementById('d-overview').textContent = tmp.textContent || tmp.innerText;
    }

    // 지도
    var lat = parseFloat(item.mapy);
    var lng = parseFloat(item.mapx);
    if (lat && lng) {
        // 👇 카카오맵 객체가 제대로 로드되었는지 확인하는 방어 코드 추가
        if (window.kakao && window.kakao.maps) {
            var pos = new kakao.maps.LatLng(lat, lng);
            var kakaoMap = new kakao.maps.Map(document.getElementById('map'), {
                center: pos, level: 5
            });
            var marker = new kakao.maps.Marker({ map: kakaoMap, position: pos });
            var info   = new kakao.maps.InfoWindow({
                content: '<div style="padding:8px 12px;font-size:13px;">' + (item.title || '') + '</div>',
                removable: true
            });
            info.open(kakaoMap, marker);
        } else {
            // 카카오맵 로드 실패 시 지도 영역에 안내 문구 표시
            var mapEl = document.getElementById('map');
            mapEl.style.display = 'flex';
            mapEl.style.alignItems = 'center';
            mapEl.style.justifyContent = 'center';
            mapEl.style.color = '#888';
            mapEl.textContent = '카카오 지도를 불러오지 못했습니다. (API 키 및 도메인 확인)';
        }
    } else {
        document.getElementById('map').style.display = 'none';
    }
}

function showError() {
    document.getElementById('loading').style.display = 'none';
    document.getElementById('error-msg').style.display = 'block';
}
</script>
</body>
</html>
