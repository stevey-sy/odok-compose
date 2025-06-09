<h1 align="center">오독오독 - Compose</h1>

<p align="center">
오독오독은 자신이 읽었던 책, 읽고 있는 책, 앞으로 읽을 책을 자신의 서재에 보관하고 관리하는 모바일 앱 입니다.
</p>

> [!TIP]
> 오독오독 프로젝트는 연구개발의 목적으로 [Kotlin & XML] / [Kotlin & Compose] 버전으로 각각 개발되었습니다. 
> XML 버전은 [오독오독 - XML](https://github.com/stevey-sy/bookchibakchi) 에서 확인 가능합니다.

<h3>🛠 Tech Stack</h3>
- 최소 SDK 버전: 24
- [Kotlin](https://kotlinlang.org/) 기반으로 개발되었으며, 비동기 처리를 위해 [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) 를 활용하였습니다.
- Jetpack 라이브러리:
  - Jetpack Compose: 선언형 UI를 위한 안드로이드의 최신 툴킷입니다.
  - Lifecycle: 안드로이드 생명주기를 관찰하고 UI 상태를 자동으로 관리합니다.
  - ViewModel: UI 관련 데이터를 관리하며 생명주기를 인식하여 구성 변경에도 데이터를 유지합니다.
  - Navigation: 화면 간의 이동을 돕는 라이브러리로, 의존성 주입을 위해 Hilt Navigation Compose와 함께 사용됩니다.
  - Room: SQLite를 추상화하여 간편하게 데이터베이스를 구축하고 접근할 수 있도록 도와줍니다.
  - [Hilt](https://dagger.dev/hilt/): 의존성 주입을 간편하게 구성할 수 있도록 지원하는 라이브러리입니다.

- Architecture:
  - **MVVM 아키텍처** (View - ViewModel - Model): 관심사의 분리를 통해 유지보수성과 확장성을 높입니다.
  - **Repository 패턴**: 다양한 데이터 소스와 비즈니스 로직 사이의 중간 계층 역할을 수행합니다.
  - **멀티 모듈 구조(Multi-Module Architecture)**: 기능별로 모듈을 분리하여 빌드 효율성, 의존성 관리, 코드 재사용성, 테스트 용이성을 극대화하였습니다.

- Retrofit2 & OkHttp3: REST API 통신을 구성하며, 네트워크 데이터 페이징 처리에도 활용됩니다.
- Kotlin Serialization: 멀티플랫폼 및 다양한 포맷을 지원하는 무반사 기반 직렬화 라이브러리입니다.
- ksp: 코드 생성 및 분석을 위한 Kotlin 심볼 프로세싱 API입니다.
- Turbine: kotlinx.coroutines Flow를 테스트할 수 있도록 도와주는 경량 테스트 도구입니다.

<h3>Tech stack</h3>

- Minimum SDK level 24.
- [Kotlin](https://kotlinlang.org/) 기반으로 개발되었으며, 비동기 처리를 위해 [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) 를 활용하였습니다.
- Jetpack Libraries:
  - Jetpack Compose: Android’s modern toolkit for declarative UI development.
  - Lifecycle: Observes Android lifecycles and manages UI states upon lifecycle changes.
  - ViewModel: Manages UI-related data and is lifecycle-aware, ensuring data survival through configuration changes.
  - Navigation: Facilitates screen navigation, complemented by [Hilt Navigation Compose](https://developer.android.com/jetpack/compose/libraries#hilt) for dependency injection.
  - Room: Constructs a database with an SQLite abstraction layer for seamless database access.
  - [Hilt](https://dagger.dev/hilt/): Facilitates dependency injection.
- Architecture:
  - MVVM Architecture (View - ViewModel - Model): Facilitates separation of concerns and promotes maintainability.
  - Repository Pattern: Acts as a mediator between different data sources and the application's business logic.
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit): Constructs REST APIs and facilitates paging network data retrieval.
- [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization): Kotlin multiplatform / multi-format reflectionless serialization.
- [ksp](https://github.com/google/ksp): Kotlin Symbol Processing API for code generation and analysis.
- [Turbine](https://github.com/cashapp/turbine): A small testing library for kotlinx.coroutines Flow.

<h3>Open-source libraries</h3>

- [Material-Components](https://github.com/material-components/material-components-android): 구글의 공식 디자인 시스템을 기반으로 모던하고 일관된 UI 컴포넌트를 구현하기 위해 사용했습니다.
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit): REST API 통신을 효율적으로 처리하고, 네트워크 요청의 로깅 및 커스터마이징을 위해 사용했습니다.
- [Coil](https://github.com/coil-kt/coil): 이미지의 비동기 로딩, 캐싱, 리사이징 등을 최적화하여 UI 성능을 향상시키기 위해 활용했습니다.
- [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview?hl=ko): 도서 검색 시 출력할 데이터를 효율적으로 로드하고 UI에서 매끄럽게 표시하기 위해 Paging 아키텍처를 적용했습니다.
- [Lottie](https://github.com/airbnb/lottie-android): JSON 기반의 벡터 애니메이션을 간편하게 재생하여 인터랙티브한 UI를 구성했습니다.

<h3>Open API</h3>

**오독오독** 은 도서 정보 검색 기능을 위해 [알라딘 OpenAPI](https://blog.aladin.co.kr/openapi)를 사용하고 있습니다.

## License
```xml
Designed and developed by 2025 stevey-sy (Seyoung Shin)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```