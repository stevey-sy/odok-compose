<h1 align="center">오독오독 - Compose</h1>

<p align="center">
오독오독은 읽었던 책, 읽고 있는 책, 앞으로 읽을 책을<br>
자신의 서재에 보관하고 관리할 수 있는 [독서 기록 다이어리 App] 입니다.
</p>

> [!TIP]
> 오독오독 프로젝트는 연구개발의 목적으로 [Kotlin & XML] / [Kotlin & Compose] 버전으로 각각 개발되었습니다.
> 
> XML 버전은 [오독오독 - XML](https://github.com/stevey-sy/bookchibakchi) 에서 확인 가능합니다.

<p align="center">
<img src="/previews/intro.png"/>
</p>

<img src="/previews/preview.gif" align="right" width="240"/>

<h3>Download</h3>

**오독오독** 의 핵심 기능을 직접 확인하실 수 있도록 DEMO 버전을 제공하고 있습니다.<br>
- [APK 다운로드](https://github.com/stevey-sy/odok-compose/releases/tag/1.0.0)

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
  - **Single Activity Architecture**

<h3>Open-source libraries</h3>  

- [Material-Components](https://github.com/material-components/material-components-android): 구글의 공식 디자인 시스템을 기반으로 모던하고 일관된 UI 컴포넌트를 구현하기 위해 사용했습니다.
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit): REST API 통신을 효율적으로 처리하고, 네트워크 요청의 로깅 및 커스터마이징을 위해 사용했습니다.
- [moshi](https://github.com/kyutai-labs/moshi): JSON을 Kotlin/Java 객체로 변환해주는 직렬화 라이브러리입니다.
- [Coil](https://github.com/coil-kt/coil): 이미지의 비동기 로딩, 캐싱, 리사이징 등을 최적화하여 UI 성능을 향상시키기 위해 활용했습니다.
- [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization): Kotlin 공식 직렬화 라이브러리로, 다양한 포맷을 지원합니다.
- [ksp](https://github.com/google/ksp): Kotlin 코드 생성을 위한 심볼 프로세싱 API입니다.
- [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview?hl=ko): 대용량 데이터를 페이징 처리하며 효율적으로 로딩할 수 있도록 도와줍니다.
- [Turbine](https://github.com/cashapp/turbine): Flow 테스트를 간편하게 작성할 수 있는 경량 테스트 유틸리티입니다.
- [Lottie](https://github.com/airbnb/lottie-android): 애니메이션을 앱에서 부드럽게 재생할 수 있도록 해주는 라이브러리입니다.

<h3>Open API</h3>

**오독오독** 은 도서 정보 검색 기능을 위해 [알라딘 OpenAPI](https://blog.aladin.co.kr/openapi)를 사용하고 있습니다.

## 📘 데이터 관계 모델
- 애플리케이션의 핵심 데이터 모델은 다음과 같은 관계형 구조로 설계되었습니다

- Book ↔ Memo : 1:N (일대다 관계)
  하나의 책(Book)은 여러 개의 메모(Memo)를 가질 수 있도록 설계되어, 사용자별 독서 기록을 구조적으로 관리할 수 있습니다.

- Memo ↔ Tag : N:M (다대다 관계)
  하나의 메모는 여러 태그(Tag)를 가질 수 있고, 하나의 태그는 여러 메모에 중복될 수 있도록 설계되어, 태그 기반 분류 및 필터링 기능을 유연하게 지원합니다.
  이 관계는 Room에서 중간 조인 테이블(MemoTagCrossRef)을 통해 구현되었습니다.

## 주요 기능
<h3>도서 검색 기능</h3>
<table>
  <tr>
   <td valign="top" width="600">
       - 검색어를 입력하여 책을 검색할 수 있음<br>
       - 도서 정보 출력을 위해  알라딘 OPEN API 를 사용<br>
       - 대량의 도서 데이터를 효율적으로 불러오기 위해 Paging 라이브러리를 도입<br>
       - 사용자의 스크롤에 따라 데이터를 점진적으로 로드<br>
       - ListAdapter를 적용, DiffUtil을 활용한 데이터 변경 감지로 불필요한 View 바인딩을 줄임<br>
       - 검색된 책의 상세 정보를 확인하고 저장 가능.<br>
       <br><br><br>
    </td>
    <td>
      <img src="previews/add_book.gif" width="240"/>
    </td>
  </tr>
</table>

<h3>Filter 기능</h3>
<table>
  <tr>
   <td valign="top" width="600">
       - 검색어를 입력하여 책을 검색할 수 있음<br>
       - 도서 정보 출력을 위해  알라딘 OPEN API 를 사용<br>
       - 대량의 도서 데이터를 효율적으로 불러오기 위해 Paging 라이브러리를 도입<br>
       - 사용자의 스크롤에 따라 데이터를 점진적으로 로드<br>
       - ListAdapter를 적용, DiffUtil을 활용한 데이터 변경 감지로 불필요한 View 바인딩을 줄임<br>
       - 검색된 책의 상세 정보를 확인하고 저장 가능.<br>
       <br><br><br>
    </td>
    <td>
      <img src="previews/filer_shelf.gif" width="240"/>
    </td>
  </tr>
</table>

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