# Customer POC — 결함 삽입 정답셋

본 프로젝트는 PoC 시나리오 A (샘플 코드셋 기반 검증)에서 AI 점검 능력을 평가하기 위해 의도적으로 결함을 삽입한 테스트 프로젝트입니다.

## 프로젝트 개요

- **도메인**: 보험 고객(Customer) 관리 — 고객 CRUD / 동의(Consent) 이력 / KYC 인증 / 통계
- **언어/프레임워크**: Java 17 + Spring Boot 3.2 + JPA + H2 + **Union Framework 1.0** (`union-starter`)
- **구조**: Controller / Service / Repository / Domain / DTO / ErrorCode / Exception 계층 (`spec` / `impl` 패키지 분리)
- **프레임워크 위임**: 응답 래핑(`ApiResponse`), 전역 예외 처리, TraceId, JPA Auditing(`BaseEntity`), JWT 인증은 프레임워크 자동 설정이 담당
- **결함 수**: 총 25개 (의도적 삽입)

## 결함 정답셋

각 결함은 코드 내 주석으로 `[DEFECT-XXX]` 형태로 명시되어 있으며, AI 점검 결과와 대조 시 식별 가능합니다.

| ID | 위치 | 유형 | 심각도 | 설명 |
|---|---|---|---|---|
| DEFECT-001 | application.yml | 보안 | High | DB 접속 정보 및 JWT 서명 키 평문 하드코딩 |
| DEFECT-002 | application.yml | 운영 부적합 | Medium | show-sql 운영 환경 활성화 |
| DEFECT-003 | application.yml | 보안 | High | H2 콘솔 운영 환경 노출 |
| DEFECT-004 | application.yml | 개인정보 | High | SQL 바인딩 파라미터 TRACE 로깅 (주민번호 유출) |
| DEFECT-005 | CustomerRepository.searchByNameKeyword | 보안 | High | SQL Injection — 네이티브 쿼리 문자열 결합 |
| DEFECT-006 | CustomerRepository.findByPhoneTail | 성능 | Medium | 선행 와일드카드 LIKE — 인덱스 미사용 |
| DEFECT-007 | Customer (entity) | 개인정보 | High | 주민등록번호 평문 저장 |
| DEFECT-008 | PiiHelper | 보안 | High | 암호화 키 하드코딩 |
| DEFECT-009 | CustomerResponse | 개인정보 | Medium | 고객명 마스킹 누락 |
| DEFECT-010 | CustomerResponse | 개인정보 | High | 주민등록번호 원문 응답 노출 |
| DEFECT-011 | CustomerServiceImpl.createCustomer | 트랜잭션 | High | @Transactional 누락 |
| DEFECT-012 | CustomerServiceImpl.findByEmail | NPE | High | Optional.get() 직접 호출 |
| DEFECT-013 | CustomerServiceImpl.importCustomersFromCsv | 자원 누수 | Medium | try-with-resources 미사용 |
| DEFECT-014 | CustomerServiceImpl.calculateKoreanAge | 비즈니스 로직 | Medium | 만 나이 계산 오류 (생일 미경과 미반영) |
| DEFECT-015 | CustomerServiceImpl.BIRTH_DATE_FORMAT | 동시성 | High | SimpleDateFormat static 공유 (thread-unsafe) |
| DEFECT-016 | ConsentServiceImpl.registerConsent | 개인정보 | High | 주민등록번호 등 PII INFO 로깅 |
| DEFECT-017 | ConsentServiceImpl.withdrawConsent | 트랜잭션/동시성 | High | @Transactional 누락 + TOCTOU race |
| DEFECT-018 | ConsentServiceImpl.purgeConsents | 규제/감사 | High | 동의 이력 하드 삭제 (보관 의무 위반, 프레임워크 `SoftDeletableEntity` 미사용) |
| DEFECT-019 | KycServiceImpl.KYC_API_KEY | 보안 | High | 외부 KYC API 키 하드코딩 |
| DEFECT-020 | KycServiceImpl.restTemplate | 운영 부적합 | High | RestTemplate 타임아웃 미설정 (장애 전파) |
| DEFECT-021 | KycServiceImpl.verify | 예외 처리 | High | 외부 호출 예외를 빈 catch로 무시 |
| DEFECT-022 | CustomerStatisticsServiceImpl.averageAge | 성능 | Medium | 전체 조회 후 in-memory 평균 (OOM 위험) |
| DEFECT-023 | CustomerStatisticsServiceImpl.channelCountCache | 동시성 | High | HashMap 멀티스레드 사용 |
| DEFECT-024 | CustomerController.get | 보안/인가 | High | 고객 상세 조회 인가 누락 — 인증만 통과하면 타 고객 PII 조회 가능 (IDOR) |
| DEFECT-025 | CustomerController.updateContact | 입력 검증 | Medium | @Valid 누락 |

