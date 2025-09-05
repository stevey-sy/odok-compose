# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

오독오독 (Odok-Odok) is a reading diary Android application built with Jetpack Compose. It allows users to manage their book collection, record reading progress, and create memos about their readings.

## Build & Development Commands

### Basic Gradle Commands
```bash
# Build the app (debug)
./gradlew assembleDebug

# Build the app (release)
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Lint check
./gradlew lint

# Check for dependency updates
./gradlew dependencyUpdates
```

### Running the App
- Use Android Studio's Run/Debug configuration
- Or install via: `./gradlew installDebug`

## Architecture Overview

The project follows **Clean Multi-Module Architecture** with these key principles:

### Architecture Layers
1. **UI Layer** - Jetpack Compose screens and ViewModels
2. **Domain Layer** - Business logic through Use Cases
3. **Data Layer** - Repositories and DataSources (local Room database, network via Retrofit)

### Module Structure
```
app/                    # Main application module
├── core/
│   ├── data/          # Repository implementations
│   ├── database/      # Room database entities & DAOs  
│   ├── designsystem/  # UI components & theme
│   ├── domain/        # Use cases (business logic)
│   ├── model/         # UI data models
│   ├── network/       # Retrofit API services
│   └── ui/            # Shared UI utilities
└── feature/
    ├── login/         # Authentication screens
    ├── mylibrary/     # Book shelf & book details
    ├── memo/          # Memo creation/editing
    ├── profile/       # User profile
    ├── search/        # Book search via Aladin API
    └── timer/         # Reading timer functionality
```

### Key Architectural Decisions
- **Single Activity Architecture** with Navigation Compose
- **MVVM pattern** (ViewModel + UI State)
- **Repository pattern** for data access abstraction
- **Dependency injection** with Hilt
- **SharedTransitionLayout** for smooth animations between screens

## Data Models & Relationships

### Core Entities (Room Database)
- **BookEntity** ↔ **MemoEntity**: 1:N relationship
- **MemoEntity** ↔ **TagEntity**: N:M relationship (via MemoTagCrossRef join table)

### Database Versioning
- Current version: 2
- Migration from v1 to v2 adds `finishedReadCnt` column to books table

## Key Technologies

- **UI**: Jetpack Compose, Material3
- **Architecture**: MVVM, Navigation Compose, Hilt DI
- **Database**: Room with SQLite
- **Network**: Retrofit2 + OkHttp3, Moshi for JSON
- **Images**: Coil for async image loading
- **Animations**: Lottie for vector animations
- **External API**: Aladin OpenAPI for book search
- **Authentication**: Firebase Auth with Google Sign-In
- **Paging**: Paging 3 for efficient data loading

## Navigation Structure

Navigation uses type-safe routes defined in each feature module:
- `MY_LIBRARY_ROUTE` - Main screen (book shelf)
- `SEARCH_ROUTE` - Book search
- `BOOK_DETAIL_ROUTE` - Individual book details
- `PROFILE_ROUTE` - User profile
- Timer, memo, and other screens are accessed through navigation actions

## Development Guidelines

### Module Dependencies
- Feature modules depend only on core modules
- Core modules have no dependencies on features
- Follow unidirectional dependency flow: UI → Domain → Data

### Code Conventions
- Use Hilt for dependency injection across all modules
- Follow repository pattern for data access
- Implement Use Cases for business logic
- Use sealed classes for UI state management
- Navigation functions are prefixed with `navigateTo`

### Testing
- Unit tests: `./gradlew test`
- Instrumented tests: `./gradlew connectedAndroidTest`  
- Each module has its own test directory structure

### Resource Management
- Design system resources are centralized in `core:designsystem`
- Custom fonts: Maruburi family, Dashi
- Color scheme follows Material3 guidelines
- Images and animations stored in appropriate feature modules