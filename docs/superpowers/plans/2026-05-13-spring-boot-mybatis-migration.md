# Spring Boot MyBatis Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Convert the current Jakarta Servlet/JSP EnjoyTrip app into a Spring Boot executable WAR using JSP, MyBatis, session authentication, REST endpoints for dynamic JSON features, structured logging, global exception handling, and required tests.

**Architecture:** Rebuild the project into standard Maven/Spring Boot layout while preserving the existing DB schema and JSP screens. Split page-rendering controllers from JSON API controllers, replace JDBC DAO implementations with MyBatis mappers, keep `HttpSession` authentication, and centralize request logging and exception responses through Spring MVC interceptors/advice.

**Tech Stack:** Java 17, Spring Boot 3, Spring MVC, JSP/JSTL, MyBatis, MySQL 8, Docker Compose, Testcontainers, JUnit 5, Mockito, Lombok, SLF4J/Logback, Maven.

---

## Scope Note

This is a large migration. Execute tasks in order and commit after each task. Do not start domain migrations until the Boot skeleton, exception model, interceptor model, and test infrastructure are passing.

## File Structure Map

- `pom.xml`: convert to Spring Boot executable WAR with MyBatis, JSP, Lombok, testing, and Testcontainers dependencies.
- `src/main/java/com/enjoytrip/EnjoyTripApplication.java`: Boot entry point.
- `src/main/resources/application.yml`: datasource, JSP resolver, MyBatis, multipart, API key, and logging config.
- `src/main/resources/mappers/*.xml`: MyBatis SQL mapped from existing DAO implementations.
- `src/main/webapp/WEB-INF/views/**`: existing JSPs moved from `WebContent/WEB-INF/views/**`.
- `src/main/webapp/css/style.css`: existing CSS moved from `WebContent/css/style.css`.
- `src/main/java/com/enjoytrip/common/**`: exception and response primitives shared by page/API flows.
- `src/main/java/com/enjoytrip/config/**`: MVC config, auth interceptor, request logging interceptor, `.env` initializer.
- `src/main/java/com/enjoytrip/{member,board,plan,hotplace,attraction}/**`: migrated domain DTOs, controllers, services, mappers, and client classes.
- `src/test/java/com/enjoytrip/**`: unit, MVC slice, MyBatis slice, and selected integration tests.

## Task 1: Bootstrap Spring Boot Build and Standard Layout

**Files:**
- Modify: `pom.xml`
- Create: `src/main/java/com/enjoytrip/EnjoyTripApplication.java`
- Create: `src/main/resources/application.yml`
- Create: `src/main/resources/application-test.yml`
- Create: `src/main/resources/.keep`
- Create: `.env.example`
- Test: `src/test/java/com/enjoytrip/EnjoyTripApplicationTest.java`

- [ ] **Step 1: Write the failing context smoke test**

Create `src/test/java/com/enjoytrip/EnjoyTripApplicationTest.java`:

```java
package com.enjoytrip;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class EnjoyTripApplicationTest {

    @Test
    void contextLoads() {
    }
}
```

- [ ] **Step 2: Run the smoke test and verify it fails before Boot exists**

Run:

```bash
mvn -Dtest=EnjoyTripApplicationTest test
```

Expected: FAIL because `@SpringBootTest` cannot find a Spring Boot configuration class.

- [ ] **Step 3: Replace `pom.xml` with Spring Boot executable WAR configuration**

