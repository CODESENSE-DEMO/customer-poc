# Customer POC — AI 코드 점검 검증용 테스트 프로젝트

PoC 시나리오 A (샘플 코드셋 기반 검증)에서 AI 코드 점검 능력을 평가하기 위한 테스트 프로젝트입니다.
사내 표준 프레임워크인 **Union Framework**(`io.union:union-starter`) 위에 구축된 실제 서비스 코드와 유사한 형태를 갖추고 있습니다.

## 용도

- AI 점검 도구의 결함 탐지 능력 측정
- 정상 코드에 대한 오탐(False Positive) 측정
- 결함 유형별 탐지 정확도 분석
- 사내 코딩 컨벤션 / 프레임워크 사용 규칙 위반 탐지 측정

## 구성

- Java 17 + Spring Boot 3.2 + **Union Framework 1.0** 기반 보험 고객(Customer) 관리 / 동의(Consent) / KYC / 통계
- 의도적으로 삽입된 25개 결함 + 정상 코드
- 오탐(False Positive) 측정용 정상 코드 모듈: 고객 상담 메모(`customer/memo`, `/api/customers/{customerNo}/memos`)
- Controller / Service / Repository / Domain / DTO / ErrorCode / Exception 계층 (`spec` / `impl` 패키지 분리)

### 프레임워크가 담당하는 것

| 영역 | 프레임워크 구성요소 | 애플리케이션에서 하는 일 |
|------|--------------------|--------------------------|
| 응답 포맷 | `ApiResponse` 자동 래핑 | 컨트롤러는 DTO 를 그대로 반환 |
| 예외 처리 | `GlobalExceptionHandler`, `ErrorCode` | 도메인별 `XxxErrorCode` enum + `BusinessException`/`EntityNotFoundException` |
| 요청 추적 | `TraceIdFilter`, `RequestLoggingFilter` | 없음 (로그 패턴에 `%X{traceId}` 만 추가) |
| 영속성 | `BaseEntity`(생성/수정 일시·작성자 자동 기록) | 엔티티가 `io.union.data.entity.BaseEntity` 상속 |
| 인증/인가 | `JwtTokenProvider`, `JwtAuthenticationFilter`, `@CurrentUser`, `@PreAuthorize` | `/api/auth/login` 에서 토큰 발급 |

## 결함 정답셋

`docs/DEFECTS.md` 참고

## 빌드 & 실행

```bash
# 1. 프레임워크를 로컬 저장소에 설치 (최초 1회)
cd ../union-framework && mvn -q -DskipTests install

# 2. PoC 빌드 & 테스트 (JDK 17)
cd ../customer-poc && mvn clean package

# 3. 실행
java -jar target/customer-poc-0.0.1-SNAPSHOT.jar
```

### 운영자 계정 (application.yml `app.auth.operators`)

| 아이디 | 비밀번호 | 권한 |
|--------|----------|------|
| admin | admin1234 | ADMIN, OPERATOR |
| operator | operator1234 | OPERATOR |

### 호출 예시

```bash
# 로그인 → accessToken / refreshToken
curl -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin1234"}'

# 고객 등록 (201 + ApiResponse 래핑 + X-Trace-Id 헤더)
curl -X POST localhost:8080/api/customers -H 'Content-Type: application/json' \
  -H "Authorization: Bearer <accessToken>" \
  -d '{"customerNo":"C-1001","name":"홍길동","residentRegistrationNumber":"900101-1234567","email":"hong@example.com","phoneNumber":"010-1234-5678","birthDate":"1990-01-01"}'

# 없는 고객 → 404 + CUSTOMER_001
curl localhost:8080/api/customers/9999 -H "Authorization: Bearer <accessToken>"
```

응답 포맷:

```json
{
  "success": false,
  "code": "CUSTOMER_001",
  "message": "고객을 찾을 수 없습니다. id=9999",
  "traceId": "7ba45c502cb94bdb",
  "timestamp": "2026-09-22T09:47:34.306"
}
```

> ※ 본 프로젝트는 측정용이며 실제 운영을 목적으로 하지 않습니다.
> ※ 결함 정답셋은 초안이며 자료 요청서 회신 후 보완 예정입니다.
