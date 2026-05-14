# 1. API Overview (개요)

이 API는 코드 조회, 관광정보 통합/상세 검색, 위치기반 및 지역기반 등 국내 관광에 대한 전반적인 정보를 국문으로 제공합니다.

- 서비스 ID: KorService2
- 서비스 버전: 4.0 (문서 버전: 4.4, 2026-02-10 기준)

# 2. Base Information (기본 정보)

| **항목**         | **내용**                                                 |
| ---------------- | -------------------------------------------------------- |
| Base URL         | `http://apis.data.go.kr/B551011/KorService2`             |
| Method           | GET (REST)                                               |
| Authentication   | 발급받은 공공데이터포털 Service Key (파라미터 전달 방식) |
| Response Format  | XML (디폴트) 또는 JSON (`_type=json` 요청 시)            |
| 데이터 갱신 주기 | 일 1회                                                   |
| 전송 레벨 암호화 | HTTPS / HTTP 모두 지원                                   |

### 공통 요청 파라미터 (Common Request Parameters)

모든 API 엔드포인트 호출 시 공통으로 포함되어야 하는 파라미터입니다.

| **파라미터명** | **타입** | **필수 여부** | **설명**                                             |
| -------------- | -------- | ------------- | ---------------------------------------------------- |
| serviceKey     | String   | 필수          | 공공데이터포털에서 발급받은 인증키                   |
| MobileOS       | String   | 필수          | IOS (아이폰), AND (안드로이드), WEB (웹), ETC (기타) |
| MobileApp      | String   | 필수          | 서비스명 (어플명)                                    |
| numOfRows      | Integer  | 옵션          | 한 페이지 결과 수 (기본값: 10)                       |
| pageNo         | Integer  | 옵션          | 현재 페이지 번호 (기본값: 1)                         |
| _type          | String   | 옵션          | 응답 메세지 형식 (json 등, 디폴트: XML)              |

### 공통 응답 속성 (Common Response Properties)

목록 및 상세 조회 시 공통으로 반환되는 결과값 포맷입니다.

| **파라미터명** | **타입** | **필수 여부** | **설명**                      |
| -------------- | -------- | ------------- | ----------------------------- |
| resultCode     | String   | 필수          | 응답 결과 코드 (성공 시 0000) |
| resultMsg      | String   | 필수          | 응답 결과 메시지 (성공 시 OK) |
| numOfRows      | Integer  | 필수          | 한 페이지 결과 수             |
| pageNo         | Integer  | 필수          | 현재 페이지 번호              |
| totalCount     | Integer  | 필수          | 전체 결과 수                  |

### 콘텐츠타입(ContentTypeId) 코드표

| **타입종류**   | **ContentTypeId** |
| -------------- | ----------------- |
| 관광지         | 12                |
| 문화시설       | 14                |
| 행사/공연/축제 | 15                |
| 여행코스       | 25                |
| 레포츠         | 28                |
| 숙박           | 32                |
| 쇼핑           | 38                |
| 음식점         | 39                |

---

# 3. API Endpoints (엔드포인트 상세)

## 3.1. 지역기반 관광정보 조회

지역 및 시군구를 기반으로 관광정보 목록을 조회합니다. 제목순, 수정일순(최신순), 등록일순 정렬 검색을 제공합니다.

- Endpoint: `/areaBasedList2`

### Request Parameters

| **파라미터명** | **타입** | **필수 여부** | **설명**                                                                                              |
| -------------- | -------- | ------------- | ----------------------------------------------------------------------------------------------------- |
| arrange        | String   | 옵션          | 정렬 구분 (A=제목순, C=수정일순, D=생성일순). 대표이미지 필수 정렬 (O=제목순, Q=수정일순, R=생성일순) |
| contentTypeId  | Integer  | 옵션          | 관광타입 ID (12, 14, 15, 25, 28, 32, 38, 39)                                                          |
| modifiedtime   | String   | 옵션          | 콘텐츠 수정일 (형식: YYYYMMDD)                                                                        |
| lDongRegnCd    | String   | 옵션          | 법정동 시도 코드 (법정동 코드 조회 참고)                                                              |
| lDongSignguCd  | String   | 옵션          | 법정동 시군구 코드 (lDongRegnCd 필수)                                                                 |
| lclsSystm1     | String   | 옵션          | 분류체계 대분류 (분류체계 코드 조회 참고)                                                             |
| lclsSystm2     | String   | 옵션          | 분류체계 중분류 (lclsSystm1 필수)                                                                     |
| lclsSystm3     | String   | 옵션          | 분류체계 소분류 (lclsSystm1, lclsSystm2 필수)                                                         |

### Response Properties

