# 프로젝트 소개

Spring Boot로 만든 상품 관리 REST API임. 상품을 등록·조회·수정·삭제하고 이름으로 검색할 수 있으며, 데이터는 DB 없이 메모리(Java Collection)에 저장함.

## 관리하는 데이터

- `id`: 등록 시 서버가 UUID로 자동 생성
- `name`: 상품 이름 (빈 값 불가)
- `category`: 상품 분류 (빈 값 불가)
- `count`: 수량, 예: "2개" (빈 값 불가)
- `price`: 가격 (0 이상)
- `day`: 유통기한 

## 프로젝트 구조

```
src/main/java/com/webservice/week05
├── Week05ProductCrudApplication.java
├── controller/ProductController.java
├── service/ProductService.java
├── repository/ProductRepository.java
├── repository/MemoryProductRepository.java
├── domain/Product.java
└── dto/ProductRequest.java, ProductResponse.java
```

## 로컬 실행 방법

```bash
git clone https://github.com/2026-2-WebService/assign05-c01-22500802.git
cd assign05-c01-22500802
./gradlew bootRun
```

`http://localhost:8080`에서 실행됨. 8080 포트가 사용 중이면 `./gradlew bootRun --args='--server.port=8081'`로 실행함.

## API Endpoint

- `POST /api/products`: 상품 등록 (201, 입력 오류 시 400)
- `GET /api/products`: 전체 조회 (200)
- `GET /api/products?name=우유`: 이름 검색, 부분 일치·대소문자 무시 
- `GET /api/products/{id}`: 1개 조회 (200, 없으면 404)
- `PUT /api/products/{id}`: 수정 (200, 입력 오류 400, 없으면 404)
- `DELETE /api/products/{id}`: 삭제 (204, 없으면 404)

## 요청·응답 JSON 예시

실제로 앱을 실행해 `curl`로 받은 결과임. 

등록 `POST /api/products`

```json
{ "name": "우유", "category": "유제품", "count": "2개", "price": 3000, "day": 7 }
```
```json
// 201 Created
{ "id": "91b3ee35-a63b-4007-8721-21251728a689", "name": "우유", "category": "유제품", "count": "2개", "price": 3000, "day": 7 }
```

수정 `PUT /api/products/91b3ee35-a63b-4007-8721-21251728a689`

```json
{ "name": "우유", "category": "유제품", "count": "5개", "price": 3200, "day": 5 }
```
```json
// 200 OK
{ "id": "91b3ee35-a63b-4007-8721-21251728a689", "name": "우유", "category": "유제품", "count": "5개", "price": 3200, "day": 5 }
```

삭제 `DELETE /api/products/{id}`는 204를 반환하고, 이후 같은 id를 조회하면 404임.

```json
{ "timestamp": "2026-10-05T06:42:53.840+00:00", "status": 404, "error": "Not Found", "path": "/api/products/91b3ee35-a63b-4007-8721-21251728a689" }
```

## GitHub / 배포 URL

- GitHub: https://github.com/2026-2-WebService/assign05-c01-22500802

---

# 개발환경 및 Dependency

- IDE: IntelliJ IDEA 
- JDK: 17 (`build.gradle`)
- Spring Boot: 3.5.5
- Build Tool: Gradle 9.6.0 (Wrapper)
- 데이터 저장: `LinkedHashMap<String, Product>` (`MemoryProductRepository`)
- 배포 환경: `TODO`

## Dependency

- `spring-boot-starter-web`: REST API(`@RestController`, `@RequestBody`), 내장 Tomcat, JSON 변환에 필요함.
- `spring-boot-starter-validation`: 직접 추가함. `ProductRequest`의 `@NotBlank`, `@PositiveOrZero`와 컨트롤러의 `@Valid`로 잘못된 입력을 400으로 막기 위해 필요함.
- `spring-boot-starter-test`: 테스트 환경임.

---

# Solution 분석

**Q1. POST /api/products 요청이 들어오면 어떤 메서드를 순서대로 거치나요?**
ProductController의 createProduct() → ProductService의 create() → MemoryProductRepository의 save() 순서임. 결과는 반대로 Product → ProductResponse.from() → ResponseEntity(201)로 돌아옴.

**Q4. ProductService는 왜 ProductRepository 인터페이스 타입으로 주입받나요?**
생성자 주입에서 ProductRepository 인터페이스 타입으로 받으면 MemoryProductRepository를 다른 구현체(예: DB)로 바꿔도 Service 코드를 고치지 않아도 됨.

**Q5. findById()에서 찾는 ID가 없으면 어떤 과정으로 404가 반환되나요?**
ProductController의 findById() → ProductService의 findById() → findProduct() 순서로 호출됨. findProduct()에서 repository.findById(id)가 빈 Optional을 돌려주면 orElseThrow()가 ResponseStatusException(HttpStatus.NOT_FOUND)을 던지고, Spring이 이 예외를 404 응답으로 바꿈. update(), delete()도 findProduct()를 써서 같은 방식으로 404가 됨.

**Q6. Optional.ofNullable()과 orElseThrow()는 각각 어떤 역할을 하나요?**
MemoryProductRepository의 findById()에서 쓰는 Optional.ofNullable(products.get(id))는 Map에 없어서 null이 나와도 빈 Optional로 감싸 줌. ProductService의 findProduct()에서 쓰는 orElseThrow()는 값이 있으면 꺼내고, 비어 있으면 예외를 던짐.

