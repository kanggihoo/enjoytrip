-- EnjoyTrip DB 스키마 (F101~F108)

CREATE DATABASE IF NOT EXISTS enjoytrip DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SET NAMES utf8mb4;
USE enjoytrip;

-- F107: 회원 테이블
CREATE TABLE IF NOT EXISTS member (
    user_id    VARCHAR(50)  NOT NULL PRIMARY KEY,
    user_pw    VARCHAR(200) NOT NULL,
    user_name  VARCHAR(50)  NOT NULL,
    email      VARCHAR(100),
    join_date  DATE         DEFAULT (CURRENT_DATE)
);

-- F101~103: 시도 (광역시/도)
CREATE TABLE IF NOT EXISTS sido (
    sido_code  INT          NOT NULL PRIMARY KEY,
    sido_name  VARCHAR(30)  NOT NULL
);

-- F101~103: 구군
CREATE TABLE IF NOT EXISTS gugun (
    gugun_code INT          NOT NULL,
    sido_code  INT          NOT NULL,
    gugun_name VARCHAR(30)  NOT NULL,
    PRIMARY KEY (gugun_code, sido_code)
);

-- F101~103: 관광지 정보
CREATE TABLE IF NOT EXISTS attraction_info (
    content_id       INT          NOT NULL PRIMARY KEY,
    content_type_id  INT,
    title            VARCHAR(500),
    sido_code        INT,
    gugun_code       INT,
    addr1            VARCHAR(500),
    addr2            VARCHAR(500),
    zipcode          VARCHAR(10),
    tel              VARCHAR(50),
    first_image      VARCHAR(500),
    first_image2     VARCHAR(500),
    readcount        INT          DEFAULT 0,
    mapx             DOUBLE,
    mapy             DOUBLE,
    mlevel           INT,
    cat1             VARCHAR(10),
    cat2             VARCHAR(10),
    cat3             VARCHAR(20),
    created_time     VARCHAR(20),
    modified_time    VARCHAR(20),
    overview         TEXT
);

-- F104: 여행 계획 테이블
CREATE TABLE IF NOT EXISTS travel_plan (
    plan_id       INT AUTO_INCREMENT PRIMARY KEY,
    user_id       VARCHAR(50)  NOT NULL,
    title         VARCHAR(200) NOT NULL,
    start_date    DATE,
    end_date      DATE,
    total_budget  INT          DEFAULT 0,
    memo          TEXT,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES member(user_id) ON DELETE CASCADE
);

-- F104: 여행 계획 상세
CREATE TABLE IF NOT EXISTS plan_detail (
    detail_id    INT AUTO_INCREMENT PRIMARY KEY,
    plan_id      INT          NOT NULL,
    content_id   INT,
    title        VARCHAR(200),
    latitude     DOUBLE,
    longitude    DOUBLE,
    visit_order  INT          NOT NULL DEFAULT 0,
    visit_date   DATE,
    memo         TEXT,
    FOREIGN KEY (plan_id) REFERENCES travel_plan(plan_id) ON DELETE CASCADE
);

-- F105: Hotplace 테이블
CREATE TABLE IF NOT EXISTS hotplace (
    hotplace_id  INT AUTO_INCREMENT PRIMARY KEY,
    user_id      VARCHAR(50)  NOT NULL,
    title        VARCHAR(200) NOT NULL,
    visit_date   DATE,
    place_type   VARCHAR(50),
    description  TEXT,
    latitude     DOUBLE,
    longitude    DOUBLE,
    image_path   VARCHAR(500),
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES member(user_id) ON DELETE CASCADE
);



-- F106: 게시판 (공지사항 / 자유게시판)
CREATE TABLE IF NOT EXISTS board (
    board_id    INT AUTO_INCREMENT PRIMARY KEY,
    type        VARCHAR(20)  NOT NULL COMMENT 'notice|free',
    title       VARCHAR(300) NOT NULL,
    content     TEXT         NOT NULL,
    user_id     VARCHAR(50)  NOT NULL,
    views       INT          DEFAULT 0,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES member(user_id) ON DELETE CASCADE
);
