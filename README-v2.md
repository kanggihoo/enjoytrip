# EnjoyTrip v2

EnjoyTrip v2는 기존 Jakarta Servlet/JSP 기반 여행 정보 공유 서비스를 Spring Boot 3 + MyBatis 구조로 마이그레이션한 버전입니다.

기존 DB 스키마와 JSP 화면 흐름은 유지하면서, 서버 실행/설정/테스트 구조를 Spring Boot 표준 Maven 프로젝트로 정리했습니다. 일반 화면은 JSP 서버 렌더링과 form submit을 사용하고, 관광지 검색과 여행계획 순서 변경처럼 JSON 응답이 필요한 동적 기능만 `/api/**` REST API로 분리합니다.

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Backend | Java 17, Spring Boot 3.3.5, Spring MVC |
| View | JSP, JSTL, HTML5, CSS3, Vanilla JavaScript |
| Persistence | MyBatis Spring Boot Starter 3.0.3, XML Mapper |
| Database | MySQL 8, Docker Compose |
| External API | 공공데이터포털 한국관광공사 KorService2 API |
| Map | Kakao Maps JavaScript API |
| Auth | `HttpSession`, `JSESSIONID` |
| Build | Maven Wrapper, executable WAR |
| Test | JUnit 5, Mockito, Spring MVC Test, MyBatis Test, Testcontainers, MockWebServer |
| Logging | SLF4J, Logback, Spring MVC Interceptor |

---

## v2 설계 방향

- Spring Boot executable WAR로 실행합니다.
- 기존 `WebContent`, Servlet, JDBC DAO 중심 구조를 `src/main/java`, `src/main/resources`, `src/main/webapp` 표준 구조로 재배치했습니다.
- Page Controller와 API Controller를 분리합니다.
- 기존 `DBUtil`, `Dao`, `DaoImpl` 계층은 MyBatis Mapper와 XML SQL로 전환했습니다.
- 인증은 Spring Security 없이 기존 세션 키인 `loginUser`, `loginUserName`을 유지합니다.
- 로그인 여부는 `AuthInterceptor`에서 확인하고, 리소스 소유자 검사는 서비스 계층에서 수행합니다.
- Page 요청과 API 요청의 예외 처리는 전역 핸들러에서 분리합니다.
- 요청 로깅은 `RequestLoggingInterceptor`에서 처리하며 민감 정보는 마스킹합니다.
- API 키와 DB 접속 정보는 코드에 두지 않고 환경변수 또는 `.env`로 관리합니다.

---

## 주요 기능

### F101~103. 지역별 관광지 조회

- 시/도, 구/군, 관광지 유형, 키워드 기반 관광지 검색
- 공공데이터포털 KorService2 API 서버 사이드 프록시
- Kakao Map 마커 표시 및 관광지 상세 화면 제공
- `/api/attractions/**` JSON API와 JSP 화면 컨트롤러 분리

### F104. 나의 여행계획

- 관광지를 여행계획 방문지로 추가
- 여행계획 목록, 상세, 작성, 수정, 삭제
- 드래그앤드롭 기반 방문 순서 변경
- 방문 순서 변경은 `PATCH /api/plans/details/{detailId}/order`로 처리
- 계획 수정/삭제 및 상세 변경 시 서비스 계층에서 소유자 검증

### F105. 핫플레이스

- 사용자가 직접 장소와 방문 경험 등록
- 이미지 업로드 처리
- 목록, 상세, 작성, 수정, 삭제
- 수정/삭제 시 작성자 소유권 검증

### F106. 게시판

- 공지사항과 자유게시판 분리
- 글 목록, 상세, 작성, 수정, 삭제
- 조회수 증가
- 작성/수정/삭제 권한 검증

### F107~108. 회원관리와 로그인관리

- 회원가입, 로그인, 로그아웃
- 마이페이지, 정보 수정, 회원 탈퇴
- `HttpSession` 기반 로그인 상태 유지

---

## 프로젝트 구조

```text
EnjoyTrip/
├── pom.xml
├── docker-compose.yml
├── scripts/
│   ├── ai-test.ps1
│   └── ai-test.sh
├── sql/
│   └── schema.sql
├── src/main/java/com/enjoytrip/
│   ├── EnjoyTripApplication.java
│   ├── common/
│   │   ├── controller/
│   │   ├── exception/
│   │   └── response/
│   ├── config/
│   │   ├── AuthInterceptor.java
│   │   ├── DotenvApplicationContextInitializer.java
│   │   ├── RequestLoggingInterceptor.java
│   │   └── WebMvcConfig.java
│   ├── attraction/
│   │   ├── client/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── mapper/
│   │   └── service/
│   ├── board/
│   ├── hotplace/
│   ├── member/
│   └── plan/
├── src/main/resources/
│   ├── application.yml
│   ├── application-test.yml
│   └── mappers/
├── src/main/webapp/
│   ├── css/
│   └── WEB-INF/views/
└── src/test/java/com/enjoytrip/
```

---

## 주요 URL

### Page URL

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/` | 메인 화면 |
| GET | `/attractions` | 관광지 검색 화면 |
| GET | `/attractions/{contentId}` | 관광지 상세 화면 |
| GET | `/boards` | 게시판 목록 |
| GET | `/boards/{boardId}` | 게시글 상세 |
| GET | `/plans` | 내 여행계획 목록 |
| GET | `/plans/{planId}` | 여행계획 상세 |
| GET | `/hotplaces` | 핫플레이스 목록 |
| GET | `/hotplaces/{hotplaceId}` | 핫플레이스 상세 |

기존 URL은 새 URL로 redirect 호환을 둡니다.

```text
/attraction/list -> /attractions
/board/list      -> /boards
/plan/list       -> /plans
/hotplace/list   -> /hotplaces
```

### REST API URL

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/attractions/sidos` | 시/도 목록 조회 |
| GET | `/api/attractions/guguns?sidoCode=...` | 구/군 목록 조회 |
| GET | `/api/attractions/search` | 관광지 검색 |
| GET | `/api/attractions/{contentId}` | 관광지 상세 JSON 조회 |
| PATCH | `/api/plans/details/{detailId}/order` | 여행계획 방문 순서 변경 |

