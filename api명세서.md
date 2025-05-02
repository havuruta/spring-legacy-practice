각 컨트롤러의 상세 정보를 더 자세히 정리하겠습니다.

| 컨트롤러                | 기능     | 상세 내용       | URL               | HTTP Method | 요청 파라미터/바디                                                                                           | 응답                            | HTTP 상태 코드                  | 우선순위 |
|---------------------|--------|-------------|-------------------|-------------|------------------------------------------------------------------------------------------------------|-------------------------------|-----------------------------|------|
| MainController      | 메인 페이지 | 메인 페이지 렌더링  | /                 | GET         | -                                                                                                    | index.jsp                     | 200                         | 1    |
| AuthController      | 인증 관련  | 로그인         | /auth/login       | POST        | LoginRequest { username, password }                                                                  | User 객체                       | 200(성공), 401(실패)            | 1    |
|                     |        | 회원가입        | /auth/register    | POST        | RegisterRequest { username, password, passwordConfirm }                                              | String (username)             | 200(성공), 400(실패)            | 1    |
|                     |        | 로그아웃        | /auth/logout      | GET         | -                                                                                                    | -                             | 200(성공), 401(실패)            | 1    |
|                     |        | 회원탈퇴        | /auth/delete      | POST        | @SessionAttribute("user")                                                                            | -                             | 200                         | 2    |
| UserController      | 사용자 정보 | 마이페이지 조회    | /user/info        | GET         | @SessionAttribute("user")                                                                            | UserDTO { id, username, ... } | 200                         | 2    |
|                     |        | 사용자 정보 수정   | /user/info        | PUT         | UserDTO { id, username, ... }                                                                        | UserDTO                       | 200(성공), 400(실패)            | 2    |
| DietController      | 식단 관리  | 식단 목록 조회    | /diet             | GET         | @RequestParam start, end (yyyy-MM-dd)                                                                | List<DietByDateDTO>           | 200(성공), 401(인증실패)          | 1    |
|                     |        | 식단 추가       | /diet             | POST        | DietRequestDTO { dietDate, mealType, foodName, amount, calories, protein, fat, carbohydrates, note } | String ("식단이 추가되었습니다.")       | 200(성공), 401(인증실패)          | 1    |
|                     |        | 식단 수정       | /diet/{id}        | PUT         | DietUpdateRequestDTO                                                                                 | String ("식단이 수정되었습니다.")       | 200(성공), 400(실패), 401(인증실패) | 1    |
|                     |        | 식단 삭제       | /diet/{id}        | DELETE      | @PathVariable id                                                                                     | String ("식단이 삭제되었습니다.")       | 200(성공), 400(실패), 401(인증실패) | 1    |
| NutritionController | 영양 정보  | 영양 정보 목록 조회 | /nutrition        | GET         | -                                                                                                    | List<Nutrition>               | 200                         | 2    |
|                     |        | 영양 정보 상세 조회 | /nutrition/{id}   | GET         | @PathVariable id                                                                                     | Nutrition                     | 200(성공), 404(없음)            | 2    |
|                     |        | 영양 정보 검색    | /nutrition/search | GET         | @RequestParam(required=false) description, name                                                      | List<Nutrition>               | 200                         | 2    |

DTO 상세 정보:

1. Auth 관련 DTO:
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

2. User 관련 DTO:
```java
// UserDTO
{
    private String id;
    private String username;
    // 기타 사용자 정보 필드
}
```

3. Diet 관련 DTO:
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

4. Nutrition 관련 Entity:
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

보안 및 인증 관련:
1. 모든 API는 세션 기반 인증을 사용
2. 인증이 필요한 API는 `@SessionAttribute("user")`를 통해 사용자 정보를 확인
3. 인증 실패 시 401 상태 코드 반환
4. 비밀번호는 서버에서 해시 처리되어 저장됨

에러 처리:
1. 400: 잘못된 요청 (파라미터 오류, 유효성 검사 실패)
2. 401: 인증 실패 (로그인 필요)
3. 404: 리소스 없음
4. 500: 서버 내부 오류

우선순위 설명:
1. 최우선 (1순위):
    - 사용자 인증 (로그인, 회원가입)
    - 식단 관리 (CRUD)
    - 메인 페이지
2. 차순위 (2순위):
    - 사용자 정보 관리
    - 영양 정보 조회
    - 회원 탈퇴

각 컨트롤러의 특징:
1. AuthController:
    - 세션 기반 인증 처리
    - 로그인/로그아웃 상태 관리
    - 회원가입 시 비밀번호 확인 검증

2. UserController:
    - 사용자 정보 조회/수정
    - 세션 기반 사용자 식별

3. DietController:
    - 날짜별 식단 관리
    - 영양 정보 요약 제공
    - 식사 유형별 구분

4. NutritionController:
    - 영양 정보 검색 기능
    - 상세 영양 정보 제공
    - 설명/이름 기반 검색

5. MainController:
    - 기본 페이지 제공
    - JSP 기반 뷰 렌더링