| **파라미터명** | **타입** | **필수 여부** | **설명**                                       |
| -------------- | -------- | ------------- | ---------------------------------------------- |
| contentid      | String   | 필수          | 콘텐츠 ID                                      |
| contenttypeid  | String   | 필수          | 관광타입 ID                                    |
| title          | String   | 필수          | 콘텐츠 제목                                    |
| createdtime    | String   | 필수          | 콘텐츠 최초 등록일                             |
| modifiedtime   | String   | 필수          | 콘텐츠 수정일                                  |
| addr1          | String   | 옵션          | 주소                                           |
| addr2          | String   | 옵션          | 상세주소                                       |
| mapx           | String   | 옵션          | GPS X좌표 (WGS84 경도)                         |
| mapy           | String   | 옵션          | GPS Y좌표 (WGS84 위도)                         |
| mlevel         | String   | 옵션          | Map Level                                      |
| tel            | String   | 옵션          | 전화번호                                       |
| firstimage     | String   | 옵션          | 대표이미지(원본) URL (약 500*333)              |
| firstimage2    | String   | 옵션          | 대표이미지(썸네일) URL (약 150*100)            |
| cpyrhtDivCd    | String   | 옵션          | 저작권 유형 (Type1: 출처표시, Type3: 변경금지) |
| zipcode        | String   | 옵션          | 우편번호                                       |
| lDongRegnCd    | String   | 옵션          | 법정동 시도 코드                               |
| lDongSignguCd  | String   | 옵션          | 법정동 시군구 코드                             |
| lclsSystm1     | String   | 옵션          | 분류체계 대분류                                |
| lclsSystm2     | String   | 옵션          | 분류체계 중분류                                |
| lclsSystm3     | String   | 옵션          | 분류체계 소분류                                |

### 요청 예제

```
https://apis.data.go.kr/B551011/KorService2/areaBasedList2?serviceKey=인증키&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json&arrange=C&contentTypeId=12&lDongRegnCd=26&lDongSignguCd=380&lclsSystm1=NA&lclsSystm2=NA04&lclsSystm3=NA040500
```

---

## 3.2. 위치기반 관광정보 조회

내 주변 좌표를 기반으로 관광정보 목록을 조회합니다. 제목순, 수정일순, 등록일순, 거리순 정렬 검색을 제공합니다.

- Endpoint: `/locationBasedList2`

### Request Parameters

| **파라미터명** | **타입** | **필수 여부** | **설명**                                                                             |
| -------------- | -------- | ------------- | ------------------------------------------------------------------------------------ |
| mapX           | String   | 필수          | GPS X좌표 (WGS84 경도좌표)                                                           |
| mapY           | String   | 필수          | GPS Y좌표 (WGS84 위도좌표)                                                           |
| radius         | Integer  | 필수          | 거리 반경 (단위: m, Max값 20000m=20Km)                                               |
| arrange        | String   | 옵션          | 정렬 구분 (A=제목순, C=수정일순, D=생성일순, E=거리순). 대표이미지 필수 (O, Q, R, S) |
| contentTypeId  | Integer  | 옵션          | 관광타입 ID                                                                          |
| modifiedtime   | String   | 옵션          | 콘텐츠 수정일 (형식: YYYYMMDD)                                                       |
| lDongRegnCd    | String   | 옵션          | 법정동 시도 코드                                                                     |
| lDongSignguCd  | String   | 옵션          | 법정동 시군구 코드 (lDongRegnCd 필수)                                                |
| lclsSystm1     | String   | 옵션          | 분류체계 대분류                                                                      |
| lclsSystm2     | String   | 옵션          | 분류체계 중분류 (lclsSystm1 필수)                                                    |
| lclsSystm3     | String   | 옵션          | 분류체계 소분류 (lclsSystm1, lclsSystm2 필수)                                        |

### Response Properties

지역기반 조회의 응답 항목에 추가로 아래 항목이 포함됩니다.

| **파라미터명** | **타입** | **필수 여부** | **설명**                       |
| -------------- | -------- | ------------- | ------------------------------ |
| dist           | String   | 필수          | 중심 좌표로부터 거리 (단위: m) |

### 요청 예제

```
https://apis.data.go.kr/B551011/KorService2/locationBasedList2?serviceKey=인증키&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json&arrange=C&mapX=126.98375&mapY=37.563446&radius=1000&contentTypeId=39
```

---

## 3.3. 키워드 검색 조회

키워드로 검색을 하여 관광정보 전체 목록을 조회합니다. 제목순, 수정일순, 등록일순 정렬 검색을 제공합니다.

- Endpoint: `/searchKeyword2`

### Request Parameters

| **파라미터명** | **타입** | **필수 여부** | **설명**                                                                |
| -------------- | -------- | ------------- | ----------------------------------------------------------------------- |
| keyword        | String   | 필수          | 검색 요청할 키워드 (국문=인코딩 필요)                                   |
| arrange        | String   | 옵션          | 정렬 구분 (A=제목순, C=수정일순, D=생성일순). 대표이미지 필수 (O, Q, R) |
| lDongRegnCd    | String   | 옵션          | 법정동 시도 코드                                                        |
| lDongSignguCd  | String   | 옵션          | 법정동 시군구 코드 (lDongRegnCd 필수)                                   |
| lclsSystm1     | String   | 옵션          | 분류체계 대분류                                                         |
| lclsSystm2     | String   | 옵션          | 분류체계 중분류 (lclsSystm1 필수)                                       |
| lclsSystm3     | String   | 옵션          | 분류체계 소분류 (lclsSystm1, lclsSystm2 필수)                           |

### Response Properties

지역기반 관광정보 조회와 동일한 응답 항목을 반환합니다.

### 요청 예제

```
https://apis.data.go.kr/B551011/KorService2/searchKeyword2?serviceKey=인증키&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json&arrange=C&keyword=시장
```

