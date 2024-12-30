# Simple Login App with JWT Authentication and CAPTCHA

A modern Android application demonstrating secure user authentication using JWT tokens and Tencent Captcha verification, following Material Design guidelines and MVVM architecture.

## Features

- User Registration
- User Login with JWT Authentication
- Tencent Captcha Integration for Login Security
  - Slide verification
  - Popup mode display
  - Multi-language support
  - Dark mode support
- Secure Token Storage using EncryptedSharedPreferences
- Profile View
- Auto-login if valid token exists
- Logout functionality
- Input validation
- Error handling
- Loading state indicators

## Technical Stack

- **Language**: Kotlin
- **Minimum SDK**: 28 (Android 9.0)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Network**: Retrofit2 with OkHttp3
- **Security**: 
  - EncryptedSharedPreferences
  - Tencent Captcha SDK
- **UI**: Material Design Components
- **Async Operations**: Kotlin Coroutines
- **Data Binding**: ViewBinding

## Project Structure

```
app/src/main/java/com/example/dwcaptchademo/
├── api/
│   ├── AuthApi.kt            # API interface definitions
│   └── RetrofitClient.kt     # Network client setup
├── models/
│   ├── LoginRequest.kt       # Login request with CAPTCHA ticket
│   ├── RegisterRequest.kt
│   ├── LoginResponse.kt
│   └── UserProfile.kt
├── viewmodels/
│   ├── LoginViewModel.kt     # Login logic with CAPTCHA
│   ├── RegisterViewModel.kt
│   └── ProfileViewModel.kt
├── utils/
│   ├── TokenManager.kt       # JWT token management
│   └── NetworkUtils.kt       # Network utilities
└── ui/
    ├── LoginActivity.kt      # Login UI with CAPTCHA
    ├── RegisterActivity.kt
    └── ProfileActivity.kt
```

## API Endpoints

### Register
- **Endpoint**: `/auth/register`
- **Method**: POST
- **Body**:
  ```json
  {
      "email": "test@example.com",
      "password": "password123",
      "name": "Test User"
  }
  ```

### Login
- **Endpoint**: `/auth/login`
- **Method**: POST
- **Body**:
  ```json
  {
      "email": "test@example.com",
      "password": "password123",
      "captcha_ticket": "CAPTCHA_VERIFICATION_TICKET"
  }
  ```
- **Response**:
  ```json
  {
      "access_token": "jwt.token.here"
  }
  ```

### Get Profile
- **Endpoint**: `/auth/profile`
- **Method**: GET
- **Headers**: 
  - `Authorization: Bearer {jwt_token}`
- **Response**:
  ```json
  {
      "id": 1,
      "email": "test@example.com",
      "name": "Test User"
  }
  ```

## Security Features

1. Password Requirements
   - Minimum 8 characters
   - At least one number
   - At least one letter

2. Token Storage
   - Use Android's EncryptedSharedPreferences
   - Clear on logout
   - Handle token expiration

3. CAPTCHA Security
   - Tencent CAPTCHA integration
   - Slide verification
   - Ticket validation
   - Privacy policy compliance

4. Input Validation
   - Email format validation
   - Password strength validation
   - Empty field validation

## Error Handling

1. Network Errors
   - Display user-friendly error messages
   - Handle connection timeouts
   - Handle server errors

2. Validation Errors
   - Show inline validation errors
   - Display server-side validation errors

3. CAPTCHA Errors
   - Handle verification failures
   - Show retry options
   - Log error details

## Development Environment

1. Android Studio Arctic Fox or later
2. Gradle 7.0+
3. Kotlin 1.5+
4. Minimum SDK: API 28
5. Target SDK: API 34

## Build Instructions

1. Clone the repository
2. Open in Android Studio
3. Configure Tencent CAPTCHA:
   - Add AAR file to `app/libs`
   - Set your CAPTCHA App ID in `LoginActivity.kt`
4. Build and run

## Testing

1. Unit Tests
   - ViewModel tests
   - Repository tests
   - Utility tests

2. Integration Tests
   - API integration tests
   - CAPTCHA verification tests
   - Token management tests

3. UI Tests
   - Login flow tests
   - Registration flow tests
   - CAPTCHA interaction tests

## Production Considerations

1. Security
   - Implement proper privacy policy checks
   - Secure token storage
   - CAPTCHA ticket validation

2. Performance
   - Optimize WebView loading
   - Handle network conditions
   - Manage memory usage

3. User Experience
   - Clear error messages
   - Loading indicators
   - Smooth CAPTCHA integration
