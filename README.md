# Cookware 프로젝트

## 개요

Cookware 프로젝트는 재사용 가능한 알고리즘과 일반적인 패턴을 제공하는 라이브러리입니다. 이 프로젝트는 Gradle 또는 Maven과 같은 빌드 도구와 함께 사용하도록 고안되었습니다.

## 목적

Cookware 프로젝트의 주요 목적은 다음과 같습니다:

* 자주 사용되는 알고리즘 및 디자인 패턴을 재사용 가능한 형태로 제공하여 개발 생산성을 향상시킵니다.
* 공통적인 문제에 대한 표준화된 해결책을 제시하여 코드의 일관성과 유지보수성을 높입니다.
* 핵심 로직에 집중할 수 있도록 유틸리티 및 확장 기능을 제공합니다.

## 사용 방법

Cookware 프로젝트를 사용하려면 Gradle 또는 Maven과 같은 빌드 도구를 사용하여 라이브러리를 프로젝트에 추가하십시오.

**Gradle:**

```gradle
dependencies {
  implementation 'io.foreshore:cookware:{version}'
}
```

**Maven:**

```xml
<dependency>
  <groupId>io.foreshore</groupId>
  <artifactId>cookware</artifactId>
  <version>{version}</version>
</dependency>
```

`{version}`을 최신 Cookware 버전으로 교체하십시오.

## 주요 모듈

Cookware 프로젝트는 다음과 같은 주요 모듈로 구성됩니다:

*   **`io.foreshore.cookware.annotation`**: 사용자 정의 어노테이션을 제공합니다. 이 모듈은 코드에 메타데이터를 추가하고 컴파일 타임 또는 런타임에 특정 동작을 지시하는 데 사용됩니다.
*   **`io.foreshore.cookware.lang`**: 핵심 언어 확장, 사용자 정의 예외 및 기본 열거형 기능을 제공합니다. Java 표준 라이브러리의 기능을 보완하고 확장하여 더 풍부한 프로그래밍 환경을 지원합니다.
*   **`io.foreshore.cookware.support`**: Hibernate 및 로깅과 같은 외부 라이브러리 또는 특정 기능에 대한 지원을 제공하는 모듈입니다. 타사 라이브러리와의 통합을 단순화하고 특정 작업에 대한 유틸리티를 제공합니다.
*   **`io.foreshore.cookware.time`**: 시간 관련 유틸리티 클래스를 제공합니다. 날짜 및 시간 조작, 형식 지정 및 구문 분석을 위한 편리한 방법을 제공합니다.
*   **`io.foreshore.cookware.util`**: 키 생성기와 같은 기타 유틸리티 클래스를 제공합니다. 애플리케이션 개발에 유용한 다양한 보조 기능을 포함합니다.
*   **`io.foreshore.cookware.crypto`**: 다양한 암호화 기능을 제공합니다. 해싱(SHA-256, SHA-512, MD5), 대칭키 암호화(AES), 공개키 암호화/서명(RSA) 등을 포함하여 데이터를 안전하게 처리할 수 있는 유틸리티를 지원합니다.

## 기여

Cookware 프로젝트에 기여하고 싶다면 GitHub 리포지토리에서 이슈를 확인하거나 풀 리퀘스트를 보내주십시오.

## 라이선스

Cookware 프로젝트는 [Apache License 2.0](LICENSE)에 따라 라이선스가 부여됩니다.