**Q7. findAll()은 List<Product>를 List<ProductResponse>로 어떻게 바꾸나요?**
ProductService의 findAll()에서 repository.findAll()로 가져온 목록을 stream으로 돌며 map(ProductResponse::from)으로 Product 하나하나를 ProductResponse로 변환한 뒤 toList()로 모음.

**Q8. 서버를 재시작하면 등록한 데이터는 어떻게 되나요?**
모두 사라짐. 데이터가 MemoryProductRepository의 products(LinkedHashMap) 필드, 즉 JVM 메모리에만 있어서 프로세스가 종료되면 함께 없어지기 때문임.

---

# 개발 과정 요약

1. **프로젝트 생성**: Spring Boot(Gradle) 프로젝트와 패키지 구조를 만듦. `./gradlew bootRun`으로 Tomcat 기동을 확인함.
2. **도메인·DTO**: `Product`, `ProductRequest`, `ProductResponse`를 작성함. 컴파일로 확인함.
3. **Repository**: `ProductRepository` 인터페이스와 `MemoryProductRepository`(`save`, `findAll`, `findById`, `update`, `deleteById`)를 작성함.
4. **Service**: `ProductService`의 `create`, `findAll`, `findById`, `update`, `delete`, `findProduct`(404 처리)를 작성함.
5. **Controller**: `ProductController`에 5개 endpoint를 만듦. `curl`로 등록 → 조회 → 수정 → 삭제 → 삭제된 id 조회(404) 순서로 확인함.

---

# 기능 수정·확장 (STEP 5)

## A. 잘못된 입력 처리 (400)

- 추가한 이유: 검증이 없으면 이름이 빈 값이거나 가격이 음수인 상품도 등록됨.
- 수정한 곳:
  - `build.gradle`: `spring-boot-starter-validation` 추가
  - `ProductRequest`: `@NotBlank`, `@PositiveOrZero` 추가
  - `ProductController.createProduct()`, `update()`: `@Valid` 추가
- 테스트 요청과 예상 결과 (`POST /api/products`):
  - `name`이 `""` → 400
  - `price`가 `-1` → 400
  - `category` 필드 누락 → 400
  - 정상 값 → 201
- 실제 응답: 위 세 경우 모두 `400`, 본문은 `{"status":400,"error":"Bad Request","path":"/api/products",...}`. 정상 값은 `201 Created`와 상품 JSON.

## B. 이름으로 검색

- 추가한 이유: 상품이 많아지면 전체 목록에서 찾기 어려움.
- 수정한 곳:
  - `ProductController.findAll(@RequestParam(required = false) String name)`
  - `ProductService.findAll(String name)`: `name`이 `null`이면 전체, 아니면 `toLowerCase().contains()`로 필터
- 테스트 데이터: `우유`, `Apple Juice`, `초코우유`를 등록한 뒤 검색함.
- 테스트 요청, 예상 결과, 실제 응답:
  - `GET /api/products?name=우유` → 2개 예상 → `200`, `우유`와 `초코우유` 반환
  - `GET /api/products?name=apple` → 대소문자 무시, 1개 예상 → `200`, `Apple Juice` 반환
  - `GET /api/products?name=쌀` → 빈 배열 예상 → `200 []`
  - `GET /api/products` → 3개 예상 → `200`, 3개 모두 반환

한글 쿼리는 `curl -G --data-urlencode "name=우유"`처럼 인코딩해서 보내야 함. 인코딩하지 않으면 Tomcat이 400을 반환함.

## 참고: category 추가

`Product`, `ProductRequest`, `ProductResponse`, `ProductService.create()/update()`에 `category`를 추가했고, id 타입을 `int`에서 `String`(UUID)으로 바꿈.

---

# 배포 과정 요약

- 빌드·배포 순서:
- 추가·수정한 파일·설정:
- 발생한 문제와 해결:
- 배포 URL로 확인한 요청과 응답:

---

# Weekly Report

## Key Learning

1. 계층 분리: Controller, Service, Repository로 나누면 각 클래스가 한 가지 일만 함.
2. 인터페이스 의존: Service가 `ProductRepository` 인터페이스에만 의존하므로 저장 방식을 바꿔도 Service는 그대로임.
3. 입력 검증: `ProductRequest`에 검증 어노테이션을 붙이고 `@Valid`를 쓰면 잘못된 입력이 Service에 닿기 전에 막힘.


## Problem & Solution

`./gradlew bootRun` 시 `Web server failed to start. Port 8080 was already in use.` 오류가 남. `lsof -iTCP:8080 -sTCP:LISTEN`으로 확인하니 다른 Java 프로세스가 8080을 쓰고 있었고, `--args='--server.port=8081'`로 포트를 바꿔 실행함.


## Code Review

`ProductService.findAll(String name)`

```java
public List<ProductResponse> findAll(String name) {
    return repository.findAll().stream()
            .filter(product -> name == null || product.getName().toLowerCase().contains(name.toLowerCase()))
            .map(ProductResponse::from)
            .toList();
}
```

저장소의 전체 상품을 stream으로 돌며, `name`이 `null`이면 모두 통과시키고 아니면 이름에 검색어가 포함된(대소문자 무시) 상품만 남김. 남은 상품은 `ProductResponse.from`으로 변환해 반환함. 한 메서드로 전체 조회와 검색을 모두 처리함.

## AI Usage

- 질문한 내용: 
- 참고한 답변: README 구조와 코드 기반 설명 초안에 AI(Claude Code)를 사용함.
- 직접 확인·수정한 부분: 앱을 실행해 `curl`로 각 endpoint, 검증, 검색 응답을 확인함. `TODO: 본인이 확인·수정한 내용 추가`

## Reflection


#

