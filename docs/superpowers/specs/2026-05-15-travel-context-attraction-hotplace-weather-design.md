# Travel Context Attraction, Hotplace, and Weather Design

## 1. Summary

EnjoyTrip v2의 다음 개선은 새 CRUD를 늘리는 것이 아니라, 이미 존재하는 관광지 조회, 여행계획, 핫플레이스, 날씨 정보를 여행자의 의사결정 흐름에 맞게 연결하는 것이다.

핵심 흐름은 다음과 같다.

```text
관광지를 조회한다
-> 검색 결과 중 내 여행계획에 이미 포함된 장소를 자동으로 표시한다
-> 상세보기와 여행계획 추가 action을 명확히 분리한다
-> 여행계획에 추가할 때 대상 여행계획을 선택한다
-> 관광지 상세에서 이 장소가 포함된 내 여행계획과 연결된 핫플레이스 후기/사진을 본다
-> 여행계획 상세에서 여행 기간의 날씨를 확인한다
```

1차 범위에서는 마지막 방문지 기준 거리 추천 배지, 태그 통계, 외부 리뷰 수집, 날씨 기반 자동 추천은 제외한다.

## 2. Goals

- 관광지 검색 결과에서 내 여행계획에 이미 포함된 장소를 자동으로 표시한다.
- 검색 결과 카드에서 `상세보기`와 `여행계획에 추가` action을 분리한다.
- 관광지를 여행계획에 추가할 때 대상 여행계획을 선택할 수 있다.
- 추가 대상 여행계획 선택 UI에서 각 여행계획의 상태를 `진행 예정`, `진행 중`, `지난 계획`, `날짜 미정`으로 표시한다.
- 관광지 상세 화면에서 이 장소가 포함된 내 여행계획 목록, 상태, 방문일, 방문 순서를 자세히 보여준다.
- 이미 포함된 여행계획에는 같은 관광지를 중복 추가할 수 없다.
- 핫플레이스를 공식 관광지 `content_id`와 연결해, 관광지 상세에서 EnjoyTrip 사용자들의 방문 기록, 사진, 후기를 보여준다.
- 여행계획 상세 화면에서 여행 기간 기준 날짜별 날씨 정보를 보여준다.
- 날씨 API 제공자는 이 spec에서 고정하지 않고, 구현 단계에서 예보 가능 기간, 좌표 조회 방식, 무료 호출량, 응답 안정성을 기준으로 선택한다.

## 3. Non-Goals

- 마지막 방문지와의 거리 기반 추천 배지는 1차 범위에서 제외한다.
- 최단 경로, 대중교통, 자동차 이동 시간 계산은 구현하지 않는다.
- 네이버, 구글, 카카오 리뷰/평점을 직접 수집하거나 저장하지 않는다.
- 핫플레이스 태그 통계는 구현하지 않는다.
- 별도 평점 기능은 구현하지 않는다.
- Spring Security, JWT, SPA 전환은 포함하지 않는다.
- 날씨 기반 관광지 자동 추천은 2차 확장으로 둔다.

## 4. Current Code Context

현재 관광지 조회는 `AttractionApiController`가 공공 관광 API 응답을 JSON passthrough로 반환하고, `src/main/webapp/WEB-INF/views/attraction/list.jsp`가 검색 결과를 렌더링한다.

현재 여행계획은 `PlanController`가 JSP 화면을 담당하고, `PlanApiController`는 방문 순서 변경 API만 제공한다.

현재 `TravelPlanService`에는 이미 `addDetail(PlanDetail detail, String userId)`가 존재한다. 관광지 조회에서 일정에 바로 추가하는 기능은 중복 검증과 기본 방문 순서 계산을 포함한 전용 method인 `addAttractionToPlan(planId, request, userId)`를 추가해 구현한다. 기존 `addDetail`은 여행계획 상세 화면의 수동 방문지 추가 흐름에 유지한다.

현재 `plan_detail`에는 `content_id`, `latitude`, `longitude`, `visit_order`, `visit_date`가 있어 관광지와 여행계획 포함 여부를 비교할 수 있다.

현재 `hotplace`에는 `content_id`가 없다. 관광지 상세에서 “이 명소에 대한 핫플레이스”를 정확하게 보여주려면 `hotplace.content_id`를 추가해야 한다.

현재 `travel_plan`에는 `start_date`, `end_date`가 있어 여행계획 상태와 여행 기간 날씨 표시 기준을 계산할 수 있다.