## 결함 유형 분포

| 유형 | 건수 | 비율 |
|---|---|---|
| 보안 (인증/인가/키 관리/SQLi) | 6 | 24.0% |
| 개인정보 (PII 노출·저장·로깅) | 5 | 20.0% |
| 트랜잭션·동시성 | 4 | 16.0% |
| 운영 부적합·예외 처리 | 4 | 16.0% |
| 비즈니스 로직·NPE·자원 누수 | 3 | 12.0% |
| 성능 | 2 | 8.0% |
| 입력 검증 | 1 | 4.0% |

## 심각도 분포

| 심각도 | 건수 |
|---|---|
| High | 17 |
| Medium | 8 |

## 평가 방식

PoC 시나리오 A 측정 시 다음 지표 산출 가능

- **탐지율 (Recall)** = AI 탐지 결함 수 / 25
- **정확도 (Precision)** = AI 탐지 중 실제 결함 / AI 탐지 총 건수
- **F1 Score** = 2 × (Precision × Recall) / (Precision + Recall)
- **유형별 탐지율**: 보안 / 개인정보 / 동시성 / 성능 등 카테고리별 분석

## 참고 사항

- 본 결함셋은 **초안**이며, 자료 요청서 회신 후 사내 컨벤션·코드마인드 룰셋에 맞춰 보완 예정
- 결함은 단일 파일에 1~5건씩 분산 배치하여 실제 코드 점검 환경에 근접
- 정상 코드(엔티티 도메인 로직, DTO, 예외 클래스, 마스킹 유틸 등)도 포함하여 오탐(False Positive) 측정 가능
- contract-poc 대비 **개인정보(PII) 관련 결함 비중**을 높여 금융사 고객정보 처리 컨텍스트를 강화

## 파일 구조

```
customer-poc/
├── pom.xml                                          (io.union:union-starter 의존)
├── README.md
├── src/main/
│   ├── java/com/unionplace/
│   │   ├── CustomerPocApplication.java              (정상)
│   │   ├── auth/                                    ── 운영자 로그인 (프레임워크 JWT 연동)
│   │   │   ├── spec/
│   │   │   │   ├── AuthService.java                 (정상)
│   │   │   │   ├── AuthErrorCode.java               (정상)
│   │   │   │   ├── LoginRequest.java                (정상)
│   │   │   │   └── OperatorProperties.java          (정상)
│   │   │   └── impl/
│   │   │       └── AuthServiceImpl.java             (정상)
│   │   ├── customer/management/
│   │   │   ├── spec/
│   │   │   │   ├── Customer.java                    (결함 7)
│   │   │   │   ├── CustomerStatus.java              (정상)
│   │   │   │   ├── CustomerTier.java                (정상)
│   │   │   │   ├── BlockReason.java                 (정상)
│   │   │   │   ├── CustomerRepository.java          (결함 5, 6)
│   │   │   │   ├── CustomerCreateRequest.java       (정상)
│   │   │   │   ├── CustomerUpdateRequest.java       (정상)
│   │   │   │   ├── CustomerSearchRequest.java       (정상)
│   │   │   │   ├── TierUpdateRequest.java           (정상)
│   │   │   │   ├── BlockRequest.java                (정상)
│   │   │   │   ├── CustomerResponse.java            (결함 9, 10)
│   │   │   │   ├── CustomerErrorCode.java           (정상)
│   │   │   │   ├── CustomerNotFoundException.java   (정상)
│   │   │   │   ├── CustomerService.java             (정상)
│   │   │   │   ├── CustomerTierService.java         (정상)
│   │   │   │   └── CustomerBlockService.java        (정상)
│   │   │   └── impl/
│   │   │       ├── PiiHelper.java                   (결함 8)
│   │   │       ├── CustomerServiceImpl.java         (결함 11, 12, 13, 14, 15)
│   │   │       ├── CustomerTierServiceImpl.java     (정상 — 컨벤션 위반 포함)
│   │   │       └── CustomerBlockServiceImpl.java    (정상 — 컨벤션 위반 포함)
│   │   ├── customer/consent/
│   │   │   ├── spec/
│   │   │   │   ├── Consent.java                     (정상)
│   │   │   │   ├── ConsentType.java                 (정상)
│   │   │   │   ├── ConsentRepository.java           (정상)
│   │   │   │   ├── ConsentRequest.java              (정상)
│   │   │   │   ├── ConsentResponse.java             (정상)
│   │   │   │   ├── ConsentSummaryResponse.java      (정상)
│   │   │   │   ├── ConsentErrorCode.java            (정상)
│   │   │   │   ├── ConsentNotFoundException.java    (정상)
│   │   │   │   └── ConsentService.java              (정상)
│   │   │   └── impl/
│   │   │       └── ConsentServiceImpl.java          (결함 16, 17, 18)
│   │   ├── customer/kyc/
│   │   │   ├── spec/
│   │   │   │   ├── KycVerifyRequest.java            (정상)
│   │   │   │   ├── KycResponse.java                 (정상)
│   │   │   │   ├── KycErrorCode.java                (정상)
│   │   │   │   ├── KycVerificationException.java    (정상)
│   │   │   │   └── KycService.java                  (정상)
│   │   │   └── impl/
│   │   │       └── KycServiceImpl.java              (결함 19, 20, 21)
│   │   ├── customer/statistics/
│   │   │   ├── spec/
│   │   │   │   └── CustomerStatisticsService.java   (정상)
│   │   │   └── impl/
│   │   │       └── CustomerStatisticsServiceImpl.java (결함 22, 23)
│   │   └── api/
│   │       ├── auth/AuthController.java             (정상)
│   │       └── customer/
│   │           ├── management/CustomerController.java      (결함 24, 25)
│   │           ├── management/CustomerTierController.java  (정상 — 컨벤션 위반 포함)
│   │           ├── management/CustomerBlockController.java (정상 — 컨벤션 위반 포함)
│   │           ├── consent/ConsentController.java   (정상)
│   │           └── kyc/KycController.java           (정상)
│   └── resources/
│       └── application.yml                          (결함 1, 2, 3, 4)
├── src/test/java/com/unionplace/
│   └── CustomerPocApplicationTests.java             (프레임워크 연동 스모크 테스트)
└── docs/
    └── DEFECTS.md                                   (본 문서)
```