---

## 데이터베이스

로컬 개발 DB는 Docker Compose 기반 MySQL 8을 사용합니다.

| 항목 | 값 |
|------|----|
| Container | `mysql-enjoytrip` |
| Host port | `3305` |
| Database | `enjoytrip` |
| Username | `ssafy` |
| Password | `ssafy` |
| Schema | `sql/schema.sql` |

주요 테이블은 다음과 같습니다.

| 테이블 | 설명 |
|--------|------|
| `member` | 회원 |
| `sido` | 광역시/도 코드 |
| `gugun` | 구/군 코드 |
| `attraction_info` | 관광지 정보 |
| `travel_plan` | 여행계획 헤더 |
| `plan_detail` | 여행계획 방문지 상세 |
| `hotplace` | 핫플레이스 |
| `board` | 게시판 |

ERD는 기존 산출물인 `결과이미지/ERD.png`와 `erd.md`를 참고합니다.

---

## 환경변수

프로젝트 루트에 `.env` 파일을 생성합니다.

```text
TOUR_API_KEY=replace-with-tour-api-key
KAKAO_JAVASCRIPT_KEY=replace-with-kakao-javascript-key
DB_URL=jdbc:mysql://localhost:3305/enjoytrip?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8
DB_USERNAME=ssafy
DB_PASSWORD=ssafy
```

`application.yml`은 위 환경변수를 참조합니다. `.env`는 로컬 비밀값이므로 Git에 포함하지 않습니다.

---

## 실행 방법

### 1. DB 실행

```powershell
docker compose up -d
```

### 2. 애플리케이션 실행

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
./mvnw spring-boot:run
```

실행 후 `http://localhost:8080/`에 접속합니다.

### 3. 패키징

Windows PowerShell:

```powershell
.\mvnw.cmd package
```

macOS/Linux:

```bash
./mvnw package
```

---

## 테스트

이 저장소에서는 raw Maven 출력보다 필터링된 테스트 러너 사용을 권장합니다.

Windows PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\ai-test.ps1
```

macOS/Linux:

```bash
./scripts/ai-test.sh
```

특정 테스트만 실행할 수 있습니다.

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\ai-test.ps1 com.enjoytrip.common.controller.HomeControllerTest
powershell -ExecutionPolicy Bypass -File .\scripts\ai-test.ps1 com.enjoytrip.common.controller.HomeControllerTest#indexReturnsIndexView
```

성공 출력은 `OK`입니다. 실패 시 축약된 실패 정보와 `.tmp/ai-test.log`의 원본 로그를 확인합니다.

---

## 테스트 전략

- 서비스 단위 테스트는 Mapper와 외부 Client를 Mockito로 대체합니다.
- MVC slice 테스트는 Page Controller, API Controller, Interceptor, Exception Handler를 검증합니다.
- MyBatis slice 테스트는 Mapper SQL, result mapping, insert/update/delete를 검증합니다.
- DB 연동 테스트는 가능하면 Testcontainers MySQL을 사용합니다.
- 외부 관광 API Client는 MockWebServer로 정상 응답과 실패 응답을 검증합니다.

---

## 예외 처리

공통 예외 모델은 `common.exception` 패키지에서 관리합니다.

```text
BusinessException
BadRequestException
UnauthorizedException
ForbiddenException
NotFoundException
ExternalApiException
FileStorageException
ErrorCode
```

API 오류 응답은 JSON 형식을 사용합니다.

```json
{
  "code": "BAD_REQUEST",
  "message": "요청 파라미터가 올바르지 않습니다.",
  "path": "/api/attractions/search",
  "timestamp": "2026-05-13T12:00:00"
}
```

Page 요청은 JSP 오류 화면 또는 redirect로 처리하고, API 요청은 JSON 오류 응답으로 처리합니다.

---

## 인증과 권한

Spring Security는 v2 1차 범위에서 제외했습니다.

로그인 성공 시 세션에 다음 값을 저장합니다.

```text
session.loginUser = userId
session.loginUserName = userName
```

인증이 필요한 URL은 `AuthInterceptor`가 로그인 여부를 검사합니다. 게시글, 여행계획, 핫플레이스의 소유자 검증은 각 서비스 계층에서 수행하며, 리소스가 없으면 `NotFoundException`, 소유자가 다르면 `ForbiddenException`을 사용합니다.

---

## 요청 로깅

`RequestLoggingInterceptor`는 요청과 응답의 핵심 정보를 기록합니다.

```text
[REQ] GET /api/attractions/search handler=AttractionApiController.search user=ssafy params={areaCode=1, sigunguCode=2}
[RES] GET /api/attractions/search status=200 elapsedMs=183
```

다음 민감 정보는 로그에 노출하지 않도록 마스킹합니다.

```text
userPw
password
serviceKey
apiKey
token
JSESSIONID
```

---

## 관련 문서

- `docs/superpowers/specs/2026-05-13-spring-boot-mybatis-migration-design.md`
- `docs/superpowers/plans/2026-05-13-spring-boot-mybatis-migration.md`
- `API-Overview.md`
- `DESIGN.md`
- `erd.md`