## 5. Travel Plan Status

여행계획 상태는 DB 컬럼으로 저장하지 않고 `startDate`, `endDate`, 현재 날짜를 기준으로 서버에서 계산한다.

상태 계산 기준:

```text
startDate 없음 또는 endDate 없음 -> 날짜 미정
endDate < today                -> 지난 계획
startDate > today              -> 진행 예정
startDate <= today <= endDate  -> 진행 중
```

여행계획 추가 modal에는 계획 제목과 상태를 함께 표시한다.

```text
어느 여행계획에 추가할까요?
[부산 1박 2일 · 진행 예정]
```

지난 계획은 추가 대상에서 비활성화한다.

```text
지난 여행계획입니다. 추가하려면 여행계획 날짜를 수정하세요.
```

로그인하지 않은 사용자는 여행계획 추가 action을 보지 않는다. 관광지 조회와 상세보기는 계속 공개 기능으로 유지한다.

## 6. Attraction Search Integration

### 6.1 User Flow

1. 로그인 사용자가 관광지 조회 화면에 진입한다.
2. 사용자가 지역/분류/키워드로 관광지를 검색한다.
3. 검색 결과의 각 관광지 `contentId`가 로그인 사용자의 여행계획들 중 어디에 포함되어 있는지 확인한다.
4. 이미 포함된 관광지는 `내 여행계획 N개에 포함됨`으로 표시한다.
5. 검색 결과 카드에는 `상세보기`와 `여행계획에 추가`를 별도 action으로 표시한다.
6. 사용자가 `상세보기`를 선택하면 관광지 상세 화면으로 이동한다.
7. 사용자가 `여행계획에 추가`를 선택하면 대상 여행계획 선택 modal을 연다.
8. modal에서 이미 포함된 계획과 지난 계획은 비활성화한다.
9. 추가 성공 후 해당 카드의 포함 상태를 갱신한다.

### 6.2 UX States

검색 결과 카드의 상태는 다음 중 하나다.

```text
비로그인 사용자
-> [상세보기]만 표시

로그인 사용자 + 어떤 여행계획에도 포함되지 않음
-> "아직 내 여행계획에 포함되지 않음"
-> [상세보기] [여행계획에 추가]

로그인 사용자 + 1개 여행계획에 포함됨
-> "내 여행계획에 포함됨"
-> 포함된 계획명 1개 요약 표시
-> [상세보기] [추가됨]

로그인 사용자 + 2개 이상 여행계획에 포함됨
-> "내 여행계획 N개에 포함됨"
-> [포함된 계획 보기] [상세보기] [추가됨]
```

검색 결과 카드 전체에는 클릭 action을 두지 않는다. 카드 내부의 `상세보기` 버튼만 상세 화면 이동을 담당하고, `여행계획에 추가` 버튼만 추가 modal을 담당한다. 이렇게 해서 카드 클릭 하나에 두 의미가 섞이지 않도록 한다.

`[추가됨]`은 이미 모든 추가 가능한 여행계획에 포함되어 있거나, 로그인 사용자의 추가 가능한 계획이 없을 때 표시하는 비활성 상태다. 다른 추가 가능한 여행계획이 남아 있다면 `여행계획에 추가` 버튼은 계속 활성화하고, modal 내부에서 이미 포함된 계획만 비활성화한다.

상세 화면도 같은 정책을 사용하되, 포함 정보를 더 자세히 보여준다. `attraction/detail.jsp`는 관광지 기본 정보 아래에 “내 여행계획 포함 정보” 영역을 추가한다.

상세 화면 포함 정보 예시:

```text
내 여행계획 포함 정보

이 장소는 내 여행계획 2개에 포함되어 있습니다.

- 부산 1박 2일
  진행 예정 · 2026.05.20 ~ 2026.05.21
  1일차 · 2번째 방문지
  [여행계획 보기]

- 여름휴가
  날짜 미정
  방문일 미정 · 1번째 방문지
  [여행계획 보기]
```

포함되지 않은 장소의 상세 화면 예시:

```text
내 여행계획 포함 정보

아직 내 여행계획에 포함되지 않은 장소입니다.

[여행계획에 추가]
```

### 6.3 Add-To-Plan Modal

`여행계획에 추가` modal은 로그인 사용자의 여행계획 목록을 보여준다.

