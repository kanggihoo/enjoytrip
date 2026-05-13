# EnjoyTrip Spring Boot + MyBatis Migration Design

## 1. Summary

EnjoyTrip의 기존 Jakarta Servlet/JSP MVC 애플리케이션을 Spring Boot 기반 구조로 마이그레이션한다.

주요 목표는 단순 포팅이 아니라 기존 기능을 보존하면서 Spring Boot식 구조로 정리하는 것이다. DB 스키마와 JSP 화면은 유지하고, DAO 계층은 MyBatis로 전환한다. JSON 응답이 필요한 동적 기능만 REST API로 분리하며, 일반 화면 흐름은 JSP 서버 렌더링과 form submit을 유지한다.

Spring Security는 1차 범위에서 제외한다. 인증은 기존처럼 서버 `HttpSession`과 브라우저의 `JSESSIONID` 쿠키를 기반으로 처리한다.

## 2. Project Structure

기존 Eclipse/Tomcat 중심 구조인 `src/`, `WebContent/`를 Spring Boot 표준 Maven 구조로 재배치한다.

```text
src/main/java/com/enjoytrip
├── EnjoyTripApplication.java
├── common
│   ├── exception
│   └── response
├── config
│   ├── AuthInterceptor.java
│   ├── RequestLoggingInterceptor.java
│   └── WebMvcConfig.java
├── member
│   ├── controller
│   ├── service
│   ├── mapper
│   └── dto
├── board
├── plan
├── hotplace
└── attraction
    ├── controller
    │   ├── AttractionPageController.java
    │   └── AttractionApiController.java
    ├── service
    ├── mapper
    ├── dto
    └── client
        └── TourApiClient.java

src/main/resources
├── application.yml
└── mappers

src/main/webapp
├── WEB-INF/views
└── css

src/test/java/com/enjoytrip
```

Packaging은 JSP 호환성을 고려해 executable WAR로 둔다. Java 버전은 README 기준 Java 17을 우선한다.

## 3. Controller and API Design

Page Controller와 API Controller를 분리한다.

- Page Controller: JSP view name 반환
- API Controller: JSON 응답 반환
- Service: 업무 규칙, 트랜잭션, 권한 검증
- Mapper: MyBatis 기반 DB 접근
- Client: 외부 공공데이터 API 호출

### Page URLs

대표 URL은 RESTful하게 정리한다.

```text
GET  /                         -> index.jsp
GET  /attractions              -> attraction/list.jsp
GET  /attractions/{contentId}  -> attraction/detail.jsp
GET  /boards                   -> board/list.jsp
GET  /boards/{boardId}         -> board/detail.jsp
GET  /plans                    -> plan/list.jsp
GET  /plans/{planId}           -> plan/detail.jsp
GET  /hotplaces                -> hotplace/list.jsp
GET  /hotplaces/{hotplaceId}   -> hotplace/detail.jsp
```

기존 URL은 새 URL로 redirect 호환을 둔다.

```text
/board/list       -> /boards
/plan/list        -> /plans
/hotplace/list    -> /hotplaces
/attraction/list  -> /attractions
```

### REST API URLs

1차 REST API는 JSON 응답이 필요한 동적 기능만 대상으로 한다.

```text
GET   /api/attractions/sidos
GET   /api/attractions/guguns?sidoCode=...
GET   /api/attractions/search?areaCode=...&sigunguCode=...&contentTypeId=...&keyword=...
GET   /api/attractions/{contentId}
PATCH /api/plans/details/{detailId}/order
```

## 4. Attraction API and DTO Boundary

Attraction 도메인은 현재 컨트롤러가 JSP 반환과 외부 API 프록시를 함께 담당한다. 마이그레이션 후에는 다음처럼 분리한다.

- `AttractionPageController`: `/attractions`, `/attractions/{contentId}` JSP 반환
- `AttractionApiController`: `/api/attractions/**` JSON 응답
- `TourApiClient`: 공공데이터 API 호출

