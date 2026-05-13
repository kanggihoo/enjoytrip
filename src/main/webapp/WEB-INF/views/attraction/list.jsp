<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>지역별 관광지 - EnjoyTrip</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f5f5; }
        .page-wrap { max-width: 1300px; margin: 0 auto; padding: 24px 16px; }
        h2 { margin-bottom: 20px; color: #1a73e8; }

        .search-bar {
            background: #fff; padding: 16px 20px; border-radius: 8px;
            box-shadow: 0 1px 6px rgba(0,0,0,.08);
            display: flex; flex-wrap: wrap; gap: 10px;
            margin-bottom: 20px; align-items: flex-end;
        }
        .search-bar select, .search-bar input[type=text] {
            padding: 8px 12px; border: 1px solid #ddd;
            border-radius: 6px; font-size: 14px; min-width: 130px;
            color: #333; background-color: #fff;
        }
        .search-bar input[type=text] { flex: 1; min-width: 200px; }
        .btn-search {
            padding: 8px 22px; background: #1a73e8; color: #fff;
            border: none; border-radius: 6px; font-size: 14px;
            cursor: pointer; white-space: nowrap;
        }
        .btn-search:hover { background: #1557b0; }

        .layout { display: flex; gap: 20px; }
        #map {
            flex: 1; height: 620px; border-radius: 8px;
            box-shadow: 0 1px 6px rgba(0,0,0,.1); background: #e8e8e8;
        }
        .map-fallback {
            display: flex; align-items: center; justify-content: center;
            color: #666; text-align: center; padding: 24px;
        }

        .list-panel {
            width: 360px; height: 620px; overflow-y: auto;
            background: #fff; border-radius: 8px;
            box-shadow: 0 1px 6px rgba(0,0,0,.08);
        }
        .list-header {
            padding: 12px 16px; font-size: 13px; font-weight: 600;
            color: #555; border-bottom: 1px solid #f0f0f0;
            position: sticky; top: 0; background: #fff; z-index: 1;
        }
        .attraction-card {
            padding: 12px 14px; border-bottom: 1px solid #f5f5f5;
            cursor: pointer; display: flex; gap: 10px;
            transition: background .15s;
        }
        .attraction-card:hover { background: #f0f7ff; }
        .card-thumb {
            width: 70px; height: 70px; object-fit: cover;
            border-radius: 6px; flex-shrink: 0;
        }
        .card-no-img {
            width: 70px; height: 70px; border-radius: 6px;
            background: #e8f0fe; display: flex; align-items: center;
            justify-content: center; flex-shrink: 0;
            color: #1a73e8; font-size: 26px;
        }
        .card-body h4 { font-size: 13px; font-weight: 600; color: #222; margin-bottom: 4px; }
        .card-body p { font-size: 12px; color: #888; line-height: 1.4; }
        .card-body .detail-link {
            font-size: 11px; color: #1a73e8; margin-top: 5px;
            display: inline-block;
        }
        .empty-msg { padding: 60px 20px; text-align: center; color: #888; font-size: 14px; }

        .custom-overlay-node { position: relative; bottom: 10px; }
        .info-card {
            background: #fff; border-radius: 8px; padding: 10px 14px;
            box-shadow: 0 2px 12px rgba(0,0,0,.2); min-width: 180px; max-width: 240px;
        }
        .info-card .title { font-size: 13px; font-weight: 700; color: #1a73e8; margin-bottom: 6px; }
        .info-card .info-body { display: flex; gap: 8px; }
        .info-card .img-box img { width: 56px; height: 56px; object-fit: cover; border-radius: 4px; }
        .info-card .text-box { font-size: 11px; color: #666; }

        .loading { padding: 40px; text-align: center; color: #888; }
        .spinner {
            display: inline-block; width: 28px; height: 28px;
            border: 3px solid #e0e0e0; border-top-color: #1a73e8;
            border-radius: 50%; animation: spin .8s linear infinite; margin-bottom: 10px;
        }
        @keyframes spin { to { transform: rotate(360deg); } }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-wrap">
    <h2>지역별 관광지 조회</h2>

    <div class="search-bar">
        <select id="sidoSelect" onchange="onSidoChange(this.value)">
            <option value="">-- 시도 선택 --</option>
            <c:forEach var="sido" items="${sidoList}">
                <option value="${sido.sidoCode}">${sido.sidoName}</option>
            </c:forEach>
        </select>

        <select id="gugunSelect">
            <option value="">-- 구군 선택 --</option>
        </select>

        <select id="contentTypeSelect">
            <option value="">전체 분류</option>
            <option value="12">관광지</option>
            <option value="14">문화시설</option>
            <option value="15">축제/공연/행사</option>
            <option value="25">여행코스</option>
            <option value="28">레포츠</option>
            <option value="32">숙박</option>
            <option value="38">쇼핑</option>
            <option value="39">음식점</option>
        </select>

        <input type="text" id="keywordInput" placeholder="관광지명 검색">
        <button class="btn-search" onclick="searchAttractions()">검색</button>
    </div>

    <div class="layout">
        <div id="map"></div>
        <div class="list-panel">
            <div class="list-header" id="listHeader">관광지를 검색해보세요</div>
            <div id="listBody">
                <div class="empty-msg">
                    지역을 선택하거나 검색어를 입력한 뒤<br>검색 버튼을 누르세요
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript"
    src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${initParam.kakaoJavascriptKey}&libraries=services">
</script>
<script>
const CONTEXT = "${pageContext.request.contextPath}";
const PROXY = CONTEXT + "/attraction/api";

let map = null;
let geocoder = null;
let markers = [];
let markerMap = {};

function initMap() {
    const mapEl = document.getElementById('map');
    if (!window.kakao || !kakao.maps) {
        mapEl.classList.add('map-fallback');
        mapEl.textContent = '카카오 지도를 불러오지 못했습니다. JavaScript 키의 Web 플랫폼 도메인에 127.0.0.1이 등록되어 있는지 확인하세요.';
        return;
    }

    map = new kakao.maps.Map(mapEl, {
        center: new kakao.maps.LatLng(36.5, 127.8),
        level: 13
    });
    geocoder = new kakao.maps.services.Geocoder();
}

async function apiFetch(proxyPath, params) {
    const qs = new URLSearchParams(params || {});
    const res = await fetch(PROXY + "/" + proxyPath + (qs.toString() ? "?" + qs : ""));
    if (!res.ok) throw new Error("HTTP " + res.status);
    return res.json();
}

async function onSidoChange(sidoCode) {
    const sel = document.getElementById('gugunSelect');
    sel.innerHTML = '<option value="">-- 구군 선택 --</option>';
    if (!sidoCode) return;

    try {
        const json = await apiFetch("gugun", { sidoCode: sidoCode });
        const items = json.response.body.items;
        if (!items || !items.item) return;

        const list = Array.isArray(items.item) ? items.item : [items.item];
        list.forEach(function(g) {
            const opt = document.createElement('option');
            opt.value = g.code;
            opt.textContent = g.name;
            sel.appendChild(opt);
        });
    } catch (e) {
        console.error("구군 로드 실패:", e);
    }
}

async function searchAttractions() {
    const sido = document.getElementById('sidoSelect').value;
    const gugun = document.getElementById('gugunSelect').value;
    const typeId = document.getElementById('contentTypeSelect').value;
    const keyword = document.getElementById('keywordInput').value.trim();

    const listHeader = document.getElementById('listHeader');
    const listBody = document.getElementById('listBody');
    listBody.innerHTML = '<div class="loading"><div class="spinner"></div><br>검색 중...</div>';

    const params = {};
    if (sido) params.areaCode = sido;
    if (gugun) params.sigunguCode = gugun;
    if (typeId) params.contentTypeId = typeId;
    if (keyword) params.keyword = keyword;

    try {
        const json = await apiFetch("search", params);
        const body = json.response.body;
        const items = body.items;

        if (!items || !items.item || Number(body.totalCount) === 0) {
            listHeader.textContent = "검색 결과 없음";
            listBody.innerHTML = '<div class="empty-msg">검색 결과가 없습니다.</div>';
            clearMarkers();
            return;
        }

        const spots = Array.isArray(items.item) ? items.item : [items.item];
        listHeader.textContent = "검색 결과 " + spots.length + "건";
        renderList(spots);
        renderMarkers(spots);
    } catch (e) {
        console.error("검색 오류:", e);
        listHeader.textContent = "검색 오류";
        listBody.innerHTML = '<div class="empty-msg">검색 중 오류가 발생했습니다.<br>잠시 후 다시 시도해주세요.</div>';
    }
}

function renderList(spots) {
    const listBody = document.getElementById('listBody');
    listBody.innerHTML = '';

    spots.forEach(function(s) {
        const card = document.createElement('div');
        card.className = 'attraction-card';

        const imgHtml = s.firstimage
            ? '<img class="card-thumb" src="' + escapeAttr(s.firstimage) + '" alt="' + escapeAttr(s.title || '') + '"'
              + ' onerror="this.outerHTML=\'<div class=&quot;card-no-img&quot;>📍</div>\'">'
            : '<div class="card-no-img">📍</div>';

        card.innerHTML = imgHtml
            + '<div class="card-body">'
            + '<h4>' + escapeHtml(s.title || '') + '</h4>'
            + '<p>' + escapeHtml(s.addr1 || '') + '</p>'
            + '<a class="detail-link" href="' + CONTEXT + '/attraction/detail?contentId=' + encodeURIComponent(s.contentid) + '">'
            + '상세보기 &rarr;</a>'
            + '</div>';

        card.addEventListener('click', function() { focusMarker(s.contentid); });
        listBody.appendChild(card);
    });
}

async function renderMarkers(spots) {
    if (!map || !geocoder) return;

    clearMarkers();
    markerMap = {};
    const bounds = new kakao.maps.LatLngBounds();

    const noCoords = spots.filter(function(s) { return !s.mapx || !s.mapy; });
    await Promise.all(noCoords.map(function(s) {
        return new Promise(function(resolve) {
            geocoder.addressSearch(s.addr1, function(result, status) {
                if (status === kakao.maps.services.Status.OK) {
                    s.mapx = result[0].x;
                    s.mapy = result[0].y;
                }
                resolve();
            });
        });
    }));

    spots.forEach(function(s) {
        const lng = parseFloat(s.mapx);
        const lat = parseFloat(s.mapy);
        if (!lat || !lng || lat < 33 || lat > 38.6 || lng < 124.5 || lng > 132) return;

        const pos = new kakao.maps.LatLng(lat, lng);
        const marker = new kakao.maps.Marker({ map: map, position: pos, title: s.title });
        markers.push(marker);
        markerMap[s.contentid] = { marker: marker, pos: pos };
        bounds.extend(pos);

        const overlay = buildOverlay(s, pos);
        kakao.maps.event.addListener(marker, 'mouseover', function() { overlay.setMap(map); });
        kakao.maps.event.addListener(marker, 'mouseout', function() { overlay.setMap(null); });
        kakao.maps.event.addListener(marker, 'click', function() {
            location.href = CONTEXT + '/attraction/detail?contentId=' + encodeURIComponent(s.contentid);
        });
    });

    if (!bounds.isEmpty()) map.setBounds(bounds);
}

function buildOverlay(s, pos) {
    const div = document.createElement('div');
    div.className = 'custom-overlay-node';
    const imgSrc = s.firstimage || 'https://via.placeholder.com/56x56?text=No';
    div.innerHTML = '<div class="info-card">'
        + '<div class="title">' + escapeHtml(s.title || '') + '</div>'
        + '<div class="info-body">'
        + '<div class="img-box"><img src="' + escapeAttr(imgSrc) + '"'
        + ' onerror="this.src=\'https://via.placeholder.com/56x56?text=No\'"></div>'
        + '<div class="text-box">'
        + '<div>' + escapeHtml(s.addr1 || '') + '</div>'
        + '<div>' + escapeHtml(s.tel || '') + '</div>'
        + '</div></div></div>';
    return new kakao.maps.CustomOverlay({ content: div, position: pos, yAnchor: 1.3, zIndex: 3 });
}

function clearMarkers() {
    markers.forEach(function(m) { m.setMap(null); });
    markers = [];
}

function focusMarker(contentid) {
    const m = markerMap[contentid];
    if (!m || !map) return;
    map.setCenter(m.pos);
    map.setLevel(5);
}

function escapeHtml(value) {
    return String(value).replace(/[&<>"']/g, function(ch) {
        return ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[ch];
    });
}

function escapeAttr(value) {
    return escapeHtml(value);
}

document.getElementById('keywordInput').addEventListener('keydown', function(e) {
    if (e.key === 'Enter') searchAttractions();
});

initMap();
</script>
</body>
</html>