```text
어느 여행계획에 추가할까요?

부산 1박 2일 · 이미 포함됨       비활성
대전 당일치기 · 지난 계획        비활성
제주 가족여행 · 진행 예정        [추가]
여름휴가 · 날짜 미정             [추가]
```

비활성화 기준:

```text
이미 해당 contentId가 포함된 계획 -> 비활성, "이미 포함됨"
지난 계획                         -> 비활성, "지난 계획"
진행 예정/진행 중/날짜 미정 계획  -> 추가 가능
```

추가 가능한 여행계획이 하나도 없으면 다음 안내를 표시한다.

```text
추가 가능한 여행계획이 없습니다.
새 여행계획을 만들거나 기존 계획 날짜를 수정해 주세요.
```

### 6.4 Required Server Changes

`PlanApiController`에 검색 결과 포함 여부 확인과 여행계획 추가를 위한 API를 추가한다.

```text
GET  /api/plans/attraction-membership?contentIds=1,2,3
GET  /api/plans/attraction-membership/{contentId}
POST /api/plans/{planId}/details
```

`GET /api/plans/attraction-membership`

- 검색 결과에 포함된 여러 `contentId`가 로그인 사용자의 어떤 여행계획에 포함되어 있는지 반환한다.
- 관광지 검색 페이지가 검색 결과 카드의 포함 상태를 표시할 때 사용한다.
- 응답 예시:

```json
{
  "items": [
    {
      "contentId": 126508,
      "includedPlans": [
        {
          "planId": 3,
          "title": "부산 1박 2일",
          "status": "UPCOMING",
          "startDate": "2026-05-20",
          "endDate": "2026-05-21",
          "visitDate": "2026-05-20",
          "visitOrder": 2
        }
      ]
    }
  ]
}
```

`GET /api/plans/attraction-membership/{contentId}`

- 관광지 상세 화면에서 단일 `contentId`의 포함 정보를 자세히 조회할 때 사용한다.
- 응답은 포함된 여행계획 목록, 상태, 날짜, 방문일, 방문 순서를 포함한다.

`POST /api/plans/{planId}/details`

- 관광지 검색 결과나 상세 화면에서 사용자가 modal로 고른 여행계획에 방문지를 추가한다.
- request body는 최소한 `contentId`, `title`, `latitude`, `longitude`를 포함한다.
- `visitDate`, `memo`는 선택값으로 둔다.
- service 계층에서 소유자 검증, 지난 계획 여부 검증, 중복 포함 여부 검증을 수행한다.
- 이미 포함된 `contentId`를 다시 추가하려고 하면 중복 추가하지 않고 conflict 응답을 반환한다.

요청 예시:

```json
{
  "contentId": 126508,
  "title": "해운대해수욕장",
  "latitude": 35.1587,
  "longitude": 129.1604,
  "visitDate": null,
  "memo": null
}
```

### 6.5 Mapper and Service Changes

`TravelPlanMapper`에 다음 query를 추가한다.

```text
selectPlanMembershipByUserIdAndContentIds(userId, contentIds)
selectPlanMembershipByUserIdAndContentId(userId, contentId)
selectMaxVisitOrderByPlanId(planId)
```

`TravelPlanService`에는 관광지 조회 화면에서 쓰기 좋은 깊은 method를 추가한다.

```text
getAttractionMembership(contentIds, userId)
getAttractionMembership(contentId, userId)
addAttractionToPlan(planId, request, userId)
```

`getAttractionMembership`은 사용자의 모든 여행계획을 대상으로 포함 여부를 반환한다. `addAttractionToPlan`은 controller가 `visit_order` 계산, 중복 확인, 소유자 검증, 지난 계획 차단을 알 필요 없도록 service 내부에서 처리한다.

## 7. Hotplace and Attraction Linking

### 7.1 Data Model

`hotplace`에 `content_id`를 추가한다.

```sql
ALTER TABLE hotplace
ADD COLUMN content_id INT NULL AFTER user_id;

CREATE INDEX idx_hotplace_content_id ON hotplace(content_id);
```

`content_id`는 nullable로 둔다.

- 관광지 상세에서 등록한 핫플레이스는 `content_id`를 가진다.
- 사용자가 직접 등록한 독립 핫플레이스는 `content_id`가 없을 수 있다.
- 관광지 상세에서는 같은 `content_id`를 가진 핫플레이스만 “이 명소에 대한 EnjoyTrip 기록”으로 보여준다.
- 핫플레이스 목록/지도에서는 `content_id` 유무와 관계없이 전체 핫플레이스를 보여준다.