---

## 3.4. 행사정보 조회

행사/공연/축제 정보를 날짜로 조회합니다. 콘텐츠 타입이 "행사/공연/축제"인 경우만 유효합니다.

- Endpoint: `/searchFestival2`

### Request Parameters

| **파라미터명** | **타입** | **필수 여부** | **설명**                                                                |
| -------------- | -------- | ------------- | ----------------------------------------------------------------------- |
| eventStartDate | String   | 필수          | 행사 시작일 (형식: YYYYMMDD)                                            |
| eventEndDate   | String   | 옵션          | 행사 종료일 (형식: YYYYMMDD)                                            |
| arrange        | String   | 옵션          | 정렬 구분 (A=제목순, C=수정일순, D=생성일순). 대표이미지 필수 (O, Q, R) |
| modifiedtime   | String   | 옵션          | 콘텐츠 수정일 (형식: YYYYMMDD)                                          |
| lDongRegnCd    | String   | 옵션          | 법정동 시도 코드                                                        |
| lDongSignguCd  | String   | 옵션          | 법정동 시군구 코드 (lDongRegnCd 필수)                                   |
| lclsSystm1     | String   | 옵션          | 분류체계 대분류                                                         |
| lclsSystm2     | String   | 옵션          | 분류체계 중분류 (lclsSystm1 필수)                                       |
| lclsSystm3     | String   | 옵션          | 분류체계 소분류 (lclsSystm1, lclsSystm2 필수)                           |

### Response Properties

지역기반 조회의 응답 항목에 추가로 아래 항목이 포함됩니다.

| **파라미터명** | **타입** | **필수 여부** | **설명**                     |
| -------------- | -------- | ------------- | ---------------------------- |
| eventstartdate | String   | 필수          | 행사 시작일 (형식: YYYYMMDD) |
| eventenddate   | String   | 필수          | 행사 종료일 (형식: YYYYMMDD) |
| progresstype   | String   | 옵션          | 진행상태정보                 |
| festivaltype   | String   | 옵션          | 축제유형명                   |

### 요청 예제

```
https://apis.data.go.kr/B551011/KorService2/searchFestival2?serviceKey=인증키&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json&arrange=C&eventStartDate=20260101&eventEndDate=20261231
```

---

## 3.5. 숙박정보 조회

숙박 정보 목록을 조회합니다. 콘텐츠 타입이 "숙박"인 경우만 유효합니다.

- Endpoint: `/searchStay2`

### Request Parameters

| **파라미터명** | **타입** | **필수 여부** | **설명**                                                                |
| -------------- | -------- | ------------- | ----------------------------------------------------------------------- |
| arrange        | String   | 옵션          | 정렬 구분 (A=제목순, C=수정일순, D=생성일순). 대표이미지 필수 (O, Q, R) |
| modifiedtime   | String   | 옵션          | 콘텐츠 수정일 (형식: YYYYMMDD)                                          |
| lDongRegnCd    | String   | 옵션          | 법정동 시도 코드                                                        |
| lDongSignguCd  | String   | 옵션          | 법정동 시군구 코드 (lDongRegnCd 필수)                                   |
| lclsSystm1     | String   | 옵션          | 분류체계 대분류                                                         |
| lclsSystm2     | String   | 옵션          | 분류체계 중분류 (lclsSystm1 필수)                                       |
| lclsSystm3     | String   | 옵션          | 분류체계 소분류 (lclsSystm1, lclsSystm2 필수)                           |

### Response Properties

지역기반 관광정보 조회와 동일한 응답 항목을 반환합니다.

### 요청 예제

```
https://apis.data.go.kr/B551011/KorService2/searchStay2?serviceKey=인증키&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json&arrange=C&lDongRegnCd=51&lDongSignguCd=820
```

---

## 3.6. 공통정보 조회 (상세정보1)

타입별 공통정보(기본정보, 대표 이미지, 분류, 지역 정보, 주소, 좌표, 개요 정보 등)를 조회합니다.

- Endpoint: `/detailCommon2`

### Request Parameters

| **파라미터명** | **타입** | **필수 여부** | **설명**         |
| -------------- | -------- | ------------- | ---------------- |
| contentId      | String   | 필수          | 조회할 콘텐츠 ID |

### Response Properties