쿼리 파라미터 DTO는 `/api/attractions/search`에만 둔다. 단일 파라미터 요청은 컨트롤러 메서드 파라미터로 직접 받는다.

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttractionSearchRequest {
    private Integer areaCode;
    private Integer sigunguCode;
    private Integer contentTypeId;
    private String keyword;
    private Integer pageNo;
    private Integer numOfRows;
}
```

외부 API 고정 파라미터는 DTO에 포함하지 않는다.

- `serviceKey`: `application.yml`에서 주입
- `MobileOS`: 서버 내부 기본값
- `MobileApp`: 서버 내부 기본값
- `_type=json`: 서버 내부 기본값

외부 API 응답은 1차 마이그레이션에서 원본 JSON passthrough를 유지한다. 기존 JSP와 JavaScript가 공공데이터 API 응답 구조에 맞춰져 있기 때문이다.

## 5. MyBatis Migration

기존 `DBUtil`, `Dao`, `DaoImpl` 구조를 MyBatis Mapper로 전환한다.

- Mapper 인터페이스는 도메인별로 둔다.
- SQL은 `src/main/resources/mappers` 아래 XML로 분리한다.
- 기존 SQL과 테이블명은 최대한 유지한다.
- 변경 작업이 여러 쿼리를 포함하는 서비스 메서드에는 `@Transactional`을 적용한다.

예상 Mapper 범위:

```text
MemberMapper
BoardMapper
TravelPlanMapper
HotplaceMapper
AttractionMapper
```

Plan 생성과 상세 저장, 상세 교체, 삭제 같은 기능은 트랜잭션 경계가 필요하다.

## 6. Configuration and Secrets

API 키와 DB 접속 정보는 `.env` 기반으로 관리한다.

```text
TOUR_API_KEY=...
KAKAO_JAVASCRIPT_KEY=...
DB_URL=jdbc:mysql://localhost:3305/enjoytrip
DB_USERNAME=ssafy
DB_PASSWORD=ssafy
```

`.env`는 git에 포함하지 않는다. 저장소에는 `.env.example`만 둔다.

`application.yml`은 환경변수를 참조한다.

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

tour:
  api:
    base-url: https://apis.data.go.kr/B551011/KorService2
    service-key: ${TOUR_API_KEY}

kakao:
  javascript-key: ${KAKAO_JAVASCRIPT_KEY}
```

Spring Boot는 기본적으로 `.env`를 자동 로딩하지 않으므로, 로컬 개발 편의를 위한 `.env` 로딩 방식을 추가한다.

## 7. Authentication and Authorization

Spring Security는 1차 마이그레이션 범위에서 제외한다.

로그인 성공 시 기존 세션 키를 유지한다.

```text
session.loginUser = userId
session.loginUserName = userName
```

브라우저는 `JSESSIONID` 쿠키로 서버 세션을 유지한다.

인증 범위:

- 공개:
  - `/`
  - `/attractions/**`
  - `/api/attractions/**`
  - 로그인/회원가입 요청
  - 정적 리소스
- Board:
  - 목록/상세 조회는 공개
  - 작성/수정/삭제는 인증 필요
- Hotplace:
  - 목록/상세 조회는 공개
  - 작성/수정/삭제는 인증 필요
- Plan:
  - 전체 인증 필요
- Member:
  - 로그인/회원가입 제외 인증 필요

`AuthInterceptor`는 로그인 여부만 확인한다. 리소스 소유자 검사는 서비스에서 수행한다.

```text
AuthInterceptor
-> 로그인 없음: UnauthorizedException

Service
-> 리소스 없음: NotFoundException
-> 소유자 불일치: ForbiddenException
```

## 8. Request Logging

요청 로깅은 `RequestLoggingInterceptor`에서 처리한다.

로깅 대상:

- HTTP method
- URI
- 매핑된 handler
- 로그인 사용자
- query/form parameter
- response status
- elapsed time

예시:

```text
[REQ] GET /api/attractions/search handler=AttractionApiController.search user=ssafy params={areaCode=1, sigunguCode=2}
[RES] GET /api/attractions/search status=200 elapsedMs=183
```

민감 정보는 마스킹한다.

```text
userPw
password
serviceKey
apiKey
token
JSESSIONID
```

정적 리소스, JSP 내부 forward, favicon 등은 로깅 대상에서 제외한다.

## 9. Exception Handling

예외는 의미 있는 경계에서만 변환한다. 서비스에서 아무 의미 없이 `catch -> throw`만 하는 코드는 만들지 않는다.

서비스에서 처리할 예외:

- 리소스 없음 -> `NotFoundException`
- 소유자 불일치 -> `ForbiddenException`
- 아이디 중복 -> `BusinessException`
- 외부 API 실패에 도메인 맥락 부여 -> `ExternalApiException`
- 파일 업로드 실패 -> `FileStorageException`

대부분의 `DataAccessException`은 전역 핸들러에서 `DATABASE_ERROR`로 처리한다.

공통 예외 타입:

```text
BusinessException
ErrorCode
UnauthorizedException
ForbiddenException
NotFoundException
BadRequestException
ExternalApiException
FileStorageException
```

핸들러는 Page와 API를 분리한다.

