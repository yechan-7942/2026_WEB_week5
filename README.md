# 4주차 과제: Spring Boot CRUD REST API - Book 관리

## ① 프로젝트 소개

### 프로젝트 주제
Spring Boot로 Book(도서) 데이터를 관리하는 REST API를 구현했습니다. Database 없이 `Map<Long, Book>`을 저장소로 사용하여 CRUD(Create, Read, Update, Delete)를 처리합니다.

### 프로젝트 구조
```
src/main/java/com/webservice/week05
├── Week05BookCrudApplication.java
├── controller
│   └── BookController.java        # HTTP 요청/응답 처리
├── service
│   └── BookService.java           # 비즈니스 로직, 404 처리
├── repository
│   ├── BookRepository.java        # 저장소 인터페이스
│   └── MemoryBookRepository.java  # Map 기반 구현체
├── domain
│   └── Book.java                  # 도메인 객체
└── dto
    ├── BookRequest.java           # 요청 DTO
    └── BookResponse.java          # 응답 DTO
```

**Development Flow**
```
HTTP Request → Controller → Service → Repository → Memory(Map) → JSON Response
```

### API Endpoint

| Method | URL | 기능 |
|--------|-----|------|
| POST | `/api/books` | 도서 등록 |
| GET | `/api/books` | 전체 조회 |
| GET | `/api/books/{id}` | 단건 조회 |
| PUT | `/api/books/{id}` | 수정 |
| DELETE | `/api/books/{id}` | 삭제 |

### Request / Response 예시

**POST /api/books**

Request
```json
{
  "title": "Hello World",
  "author": "Hwang",
  "price": 25000
}
```

Response (201 Created)
```json
{
  "id": 0,
  "title": "Hello World",
  "author": "Hwang",
  "price": 25000
}
```

**GET /api/books/{id} (존재하지 않는 id)**

Response (404 Not Found)
```json
{
  "timestamp": "2026-09-28T12:50:10.875+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/books/0"
}
```

### 실행 방법
1. IntelliJ에서 `Week05BookCrudApplication.java` 열고 `main()` 옆 ▶ 버튼 실행
   또는 터미널에서 `./gradlew bootRun`
2. `Tomcat started on port 8080` 로그가 뜨면 정상 실행된 것
3. `http://localhost:8080/api/books`로 API 호출

### Postman / curl 테스트 결과

```bash
# 1. 등록
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Hello World","author":"Hwang","price":25000}'
# -> {"id":0,"title":"Hello World","author":"Hwang","price":25000}

# 2. 전체 조회
curl http://localhost:8080/api/books
# -> [{"id":0,"title":"Hello World","author":"Hwang","price":25000}]

# 3. 단건 조회
curl http://localhost:8080/api/books/0
# -> {"id":0,"title":"Hello World","author":"Hwang","price":25000}

# 4. 수정
curl -X PUT http://localhost:8080/api/books/0 \
  -H "Content-Type: application/json" \
  -d '{"title":"Hello World 개정판","author":"Hwang","price":28000}'
# -> {"id":0,"title":"Hello World 개정판","author":"Hwang","price":28000}

# 5. 삭제
curl -i -X DELETE http://localhost:8080/api/books/0
# -> HTTP/1.1 204 No Content

# 6. 삭제 확인
curl -i http://localhost:8080/api/books/0
# -> HTTP/1.1 404 Not Found
```

---

## ② 이번 과제 키워드

REST API, CRUD, `@RequestBody`, `@PathVariable`, `@RestController`, Layered Architecture, Repository Interface, DTO, `Optional`, `ResponseStatusException`

---

## ③ 핵심 내용 정리

1. Controller-Service-Repository로 계층을 분리하면 각 계층이 자기 책임(HTTP 처리 / 비즈니스 로직 / 데이터 저장)만 맡게 되어 코드를 이해하고 수정하기 쉬워진다.
2. Repository를 인터페이스로 먼저 정의하고 `MemoryBookRepository`로 구현을 분리하면, 나중에 DB로 바꿀 때 Service/Controller 코드는 건드리지 않아도 된다.
3. `Optional`은 값이 없을 수 있는 경우(`findById`)를 명시적으로 표현해주는 타입이라, null 체크를 빼먹는 실수를 줄여준다.
4. Domain 객체(`Book`)와 DTO(`BookRequest`/`BookResponse`)를 분리하면, 클라이언트에게 노출할 필드와 서버 내부에서 쓰는 필드를 독립적으로 관리할 수 있다.
5. `ResponseStatusException`을 던지면 별도의 예외 처리 클래스 없이도 원하는 HTTP 상태 코드(404 등)로 응답할 수 있다.

---

## ④ Weekly Report

### Key Learning
1. Controller → Service → Repository로 이어지는 계층형 아키텍처의 역할 분리 방식
2. `@PathVariable`(URL 경로 값)과 `@RequestParam`(쿼리 파라미터), `@RequestBody`(JSON body)의 차이와 사용 시점
3. `Optional`을 활용해 존재하지 않는 데이터를 안전하게 처리하는 방법

### CRUD Flow
```
Client 요청
  → BookController (HTTP 매핑, @RequestBody/@PathVariable로 데이터 수신)
  → BookService (요청 DTO를 Domain으로 변환, 존재 여부 확인, 비즈니스 로직 처리)
  → BookRepository 인터페이스
  → MemoryBookRepository (Map<Long, Book>에 실제 저장/조회/수정/삭제)
  → Service가 결과를 다시 BookResponse DTO로 변환
  → Controller가 JSON으로 응답
```

### Problem & Solution
**문제**: 로컬 IntelliJ 환경에서 여러 과제 폴더(assign1~5)를 하나의 프로젝트로 묶어서 열어두었더니, `assign04`(Gradle, Spring Boot 3.5.5) 빌드 시 `org.springframework.core.NestedRuntimeException` 클래스를 찾을 수 없다는 에러가 발생했다. 터미널에서 `./gradlew compileJava`로는 정상 빌드되는 걸로 봐서 실제 코드 문제가 아니라 IntelliJ가 다른 프로젝트의 라이브러리 버전을 잘못 참조하는 IDE 캐시 문제임을 확인했다.

**해결**: week4주차 과제폴더만 별도의 IntelliJ 창으로 독립적으로 열어서 다른 과제 폴더의 의존성과 섞이지 않게 하니 정상적으로 해결되었다.

### Code Review
가장 중요하다고 생각한 코드는 `BookService`의 존재 여부 확인 패턴이다.

```java
public BookResponse findById(Long id) {
    Optional<Book> book = repository.findById(id);
    if (book.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
    }
    return new BookResponse(book.get().getId(), book.get().getTitle(), book.get().getAuthor(), book.get().getPrice());
}
```

이유: `findById`, `update`, `delete` 세 곳에서 반복되는 "존재하지 않으면 404" 패턴의 기본형이기 때문이다. Repository가 `Optional`을 반환하도록 설계해둔 덕분에, Service 계층에서 null 체크 없이 `isEmpty()`만으로 명확하게 예외 상황을 처리할 수 있었다.

### Reflection
`Map.put()`이 새로 추가한 값이 아니라 **이전에 있던 값**을 반환한다는 걸 모르고 `update` 구현 시 리턴값을 잘못 사용했던 적이 있다. 앞으로 표준 라이브러리 메서드를 쓸 때 반환값의 의미를 문서로 한 번 더 확인해야겠다고 느꼈다.

\

