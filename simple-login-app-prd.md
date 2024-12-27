# Product Requirements Document: Android Login App

**Project Name**: Simple Login App  
**Document Version**: 1.0  
**Date**: 2024-12-27  

## 1. Overview

This document outlines the requirements for implementing user authentication features in an Android native application. The app will communicate with a backend server to handle user registration, login, profile viewing, and logout functionality.

## 2. Current Project Structure

```
dwcaptchademo/
├── app
│   ├── build
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src
├── build.gradle.kts
├── gradle
│   ├── libs.versions.toml
│   └── wrapper
├── gradle.properties
├── gradlew
├── gradlew.bat
├── local.properties
└── settings.gradle.kts
```

## 3. Technical Specifications

### 3.1 Backend API Endpoints

**Base URL**: `http://10.0.2.2:3001`

#### Register
- **Endpoint**: `/auth/register`
- **Method**: POST
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "email": "test@example.com",
    "password": "password123",
    "name": "Test User"
}
```
- **Success Response**: 200 OK

#### Login
- **Endpoint**: `/auth/login`
- **Method**: POST
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
    "email": "test@example.com",
    "password": "password123"
}
```
- **Success Response**:
```json
{
    "access_token": "jwt.token.here"
}
```

#### Get Profile
- **Endpoint**: `/auth/profile`
- **Method**: GET
- **Headers**: 
  - `Content-Type: application/json`
  - `Authorization: Bearer {jwt_token}`
- **Success Response**:
```json
{
    "id": 1,
    "email": "test@example.com",
    "name": "Test User"
}
```

## 4. Functional Requirements

### 4.1 Activities/Screens
1. **Login Screen** (`LoginActivity`)
   - Email input field
   - Password input field
   - Login button
   - Register button (navigates to Register screen)
   - Error message display area

2. **Register Screen** (`RegisterActivity`)
   - Name input field
   - Email input field
   - Password input field
   - Register button
   - Back to Login button
   - Error message display area

3. **Profile Screen** (`ProfileActivity`)
   - Display user name
   - Display user email
   - Logout button
   - Error message display area

### 4.2 Data Management
1. **Token Storage**
   - Store JWT token securely using `EncryptedSharedPreferences`
   - Clear token on logout

2. **User Session**
   - Maintain user session using stored JWT token
   - Auto-redirect to Login if token is invalid/expired

## 5. Implementation Requirements

### 5.1 Dependencies
Add to `app/build.gradle.kts`:
```kotlin
dependencies {
    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    
    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
}
```

### 5.2 Package Structure
```
app/src/main/java/com/example/dwcaptchademo/
├── api/
│   ├── AuthApi.kt
│   └── RetrofitClient.kt
├── models/
│   ├── LoginRequest.kt
│   ├── RegisterRequest.kt
│   ├── LoginResponse.kt
│   └── UserProfile.kt
├── viewmodels/
│   ├── LoginViewModel.kt
│   ├── RegisterViewModel.kt
│   └── ProfileViewModel.kt
├── utils/
│   ├── TokenManager.kt
│   └── NetworkUtils.kt
└── ui/
    ├── LoginActivity.kt
    ├── RegisterActivity.kt
    └── ProfileActivity.kt
```

## 6. Security Requirements

1. Password Requirements
   - Minimum 8 characters
   - At least one number
   - At least one letter

2. Token Storage
   - Use Android's EncryptedSharedPreferences
   - Clear on logout
   - Handle token expiration

3. Input Validation
   - Email format validation
   - Password strength validation
   - Empty field validation

## 7. Error Handling

1. Network Errors
   - Display user-friendly error messages
   - Handle connection timeouts
   - Handle server errors

2. Validation Errors
   - Show inline validation errors
   - Display server-side validation errors

3. Authentication Errors
   - Handle invalid credentials
   - Handle expired tokens
   - Handle network failures

## 8. Testing Requirements

1. Unit Tests
   - ViewModels
   - API calls
   - Token management

2. UI Tests
   - Input validation
   - Navigation flow
   - Error states

3. Integration Tests
   - Full authentication flow
   - Token persistence
   - Session management

## 9. Success Criteria

1. Users can successfully register with email and password
2. Users can login and receive a valid JWT token
3. Users can view their profile information
4. Users can logout and clear their session
5. All error cases are properly handled and displayed
6. JWT token is securely stored and managed

## 10. Next Steps

1. Set up project dependencies
2. Implement network layer with Retrofit
3. Create UI layouts for all screens
4. Implement ViewModels and LiveData
5. Add token management
6. Implement error handling
7. Add unit tests
8. Perform integration testing

## Document Version History

- v1.0: Initial PRD for simple login app implementation