| **파라미터명** | **타입** | **필수 여부** | **설명**                         |
| -------------- | -------- | ------------- | -------------------------------- |
| contentid      | String   | 필수          | 콘텐츠 ID                        |
| contenttypeid  | String   | 필수          | 관광타입 ID                      |
| title          | String   | 필수          | 콘텐츠명(제목)                   |
| createdtime    | String   | 필수          | 콘텐츠 최초 등록일               |
| modifiedtime   | String   | 필수          | 콘텐츠 수정일                    |
| tel            | String   | 옵션          | 전화번호                         |
| telname        | String   | 옵션          | 전화번호명                       |
| homepage       | String   | 옵션          | 홈페이지 주소 (HTML a 태그 포함) |
| firstimage     | String   | 옵션          | 대표이미지(원본) URL             |
| firstimage2    | String   | 옵션          | 대표이미지(썸네일) URL           |
| cpyrhtDivCd    | String   | 옵션          | 저작권 유형 (Type1, Type3)       |
| addr1          | String   | 옵션          | 주소                             |
| addr2          | String   | 옵션          | 상세주소                         |
| zipcode        | String   | 옵션          | 우편번호                         |
| mapx           | String   | 옵션          | GPS X좌표 (WGS84 경도)           |
| mapy           | String   | 옵션          | GPS Y좌표 (WGS84 위도)           |
| mlevel         | String   | 옵션          | Map Level                        |
| overview       | String   | 옵션          | 콘텐츠 개요 정보                 |
| lDongRegnCd    | String   | 옵션          | 법정동 시도 코드                 |
| lDongSignguCd  | String   | 옵션          | 법정동 시군구 코드               |
| lclsSystm1     | String   | 옵션          | 분류체계 대분류                  |
| lclsSystm2     | String   | 옵션          | 분류체계 중분류                  |
| lclsSystm3     | String   | 옵션          | 분류체계 소분류                  |

### 요청 예제

```
https://apis.data.go.kr/B551011/KorService2/detailCommon2?serviceKey=인증키&MobileApp=AppTest&MobileOS=ETC&pageNo=1&numOfRows=10&contentId=126128&_type=json
```

---

## 3.7. 소개정보 조회 (상세정보2)

타입별 소개정보(휴무일, 개장시간, 주차시설 등)를 조회합니다. 각 타입마다 응답 항목이 다르게 제공됩니다.

- Endpoint: `/detailIntro2`

### Request Parameters

| **파라미터명** | **타입** | **필수 여부** | **설명**    |
| -------------- | -------- | ------------- | ----------- |
| contentId      | String   | 필수          | 콘텐츠 ID   |
| contentTypeId  | Integer  | 필수          | 관광타입 ID |

### Response Properties (타입별)

#### 관광지 (contentTypeId=12)

| **파라미터명**  | **설명**             |
| --------------- | -------------------- |
| accomcount      | 수용인원             |
| chkbabycarriage | 유모차대여정보       |
| chkcreditcard   | 신용카드가능정보     |
| chkpet          | 애완동물동반가능정보 |
| expagerange     | 체험가능연령         |
| expguide        | 체험안내             |
| heritage1       | 세계문화유산유무     |
| heritage2       | 세계자연유산유무     |
| heritage3       | 세계기록유산유무     |
| infocenter      | 문의및안내           |
| opendate        | 개장일               |
| parking         | 주차시설             |
| restdate        | 쉬는날               |
| useseason       | 이용시기             |
| usetime         | 이용시간             |

#### 문화시설 (contentTypeId=14)

| **파라미터명**         | **설명**             |
| ---------------------- | -------------------- |
| accomcountculture      | 수용인원             |
| chkbabycarriageculture | 유모차대여정보       |
| chkcreditcardculture   | 신용카드가능정보     |
| chkpetculture          | 애완동물동반가능정보 |
| discountinfo           | 할인정보             |
| infocenterculture      | 문의및안내           |
| parkingculture         | 주차시설             |
| parkingfee             | 주차요금             |
| restdateculture        | 쉬는날               |
| usefee                 | 이용요금             |
| usetimeculture         | 이용시간             |
| scale                  | 규모                 |
| spendtime              | 관람소요시간         |

#### 행사/공연/축제 (contentTypeId=15)

| **파라미터명**       | **설명**       |
| -------------------- | -------------- |
| agelimit             | 관람가능연령   |
| bookingplace         | 예매처         |
| discountinfofestival | 할인정보       |
| eventenddate         | 행사종료일     |
| eventhomepage        | 행사홈페이지   |
| eventplace           | 행사장소       |
| eventstartdate       | 행사시작일     |
| festivalgrade        | 축제등급       |
| placeinfo            | 행사장위치안내 |
| playtime             | 공연시간       |
| program              | 행사프로그램   |
| spendtimefestival    | 관람소요시간   |
| sponsor1             | 주최자정보     |
| sponsor1tel          | 주최자연락처   |
| sponsor2             | 주관사정보     |
| sponsor2tel          | 주관사연락처   |
| subevent             | 부대행사       |
| usetimefestival      | 이용요금       |

#### 여행코스 (contentTypeId=25)

| **파라미터명**       | **설명**       |
| -------------------- | -------------- |
| distance             | 코스총거리     |
| infocentertourcourse | 문의및안내     |
| schedule             | 코스일정       |
| taketime             | 코스총소요시간 |
| theme                | 코스테마       |

#### 레포츠 (contentTypeId=28)

| **파라미터명**         | **설명**             |
| ---------------------- | -------------------- |
| accomcountleports      | 수용인원             |
| chkbabycarriageleports | 유모차대여정보       |
| chkcreditcardleports   | 신용카드가능정보     |
| chkpetleports          | 애완동물동반가능정보 |
| expagerangeleports     | 체험가능연령         |
| infocenterleports      | 문의및안내           |
| openperiod             | 개장기간             |
| parkingfeeleports      | 주차요금             |
| parkingleports         | 주차시설             |
| reservation            | 예약안내             |
| restdateleports        | 쉬는날               |
| scaleleports           | 규모                 |
| usefeeleports          | 입장료               |
| usetimeleports         | 이용시간             |

