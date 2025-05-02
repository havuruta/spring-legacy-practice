# BalanceEat 프로젝트 API 문서

## 1. 프로젝트 구조

```
src/
├── main/
│   ├── java/
│   │   └── com/balanceeat/demo/
│   │       ├── domain/
│   │       │   ├── user/        // 회원 관리
│   │       │   ├── diet/        // 식단 관리
│   │       │   ├── nutrition/   // 영양소 관리
│   │       │   └── auth/        // 인증 관리
│   │       ├── config/          // 설정 파일
│   │       └── DemoApplication.java
│   └── resources/
│       ├── application.properties
│       └── mybatis/
```

## 2. API 엔드포인트

### 2.1 인증 API

| 기능 | HTTP Method | URL | 요청 | 응답 | 상태 코드 |
|:---|:---|:---|:---|:---|:---|
| 로그인 | POST | /auth/login | LoginRequest { username, password } | User | 200(성공), 401(실패) |
| 회원가입 | POST | /auth/register | RegisterRequest { username, password, passwordConfirm } | String(username) | 200(성공), 400(실패) |
| 로그아웃 | GET | /auth/logout | - | - | 200(성공), 401(실패) |
| 회원탈퇴 | POST | /auth/delete | @SessionAttribute("user") | - | 200 |

### 2.2 사용자 API

| 기능 | HTTP Method | URL | 요청 | 응답 | 상태 코드 |
|:---|:---|:---|:---|:---|:---|
| 사용자 정보 조회 | GET | /user/info | @SessionAttribute("user") | UserDTO | 200 |
| 사용자 정보 수정 | PUT | /user/info | UserDTO | UserDTO | 200(성공), 400(실패) |

### 2.3 식단 API

| 기능 | HTTP Method | URL | 요청 | 응답 | 상태 코드 |
|:---|:---|:---|:---|:---|:---|
| 식단 목록 조회 | GET | /diet | @RequestParam start, end (yyyy-MM-dd) | List<DietByDateDTO> | 200(성공), 401(인증실패) |
| 식단 추가 | POST | /diet | DietRequestDTO | String | 200(성공), 401(인증실패) |
| 식단 수정 | PUT | /diet/{id} | DietUpdateRequestDTO | String | 200(성공), 400(실패), 401(인증실패) |
| 식단 삭제 | DELETE | /diet/{id} | @PathVariable id | String | 200(성공), 400(실패), 401(인증실패) |

### 2.4 영양 정보 API

| 기능 | HTTP Method | URL | 요청 | 응답 | 상태 코드 |
|:---|:---|:---|:---|:---|:---|
| 영양 정보 목록 조회 | GET | /nutrition | - | List<Nutrition> | 200 |
| 영양 정보 상세 조회 | GET | /nutrition/{id} | @PathVariable id | Nutrition | 200(성공), 404(없음) |
| 영양 정보 검색 | GET | /nutrition/search | @RequestParam(required=false) description, name | List<Nutrition> | 200 |

## 3. DTO 구조

### 3.1 인증 관련 DTO
```java
// LoginRequest
{
    private String username;
    private String password;
}

// RegisterRequest
{
    private String username;
    private String password;
    private String passwordConfirm;
}
```

### 3.2 사용자 관련 DTO
```java
// UserDTO
{
    private String id;
    private String username;
    // 기타 사용자 정보 필드
}
```

### 3.3 식단 관련 DTO
```java
// DietRequestDTO
{
    private LocalDate dietDate;
    private String mealType;
    private String foodName;
    private Double amount;
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbohydrates;
    private String note;
}

// DietByDateDTO
{
    private LocalDate date;
    private List<DietByMealDTO> meals;
    private NutritionSummaryDTO summary;
}

// NutritionSummaryDTO
{
    private int calories;
    private int protein;
    private int fat;
    private int carbohydrates;
}
```

### 3.4 영양 정보 관련 Entity
```java
// Nutrition
{
    private Long id;
    private String name;
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbohydrates;
    private String description;
}
```

## 4. 보안 및 인증

1. 세션 기반 인증 시스템
2. 모든 API는 세션 기반 인증을 사용
3. 인증이 필요한 API는 `@SessionAttribute("user")`를 통해 사용자 정보 확인
4. 비밀번호는 서버에서 해시 처리되어 저장

## 5. 에러 처리

| 상태 코드 | 설명 |
|:---|:---|
| 200 | 성공 |
| 400 | 잘못된 요청 (파라미터 오류, 유효성 검사 실패) |
| 401 | 인증 실패 (로그인 필요) |
| 404 | 리소스 없음 |
| 500 | 서버 내부 오류 |

## 6. 우선순위

### 6.1 최우선 (1순위)
- 사용자 인증 (로그인, 회원가입)
- 식단 관리 (CRUD)
- 메인 페이지

### 6.2 차순위 (2순위)
- 사용자 정보 관리
- 영양 정보 조회
- 회원 탈퇴

## 7. 기술 스택

### 7.1 백엔드
- Spring Boot
- MyBatis
- JWT (추후 도입 예정)
- Swagger (API 문서화)

### 7.2 데이터베이스
- MySQL
- Redis (세션 관리, 추후 도입 예정)