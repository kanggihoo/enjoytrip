# EnjoyTrip 클래스 다이어그램

도메인별로 분리한 클래스 다이어그램입니다.

---

## 그림 1 · 레이어 구조

```mermaid
graph TD
    subgraph Layer["레이어 구조 (전 도메인 공통)"]
        C["Controller\n@WebServlet — 요청 라우팅"]
        S["Service\n비즈니스 로직"]
        D["DAO\nJDBC 쿼리 실행"]
        DB[("MySQL\nenjoytrip DB")]
    end

    C --> S --> D --> DB
```

---

## 그림 2 · 회원 도메인 (F107~108)

```mermaid
classDiagram
    class MemberController {
        <<HttpServlet /user/*>>
        +doGet() void
        +doPost() void
    }

    class MemberService {
        <<interface>>
        +login(userId, userPw) Member
        +join(member) void
        +getById(userId) Member
        +modify(member) void
        +remove(userId) void
    }

    class MemberServiceImpl {
        -MemberDao dao
    }

    class MemberDao {
        <<interface>>
        +selectById(userId) Member
        +insert(member) void
        +update(member) void
        +delete(userId) void
    }

    class MemberDaoImpl {
        +selectById(userId) Member
        +insert(member) void
        +update(member) void
        +delete(userId) void
    }

    class Member {
        -String userId
        -String userPw
        -String userName
        -String email
        -String joinDate
    }

    MemberController --> MemberService
    MemberService <|.. MemberServiceImpl
    MemberServiceImpl --> MemberDao
    MemberDao <|.. MemberDaoImpl
    MemberDaoImpl ..> Member
```

---

## 그림 3 · 관광지 도메인 (F101~103)

```mermaid
classDiagram
    class AttractionController {
        <<HttpServlet /attraction/*>>
        -SERVICE_KEY String
        -API_BASE String
        +doGet() void
        +forwardApiResponse() void
    }

    class AttractionService {
        <<interface>>
        +getSidoList() List~Sido~
        +getGugunList(sidoCode) List~Gugun~
        +getById(contentId) AttractionInfo
    }

    class AttractionServiceImpl {
        -AttractionDao dao
    }

    class AttractionDao {
        <<interface>>
        +selectSidoList() List~Sido~
        +selectGugunList(sidoCode) List~Gugun~
        +selectById(contentId) AttractionInfo
    }

    class AttractionDaoImpl {
        +selectSidoList() List~Sido~
        +selectGugunList(sidoCode) List~Gugun~
        +selectById(contentId) AttractionInfo
    }

    class AttractionInfo {
        -int contentId
        -int contentTypeId
        -String title
        -int sidoCode
        -int gugunCode
        -String addr1
        -String tel
        -String firstImage
        -double mapx
        -double mapy
        -String overview
    }

    class Sido {
        -int sidoCode
        -String sidoName
    }

    class Gugun {
        -int gugunCode
        -int sidoCode
        -String gugunName
    }

    AttractionController --> AttractionService
    AttractionService <|.. AttractionServiceImpl
    AttractionServiceImpl --> AttractionDao
    AttractionDao <|.. AttractionDaoImpl
    AttractionDaoImpl ..> AttractionInfo
    AttractionDaoImpl ..> Sido
    AttractionDaoImpl ..> Gugun
    AttractionInfo --> Sido : sidoCode
    AttractionInfo --> Gugun : gugunCode
```

---

## 그림 4 · 여행계획 도메인 (F104)