#### 숙박 (contentTypeId=32)

| **파라미터명**     | **설명**         |
| ------------------ | ---------------- |
| accomcountlodging  | 수용가능인원     |
| checkintime        | 입실시간         |
| checkouttime       | 퇴실시간         |
| chkcooking         | 객실내취사여부   |
| foodplace          | 식음료장         |
| infocenterlodging  | 문의및안내       |
| parkinglodging     | 주차시설         |
| pickup             | 픽업서비스       |
| roomcount          | 객실수           |
| reservationlodging | 예약안내         |
| reservationurl     | 예약안내홈페이지 |
| roomtype           | 객실유형         |
| scalelodging       | 규모             |
| subfacility        | 부대시설(기타)   |
| barbecue           | 바비큐장여부     |
| beauty             | 뷰티시설정보     |
| beverage           | 식음료장여부     |
| bicycle            | 자전거대여여부   |
| campfire           | 캠프파이어여부   |
| fitness            | 휘트니스센터여부 |
| karaoke            | 노래방여부       |
| publicbath         | 공용샤워실여부   |
| publicpc           | 공용PC실여부     |
| sauna              | 사우나실여부     |
| seminar            | 세미나실여부     |
| sports             | 스포츠시설여부   |
| refundregulation   | 환불규정         |

#### 쇼핑 (contentTypeId=38)

| **파라미터명**          | **설명**             |
| ----------------------- | -------------------- |
| chkbabycarriageshopping | 유모차대여정보       |
| chkcreditshopping       | 신용카드가능정보     |
| chkpetshopping          | 애완동물동반가능정보 |
| culturecenter           | 문화센터바로가기     |
| fairday                 | 장서는날             |
| infocentershopping      | 문의및안내           |
| opendateshopping        | 개장일               |
| opentime                | 영업시간             |
| parkingshopping         | 주차시설             |
| restdateshopping        | 쉬는날               |
| restroom                | 화장실설명           |
| saleitem                | 판매품목             |
| saleitemcost            | 판매품목별가격       |
| scaleshopping           | 규모                 |
| shopguide               | 매장안내             |

#### 음식점 (contentTypeId=39)

| **파라미터명**    | **설명**         |
| ----------------- | ---------------- |
| chkcreditcardfood | 신용카드가능정보 |
| discountinfofood  | 할인정보         |
| firstmenu         | 대표메뉴         |
| infocenterfood    | 문의및안내       |
| kidsfacility      | 어린이놀이방여부 |
| opendatefood      | 개업일           |
| opentimefood      | 영업시간         |
| packing           | 포장가능         |
| parkingfood       | 주차시설         |
| reservationfood   | 예약안내         |
| restdatefood      | 쉬는날           |
| scalefood         | 규모             |
| seat              | 좌석수           |
| smoking           | 금연/흡연여부    |
| treatmenu         | 취급메뉴         |
| lcnsno            | 인허가번호       |

### 요청 예제

```
https://apis.data.go.kr/B551011/KorService2/detailIntro2?serviceKey=인증키&MobileApp=AppTest&MobileOS=ETC&pageNo=1&numOfRows=10&_type=json&contentTypeId=12&contentId=126128
```

---

## 3.8. 반복정보 조회 (상세정보3)

타입별 추가 관광정보 상세내역을 조회합니다. "숙박"은 객실정보, "여행코스"는 코스정보를 제공하며, 나머지 타입은 다양한 정보를 반복적인 형태로 제공합니다.

- Endpoint: `/detailInfo2`

### Request Parameters

| **파라미터명** | **타입** | **필수 여부** | **설명**    |
| -------------- | -------- | ------------- | ----------- |
| contentId      | String   | 필수          | 콘텐츠 ID   |
| contentTypeId  | Integer  | 필수          | 관광타입 ID |

### Response Properties (타입별)

#### 일반 타입 (숙박, 여행코스 제외)

| **파라미터명** | **설명**     |
| -------------- | ------------ |
| fldgubun       | 일련번호     |
| infoname       | 제목         |
| infotext       | 내용         |
| serialnum      | 반복일련번호 |

#### 여행코스 (contentTypeId=25)

| **파라미터명**    | **설명**       |
| ----------------- | -------------- |
| subcontentid      | 하위콘텐츠ID   |
| subdetailalt      | 코스이미지설명 |
| subdetailimg      | 코스이미지     |
| subdetailoverview | 코스개요       |
| subname           | 코스명         |
| subnum            | 반복일련번호   |

#### 숙박 (contentTypeId=32)