- `GlobalApiExceptionHandler`
  - `/api/**` 요청 대상
  - JSON 오류 응답
- `GlobalPageExceptionHandler`
  - JSP 화면 요청 대상
  - redirect 또는 error JSP forward

API 오류 응답 형식:

```json
{
  "code": "BAD_REQUEST",
  "message": "요청 파라미터가 올바르지 않습니다.",
  "path": "/api/attractions/search",
  "timestamp": "2026-05-13T12:00:00"
}
```

자주 처리할 Spring 예외:

```text
MethodArgumentTypeMismatchException
MissingServletRequestParameterException
BindException
HttpRequestMethodNotSupportedException
MaxUploadSizeExceededException
DataAccessException
```

## 10. Database Runtime and Test Database

로컬 개발 DB는 현재처럼 Docker Compose 기반 MySQL을 유지한다.

```text
image: mysql:8
host port: 3305
database: enjoytrip
username: ssafy
password: ssafy
schema: sql/schema.sql
```

`sql/schema.sql`은 로컬 Docker 초기화와 테스트 컨테이너 초기화에서 공통으로 사용한다.

DB 테스트는 가능하면 Testcontainers MySQL을 사용한다.

- `mysql:8` 컨테이너 사용
- `@DynamicPropertySource`로 datasource 주입
- `sql/schema.sql`로 스키마 초기화
- Docker 사용 불가 환경에서는 DB 통합 테스트를 skip할 수 있도록 JUnit 조건 또는 Maven profile을 둔다.

## 11. Test Strategy

테스트 코드는 필수로 작성한다. 단위 테스트와 slice 테스트를 모두 포함한다.

### Unit Tests

서비스 계층은 Mockito 기반으로 mapper/client를 mock 처리한다.

검증 대상:

- 비즈니스 예외 변환
- 소유자 검증
- 로그인 사용자 기준 필터링
- 트랜잭션 대상 흐름
- 외부 API 실패 시 예외 변환

### Slice Tests

`@WebMvcTest`:

- Page Controller view name
- redirect
- API JSON 응답
- `AuthInterceptor`
- `RequestLoggingInterceptor`
- `GlobalExceptionHandler`

`@MybatisTest`:

- Mapper SQL
- result mapping
- insert/update/delete
- 동적 조건 검색
- Testcontainers MySQL 연동

외부 API client:

- mock server 또는 HTTP stub 사용
- 정상 응답 passthrough
- timeout/error 응답
- API key가 로그에 노출되지 않는지 확인

### Integration Tests

핵심 흐름만 `@SpringBootTest`로 검증한다.

- 로그인 -> Plan 생성 -> 상세 추가 -> 순서 변경
- Board 작성/수정/삭제 권한
- Hotplace 파일 업로드 처리
- Attraction API 프록시 정상/실패 흐름

### Build Verification

```bash
mvn test
mvn package
```

## 12. Acceptance Criteria

- Spring Boot 애플리케이션이 executable WAR로 실행된다.
- 기존 JSP 화면이 정상 렌더링된다.
- 기존 DB 스키마를 변경하지 않고 주요 기능이 동작한다.
- JSON 동적 기능은 `/api/**` 컨트롤러로 분리된다.
- 공공데이터 API 키와 Kakao 키가 코드에서 제거되고 환경 설정으로 관리된다.
- 로그인 상태는 기존 세션 키와 `JSESSIONID` 쿠키로 유지된다.
- 인증이 필요한 요청은 인터셉터에서 차단된다.
- 권한 검사는 서비스에서 수행된다.
- 전역 예외 핸들러가 Page/API 오류 응답을 일관되게 처리한다.
- 요청 로깅에 endpoint, handler, masked parameter, status, elapsed time이 남는다.
- 민감 정보가 로그에 출력되지 않는다.
- 단위 테스트, slice 테스트, 핵심 통합 테스트가 존재한다.
- MyBatis 테스트는 가능하면 Testcontainers MySQL로 격리 실행된다.

## 13. Assumptions

- 원본 코드는 GitLab에 보존되어 있으므로 현재 프로젝트는 Boot 표준 구조로 직접 재배치해도 된다.
- DB 스키마와 테이블명은 1차 마이그레이션에서 변경하지 않는다.
- JSP는 1차 마이그레이션에서 유지한다.
- 프론트엔드 fetch 전환은 기존에도 JSON을 사용하던 동적 기능에만 적용한다.
- Spring Security, JWT, SPA 전환은 1차 범위에서 제외한다.
- 로컬 DB는 Docker Compose를 유지하고, 테스트 DB는 Testcontainers를 기본 방향으로 한다.