현재 초기 데이터가 거의 없으므로 schema 변경은 `sql/schema.sql`, 테스트용 `src/test/resources/schema.sql`, DTO, mapper XML을 함께 업데이트한다.

### 7.2 Attraction Detail UX

`attraction/detail.jsp`에 “EnjoyTrip 방문 기록” 영역을 추가한다.

```text
EnjoyTrip 방문 기록

핫플레이스 등록 7개
사진 후기 5개

최근 후기
- 야경이 좋았어요.
- 가족이랑 가기 좋았습니다.
- 주차는 조금 불편했어요.

[이 명소를 핫플레이스로 등록]
```

후기는 별도 review table을 만들지 않고 `hotplace.description`을 사용한다. 사진은 `hotplace.image_path`가 있는 항목을 노출한다.

1차에서는 다음 항목만 제공한다.

```text
hotplaceCount
photoCount
recentHotplaces 최대 3개
```

태그 통계는 2차 확장으로 둔다.

### 7.3 Required Server Changes

핫플레이스 요약 API를 추가한다.

```text
GET /api/attractions/{contentId}/hotplaces/summary
```

응답 예시:

```json
{
  "contentId": 126508,
  "hotplaceCount": 7,
  "photoCount": 5,
  "recentHotplaces": [
    {
      "hotplaceId": 10,
      "title": "해운대 야경",
      "description": "저녁 산책하기 좋았습니다.",
      "imagePath": "uploads/abc.jpg",
      "visitDate": "2026-05-10",
      "userId": "ssafy"
    }
  ]
}
```

`HotplaceMapper`에 다음 query를 추가한다.

```text
countByContentId(contentId)
countPhotosByContentId(contentId)
selectRecentByContentId(contentId, limit)
```

관광지 상세에서 핫플레이스 등록으로 이동할 때는 다음 URL을 사용한다.

```text
GET /hotplaces/new?contentId=126508&title=...&latitude=...&longitude=...
```

`HotplaceController.writeForm`은 query parameter가 있으면 등록 form에 기본값으로 내려준다. 저장 시 `Hotplace.contentId`를 함께 저장한다.

## 8. Travel Period Weather

### 8.1 Scope

날씨 정보는 1차에서 여행계획 상세 화면에만 표시한다.

`plan/detail.jsp`는 여행계획 기본 정보 아래, 방문지 목록 위에 “여행 기간 날씨” 영역을 추가한다.

```text
여행 기간 날씨

5.20 수
흐림 · 강수확률 60% · 18~23°C
비 가능성이 있어 실내 장소를 함께 고려해보세요.

5.21 목
맑음 · 강수확률 10% · 17~25°C
야외 관광지 방문에 적합한 날씨입니다.
```

### 8.2 Weather Location Rule

여행계획 기간만으로는 날씨 조회 지역을 알 수 없으므로, 다음 기준을 사용한다.

```text
각 날짜에 등록된 첫 번째 방문지 좌표를 기준으로 날씨를 조회한다.
해당 날짜에 방문지가 없으면 여행계획의 첫 번째 방문지 좌표를 기준으로 한다.
방문지가 하나도 없으면 날씨 영역에 "방문지를 추가하면 날씨 정보를 확인할 수 있습니다"를 표시한다.
```

### 8.3 Forecast Availability

예보 제공 가능 기간은 선택하는 API에 따라 달라진다. 이 spec에서는 API를 고정하지 않는다.

공통 동작은 다음과 같다.

```text
진행 예정 또는 진행 중 계획만 날씨를 조회한다.
지난 계획은 날씨 API를 호출하지 않는다.
예보 제공 범위 밖 날짜는 "아직 예보가 제공되지 않습니다"로 표시한다.
날씨 API 오류가 발생하면 계획 상세 화면은 계속 렌더링하고, 날씨 영역에 실패 안내를 표시한다.
```

### 8.4 Weather Module Shape

날씨 기능은 controller/JSP가 특정 외부 API 응답 구조를 알지 않도록 별도 module로 둔다.

```text
WeatherService
WeatherClient
WeatherForecast
DailyWeatherSummary
```

`WeatherService`는 `TravelPlan`과 `PlanDetail` 목록을 받아 화면에 필요한 날짜별 요약 DTO를 반환한다.

`WeatherClient`는 실제 외부 API adapter다. API 제공자는 구현 단계에서 정하되, service와 JSP는 `DailyWeatherSummary`만 사용한다.