| **파라미터명**        | **설명**           |
| --------------------- | ------------------ |
| roomcode              | 객실코드           |
| roomtitle             | 객실명칭           |
| roomsize1             | 객실크기(평)       |
| roomsize2             | 객실크기(평방미터) |
| roomcount             | 객실수             |
| roombasecount         | 기준인원           |
| roommaxcount          | 최대인원           |
| roomoffseasonminfee1  | 비수기주중최소     |
| roomoffseasonminfee2  | 비수기주말최소     |
| roompeakseasonminfee1 | 성수기주중최소     |
| roompeakseasonminfee2 | 성수기주말최소     |
| roomintro             | 객실소개           |
| roombathfacility      | 목욕시설여부       |
| roombath              | 욕조여부           |
| roomhometheater       | 홈시어터여부       |
| roomaircondition      | 에어컨여부         |
| roomtv                | TV 여부            |
| roompc                | PC 여부            |
| roomcable             | 케이블설치여부     |
| roominternet          | 인터넷여부         |
| roomrefrigerator      | 냉장고여부         |
| roomtoiletries        | 세면도구여부       |
| roomsofa              | 소파여부           |
| roomcook              | 취사용품여부       |
| roomtable             | 테이블여부         |
| roomhairdryer         | 드라이기여부       |
| roomimg1~5            | 객실사진1~5        |
| roomimg1alt~5alt      | 객실사진 설명1~5   |
| cpyrhtDivCd1~5        | 저작권 유형1~5     |

### 요청 예제

```
https://apis.data.go.kr/B551011/KorService2/detailInfo2?serviceKey=인증키&MobileApp=AppTest&MobileOS=ETC&pageNo=1&numOfRows=10&_type=json&contentTypeId=12&contentId=126128
```

---

## 3.9. 이미지정보 조회 (상세정보4)

각 관광타입에 해당하는 이미지 URL 목록 및 이미지 저작권 공공누리 유형을 조회합니다. "음식점" 타입의 경우 음식메뉴 이미지를 제공합니다.

- Endpoint: `/detailImage2`

### Request Parameters

| **파라미터명** | **타입** | **필수 여부** | **설명**                                                 |
| -------------- | -------- | ------------- | -------------------------------------------------------- |
| contentId      | String   | 필수          | 콘텐츠 ID                                                |
| imageYN        | String   | 옵션          | Y=콘텐츠이미지조회, N=음식점 타입의 음식메뉴 이미지 조회 |

### Response Properties

| **파라미터명** | **타입** | **필수 여부** | **설명**                      |
| -------------- | -------- | ------------- | ----------------------------- |
| contentid      | String   | 필수          | 콘텐츠 ID                     |
| originimgurl   | String   | 옵션          | 원본이미지 URL (약 500*333)   |
| imgname        | String   | 옵션          | 이미지명                      |
| smallimageurl  | String   | 옵션          | 썸네일이미지 URL (약 160*100) |
| cpyrhtDivCd    | String   | 옵션          | 저작권 유형 (Type1, Type3)    |
| serialnum      | String   | 옵션          | 이미지일련번호                |

### 요청 예제

```
https://apis.data.go.kr/B551011/KorService2/detailImage2?serviceKey=인증키&MobileApp=AppTest&MobileOS=ETC&pageNo=1&numOfRows=10&contentId=126128&imageYN=Y&_type=json
```

---

## 3.10. 관광정보 동기화 목록 조회

관광정보 동기화 목록을 조회합니다. 제목순, 수정일순, 등록일순 정렬 검색을 제공합니다.

- Endpoint: `/areaBasedSyncList2`

### Request Parameters

| **파라미터명** | **타입** | **필수 여부** | **설명**                                                                |
| -------------- | -------- | ------------- | ----------------------------------------------------------------------- |
| showflag       | String   | 옵션          | 콘텐츠 표출여부 (1=표출, 0=비표출)                                      |
| modifiedtime   | String   | 옵션          | 콘텐츠 변경일자 (수정년도, 수정년월, 수정년월일 입력)                   |
| arrange        | String   | 옵션          | 정렬 구분 (A=제목순, C=수정일순, D=생성일순). 대표이미지 필수 (O, Q, R) |
| contentTypeId  | Integer  | 옵션          | 관광타입 ID                                                             |
| lDongRegnCd    | String   | 옵션          | 법정동 시도 코드                                                        |
| lDongSignguCd  | String   | 옵션          | 법정동 시군구 코드 (lDongRegnCd 필수)                                   |
| lclsSystm1     | String   | 옵션          | 분류체계 대분류                                                         |
| lclsSystm2     | String   | 옵션          | 분류체계 중분류 (lclsSystm1 필수)                                       |
| lclsSystm3     | String   | 옵션          | 분류체계 소분류 (lclsSystm1, lclsSystm2 필수)                           |
| oldContentid   | String   | 옵션          | 이전 콘텐츠 ID (DB저장 동기화시 이전 KEY 값으로 조회 용도)              |

### Response Properties

지역기반 조회의 응답 항목에 추가로 아래 항목이 포함됩니다.

| **파라미터명** | **타입** | **필수 여부** | **설명**        |
| -------------- | -------- | ------------- | --------------- |
| showflag       | String   | 필수          | 콘텐츠 표출여부 |

### 요청 예제

```
https://apis.data.go.kr/B551011/KorService2/areaBasedSyncList2?serviceKey=인증키&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json&showflag=1&arrange=C&contentTypeId=14&modifiedtime=20250415
```

---

## 3.11. 반려동물 동반 여행 정보 조회

타입별 반려동물 여행 정보를 조회합니다.

- Endpoint: `/detailPetTour2`

### Request Parameters

| **파라미터명** | **타입** | **필수 여부** | **설명**                                 |
| -------------- | -------- | ------------- | ---------------------------------------- |
| contentId      | String   | 옵션          | 콘텐츠 ID (미기입 시 반려동물 전부 출력) |

