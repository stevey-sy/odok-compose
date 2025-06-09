<h1 align="center">오독오독 - Compose</h1>

<p align="center">
오독오독은 자신이 읽었던 책, 읽고 있는 책, 앞으로 읽을 책을 자신의 서재에 보관하고 관리하는 모바일 앱 입니다.
</p>

> [!TIP]
> 오독오독 프로젝트는 연구개발의 목적으로 [Kotlin & XML] / [Kotlin & Compose] 버전으로 각각 개발되었습니다. 
> XML 버전은 [오독오독 - XML](https://github.com/stevey-sy/bookchibakchi) 에서 확인 가능합니다.


<h3>Tech stack</h3>

- [Kotlin](https://kotlinlang.org/)
- [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
- [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
- Jetpack
    - Lifecycle
    - ViewModel
    - DataBinding
    - Room
    - [Hilt](https://dagger.dev/hilt/)
- Architecture
    - MVVM Architecture (View - DataBinding - ViewModel - Model)
    - Repository Pattern

<h3>Open-source libraries</h3>

- [Material-Components](https://github.com/material-components/material-components-android): 구글의 공식 디자인 시스템을 기반으로 모던하고 일관된 UI 컴포넌트를 구현하기 위해 사용했습니다.
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit): REST API 통신을 효율적으로 처리하고, 네트워크 요청의 로깅 및 커스터마이징을 위해 사용했습니다.
- [Glide](https://github.com/bumptech/glide): 이미지의 비동기 로딩, 캐싱, 리사이징 등을 최적화하여 UI 성능을 향상시키기 위해 활용했습니다.
- [Dot indicator](https://github.com/tommybuonomo/dotsindicator): ViewPager2와 함께 사용하여 페이지 위치를 직관적으로 보여주는 점 기반 인디케이터를 제공했습니다.
- [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview?hl=ko): 도서 검색 시 출력할 데이터를 효율적으로 로드하고 UI에서 매끄럽게 표시하기 위해 Paging 아키텍처를 적용했습니다.
- [Shimmer](https://github.com/facebookarchive/shimmer-android): 로딩 상태를 시각적으로 표현하기 위해 Shimmer 효과를 적용하여 사용자 경험을 개선하고, 최신 UI 트렌드를 반영한 모던한 디자인을 구현했습니다.
- [Google ML Kit](https://developers.google.com/ml-kit/vision/text-recognition/v2/android?hl=ko): 책에서 읽었던 텍스트를 보다 쉽게 추출하기 위하여 OCR 기능을 구현할 때 사용했습니다.
- [Lottie](https://github.com/airbnb/lottie-android): JSON 기반의 벡터 애니메이션을 간편하게 재생하여 인터랙티브한 UI를 구성했습니다.
- [sdp](https://github.com/intuit/sdp): 다양한 해상도에서 일관된 UI 크기를 유지하기 위해 dp 단위의 자동 스케일링을 적용했습니다.
- [ImageCropper](https://github.com/Yalantis/uCrop): 사용자가 선택한 책의 페이지 이미지에서 텍스트 부분을 자를 수 있는 이미지 편집 기능을 제공했습니다.

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