총 **50개 Java 파일** + 설정/문서 4개 (pom.xml, application.yml, README.md, DEFECTS.md)

## 컨벤션 위반 (결함 정답셋과 별도)

`CustomerTierController`, `CustomerBlockController`, `CustomerTierServiceImpl`, `CustomerBlockServiceImpl` 에는 결함 정답셋과 별도로
사내 코딩 컨벤션(위키 `convention/`) 위반이 포함되어 있어, 컨벤션 기반 리뷰 능력 측정에 사용한다.

| 위치 | 위반 규칙 | 내용 |
|---|---|---|
| CustomerTierController, CustomerBlockController | JV-07 | `@Autowired` 필드 주입 |
| CustomerTierController.changeTier, CustomerBlockController.block | API-01 | URI 에 동사 사용 (`/changeTier`, `/blockNow`) |
| CustomerTierServiceImpl, CustomerBlockServiceImpl | LOG-02 | 문자열 연결(`+`)로 로그 메시지 구성 |
| CustomerTierServiceImpl, CustomerBlockServiceImpl | EX-05 | `Exception` 직접 catch |

## 정상 코드셋 — 오탐(False Positive) 측정용

`customer/memo` (고객 상담 메모) 모듈은 **의도적 결함을 포함하지 않는 정상 코드**로, AI 점검 도구의 오탐률 측정에 사용한다.
결함 정답셋과 대조 시 이 모듈에서 보고되는 지적은 원칙적으로 오탐으로 집계한다.

| 파일 | 비고 |
|---|---|
| `customer/memo/spec/CustomerMemo.java` | `SoftDeletableEntity` 상속 — 상담 이력 소프트 삭제 (FW-03) |
| `customer/memo/spec/MemoCategory.java` | 메모 분류 enum |
| `customer/memo/spec/CustomerMemoErrorCode.java` | 도메인 에러 코드 `MEMO_001 ~ MEMO_003` (EX-01) |
| `customer/memo/spec/CustomerMemoNotFoundException.java` | `EntityNotFoundException` 상속 (EX-03) |
| `customer/memo/spec/CustomerMemoRepository.java` | 파생 쿼리만 사용 — 문자열 결합 없음 |
| `customer/memo/spec/CustomerMemo{Create,Update}Request.java` | Bean Validation 적용 (API-06) |
| `customer/memo/spec/CustomerMemoResponse.java` | 엔티티 직접 노출 없음, PII 미포함 (JV-04) |
| `customer/memo/spec/CustomerMemoService.java` | 서비스 인터페이스 (`spec`) |
| `customer/memo/impl/CustomerMemoServiceImpl.java` | 조회 `@Transactional(readOnly = true)`, 변경 `@Transactional`, 로그에 식별자만 기록 (LOG-05) |
| `api/customer/memo/CustomerMemoController.java` | 명사형 URI + 표준 상태코드, 삭제는 `@PreAuthorize("hasRole('ADMIN')")` (API-01, API-03) |
| `src/test/java/com/unionplace/CustomerMemoApiTests.java` | 등록·조회·수정·삭제·권한·소유 검증 테스트 7건 |