### Response Properties

| **파라미터명**   | **타입** | **필수 여부** | **설명**               |
| ---------------- | -------- | ------------- | ---------------------- |
| contentid        | String   | 옵션          | 콘텐츠 ID              |
| acmpyPsblCpam    | String   | 옵션          | 동반가능동물           |
| acmpyNeedMtr     | String   | 옵션          | 동반시 필요사항        |
| acmpyTypeCd      | String   | 옵션          | 동반유형코드(동반구분) |
| etcAcmpyInfo     | String   | 옵션          | 기타 동반 정보         |
| relaRntlPrdlst   | String   | 옵션          | 관련 렌탈 품목         |
| relaFrnshPrdlst  | String   | 옵션          | 관련 비치 품목         |
| relaPurcPrdlst   | String   | 옵션          | 관련 구매 품목         |
| relaAcdntRiskMtr | String   | 옵션          | 관련 사고 대비사항     |
| relaPosesFclty   | String   | 옵션          | 관련 구비 시설         |
| petTursmInfo     | String   | 옵션          | 반려동물 관광정보      |

### 요청 예제

```
https://apis.data.go.kr/B551011/KorService2/detailPetTour2?serviceKey=인증키&pageNo=1&numOfRows=10&MobileOS=ETC&MobileApp=AppTest&contentId=125534&_type=json
```

---

## 3.12. 법정동 코드 조회

법정동 시도코드, 시군구코드 목록을 조회합니다. 지역기반 관광정보 및 키워드 검색에서 지역별로 목록을 보여줄 경우, 시도코드를 이용하여 지역명을 매칭하기 위한 기능입니다.

- Endpoint: `/ldongCode2`

### Request Parameters

| **파라미터명** | **타입** | **필수 여부** | **설명**                                                                     |
| -------------- | -------- | ------------- | ---------------------------------------------------------------------------- |
| lDongRegnCd    | String   | 옵션          | 법정동 시도코드 (입력 시 해당 시군구코드 조회, 미입력 시 전체 시도목록 호출) |
| lDongListYn    | String   | 옵션          | 법정동 목록조회 여부 (N: 코드조회, Y: 전체목록조회)                          |

### Response Properties

#### 코드조회 모드 (lDongListYn=N)

| **파라미터명** | **타입** | **필수 여부** | **설명**                        |
| -------------- | -------- | ------------- | ------------------------------- |
| code           | String   | 옵션          | 법정동 시도코드 또는 시군구코드 |
| name           | String   | 옵션          | 법정동 시도명 또는 시군구명     |
| rnum           | Integer  | 옵션          | 일련번호                        |

#### 전체목록조회 모드 (lDongListYn=Y)

| **파라미터명** | **타입** | **필수 여부** | **설명**          |
| -------------- | -------- | ------------- | ----------------- |
| lDongRegnCd    | String   | 옵션          | 법정동 시도코드   |
| lDongRegnNm    | String   | 옵션          | 법정동 시도명     |
| lDongSignguCd  | String   | 옵션          | 법정동 시군구코드 |
| lDongSignguNm  | String   | 옵션          | 법정동 시군구명   |
| rnum           | Integer  | 옵션          | 일련번호          |

### 요청 예제

```
https://apis.data.go.kr/B551011/KorService2/ldongCode2?serviceKey=인증키&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=APP&_type=json&lDongRegnCd=11&lDongListYn=N
```

---

## 3.13. 분류체계 코드 조회

콘텐츠별 분류체계 코드를 1Depth, 2Depth, 3Depth 코드별로 조회합니다.

- Endpoint: `/lclsSystmCode2`

### Request Parameters

| **파라미터명**  | **타입** | **필수 여부** | **설명**                                              |
| --------------- | -------- | ------------- | ----------------------------------------------------- |
| lclsSystm1      | String   | 옵션          | 대분류코드                                            |
| lclsSystm2      | String   | 옵션          | 중분류코드 (lclsSystm1 필수)                          |
| lclsSystm3      | String   | 옵션          | 소분류코드 (lclsSystm1, lclsSystm2 필수)              |
| lclsSystmListYn | String   | 옵션          | 분류체계 목록조회 여부 (N: 코드조회, Y: 전체목록조회) |

### Response Properties

#### 코드조회 모드 (lclsSystmListYn=N)

| **파라미터명** | **타입** | **필수 여부** | **설명**                    |
| -------------- | -------- | ------------- | --------------------------- |
| code           | String   | 옵션          | 1Depth/2Depth/3Depth 코드   |
| name           | String   | 옵션          | 1Depth/2Depth/3Depth 코드명 |
| rnum           | Integer  | 옵션          | 일련번호                    |

#### 전체목록조회 모드 (lclsSystmListYn=Y)

| **파라미터명** | **타입** | **필수 여부** | **설명**            |
| -------------- | -------- | ------------- | ------------------- |
| lclsSystm1Cd   | String   | 옵션          | 분류체계 대분류코드 |
| lclsSystm1Nm   | String   | 옵션          | 분류체계 대분류명   |
| lclsSystm2Cd   | String   | 옵션          | 분류체계 중분류코드 |
| lclsSystm2Nm   | String   | 옵션          | 분류체계 중분류명   |
| lclsSystm3Cd   | String   | 옵션          | 분류체계 소분류코드 |
| lclsSystm3Nm   | String   | 옵션          | 분류체계 소분류명   |
| rnum           | Integer  | 옵션          | 일련번호            |