날씨 안내 문구는 1차에서 단순 규칙으로 처리한다.

```text
강수확률 60% 이상 -> 비 가능성이 있어 실내 장소를 함께 고려해보세요.
최고기온 30도 이상 -> 더운 날씨라 실내/그늘 일정을 섞어보세요.
최저기온 5도 이하 -> 추운 날씨라 야외 일정은 방한 준비가 필요합니다.
맑음 + 강수확률 20% 이하 -> 야외 관광지 방문에 적합한 날씨입니다.
그 외 -> 날씨를 확인하고 일정을 조정해보세요.
```

## 9. Page and API Error Handling

기존 `GlobalApiExceptionHandler`와 `GlobalPageExceptionHandler` 흐름을 유지한다.

- `/api/plans/attraction-membership*`, `/api/plans/{planId}/details`는 미로그인 시 `UNAUTHORIZED` JSON 응답을 반환한다.
- 다른 사용자의 여행계획 접근은 `FORBIDDEN` JSON 응답을 반환한다.
- 존재하지 않는 여행계획은 `NOT_FOUND` JSON 응답을 반환한다.
- 지난 계획에 장소를 추가하려는 요청은 `PAST_PLAN_NOT_EDITABLE` error code를 사용하고 HTTP 400으로 응답한다.
- 날씨 API 실패는 plan detail page 전체 실패로 전파하지 않고, 날씨 영역의 partial failure로 처리한다.

## 10. Testing Strategy

### Unit Tests

- `TravelPlanStatus` 계산 테스트
- `TravelPlanService.addAttractionToPlan` 소유자 검증, 중복 검증, 지난 계획 차단, visit order 계산 테스트
- `HotplaceService` contentId 기반 summary 계산 테스트
- `WeatherService` 날짜별 위치 선택, 예보 가능 범위 밖 처리, 날씨 안내 문구 테스트

### MVC Slice Tests

- `PlanApiController` membership/add detail API 테스트
- 미로그인, 타인 plan 접근, 중복 추가, 지난 계획 추가 차단 테스트
- 관광지 상세 hotplace summary API 테스트

### MyBatis Tests

- `TravelPlanMapper` contentId membership query 테스트
- `HotplaceMapper` contentId 기반 count/photo/recent query 테스트
- `hotplace.content_id` insert/select/update mapping 테스트

### JSP Route Tests

- `attraction/list.jsp`에 여행계획 포함 정보가 없어도 렌더링 가능한지 확인
- `attraction/detail.jsp`에 contentId 기반 hotplace/action 영역이 추가되어도 렌더링 가능한지 확인
- `plan/detail.jsp`에 날씨 model이 없어도 렌더링 가능한지 확인

## 11. Acceptance Criteria

- 로그인 사용자는 관광지 검색 결과에서 내 여행계획 포함 여부를 자동으로 확인할 수 있다.
- 검색 결과 카드에는 `상세보기`와 `여행계획에 추가` action이 분리되어 표시된다.
- 내 여행계획에 이미 들어간 관광지는 검색 결과에서 포함된 계획 수를 표시한다.
- 관광지 상세 화면은 해당 장소가 포함된 내 여행계획 목록, 상태, 방문일, 방문 순서를 표시한다.
- 포함되지 않은 관광지는 `여행계획에 추가` modal을 통해 대상 여행계획에 추가할 수 있다.
- 이미 포함된 계획에는 같은 관광지를 중복 추가할 수 없다.
- 지난 계획에는 관광지를 바로 추가할 수 없다.
- `hotplace.content_id`가 있는 기록은 관광지 상세의 EnjoyTrip 방문 기록에 집계된다.
- 관광지 상세는 핫플레이스 등록 수, 사진 후기 수, 최근 후기 일부를 보여준다.
- `content_id`가 없는 독립 핫플레이스는 핫플레이스 목록에는 보이지만 특정 관광지 상세에는 집계되지 않는다.
- 여행계획 상세는 `startDate`부터 `endDate`까지 날짜별 날씨 정보를 표시한다.
- 방문지가 없는 여행계획은 날씨 대신 방문지 추가 안내를 보여준다.
- 예보 제공 범위 밖 날짜와 지난 계획은 날씨 API를 호출하지 않는다.
- 기존 관광지 조회, 여행계획 상세, 핫플레이스 목록 기능은 유지된다.
