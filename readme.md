<h1 align="center">오독오독 - Compose</h1>

<p align="center">
오독오독은 자신이 읽었던 책, 읽고 있는 책, 앞으로 읽을 책을 자신의 서재에 보관하고 관리하는 모바일 앱 입니다.
</p>

> [!TIP]
> 오독오독 프로젝트는 연구개발의 목적으로 [Kotlin & XML] / [Kotlin & Compose] 버전으로 각각 개발되었습니다.
> 
> XML 버전은 [오독오독 - XML](https://github.com/stevey-sy/bookchibakchi) 에서 확인 가능합니다.

<p align="center">
<img src="/previews/intro.png"/>
</p>

<img src="/previews/preview.gif" align="right" width="240"/>

## Download
**오독오독** 의 데모버전을 받아보실 수 있습니다. [Releases](https://github.com/stevey-sy/odok-compose/releases/tag/1.0.0)

<h3>Tech stack</h3>

- Minimum SDK level 24.
- [Kotlin](https://kotlinlang.org/) / [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
- Jetpack Libraries:
  - Jetpack Compose
  - Lifecycle
  - ViewModel
  - Navigation
  - Room
  - [Hilt](https://dagger.dev/hilt/)
- Architecture:
  - **MVVM Architecture** (View - ViewModel - Model)
  - **Repository Pattern**
  - **Multi-Module Architecture**

<h3>Open-source libraries</h3>  

- [Material-Components](https://github.com/material-components/material-components-android): 구글의 공식 디자인 시스템을 기반으로 모던하고 일관된 UI 컴포넌트를 구현하기 위해 사용했습니다.
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit): REST API 통신을 효율적으로 처리하고, 네트워크 요청의 로깅 및 커스터마이징을 위해 사용했습니다.
- [moshi](https://github.com/kyutai-labs/moshi): JSON을 Kotlin/Java 객체로 변환해주는 직렬화 라이브러리입니다.
- [Coil](https://github.com/coil-kt/coil): 이미지의 비동기 로딩, 캐싱, 리사이징 등을 최적화하여 UI 성능을 향상시키기 위해 활용했습니다.
- [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization): Kotlin 공식 직렬화 라이브러리로, 다양한 포맷을 지원합니다.
- [ksp](https://github.com/google/ksp): Kotlin 코드 생성을 위한 심볼 프로세싱 API입니다.
- [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview?hl=ko): 대용량 데이터를 페이징 처리하며 효율적으로 로딩할 수 있도록 도와줍니다.
- [Turbine](https://github.com/cashapp/turbine): Flow 테스트를 간편하게 작성할 수 있는 경량 테스트 유틸리티입니다.
- [Lottie](https://github.com/airbnb/lottie-android)>: 애니메이션을 앱에서 부드럽게 재생할 수 있도록 해주는 라이브러리입니다.

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