```mermaid
classDiagram
    class PlanController {
        <<HttpServlet /plan/*>>
        +doGet() void
        +doPost() void
    }

    class TravelPlanService {
        <<interface>>
        +getListByUser(userId) List~TravelPlan~
        +getDetail(planId) TravelPlan
        +regist(plan, details) int
        +modify(plan, details) void
        +remove(planId) void
    }

    class TravelPlanServiceImpl {
        -TravelPlanDao dao
    }

    class TravelPlanDao {
        <<interface>>
        +selectByUserId(userId) List~TravelPlan~
        +selectById(planId) TravelPlan
        +insert(plan) int
        +update(plan) void
        +delete(planId) void
        +insertDetail(detail) void
        +deleteDetails(planId) void
    }

    class TravelPlanDaoImpl {
        +selectByUserId(userId) List~TravelPlan~
        +selectById(planId) TravelPlan
        +insert(plan) int
        +insertDetail(detail) void
    }

    class TravelPlan {
        -int planId
        -String userId
        -String title
        -String startDate
        -String endDate
        -int totalBudget
        -String memo
        -List~PlanDetail~ details
    }

    class PlanDetail {
        -int detailId
        -int planId
        -int contentId
        -String title
        -double latitude
        -double longitude
        -int visitOrder
        -String visitDate
        -String memo
    }

    PlanController --> TravelPlanService
    TravelPlanService <|.. TravelPlanServiceImpl
    TravelPlanServiceImpl --> TravelPlanDao
    TravelPlanDao <|.. TravelPlanDaoImpl
    TravelPlanDaoImpl ..> TravelPlan
    TravelPlan "1" *-- "0..*" PlanDetail : contains
```

---

## 그림 5 · 핫플레이스 도메인 (F105)

```mermaid
classDiagram
    class HotplaceController {
        <<HttpServlet /hotplace/*>>
        +doGet() void
        +doPost() void
    }

    class HotplaceService {
        <<interface>>
        +getList() List~Hotplace~
        +getDetail(id) Hotplace
        +regist(hp) int
        +modify(hp) void
        +remove(id) void
    }

    class HotplaceServiceImpl {
        -HotplaceDao dao
    }

    class HotplaceDao {
        <<interface>>
        +selectAll() List~Hotplace~
        +selectById(id) Hotplace
        +insert(hp) int
        +update(hp) void
        +delete(id) void
    }

    class HotplaceDaoImpl {
        +selectAll() List~Hotplace~
        +selectById(id) Hotplace
        +insert(hp) int
        +update(hp) void
        +delete(id) void
    }

    class Hotplace {
        -int hotplaceId
        -String userId
        -String title
        -String visitDate
        -String placeType
        -String description
        -double latitude
        -double longitude
        -String imagePath
        -String createdAt
    }

    HotplaceController --> HotplaceService
    HotplaceService <|.. HotplaceServiceImpl
    HotplaceServiceImpl --> HotplaceDao
    HotplaceDao <|.. HotplaceDaoImpl
    HotplaceDaoImpl ..> Hotplace
```

---

## 그림 6 · 게시판 도메인 (F106)

```mermaid
classDiagram
    class BoardController {
        <<HttpServlet /board/*>>
        +doGet() void
        +doPost() void
    }

    class BoardService {
        <<interface>>
        +getList(type) List~Board~
        +getDetail(boardId) Board
        +write(board) int
        +modify(board) void
        +remove(boardId) void
    }

    class BoardServiceImpl {
        -BoardDao dao
        +getDetail(boardId) Board
    }

    class BoardDao {
        <<interface>>
        +selectByType(type) List~Board~
        +selectById(boardId) Board
        +insert(board) int
        +update(board) void
        +delete(boardId) void
        +incrementViews(boardId) void
    }

    class BoardDaoImpl {
        +selectByType(type) List~Board~
        +selectById(boardId) Board
        +insert(board) int
        +update(board) void
        +delete(boardId) void
        +incrementViews(boardId) void
    }

    class Board {
        -int boardId
        -String type
        -String title
        -String content
        -String userId
        -int views
        -String createdAt
        -String updatedAt
    }

    BoardController --> BoardService
    BoardService <|.. BoardServiceImpl
    BoardServiceImpl --> BoardDao
    BoardDao <|.. BoardDaoImpl
    BoardDaoImpl ..> Board
```

---

## 도메인별 클래스 목록

| 도메인 | Controller | Service | DAO | DTO |
|--------|-----------|---------|-----|-----|
| 회원 | `MemberController` | `MemberService` | `MemberDao` | `Member` |
| 관광지 | `AttractionController` | `AttractionService` | `AttractionDao` | `AttractionInfo`, `Sido`, `Gugun` |
| 여행계획 | `PlanController` | `TravelPlanService` | `TravelPlanDao` | `TravelPlan`, `PlanDetail` |
| 핫플레이스 | `HotplaceController` | `HotplaceService` | `HotplaceDao` | `Hotplace` |
| 게시판 | `BoardController` | `BoardService` | `BoardDao` | `Board` |
| 공통 유틸 | — | — | `DBUtil` | — |
