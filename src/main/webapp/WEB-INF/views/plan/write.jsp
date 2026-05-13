<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>여행계획 등록 - EnjoyTrip</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f5f5; }
        .page-wrap { max-width: 1400px; margin: 0 auto; padding: 24px 16px; }
        h2 { color: #1a73e8; margin-bottom: 20px; }

        /* 상단 폼 */
        .plan-form { background: #fff; border-radius: 8px; box-shadow: 0 1px 6px rgba(0,0,0,.08); padding: 24px; margin-bottom: 20px; }
        .plan-form table { width: 100%; border-collapse: collapse; }
        .plan-form th { width: 100px; text-align: left; padding: 8px 12px; font-size: 14px; color: #555; font-weight: 600; white-space: nowrap; }
        .plan-form td { padding: 8px 12px; }
        .plan-form input[type=text], .plan-form input[type=date], .plan-form input[type=number], .plan-form textarea {
            width: 100%; padding: 8px 10px; border: 1px solid #ddd; border-radius: 6px; font-size: 14px; color: #333;
        }
        .plan-form textarea { resize: vertical; min-height: 60px; font-family: inherit; }

        /* 검색 바 */
        .search-bar {
            background: #fff; padding: 14px 18px; border-radius: 8px;
            box-shadow: 0 1px 6px rgba(0,0,0,.08);
            display: flex; flex-wrap: wrap; gap: 8px;
            margin-bottom: 16px; align-items: center;
        }
        .search-bar select, .search-bar input[type=text] {
            padding: 7px 10px; border: 1px solid #ddd; border-radius: 6px;
            font-size: 13px; color: #333; background: #fff;
        }
        .search-bar select { min-width: 120px; }
        .search-bar input[type=text] { flex: 1; min-width: 160px; }
        .btn-search { padding: 7px 18px; background: #1a73e8; color: #fff; border: none; border-radius: 6px; font-size: 13px; cursor: pointer; }
        .btn-search:hover { background: #1557b0; }

        /* 지도 + 선택 목록 레이아웃 */
        .map-layout { display: flex; gap: 16px; }
        #map { flex: 1; height: 560px; border-radius: 8px; box-shadow: 0 1px 6px rgba(0,0,0,.1); background: #e8e8e8; }

        /* 선택된 방문지 패널 */
        .plan-panel { width: 320px; background: #fff; border-radius: 8px; box-shadow: 0 1px 6px rgba(0,0,0,.08); display: flex; flex-direction: column; }
        .panel-header { padding: 14px 16px; font-size: 14px; font-weight: 600; color: #333; border-bottom: 1px solid #f0f0f0; }
        .place-list { flex: 1; overflow-y: auto; min-height: 0; max-height: 510px; padding: 8px; }
        .place-item {
            padding: 10px 12px; margin: 4px 0; background: #f8f9ff;
            border: 1px solid #e0e8ff; border-radius: 6px;
            cursor: grab; display: flex; align-items: center; gap: 8px;
        }
        .place-item:active { cursor: grabbing; }
        .place-item.drag-over { border: 2px dashed #1a73e8; }
        .place-num { width: 22px; height: 22px; background: #1a73e8; color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 700; flex-shrink: 0; }
        .place-name { flex: 1; font-size: 13px; color: #333; }
        .place-del { background: none; border: none; color: #aaa; cursor: pointer; font-size: 16px; padding: 0 2px; }
        .place-del:hover { color: #d32f2f; }
        .empty-plan { padding: 40px 16px; text-align: center; color: #bbb; font-size: 13px; }

        /* 하단 버튼 */
        .btn-row { margin-top: 20px; display: flex; gap: 12px; justify-content: center; }
        .btn { padding: 11px 32px; border-radius: 6px; font-size: 15px; cursor: pointer; text-decoration: none; border: none; }
        .btn-primary { background: #1a73e8; color: #fff; }
        .btn-secondary { background: #eee; color: #333; }

        /* 로딩/오버레이 */
        .spinner { display: inline-block; width: 22px; height: 22px; border: 3px solid #ddd; border-top-color: #1a73e8; border-radius: 50%; animation: spin .7s linear infinite; vertical-align: middle; }
        @keyframes spin { to { transform: rotate(360deg); } }

        /* 커스텀 오버레이 */
        .spot-overlay { background: #fff; border-radius: 8px; padding: 10px 14px; box-shadow: 0 2px 10px rgba(0,0,0,.2); font-size: 13px; min-width: 160px; }
        .spot-overlay .spot-title { font-weight: 700; color: #1a73e8; margin-bottom: 6px; }
        .spot-overlay .spot-addr  { font-size: 11px; color: #777; margin-bottom: 8px; }
        .spot-overlay .btn-add { width: 100%; padding: 6px; background: #1a73e8; color: #fff; border: none; border-radius: 4px; cursor: pointer; font-size: 12px; }
        .spot-overlay .btn-add:hover { background: #1557b0; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-wrap">
    <h2>여행계획 등록</h2>

    <!-- 계획 기본 정보 폼 -->
    <form action="${pageContext.request.contextPath}/plan/regist" method="post" id="planForm">
        <div class="plan-form">
            <table>
                <tr>
                    <th>제목 *</th>
                    <td><input type="text" name="title" required placeholder="여행 제목"></td>
                    <th style="width:80px;">출발일</th>
                    <td><input type="date" name="startDate"></td>
                    <th style="width:80px;">도착일</th>
                    <td><input type="date" name="endDate"></td>
                </tr>
                <tr>
                    <th>예산(원)</th>
                    <td><input type="number" name="totalBudget" min="0" placeholder="0"></td>
                    <th>메모</th>
                    <td colspan="3"><textarea name="memo" rows="2" placeholder="여행 메모"></textarea></td>
                </tr>
            </table>
        </div>

        <!-- 관광지 검색 -->
        <div class="search-bar">
            <select id="sidoSelect" onchange="onSidoChange(this.value)">
                <option value="">-- 시/도 --</option>
            </select>
            <select id="gugunSelect">
                <option value="">-- 구/군 --</option>
            </select>
            <select id="typeSelect">
                <option value="">전체 분류</option>
                <option value="12">관광지</option>
                <option value="14">문화시설</option>
                <option value="15">축제/공연</option>
                <option value="28">레포츠</option>
                <option value="32">숙박</option>
                <option value="38">쇼핑</option>
                <option value="39">음식점</option>
            </select>
            <input type="text" id="kwInput" placeholder="관광지명 검색 (선택)">
            <button type="button" class="btn-search" onclick="searchAttractions()">검색</button>
            <span id="searchStatus"></span>
        </div>

        <div class="map-layout">
            <div id="map"></div>
            <div class="plan-panel">
                <div class="panel-header">선택된 방문지 <small style="color:#888;">(드래그로 순서 변경)</small></div>
                <div class="place-list" id="placeList">
                    <div class="empty-plan" id="emptyMsg">지도에서 관광지를 선택하세요</div>
                </div>
            </div>
        </div>

        <input type="hidden" name="detailsJson" id="detailsJson">

        <div class="btn-row">
            <button type="submit" class="btn btn-primary" onclick="prepareSubmit()">등록</button>
            <a href="${pageContext.request.contextPath}/plan/list" class="btn btn-secondary">취소</a>
        </div>
    </form>
</div>

<script type="text/javascript"
    src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoJavascriptKey}&libraries=services">
</script>
<script>
var PROXY   = '${pageContext.request.contextPath}/attraction/api';
var CONTEXT = '${pageContext.request.contextPath}';

/* ===== 카카오맵 초기화 ===== */
var map = new kakao.maps.Map(document.getElementById('map'), {
    center: new kakao.maps.LatLng(36.5, 127.8),
    level: 13
});
var geocoder = new kakao.maps.services.Geocoder();
var mapMarkers = [];
var openOverlay = null;

/* ===== 선택된 방문지 배열 ===== */
var selectedPlaces = [];

/* ===== 시도 로드 ===== */
(function loadSido() {
    fetch(PROXY + '/sido')
        .then(function(r) { return r.json(); })
        .then(function(json) {
            var items = json.response.body.items;
            if (!items || !items.item) return;
            var list = Array.isArray(items.item) ? items.item : [items.item];
            var sel = document.getElementById('sidoSelect');
            list.forEach(function(s) {
                var opt = document.createElement('option');
                opt.value = s.code;
                opt.textContent = s.name;
                sel.appendChild(opt);
            });
        }).catch(function(e) { console.warn('시도 로드 실패', e); });
})();

/* ===== 구군 로드 ===== */
function onSidoChange(sidoCode) {
    var sel = document.getElementById('gugunSelect');
    sel.innerHTML = '<option value="">-- 구/군 --</option>';
    if (!sidoCode) return;
    fetch(PROXY + '/gugun?sidoCode=' + sidoCode)
        .then(function(r) { return r.json(); })
        .then(function(json) {
            var items = json.response.body.items;
            if (!items || !items.item) return;
            var list = Array.isArray(items.item) ? items.item : [items.item];
            list.forEach(function(g) {
                var opt = document.createElement('option');
                opt.value = g.code;
                opt.textContent = g.name;
                sel.appendChild(opt);
            });
        }).catch(function(e) { console.warn('구군 로드 실패', e); });
}

/* ===== 관광지 검색 ===== */
function searchAttractions() {
    var sido   = document.getElementById('sidoSelect').value;
    var gugun  = document.getElementById('gugunSelect').value;
    var typeId = document.getElementById('typeSelect').value;
    var kw     = document.getElementById('kwInput').value.trim();
    var status = document.getElementById('searchStatus');

    status.innerHTML = '<span class="spinner"></span>';

    var params = new URLSearchParams();
    if (sido)   params.append('areaCode', sido);
    if (gugun)  params.append('sigunguCode', gugun);
    if (typeId) params.append('contentTypeId', typeId);
    if (kw)     params.append('keyword', kw);

    fetch(PROXY + '/search?' + params)
        .then(function(r) { return r.json(); })
        .then(function(json) {
            status.textContent = '';
            var body  = json.response.body;
            var items = body.items;
            if (!items || !items.item || body.totalCount === 0) {
                status.textContent = '결과 없음';
                clearMapMarkers();
                return;
            }
            var spots = Array.isArray(items.item) ? items.item : [items.item];
            status.textContent = spots.length + '건';
            renderMapMarkers(spots);
        }).catch(function(e) {
            status.textContent = '오류 발생';
            console.error(e);
        });
}

/* ===== 지도 마커 렌더링 ===== */
function renderMapMarkers(spots) {
    clearMapMarkers();
    if (openOverlay) { openOverlay.setMap(null); openOverlay = null; }
    var bounds = new kakao.maps.LatLngBounds();

    /* 좌표 없는 항목 geocoding */
    var noCoords = spots.filter(function(s) { return !s.mapx || !s.mapy; });
    var p = Promise.all(noCoords.map(function(s) {
        return new Promise(function(resolve) {
            geocoder.addressSearch(s.addr1, function(result, st) {
                if (st === kakao.maps.services.Status.OK) {
                    s.mapx = result[0].x;
                    s.mapy = result[0].y;
                }
                resolve();
            });
        });
    }));

    p.then(function() {
        spots.forEach(function(s) {
            var lng = parseFloat(s.mapx), lat = parseFloat(s.mapy);
            if (!lat || !lng || lat < 33 || lat > 38.6 || lng < 124.5 || lng > 132) return;
            var pos    = new kakao.maps.LatLng(lat, lng);
            var marker = new kakao.maps.Marker({ map: map, position: pos, title: s.title });
            mapMarkers.push(marker);
            bounds.extend(pos);

            kakao.maps.event.addListener(marker, 'click', function() {
                if (openOverlay) openOverlay.setMap(null);
                var overlay = buildSpotOverlay(s, pos);
                overlay.setMap(map);
                openOverlay = overlay;
            });
        });
        if (!bounds.isEmpty()) map.setBounds(bounds);
    });
}

function buildSpotOverlay(s, pos) {
    var div = document.createElement('div');
    div.className = 'spot-overlay';
    div.innerHTML = '<div class="spot-title">' + (s.title || '') + '</div>'
        + '<div class="spot-addr">' + (s.addr1 || '') + '</div>'
        + '<button class="btn-add" id="ov-btn">+ 방문지 추가</button>';
    div.querySelector('#ov-btn').addEventListener('click', function() {
        addPlace(s.title, parseFloat(s.mapy), parseFloat(s.mapx), s.contentid);
        if (openOverlay) { openOverlay.setMap(null); openOverlay = null; }
    });
    return new kakao.maps.CustomOverlay({ content: div, position: pos, yAnchor: 1.35, zIndex: 5 });
}

function clearMapMarkers() {
    mapMarkers.forEach(function(m) { m.setMap(null); });
    mapMarkers = [];
}

/* ===== 방문지 추가 / 렌더링 ===== */
function addPlace(title, lat, lng, contentId) {
    if (selectedPlaces.some(function(p) { return p.contentId == contentId; })) {
        alert('이미 추가된 장소입니다.'); return;
    }
    selectedPlaces.push({ title: title, latitude: lat, longitude: lng, contentId: contentId, visitOrder: selectedPlaces.length + 1 });
    renderPlaceList();
}

function removePlace(idx) {
    selectedPlaces.splice(idx, 1);
    renderPlaceList();
}

function renderPlaceList() {
    var list = document.getElementById('placeList');
    var emptyMsg = document.getElementById('emptyMsg');
    list.innerHTML = '';
    if (selectedPlaces.length === 0) {
        list.appendChild(emptyMsg || document.createElement('div'));
        if (emptyMsg) list.querySelector('.empty-plan') || list.appendChild(emptyMsg);
        var em = document.createElement('div');
        em.className = 'empty-plan';
        em.id = 'emptyMsg';
        em.textContent = '지도에서 관광지를 선택하세요';
        list.appendChild(em);
        return;
    }
    selectedPlaces.forEach(function(p, idx) {
        var item = document.createElement('div');
        item.className = 'place-item';
        item.draggable = true;
        item.dataset.idx = idx;

        var num  = document.createElement('div');  num.className = 'place-num'; num.textContent = idx + 1;
        var name = document.createElement('div'); name.className = 'place-name'; name.textContent = p.title;
        var del  = document.createElement('button'); del.className = 'place-del'; del.type = 'button'; del.textContent = '×';
        del.addEventListener('click', function() { removePlace(idx); });

        item.appendChild(num);
        item.appendChild(name);
        item.appendChild(del);
        list.appendChild(item);

        /* 드래그앤드롭 */
        item.addEventListener('dragstart', function() { this.style.opacity = '0.5'; window._dragIdx = idx; });
        item.addEventListener('dragend',   function() { this.style.opacity = '1'; });
        item.addEventListener('dragover',  function(e) { e.preventDefault(); this.classList.add('drag-over'); });
        item.addEventListener('dragleave', function() { this.classList.remove('drag-over'); });
        item.addEventListener('drop', function() {
            this.classList.remove('drag-over');
            var from = window._dragIdx, to = parseInt(this.dataset.idx);
            if (from === to) return;
            var moved = selectedPlaces.splice(from, 1)[0];
            selectedPlaces.splice(to, 0, moved);
            renderPlaceList();
        });
    });
}

/* ===== 폼 제출 ===== */
function prepareSubmit() {
    document.getElementById('detailsJson').value = JSON.stringify(selectedPlaces);
}

/* ===== 엔터 검색 ===== */
document.getElementById('kwInput').addEventListener('keydown', function(e) {
    if (e.key === 'Enter') { e.preventDefault(); searchAttractions(); }
});
</script>
</body>
</html>
