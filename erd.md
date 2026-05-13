# EnjoyTrip ERD

`sql/schema.sql` 파일을 바탕으로 작성된 데이터베이스 스키마 구조입니다.

```mermaid
erDiagram
    member ||--o{ travel_plan : "writes"
    member ||--o{ hotplace : "registers"
    member ||--o{ board : "writes"
    travel_plan ||--o{ plan_detail : "contains"
    sido ||--o{ gugun : "contains"
    sido ||--o{ attraction_info : "categorizes"
    gugun ||--o{ attraction_info : "categorizes"
    attraction_info ||--o{ plan_detail : "referenced_by"

    member {
        string user_id PK
        string user_pw
        string user_name
        string email
        date join_date
    }

    sido {
        int sido_code PK
        string sido_name
    }

    gugun {
        int gugun_code PK
        int sido_code PK
        string gugun_name
    }

    attraction_info {
        int content_id PK
        int content_type_id
        string title
        int sido_code FK
        int gugun_code FK
        string addr1
        string addr2
        string zipcode
        string tel
        string first_image
        string first_image2
        int readcount
        double mapx
        double mapy
        int mlevel
        string cat1
        string cat2
        string cat3
        string created_time
        string modified_time
        text overview
    }

    travel_plan {
        int plan_id PK
        string user_id FK
        string title
        date start_date
        date end_date
        int total_budget
        text memo
        timestamp created_at
        timestamp updated_at
    }

    plan_detail {
        int detail_id PK
        int plan_id FK
        int content_id FK
        string title
        double latitude
        double longitude
        int visit_order
        date visit_date
        text memo
    }

    hotplace {
        int hotplace_id PK
        string user_id FK
        string title
        date visit_date
        string place_type
        text description
        double latitude
        double longitude
        string image_path
        timestamp created_at
    }

    board {
        int board_id PK
        string type "notice|free"
        string title
        text content
        string user_id FK
        int views
        timestamp created_at
        timestamp updated_at
    }
```

## 테이블 상세 설명

1.  **member (회원)**: 사용자 계정 정보를 관리합니다.
2.  **sido (시도)**: 지역 코드(광역시/도)를 관리합니다.
3.  **gugun (구군)**: 시도에 속한 하위 행정구역 코드를 관리합니다.
4.  **attraction_info (관광지 정보)**: 공공데이터 API로부터 가져온 관광지 상세 정보를 저장합니다.
5.  **travel_plan (여행 계획)**: 사용자가 생성한 여행 계획의 마스터 정보입니다.
6.  **plan_detail (여행 계획 상세)**: 여행 계획에 포함된 개별 방문지 정보를 저장합니다.
7.  **hotplace (핫플레이스)**: 사용자가 직접 등록한 추천 장소 정보입니다.
8.  **board (게시판)**: 공지사항 및 자유게시판 게시글을 관리합니다.