Use this `pom.xml` content:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>

    <groupId>com.enjoytrip</groupId>
    <artifactId>enjoytrip</artifactId>
    <version>1.0.0</version>
    <packaging>war</packaging>

    <properties>
        <java.version>17</java.version>
        <mybatis.spring.boot.version>3.0.3</mybatis.spring.boot.version>
        <testcontainers.version>1.20.3</testcontainers.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>${mybatis.spring.boot.version}</version>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.tomcat.embed</groupId>
            <artifactId>tomcat-embed-jasper</artifactId>
        </dependency>
        <dependency>
            <groupId>jakarta.servlet.jsp.jstl</groupId>
            <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
        </dependency>
        <dependency>
            <groupId>org.glassfish.web</groupId>
            <artifactId>jakarta.servlet.jsp.jstl</artifactId>
        </dependency>
        <dependency>
            <groupId>io.github.cdimascio</groupId>
            <artifactId>dotenv-java</artifactId>
            <version>3.0.0</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter-test</artifactId>
            <version>${mybatis.spring.boot.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>mysql</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>mockwebserver</artifactId>
            <version>4.12.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.testcontainers</groupId>
                <artifactId>testcontainers-bom</artifactId>
                <version>${testcontainers.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 4: Create Boot entry point**

Create `src/main/java/com/enjoytrip/EnjoyTripApplication.java`:

```java
package com.enjoytrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class EnjoyTripApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(EnjoyTripApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(EnjoyTripApplication.class);
    }
}
```

- [ ] **Step 5: Add runtime and test configuration**

Create `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: enjoytrip
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3305/enjoytrip?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8}
    username: ${DB_USERNAME:ssafy}
    password: ${DB_PASSWORD:ssafy}
    driver-class-name: com.mysql.cj.jdbc.Driver
  mvc:
    view:
      prefix: /WEB-INF/views/
      suffix: .jsp
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 15MB

mybatis:
  mapper-locations: classpath:mappers/**/*.xml
  type-aliases-package: com.enjoytrip
  configuration:
    map-underscore-to-camel-case: true

tour:
  api:
    base-url: https://apis.data.go.kr/B551011/KorService2
    service-key: ${TOUR_API_KEY:}
    mobile-os: ETC
    mobile-app: EnjoyTrip

kakao:
  javascript-key: ${KAKAO_JAVASCRIPT_KEY:}

app:
  upload-dir: uploads

logging:
  level:
    com.enjoytrip: INFO
```

Create `src/main/resources/application-test.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3305/enjoytrip?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8
    username: ssafy
    password: ssafy
    driver-class-name: com.mysql.cj.jdbc.Driver

tour:
  api:
    base-url: http://localhost:0
    service-key: test-key
    mobile-os: ETC
    mobile-app: EnjoyTripTest

kakao:
  javascript-key: test-kakao-key
```

Create `.env.example`:

```text
TOUR_API_KEY=replace-with-tour-api-key
KAKAO_JAVASCRIPT_KEY=replace-with-kakao-javascript-key
DB_URL=jdbc:mysql://localhost:3305/enjoytrip?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8
DB_USERNAME=ssafy
DB_PASSWORD=ssafy
```

- [ ] **Step 6: Run the smoke test**

Run:

```bash
mvn -Dtest=EnjoyTripApplicationTest test
```

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add pom.xml src/main/java/com/enjoytrip/EnjoyTripApplication.java src/main/resources/application.yml src/main/resources/application-test.yml .env.example src/test/java/com/enjoytrip/EnjoyTripApplicationTest.java
git commit -m "chore: bootstrap spring boot project"
```

## Task 2: Move Existing Sources, JSPs, and Static Assets

**Files:**
- Move: `src/com/enjoytrip/**` -> `src/main/java/com/enjoytrip/**`
- Move: `WebContent/WEB-INF/views/**` -> `src/main/webapp/WEB-INF/views/**`
- Move: `WebContent/css/style.css` -> `src/main/webapp/css/style.css`
- Move: `WebContent/index.jsp` -> `src/main/webapp/WEB-INF/views/index.jsp`
- Preserve for reference until domain replacement completes: existing controller/service/dao classes under `src/main/java/com/enjoytrip/**`

- [ ] **Step 1: Move Java packages into Maven layout**

Run:

```bash
mkdir -p src/main/java
git mv src/com src/main/java/
```

Expected: `src/main/java/com/enjoytrip/...` exists.

- [ ] **Step 2: Move JSP and CSS assets into Boot WAR layout**

Run:

```bash
mkdir -p src/main/webapp/WEB-INF src/main/webapp/css
git mv WebContent/WEB-INF/views src/main/webapp/WEB-INF/
git mv WebContent/css/style.css src/main/webapp/css/style.css
git mv WebContent/index.jsp src/main/webapp/WEB-INF/views/index.jsp
```

Expected: JSPs are under `src/main/webapp/WEB-INF/views`.

- [ ] **Step 3: Remove obsolete deployment descriptor**

Run:

```bash
git rm WebContent/WEB-INF/web.xml
```

Expected: `web.xml` is removed because Boot MVC config replaces it.

- [ ] **Step 4: Add index page controller**

Create `src/main/java/com/enjoytrip/common/controller/HomeController.java`:

```java
package com.enjoytrip.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
```

- [ ] **Step 5: Write MVC test for index view**

Create `src/test/java/com/enjoytrip/common/controller/HomeControllerTest.java`:

```java
package com.enjoytrip.common.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void indexReturnsIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}
```

- [ ] **Step 6: Run tests**

Run:

```bash
mvn -Dtest=HomeControllerTest test
```

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add src/main/java src/main/webapp
git rm -r WebContent src/com || true
git commit -m "chore: move project to spring boot layout"
```

## Task 3: Add Common Exception and API Error Model

**Files:**
- Create: `src/main/java/com/enjoytrip/common/exception/ErrorCode.java`
- Create: `src/main/java/com/enjoytrip/common/exception/BusinessException.java`
- Create: `src/main/java/com/enjoytrip/common/exception/BadRequestException.java`
- Create: `src/main/java/com/enjoytrip/common/exception/UnauthorizedException.java`
- Create: `src/main/java/com/enjoytrip/common/exception/ForbiddenException.java`
- Create: `src/main/java/com/enjoytrip/common/exception/NotFoundException.java`
- Create: `src/main/java/com/enjoytrip/common/exception/ExternalApiException.java`
- Create: `src/main/java/com/enjoytrip/common/exception/FileStorageException.java`
- Create: `src/main/java/com/enjoytrip/common/response/ApiErrorResponse.java`
- Create: `src/main/java/com/enjoytrip/common/exception/GlobalApiExceptionHandler.java`
- Create: `src/main/java/com/enjoytrip/common/exception/GlobalPageExceptionHandler.java`
- Test: `src/test/java/com/enjoytrip/common/exception/GlobalApiExceptionHandlerTest.java`

- [ ] **Step 1: Write failing API exception handler test**

Create `src/test/java/com/enjoytrip/common/exception/GlobalApiExceptionHandlerTest.java`:

```java
package com.enjoytrip.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalApiExceptionHandlerTest.TestController.class)
@Import(GlobalApiExceptionHandler.class)
class GlobalApiExceptionHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void apiExceptionReturnsJsonBody() throws Exception {
        mockMvc.perform(get("/api/test/bad-request").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("요청 파라미터가 올바르지 않습니다."))
                .andExpect(jsonPath("$.path").value("/api/test/bad-request"));
    }

    @Controller
    static class TestController {
        @GetMapping("/api/test/bad-request")
        @ResponseBody
        String badRequest() {
            throw new BadRequestException(ErrorCode.BAD_REQUEST);
        }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
mvn -Dtest=GlobalApiExceptionHandlerTest test
```

Expected: FAIL because exception and handler classes do not exist.

- [ ] **Step 3: Add `ErrorCode` and exception classes**

Create `src/main/java/com/enjoytrip/common/exception/ErrorCode.java`:

```java
package com.enjoytrip.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "요청 파라미터가 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "외부 API 호출 중 오류가 발생했습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 처리 중 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
```

Create `src/main/java/com/enjoytrip/common/exception/BusinessException.java`:

```java
package com.enjoytrip.common.exception;

public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
```

Create these subclasses:

```java
package com.enjoytrip.common.exception;

public class BadRequestException extends BusinessException {
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
```

Use the same pattern for `UnauthorizedException`, `ForbiddenException`, `NotFoundException`, `ExternalApiException`, and `FileStorageException`. For `ExternalApiException` and `FileStorageException`, also include a constructor accepting `(ErrorCode errorCode, Throwable cause)`.

- [ ] **Step 4: Add API error response and handlers**

Create `src/main/java/com/enjoytrip/common/response/ApiErrorResponse.java`:

```java
package com.enjoytrip.common.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiErrorResponse {
    private final String code;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp;
}
```

Create `src/main/java/com/enjoytrip/common/exception/GlobalApiExceptionHandler.java`:

```java
package com.enjoytrip.common.exception;

import java.time.LocalDateTime;

import com.enjoytrip.common.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice(basePackages = "com.enjoytrip")
public class GlobalApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("Business exception. code={}, path={}, message={}", errorCode.name(), request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(toBody(errorCode, request));
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception e, HttpServletRequest request) {
        log.warn("Bad request. path={}, message={}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.badRequest().body(toBody(ErrorCode.BAD_REQUEST, request));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleDataAccess(DataAccessException e, HttpServletRequest request) {
        log.error("Database exception. path={}", request.getRequestURI(), e);
        return ResponseEntity.status(ErrorCode.DATABASE_ERROR.getStatus()).body(toBody(ErrorCode.DATABASE_ERROR, request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception. path={}", request.getRequestURI(), e);
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(toBody(ErrorCode.INTERNAL_SERVER_ERROR, request));
    }

    private ApiErrorResponse toBody(ErrorCode errorCode, HttpServletRequest request) {
        return ApiErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
```

Create `src/main/java/com/enjoytrip/common/exception/GlobalPageExceptionHandler.java`:

```java
package com.enjoytrip.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(basePackages = "com.enjoytrip")
public class GlobalPageExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorized(UnauthorizedException e, HttpServletRequest request) {
        log.warn("Unauthorized page request. path={}", request.getRequestURI());
        return "redirect:/user/login";
    }

    @ExceptionHandler(ForbiddenException.class)
    public String handleForbidden(ForbiddenException e, Model model, HttpServletRequest request) {
        log.warn("Forbidden page request. path={}", request.getRequestURI());
        model.addAttribute("errorCode", ErrorCode.FORBIDDEN.name());
        model.addAttribute("message", ErrorCode.FORBIDDEN.getMessage());
        return "error/403";
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException e, Model model, HttpServletRequest request) {
        log.warn("Not found page request. path={}", request.getRequestURI());
        model.addAttribute("errorCode", ErrorCode.NOT_FOUND.name());
        model.addAttribute("message", ErrorCode.NOT_FOUND.getMessage());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model, HttpServletRequest request) {
        log.error("Unhandled page exception. path={}", request.getRequestURI(), e);
        model.addAttribute("errorCode", ErrorCode.INTERNAL_SERVER_ERROR.name());
        model.addAttribute("message", ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        return "error/500";
    }
}
```

- [ ] **Step 5: Add error JSPs**

Create `src/main/webapp/WEB-INF/views/error/403.jsp`, `404.jsp`, and `500.jsp` with this pattern, changing the title text to match each status:

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>오류</title>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main style="max-width:800px;margin:60px auto;padding:24px;">
    <h1>${errorCode}</h1>
    <p>${message}</p>
    <a href="${pageContext.request.contextPath}/">홈으로</a>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body>
</html>
```

- [ ] **Step 6: Run exception tests**

Run:

```bash
mvn -Dtest=GlobalApiExceptionHandlerTest test
```

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/enjoytrip/common src/main/webapp/WEB-INF/views/error src/test/java/com/enjoytrip/common
git commit -m "feat: add common exception handling"
```

## Task 4: Add Request Logging, Auth Interceptor, and MVC Config

**Files:**
- Create: `src/main/java/com/enjoytrip/config/RequestLoggingInterceptor.java`
- Create: `src/main/java/com/enjoytrip/config/AuthInterceptor.java`
- Create: `src/main/java/com/enjoytrip/config/WebMvcConfig.java`
- Create: `src/main/java/com/enjoytrip/config/DotenvApplicationContextInitializer.java`
- Test: `src/test/java/com/enjoytrip/config/AuthInterceptorTest.java`
- Test: `src/test/java/com/enjoytrip/config/RequestLoggingInterceptorTest.java`

- [ ] **Step 1: Write auth interceptor failing test**

Create `src/test/java/com/enjoytrip/config/AuthInterceptorTest.java`:

```java
package com.enjoytrip.config;

import com.enjoytrip.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.HandlerMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthInterceptorTest {

    private final AuthInterceptor interceptor = new AuthInterceptor();

    @Test
    void allowsWhenSessionHasLoginUser() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loginUser")).thenReturn("ssafy");

        boolean result = interceptor.preHandle(request, response, mock(HandlerMethod.class));

        assertThat(result).isTrue();
    }

    @Test
    void throwsUnauthorizedWhenSessionMissing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getSession(false)).thenReturn(null);

        assertThatThrownBy(() -> interceptor.preHandle(request, response, mock(HandlerMethod.class)))
                .isInstanceOf(UnauthorizedException.class);
    }
}
```

- [ ] **Step 2: Implement `AuthInterceptor`**

Create `src/main/java/com/enjoytrip/config/AuthInterceptor.java`:

```java
package com.enjoytrip.config;

import com.enjoytrip.common.exception.ErrorCode;
import com.enjoytrip.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }
        return true;
    }
}
```

- [ ] **Step 3: Implement request logging interceptor**

Create `src/main/java/com/enjoytrip/config/RequestLoggingInterceptor.java`:

```java
package com.enjoytrip.config;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = RequestLoggingInterceptor.class.getName() + ".START_TIME";
    private static final Set<String> SENSITIVE_KEYS = Set.of("userpw", "password", "servicekey", "apikey", "token", "jsessionid");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        log.info("[REQ] {} {} handler={} user={} params={}",
                request.getMethod(),
                request.getRequestURI(),
                handlerName(handler),
                currentUser(request),
                maskedParams(request));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Object start = request.getAttribute(START_TIME);
        long elapsed = start instanceof Long value ? System.currentTimeMillis() - value : -1L;
        log.info("[RES] {} {} status={} elapsedMs={}", request.getMethod(), request.getRequestURI(), response.getStatus(), elapsed);
    }

    private String handlerName(Object handler) {
        if (handler instanceof HandlerMethod method) {
            return method.getBeanType().getSimpleName() + "." + method.getMethod().getName();
        }
        return handler.getClass().getSimpleName();
    }

    private String currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            return "anonymous";
        }
        return String.valueOf(session.getAttribute("loginUser"));
    }

    private Map<String, String> maskedParams(HttpServletRequest request) {
        Map<String, String> result = new LinkedHashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            String normalized = key.toLowerCase(Locale.ROOT);
            String value = SENSITIVE_KEYS.contains(normalized)
                    ? "***"
                    : Arrays.stream(values).collect(Collectors.joining(","));
            result.put(key, value);
        });
        return result;
    }
}
```

- [ ] **Step 4: Add MVC config with exact protected URL patterns**

Create `src/main/java/com/enjoytrip/config/WebMvcConfig.java`:

```java
package com.enjoytrip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final RequestLoggingInterceptor requestLoggingInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor, RequestLoggingInterceptor requestLoggingInterceptor) {
        this.authInterceptor = authInterceptor;
        this.requestLoggingInterceptor = requestLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/favicon.ico", "/error/**");

        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                        "/plans/**",
                        "/api/plans/**",
                        "/boards/new", "/boards/*/edit", "/boards/*/delete", "/boards",
                        "/hotplaces/new", "/hotplaces/*/edit", "/hotplaces/*/delete", "/hotplaces",
                        "/members/**",
                        "/user/logout", "/user/mypage", "/user/modify", "/user/delete"
                )
                .excludePathPatterns(
                        "/",
                        "/attractions/**",
                        "/api/attractions/**",
                        "/user/login", "/user/join",
                        "/css/**", "/favicon.ico", "/error/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:uploads/");
    }
}
```

- [ ] **Step 5: Add `.env` initializer**

Create `src/main/java/com/enjoytrip/config/DotenvApplicationContextInitializer.java`:

```java
package com.enjoytrip.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class DotenvApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }
}
```

Modify `src/main/java/com/enjoytrip/EnjoyTripApplication.java`:

```java
public static void main(String[] args) {
    SpringApplication application = new SpringApplication(EnjoyTripApplication.class);
    application.addInitializers(new DotenvApplicationContextInitializer());
    application.run(args);
}
```

- [ ] **Step 6: Run interceptor tests**

Run:

```bash
mvn -Dtest=AuthInterceptorTest test
```

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/enjoytrip/config src/main/java/com/enjoytrip/EnjoyTripApplication.java src/test/java/com/enjoytrip/config
git commit -m "feat: add mvc interceptors"
```

## Task 5: Add Testcontainers MySQL Test Infrastructure

**Files:**
- Create: `src/test/java/com/enjoytrip/support/AbstractMySqlContainerTest.java`
- Create: `src/test/java/com/enjoytrip/support/MySqlTestContainer.java`
- Test: `src/test/java/com/enjoytrip/support/MySqlContainerSmokeTest.java`

- [ ] **Step 1: Create shared Testcontainers base**

Create `src/test/java/com/enjoytrip/support/MySqlTestContainer.java`:

```java
package com.enjoytrip.support;

import org.testcontainers.containers.MySQLContainer;

public final class MySqlTestContainer {

    public static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8")
            .withDatabaseName("enjoytrip")
            .withUsername("ssafy")
            .withPassword("ssafy")
            .withInitScript("schema.sql");

    private MySqlTestContainer() {
    }
}
```

Copy `sql/schema.sql` into `src/test/resources/schema.sql`.

- [ ] **Step 2: Create dynamic property base class**

Create `src/test/java/com/enjoytrip/support/AbstractMySqlContainerTest.java`:

```java
package com.enjoytrip.support;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class AbstractMySqlContainerTest {

    @BeforeAll
    static void startContainer() {
        MySqlTestContainer.MYSQL.start();
    }

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MySqlTestContainer.MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MySqlTestContainer.MYSQL::getUsername);
        registry.add("spring.datasource.password", MySqlTestContainer.MYSQL::getPassword);
    }
}
```

- [ ] **Step 3: Write container smoke test**

Create `src/test/java/com/enjoytrip/support/MySqlContainerSmokeTest.java`:

```java
package com.enjoytrip.support;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MySqlContainerSmokeTest extends AbstractMySqlContainerTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void schemaIsLoaded() {
        Integer count = jdbcTemplate.queryForObject("select count(*) from member", Integer.class);

        assertThat(count).isNotNull();
        assertThat(count).isGreaterThanOrEqualTo(1);
    }
}
```

- [ ] **Step 4: Run container smoke test**

Run:

```bash
mvn -Dtest=MySqlContainerSmokeTest test
```

Expected: PASS when Docker is running. If Docker is unavailable, record the failure and continue with non-container tests until Docker is available.

- [ ] **Step 5: Commit**

```bash
git add src/test/java/com/enjoytrip/support src/test/resources/schema.sql
git commit -m "test: add mysql testcontainer support"
```

## Task 6: Migrate Member Domain to Spring MVC and MyBatis

**Files:**
- Modify: `src/main/java/com/enjoytrip/member/dto/Member.java`
- Create: `src/main/java/com/enjoytrip/member/mapper/MemberMapper.java`
- Create: `src/main/resources/mappers/MemberMapper.xml`
- Modify: `src/main/java/com/enjoytrip/member/service/MemberService.java`
- Modify: `src/main/java/com/enjoytrip/member/service/MemberServiceImpl.java`
- Replace: `src/main/java/com/enjoytrip/member/controller/MemberController.java`
- Test: `src/test/java/com/enjoytrip/member/service/MemberServiceImplTest.java`
- Test: `src/test/java/com/enjoytrip/member/mapper/MemberMapperTest.java`
- Test: `src/test/java/com/enjoytrip/member/controller/MemberControllerTest.java`

- [ ] **Step 1: Update `Member` DTO with Lombok**

Replace `src/main/java/com/enjoytrip/member/dto/Member.java` with:

```java
package com.enjoytrip.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    private String userId;
    private String userPw;
    private String userName;
    private String email;
    private String joinDate;
}
```

- [ ] **Step 2: Add mapper and XML**

Create `src/main/java/com/enjoytrip/member/mapper/MemberMapper.java`:

```java
package com.enjoytrip.member.mapper;

import com.enjoytrip.member.dto.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    void insertMember(Member member);
    Member selectMemberById(String userId);
    Member selectMemberByIdAndPw(String userId, String userPw);
    void updateMember(Member member);
    void deleteMember(String userId);
    boolean existsUserId(String userId);
}
```

Create `src/main/resources/mappers/MemberMapper.xml`:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "https://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.enjoytrip.member.mapper.MemberMapper">
    <insert id="insertMember" parameterType="com.enjoytrip.member.dto.Member">
        INSERT INTO member (user_id, user_pw, user_name, email)
        VALUES (#{userId}, #{userPw}, #{userName}, #{email})
    </insert>

    <select id="selectMemberById" resultType="com.enjoytrip.member.dto.Member">
        SELECT user_id, user_pw, user_name, email, join_date
        FROM member
        WHERE user_id = #{userId}
    </select>

    <select id="selectMemberByIdAndPw" resultType="com.enjoytrip.member.dto.Member">
        SELECT user_id, user_pw, user_name, email, join_date
        FROM member
        WHERE user_id = #{userId}
          AND user_pw = #{userPw}
    </select>

    <update id="updateMember" parameterType="com.enjoytrip.member.dto.Member">
        UPDATE member
        SET user_pw = #{userPw},
            user_name = #{userName},
            email = #{email}
        WHERE user_id = #{userId}
    </update>

    <delete id="deleteMember">
        DELETE FROM member WHERE user_id = #{userId}
    </delete>

    <select id="existsUserId" resultType="boolean">
        SELECT EXISTS(SELECT 1 FROM member WHERE user_id = #{userId})
    </select>
</mapper>
```

- [ ] **Step 3: Write service tests**

Create `src/test/java/com/enjoytrip/member/service/MemberServiceImplTest.java`:

```java
package com.enjoytrip.member.service;

import com.enjoytrip.common.exception.BusinessException;
import com.enjoytrip.common.exception.ErrorCode;
import com.enjoytrip.member.dto.Member;
import com.enjoytrip.member.mapper.MemberMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemberServiceImplTest {

    private final MemberMapper mapper = mock(MemberMapper.class);
    private final MemberService service = new MemberServiceImpl(mapper);

    @Test
    void joinThrowsDuplicateUserId() {
        Member member = Member.builder().userId("ssafy").userPw("pw").userName("name").build();
        when(mapper.existsUserId("ssafy")).thenReturn(true);

        assertThatThrownBy(() -> service.join(member))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.DUPLICATE_USER_ID);
    }

    @Test
    void loginReturnsMemberWhenCredentialsMatch() {
        Member member = Member.builder().userId("ssafy").userName("관리자").build();
        when(mapper.selectMemberByIdAndPw("ssafy", "ssafy")).thenReturn(member);

        Member result = service.login("ssafy", "ssafy");

        assertThat(result.getUserId()).isEqualTo("ssafy");
    }

    @Test
    void removeMemberDeletesById() {
        service.removeMember("ssafy");

        verify(mapper).deleteMember("ssafy");
    }
}
```

- [ ] **Step 4: Implement service with constructor injection**

Replace `src/main/java/com/enjoytrip/member/service/MemberService.java`:

```java
package com.enjoytrip.member.service;

import com.enjoytrip.member.dto.Member;

public interface MemberService {
    void join(Member member);
    Member login(String userId, String userPw);
    Member getMemberById(String userId);
    void modifyMember(Member member);
    void removeMember(String userId);
}
```

Replace `src/main/java/com/enjoytrip/member/service/MemberServiceImpl.java`:

```java
package com.enjoytrip.member.service;

import com.enjoytrip.common.exception.BusinessException;
import com.enjoytrip.common.exception.ErrorCode;
import com.enjoytrip.member.dto.Member;
import com.enjoytrip.member.mapper.MemberMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

    public MemberServiceImpl(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    @Transactional
    public void join(Member member) {
        if (memberMapper.existsUserId(member.getUserId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_USER_ID);
        }
        memberMapper.insertMember(member);
    }

    @Override
    public Member login(String userId, String userPw) {
        return memberMapper.selectMemberByIdAndPw(userId, userPw);
    }

    @Override
    public Member getMemberById(String userId) {
        return memberMapper.selectMemberById(userId);
    }

    @Override
    @Transactional
    public void modifyMember(Member member) {
        memberMapper.updateMember(member);
    }

    @Override
    @Transactional
    public void removeMember(String userId) {
        memberMapper.deleteMember(userId);
    }
}
```

- [ ] **Step 5: Replace servlet controller with Spring MVC controller**

Replace `src/main/java/com/enjoytrip/member/controller/MemberController.java`:

```java
package com.enjoytrip.member.controller;

import com.enjoytrip.common.exception.BusinessException;
import com.enjoytrip.member.dto.Member;
import com.enjoytrip.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    @PostMapping("/login")
    public String login(String userId, String userPw, HttpSession session, Model model) {
        Member member = memberService.login(userId, userPw);
        if (member == null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "member/login";
        }
        session.setAttribute("loginUser", member.getUserId());
        session.setAttribute("loginUserName", member.getUserName());
        return "redirect:/";
    }

    @GetMapping("/join")
    public String joinForm() {
        return "member/join";
    }

    @PostMapping("/join")
    public String join(Member member, Model model) {
        try {
            memberService.join(member);
            return "redirect:/user/login";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "member/join";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("loginUser");
        model.addAttribute("member", memberService.getMemberById(userId));
        return "member/mypage";
    }

    @GetMapping("/modify")
    public String modifyForm(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("loginUser");
        model.addAttribute("member", memberService.getMemberById(userId));
        return "member/modify";
    }

    @PostMapping("/modify")
    public String modify(Member member, HttpSession session) {
        String userId = (String) session.getAttribute("loginUser");
        member.setUserId(userId);
        memberService.modifyMember(member);
        session.setAttribute("loginUserName", member.getUserName());
        return "redirect:/user/mypage";
    }

    @PostMapping("/delete")
    public String delete(HttpSession session) {
        String userId = (String) session.getAttribute("loginUser");
        memberService.removeMember(userId);
        session.invalidate();
        return "redirect:/";
    }
}
```

- [ ] **Step 6: Run member tests**

Run:

```bash
mvn -Dtest=MemberServiceImplTest test
```

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/enjoytrip/member src/main/resources/mappers/MemberMapper.xml src/test/java/com/enjoytrip/member
git commit -m "feat: migrate member to spring mybatis"
```

## Task 7: Migrate Board Domain

**Files:**
- Modify: `src/main/java/com/enjoytrip/board/dto/Board.java`
- Create: `src/main/java/com/enjoytrip/board/mapper/BoardMapper.java`
- Create: `src/main/resources/mappers/BoardMapper.xml`
- Modify: `src/main/java/com/enjoytrip/board/service/BoardService.java`
- Modify: `src/main/java/com/enjoytrip/board/service/BoardServiceImpl.java`
- Replace: `src/main/java/com/enjoytrip/board/controller/BoardController.java`
- Test: `src/test/java/com/enjoytrip/board/service/BoardServiceImplTest.java`
- Test: `src/test/java/com/enjoytrip/board/controller/BoardControllerTest.java`

- [ ] **Step 1: Update DTO and mapper**

Replace `src/main/java/com/enjoytrip/board/dto/Board.java` with Lombok fields matching the existing class:

```java
package com.enjoytrip.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
    private int boardId;
    private String type;
    private String title;
    private String content;
    private String userId;
    private int views;
    private String createdAt;
    private String updatedAt;
}
```

Create `src/main/java/com/enjoytrip/board/mapper/BoardMapper.java`:

```java
package com.enjoytrip.board.mapper;

import java.util.List;

import com.enjoytrip.board.dto.Board;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {
    List<Board> selectBoardsByType(String type);
    Board selectBoardById(int boardId);
    void increaseViews(int boardId);
    int insertBoard(Board board);
    void updateBoard(Board board);
    void deleteBoard(int boardId);
}
```

Create `src/main/resources/mappers/BoardMapper.xml` using SQL from `BoardDaoImpl`; the `insertBoard` statement must use generated keys:

```xml
<insert id="insertBoard" parameterType="com.enjoytrip.board.dto.Board" useGeneratedKeys="true" keyProperty="boardId">
    INSERT INTO board (type, title, content, user_id)
    VALUES (#{type}, #{title}, #{content}, #{userId})
</insert>
```

- [ ] **Step 2: Write service ownership tests**

Create `src/test/java/com/enjoytrip/board/service/BoardServiceImplTest.java`:

```java
package com.enjoytrip.board.service;

import com.enjoytrip.board.dto.Board;
import com.enjoytrip.board.mapper.BoardMapper;
import com.enjoytrip.common.exception.ForbiddenException;
import com.enjoytrip.common.exception.NotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BoardServiceImplTest {

    private final BoardMapper mapper = mock(BoardMapper.class);
    private final BoardService service = new BoardServiceImpl(mapper);

    @Test
    void modifyThrowsNotFoundWhenBoardMissing() {
        when(mapper.selectBoardById(1)).thenReturn(null);
        Board board = Board.builder().boardId(1).title("t").content("c").build();

        assertThatThrownBy(() -> service.modify(board, "ssafy"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void modifyThrowsForbiddenWhenOwnerDiffers() {
        when(mapper.selectBoardById(1)).thenReturn(Board.builder().boardId(1).userId("owner").build());
        Board board = Board.builder().boardId(1).title("t").content("c").build();

        assertThatThrownBy(() -> service.modify(board, "other"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void deleteChecksOwnerAndDeletes() {
        when(mapper.selectBoardById(1)).thenReturn(Board.builder().boardId(1).userId("ssafy").build());

        service.remove(1, "ssafy");

        verify(mapper).deleteBoard(1);
    }
}
```

- [ ] **Step 3: Implement Board service API**

Define `BoardService` methods:

```java
List<Board> getList(String type);
Board getDetail(int boardId);
int write(Board board);
void modify(Board board, String loginUserId);
void remove(int boardId, String loginUserId);
```

Implementation rules:

```java
private Board requireBoard(int boardId) {
    Board board = boardMapper.selectBoardById(boardId);
    if (board == null) {
        throw new NotFoundException(ErrorCode.NOT_FOUND);
    }
    return board;
}

private void requireOwner(Board board, String loginUserId) {
    if (!board.getUserId().equals(loginUserId)) {
        throw new ForbiddenException(ErrorCode.FORBIDDEN);
    }
}
```

- [ ] **Step 4: Replace Board controller**

Expose these mappings:

```text
GET  /boards
GET  /boards/new
POST /boards
GET  /boards/{boardId}
GET  /boards/{boardId}/edit
POST /boards/{boardId}
POST /boards/{boardId}/delete
```

Keep compatibility redirects:

```text
GET /board/list    -> redirect:/boards?type={type}
GET /board/detail  -> redirect:/boards/{boardId}?type={type}
GET /board/write   -> redirect:/boards/new?type={type}
GET /board/modify  -> redirect:/boards/{boardId}/edit?type={type}
GET /board/delete  -> redirect:/boards/{boardId}/delete is not used; return redirect:/boards
```

- [ ] **Step 5: Run board tests**

Run:

```bash
mvn -Dtest=BoardServiceImplTest,BoardControllerTest test
```

Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/enjoytrip/board src/main/resources/mappers/BoardMapper.xml src/test/java/com/enjoytrip/board
git commit -m "feat: migrate board to spring mybatis"
```

## Task 8: Migrate Attraction Pages and REST API Client

**Files:**
- Modify: `src/main/java/com/enjoytrip/attraction/dto/Sido.java`
- Modify: `src/main/java/com/enjoytrip/attraction/dto/Gugun.java`
- Modify: `src/main/java/com/enjoytrip/attraction/dto/AttractionInfo.java`
- Create: `src/main/java/com/enjoytrip/attraction/dto/AttractionSearchRequest.java`
- Create: `src/main/java/com/enjoytrip/attraction/client/TourApiProperties.java`
- Create: `src/main/java/com/enjoytrip/attraction/client/TourApiClient.java`
- Create: `src/main/java/com/enjoytrip/attraction/mapper/AttractionMapper.java`
- Create: `src/main/resources/mappers/AttractionMapper.xml`
- Replace: `src/main/java/com/enjoytrip/attraction/controller/AttractionController.java` with `AttractionPageController.java` and `AttractionApiController.java`
- Test: `src/test/java/com/enjoytrip/attraction/client/TourApiClientTest.java`
- Test: `src/test/java/com/enjoytrip/attraction/controller/AttractionApiControllerTest.java`

- [ ] **Step 1: Add search request DTO**

Create `src/main/java/com/enjoytrip/attraction/dto/AttractionSearchRequest.java`:

```java
package com.enjoytrip.attraction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

- [ ] **Step 2: Add API properties and client**

Create `src/main/java/com/enjoytrip/attraction/client/TourApiProperties.java`:

```java
package com.enjoytrip.attraction.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tour.api")
public record TourApiProperties(
        String baseUrl,
        String serviceKey,
        String mobileOs,
        String mobileApp
) {
}
```

Create `TourApiClient` with methods:

```java
String getSidos();
String getGuguns(Integer sidoCode);
String search(AttractionSearchRequest request);
String detail(Integer contentId);
```

Use `RestClient` and `UriComponentsBuilder`. Always append `serviceKey`, `MobileOS`, `MobileApp`, and `_type=json`. Convert `RestClientException` to `ExternalApiException(ErrorCode.EXTERNAL_API_ERROR, e)`.

- [ ] **Step 3: Add page and API controllers**

`AttractionPageController` mappings:

```text
GET /attractions
GET /attractions/{contentId}
```

`AttractionApiController` mappings:

```text
GET /api/attractions/sidos
GET /api/attractions/guguns
GET /api/attractions/search
GET /api/attractions/{contentId}
```

For `/api/attractions/search`, bind query parameters with:

```java
public ResponseEntity<String> search(@ModelAttribute AttractionSearchRequest request)
```

- [ ] **Step 4: Update JSP API URLs**

Modify these JSP files:

```text
src/main/webapp/WEB-INF/views/attraction/list.jsp
src/main/webapp/WEB-INF/views/attraction/detail.jsp
src/main/webapp/WEB-INF/views/plan/write.jsp
```

Replace old proxy base:

```jsp
${pageContext.request.contextPath}/attraction/api
```

with:

```jsp
${pageContext.request.contextPath}/api/attractions
```

- [ ] **Step 5: Run attraction tests**

Run:

```bash
mvn -Dtest=TourApiClientTest,AttractionApiControllerTest test
```

Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/enjoytrip/attraction src/main/resources/mappers/AttractionMapper.xml src/main/webapp/WEB-INF/views src/test/java/com/enjoytrip/attraction
git commit -m "feat: migrate attraction api"
```

## Task 9: Migrate Plan Domain and Order API

**Files:**
- Modify: `src/main/java/com/enjoytrip/plan/dto/TravelPlan.java`
- Modify: `src/main/java/com/enjoytrip/plan/dto/PlanDetail.java`
- Create: `src/main/java/com/enjoytrip/plan/dto/PlanDetailOrderRequest.java`
- Create: `src/main/java/com/enjoytrip/plan/mapper/TravelPlanMapper.java`
- Create: `src/main/resources/mappers/TravelPlanMapper.xml`
- Modify: `src/main/java/com/enjoytrip/plan/service/TravelPlanService.java`
- Modify: `src/main/java/com/enjoytrip/plan/service/TravelPlanServiceImpl.java`
- Replace: `src/main/java/com/enjoytrip/plan/controller/PlanController.java`
- Create: `src/main/java/com/enjoytrip/plan/controller/PlanApiController.java`
- Test: `src/test/java/com/enjoytrip/plan/service/TravelPlanServiceImplTest.java`
- Test: `src/test/java/com/enjoytrip/plan/controller/PlanApiControllerTest.java`

- [ ] **Step 1: Add order request DTO**

Create `src/main/java/com/enjoytrip/plan/dto/PlanDetailOrderRequest.java`:

```java
package com.enjoytrip.plan.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlanDetailOrderRequest {
    private int visitOrder;
}
```

- [ ] **Step 2: Replace JSON string parsing with Jackson**

In `PlanController`, inject `ObjectMapper` and parse `detailsJson` with:

```java
List<PlanDetail> details = objectMapper.readValue(
        detailsJson,
        new TypeReference<List<PlanDetail>>() {}
);
```

Remove the existing manual `parseDetailsJson` method after the test passes.

- [ ] **Step 3: Add mapper XML from existing `TravelPlanDaoImpl` SQL**

Mapper methods:

```java
int insertPlan(TravelPlan plan);
TravelPlan selectPlanById(int planId);
List<TravelPlan> selectPlansByUserId(String userId);
void updatePlan(TravelPlan plan);
void deletePlan(int planId);
void insertDetail(PlanDetail detail);
List<PlanDetail> selectDetailsByPlanId(int planId);
void deleteDetailsByPlanId(int planId);
void updateDetailOrder(int detailId, int visitOrder);
```

Use `useGeneratedKeys="true" keyProperty="planId"` for `insertPlan`.

- [ ] **Step 4: Enforce ownership in service**

Add service helper:

```java
private TravelPlan requirePlanOwnedBy(int planId, String userId) {
    TravelPlan plan = mapper.selectPlanById(planId);
    if (plan == null) {
        throw new NotFoundException(ErrorCode.NOT_FOUND);
    }
    if (!plan.getUserId().equals(userId)) {
        throw new ForbiddenException(ErrorCode.FORBIDDEN);
    }
    return plan;
}
```

Use it for modify, delete, detail mutation, and order mutation.

- [ ] **Step 5: Add order API**

Create `PlanApiController`:

```java
@PatchMapping("/api/plans/details/{detailId}/order")
@ResponseBody
public Map<String, String> updateOrder(
        @PathVariable int detailId,
        @RequestBody PlanDetailOrderRequest request,
        HttpSession session) {
    String userId = (String) session.getAttribute("loginUser");
    travelPlanService.changeDetailOrder(detailId, request.getVisitOrder(), userId);
    return Map.of("result", "ok");
}
```

Update `src/main/webapp/WEB-INF/views/plan/detail.jsp` fetch call from `/plan/updateOrder` to `/api/plans/details/{detailId}/order` with method `PATCH` and JSON body.

- [ ] **Step 6: Run plan tests**

Run:

```bash
mvn -Dtest=TravelPlanServiceImplTest,PlanApiControllerTest test
```

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/enjoytrip/plan src/main/resources/mappers/TravelPlanMapper.xml src/main/webapp/WEB-INF/views/plan src/test/java/com/enjoytrip/plan
git commit -m "feat: migrate plan domain"
```

## Task 10: Migrate Hotplace Domain and File Upload

**Files:**
- Modify: `src/main/java/com/enjoytrip/hotplace/dto/Hotplace.java`
- Create: `src/main/java/com/enjoytrip/hotplace/mapper/HotplaceMapper.java`
- Create: `src/main/resources/mappers/HotplaceMapper.xml`
- Modify: `src/main/java/com/enjoytrip/hotplace/service/HotplaceService.java`
- Modify: `src/main/java/com/enjoytrip/hotplace/service/HotplaceServiceImpl.java`
- Create: `src/main/java/com/enjoytrip/hotplace/service/FileStorageService.java`
- Replace: `src/main/java/com/enjoytrip/hotplace/controller/HotplaceController.java`
- Test: `src/test/java/com/enjoytrip/hotplace/service/FileStorageServiceTest.java`
- Test: `src/test/java/com/enjoytrip/hotplace/service/HotplaceServiceImplTest.java`

- [ ] **Step 1: Add file storage service**

Create `src/main/java/com/enjoytrip/hotplace/service/FileStorageService.java`:

```java
package com.enjoytrip.hotplace.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import com.enjoytrip.common.exception.ErrorCode;
import com.enjoytrip.common.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir);
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "";
        }
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        String savedName = UUID.randomUUID() + extension;
        try {
            Files.createDirectories(uploadDir);
            file.transferTo(uploadDir.resolve(savedName));
            return "uploads/" + savedName;
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.FILE_UPLOAD_ERROR, e);
        }
    }
}
```

- [ ] **Step 2: Enforce ownership in service**

Service methods that mutate hotplaces must load the existing hotplace and compare `hotplace.userId` with `loginUserId`. Throw `NotFoundException` when missing and `ForbiddenException` when user differs.

- [ ] **Step 3: Replace controller mappings**

Expose:

```text
GET  /hotplaces
GET  /hotplaces/new
POST /hotplaces
GET  /hotplaces/{hotplaceId}
GET  /hotplaces/{hotplaceId}/edit
POST /hotplaces/{hotplaceId}
POST /hotplaces/{hotplaceId}/delete
```

Maintain redirects from `/hotplace/list`, `/hotplace/write`, `/hotplace/detail`, and `/hotplace/modify`.

- [ ] **Step 4: Run hotplace tests**

Run:

```bash
mvn -Dtest=FileStorageServiceTest,HotplaceServiceImplTest test
```

Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/enjoytrip/hotplace src/main/resources/mappers/HotplaceMapper.xml src/test/java/com/enjoytrip/hotplace
git commit -m "feat: migrate hotplace domain"
```

## Task 11: Add MyBatis Slice Tests for Core Mappers

**Files:**
- Test: `src/test/java/com/enjoytrip/member/mapper/MemberMapperTest.java`
- Test: `src/test/java/com/enjoytrip/board/mapper/BoardMapperTest.java`
- Test: `src/test/java/com/enjoytrip/plan/mapper/TravelPlanMapperTest.java`
- Test: `src/test/java/com/enjoytrip/hotplace/mapper/HotplaceMapperTest.java`
- Test: `src/test/java/com/enjoytrip/attraction/mapper/AttractionMapperTest.java`

- [ ] **Step 1: Add mapper test pattern**

Each mapper test extends `AbstractMySqlContainerTest` and uses `@MybatisTest`:

```java
@MybatisTest
@ActiveProfiles("test")
class MemberMapperTest extends AbstractMySqlContainerTest {

    @Autowired
    MemberMapper memberMapper;

    @Test
    void selectMemberByIdFindsSeedMember() {
        Member member = memberMapper.selectMemberById("ssafy");

        assertThat(member).isNotNull();
        assertThat(member.getUserName()).isEqualTo("관리자");
    }
}
```

- [ ] **Step 2: Add domain-specific mapper assertions**

Use these minimum assertions:

```text
MemberMapperTest: select seed member, insert new member, update email, delete member
BoardMapperTest: select notices, insert board with generated id, increase views, update, delete
TravelPlanMapperTest: insert plan with generated id, insert detail, select ordered details, update order, delete plan
HotplaceMapperTest: insert hotplace with generated id, select all, update title, delete
AttractionMapperTest: select all sido rows and gugun rows by sido code
```

- [ ] **Step 3: Run mapper tests**

Run:

```bash
mvn -Dtest='*MapperTest' test
```

Expected: PASS when Docker is running.

- [ ] **Step 4: Commit**

```bash
git add src/test/java/com/enjoytrip/*/mapper
git commit -m "test: add mybatis mapper slice tests"
```

## Task 12: Remove Legacy JDBC Artifacts and Fix JSP Links

**Files:**
- Delete: `src/main/java/com/enjoytrip/util/DBUtil.java`
- Delete: all `src/main/java/com/enjoytrip/**/dao/*.java`
- Modify: all JSPs under `src/main/webapp/WEB-INF/views/**`
- Modify: `README.md`

- [ ] **Step 1: Delete legacy JDBC utilities and DAO interfaces**

Run:

```bash
git rm src/main/java/com/enjoytrip/util/DBUtil.java
git rm -r src/main/java/com/enjoytrip/*/dao
```

Expected: no code references `DBUtil`, `Dao`, or `DaoImpl`.

- [ ] **Step 2: Verify no legacy references remain**

Run:

```bash
rg -n "DBUtil|DaoImpl|new .*ServiceImpl|@WebServlet|HttpServlet" src/main/java
```

Expected: no output.

- [ ] **Step 3: Fix JSP links and forms to canonical URLs**

Update JSPs so new canonical URLs are used:

```text
/attraction/list -> /attractions
/board/list -> /boards
/board/write -> /boards/new
/plan/list -> /plans
/plan/write -> /plans/new
/hotplace/list -> /hotplaces
/hotplace/write -> /hotplaces/new
```

Leave `/user/login`, `/user/join`, `/user/logout`, `/user/mypage`, `/user/modify`, and `/user/delete` unchanged.

- [ ] **Step 4: Update README run instructions**

Change README to say:

```text
1. docker compose up -d
2. cp .env.example .env
3. edit .env with API keys
4. mvn spring-boot:run
5. open http://localhost:8080/
```

- [ ] **Step 5: Run full tests and package**

Run:

```bash
mvn test
mvn package
```

Expected: both commands PASS.

- [ ] **Step 6: Commit**

```bash
git add README.md src/main/java src/main/webapp
git commit -m "chore: remove legacy servlet jdbc artifacts"
```

## Task 13: Final Verification

**Files:**
- Modify only files required to fix failures found during verification.

- [ ] **Step 1: Run clean status check**

Run:

```bash
git status --short --branch
```

Expected: either clean or only intentional uncommitted verification fixes.

- [ ] **Step 2: Run complete verification**

Run:

```bash
mvn test
mvn package
```

Expected: PASS.

- [ ] **Step 3: Start local DB**

Run:

```bash
docker compose up -d
```

Expected: MySQL container is healthy.

- [ ] **Step 4: Start app**

Run:

```bash
mvn spring-boot:run
```

Expected: app starts on port 8080.

- [ ] **Step 5: Manual smoke checks**

Open:

```text
http://localhost:8080/
http://localhost:8080/attractions
http://localhost:8080/boards?type=notice
http://localhost:8080/user/login
```

Expected:

```text
Home page renders.
Attraction page renders.
Board list renders for anonymous user.
Login page renders.
Unauthenticated /plans redirects to /user/login.
```

- [ ] **Step 6: Commit verification fixes**

If Step 2 through Step 5 required fixes:

```bash
git add .
git commit -m "fix: complete spring boot migration verification"
```

If no fixes were needed, do not create an empty commit.

## Self-Review Checklist

- Spec coverage:
  - Spring Boot executable WAR: Task 1.
  - Standard layout and JSP relocation: Task 2.
  - MyBatis mapper migration: Tasks 6 through 11.
  - REST JSON endpoints: Tasks 8 and 9.
  - `.env` and config: Task 1 and Task 4.
  - Session auth and interceptor scope: Task 4 plus domain controller tasks.
  - Request logging: Task 4.
  - Global exceptions: Task 3.
  - Docker Compose DB retention: Task 13.
  - Testcontainers DB tests: Task 5 and Task 11.
  - Slice/unit/integration tests: Tasks 3 through 11 and Task 13.
- Placeholder scan:
  - No unfinished placeholder markers are allowed in this plan.
  - Any command that can fail has an expected outcome.
- Type consistency:
  - `ErrorCode`, exception names, DTO names, mapper names, and endpoint paths are consistent with the spec.
  - `loginUser` and `loginUserName` session keys are preserved.