### 요청 예제

```
https://apis.data.go.kr/B551011/KorService2/lclsSystmCode2?serviceKey=인증키&MobileApp=APP&MobileOS=ETC&pageNo=1&numOfRows=10&_type=json&lclsSystmListYn=N
```

---

# 4. Test Example (테스트 예제)

지역기반 관광정보 조회를 기준으로 작성된 테스트 예제입니다.

### cURL Request

```bash
curl -X GET "http://apis.data.go.kr/B551011/KorService2/areaBasedList2?serviceKey=YOUR_SERVICE_KEY&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json&contentTypeId=12"
```

### Response Example

```json
{
  "response": {
    "header": {
      "resultCode": "0000",
      "resultMsg": "OK"
    },
    "body": {
      "items": {
        "item": [
          {
            "addr1": "부산광역시 사하구 낙동남로 1240 (하단동)",
            "addr2": "",
            "contentid": "127974",
            "contenttypeid": "12",
            "createdtime": "20031208090000",
            "firstimage": "http://tong.visitkorea.or.kr/cms/resource/21/3497121_image2_1.jpg",
            "firstimage2": "http://tong.visitkorea.or.kr/cms/resource/21/3497121_image3_1.jpg",
            "cpyrhtDivCd": "Type1",
            "mapx": "128.9460030322",
            "mapy": "35.1045320626",
            "mlevel": "6",
            "modifiedtime": "20250618095454",
            "tel": "",
            "title": "을숙도 공원",
            "zipcode": "49435",
            "lDongRegnCd": "26",
            "lDongSignguCd": "380",
            "lclsSystm1": "NA",
            "lclsSystm2": "NA04",
            "lclsSystm3": "NA040500"
          }
        ]
      },
      "numOfRows": 3,
      "pageNo": 1,
      "totalCount": 3
    }
  }
}
```

---

# 5. Error Codes (에러 코드)

## 공공데이터포털 에러코드

| **에러 코드** | **에러 메시지**                                  | **설명**                            |
| ------------- | ------------------------------------------------ | ----------------------------------- |
| 01            | APPLICATION_ERROR                                | 어플리케이션 에러                   |
| 04            | HTTP_ERROR                                       | HTTP 에러                           |
| 12            | NO_OPENAPI_SERVICE_ERROR                         | 해당 오픈API 서비스가 없거나 폐기됨 |
| 20            | SERVICE_ACCESS_DENIED_ERROR                      | 서비스 접근 거부                    |
| 22            | LIMITED_NUMBER_OF_SERVICE_REQUESTS_EXCEEDS_ERROR | 서비스 요청 제한 횟수 초과 에러     |
| 30            | SERVICE_KEY_IS_NOT_REGISTERED_ERROR              | 등록되지 않은 서비스키              |
| 31            | DEADLINE_HAS_EXPIRED_ERROR                       | 활용 기간 만료                      |
| 32            | UNREGISTERED_IP_ERROR                            | 등록되지 않은 IP                    |
| 99            | UNKNOWN_ERROR                                    | 기타 에러                           |

## 제공기관 에러코드

| **에러 코드** | **에러 메시지**                                  | **설명**                            |
| ------------- | ------------------------------------------------ | ----------------------------------- |
| 00            | NORMAL_CODE                                      | 정상                                |
| 01            | APPLICATION_ERROR                                | 어플리케이션 에러                   |
| 02            | DB_ERROR                                         | 데이터 베이스 에러                  |
| 03            | NODATA_ERROR                                     | 데이터 없음 에러                    |
| 04            | HTTP_ERROR                                       | HTTP 에러                           |
| 05            | SERVICETIMEOUT_ERROR                             | 서비스 연결 실패 에러               |
| 10            | INVALID_REQUEST_PARAMETER_ERROR                  | 잘못된 요청 파라메터 에러           |
| 11            | NO_MANDATORY_REQUEST_PARAMETERS_ERROR            | 필수 요청 파라메터가 없음           |
| 12            | NO_OPENAPI_SERVICE_ERROR                         | 해당 오픈API서비스가 없거나 폐기됨  |
| 20            | SERVICE_ACCESS_DENIED_ERROR                      | 서비스 접근 거부                    |
| 21            | TEMPORARILY_DISABLE_THE_SERVICEKEY_ERROR         | 일시적으로 사용할 수 없는 서비스 키 |
| 22            | LIMITED_NUMBER_OF_SERVICE_REQUESTS_EXCEEDS_ERROR | 서비스 요청 제한 횟수 초과 에러     |
| 30            | SERVICE_KEY_IS_NOT_REGISTERED_ERROR              | 등록되지 않은 서비스키              |
| 31            | DEADLINE_HAS_EXPIRED_ERROR                       | 활용기간 만료                       |
| 32            | UNREGISTERED_IP_ERROR                            | 등록되지 않은 IP                    |
| 33            | UNSIGNED_CALL_ERROR                              | 서명되지 않은 호출                  |
| 99            | UNKNOWN_ERROR                                    | 기타 에러                           |
