# SNMS

SNMS(Social Network Mini Service)는 SNS의 기본 흐름을 MSA 구조로 구현한 Java/Spring Boot 기반 API 프로젝트입니다.

회원 가입, 로그인, 프로필 수정, 팔로우, 게시글 작성, 이미지 업로드, FCM 알림 전송 기능을 다룹니다.

게시글 작성과 팔로우 이벤트는 Kafka로 발행하고, Notification Service에서 소비해 알림을 생성/전송하도록 분리했습니다.

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 3.2.4, Spring Web, Spring WebFlux, Spring Validation |
| Gateway | Spring Cloud Gateway |
| Security | Spring Security, OAuth2 Client, JWT |
| Persistence | Spring Data JPA, Hibernate, QueryDSL |
| Database | MySQL, H2 |
| Cache | Redis |
| Message Queue | Kafka, Spring Kafka |
| Internal API | Spring Cloud OpenFeign |
| Storage | AWS S3 |
| Notification | Firebase Cloud Messaging |
| Docs | Spring REST Docs, Asciidoctor |
| Infra | Docker Compose |
| Test | JUnit5, Spring REST Docs, MockMvc |

## 주요 기능

### 회원 가입 및 로그인

- 로컬 회원 가입과 로그인을 지원합니다.
- SMS 인증을 통해 핸드폰 번호를 검증합니다.
- Google, Kakao OAuth2 소셜 로그인을 지원합니다.
- 소셜 로그인 시 provider id만으로 회원을 생성하지 않고, 핸드폰 번호 인증을 통해 기존 회원과 소셜 계정을 연결합니다.
- 로그인 시 JWT를 발급하고 Redis에 Refresh Token을 저장합니다.

### 회원

- 닉네임, 자기소개, 프로필 이미지를 수정할 수 있습니다.
- 프로필 이미지는 S3에 업로드합니다.
- 사용자를 팔로우하거나 언팔로우할 수 있습니다.
- Redis를 사용해 반복적인 팔로우 요청을 제한합니다.

### 게시글

- 이미지와 함께 게시글을 작성할 수 있습니다.
- 게시글 작성자는 자신의 게시글을 조회할 수 있습니다.
- 공개 여부에 따라 다른 사용자의 게시글 접근을 제한합니다.

### 알림

- 팔로우 이벤트가 발생하면 대상 사용자에게 알림을 전송합니다.
- 게시글이 작성되면 작성자의 팔로워들에게 알림을 전송합니다.
- 클라이언트는 FCM 토큰을 등록하거나 삭제할 수 있습니다.
- 알림 전송 결과는 `PENDING`, `SENT`, `FAILED` 상태로 관리합니다.

## 프로젝트 구조

```text
snms
├── gateway
├── common
├── user-service
├── post-service
├── notification-service
├── docs
└── docker
```

## 모듈 구성

| 모듈 | 설명 |
| --- | --- |
| `gateway` | API Gateway, JWT 쿠키 검증, Authorization 헤더 전달 |
| `common` | 공통 Entity, 설정, 예외, JWT, Kafka Producer, Feign Client, S3 유틸 |
| `user-service` | 회원 가입, 로그인, OAuth2, SMS 인증, 회원 정보, 팔로우 |
| `post-service` | 게시글 작성, 게시글 조회, 이미지 업로드, 게시글 생성 이벤트 발행 |
| `notification-service` | Kafka 이벤트 소비, 알림 생성, FCM 토큰 관리, FCM 알림 전송 |
| `docs` | Spring REST Docs 기반 API 문서 |
| `docker` | MySQL, Redis, Kafka, Zookeeper 로컬 실행 환경 |

## 핵심 구현

### Kafka 기반 알림 처리

User Service와 Post Service에서 발생한 팔로우/게시글 작성 이벤트를 Kafka로 발행하고, Notification Service가 이를 소비해 알림을 생성합니다.

- `user-service`: 팔로우 이벤트 발행
- `post-service`: 게시글 작성 이벤트 발행
- `notification-service`: 이벤트 소비 후 알림 저장 및 FCM 전송

알림 전송을 각 도메인 서비스에서 직접 처리하지 않고 별도 서비스로 분리해, 알림 장애나 지연이 회원/게시글 핵심 기능에 영향을 주지 않도록 설계했습니다.

### 알림 멱등성 처리

Kafka Consumer는 같은 이벤트를 중복 소비할 수 있으므로, 이벤트마다 `eventId`를 포함합니다.

Notification Service는 Redis에 `eventId`를 저장하고, 이미 처리된 이벤트라면 알림 생성을 중단합니다.

이를 통해 Consumer 재시도나 중복 메시지 수신 상황에서도 같은 알림이 여러 번 생성되는 문제를 줄였습니다.

### DB 저장 후 비동기 Dispatcher 전송

Notification Service는 FCM을 바로 호출하지 않고, 알림을 먼저 DB에 `PENDING` 상태로 저장합니다.

트랜잭션 커밋 이후 `@TransactionalEventListener(AFTER_COMMIT)`를 통해 Dispatcher를 실행하고, FCM 전송 결과에 따라 `SENT` 또는 `FAILED` 상태로 변경합니다.

이 구조를 통해 알림 데이터 저장과 외부 FCM 호출을 분리하고, 실패한 알림을 이후 재처리할 수 있는 기반을 마련했습니다.

### OAuth2 통합 계정 처리

Google/Kakao 소셜 로그인 시 provider id 기준으로 회원을 바로 생성하지 않습니다.

핸드폰 번호 인증을 통해 기존 회원 여부를 먼저 확인하고, 기존 회원이면 `MemberProvider`를 추가해 소셜 계정을 연결합니다.

이를 통해 같은 사용자가 소셜 제공자별로 중복 가입되는 문제를 방지했습니다.
