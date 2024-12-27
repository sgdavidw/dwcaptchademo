# Android Login App with JWT Authentication

A modern Android application demonstrating secure user authentication using JWT tokens and Tencent Captcha verification, following Material Design guidelines and MVVM architecture.

## Features

- User Registration
- User Login with JWT Authentication
- Tencent Captcha Integration for Login Security
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
├── api/                    # API interfaces and network models
│   ├── AuthApi.kt         # API endpoint definitions
│   └── RetrofitClient.kt  # Retrofit configuration
├── models/                # Data models
├── viewmodels/           # ViewModels for each screen
│   ├── LoginViewModel.kt
│   ├── RegisterViewModel.kt
│   └── ProfileViewModel.kt
├── utils/                # Utility classes
│   ├── TokenManager.kt   # JWT token management
│   └── NetworkUtils.kt   # Network helpers and Resource wrapper
└── ui/                   # UI components
    ├── LoginActivity.kt
    ├── RegisterActivity.kt
    └── ProfileActivity.kt
```

## Prerequisites

- Android Studio Arctic Fox or later
- JDK 8 or later
- Android SDK with minimum API level 28
- Tencent Captcha App ID (obtain from Tencent Cloud Console)

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone git@github.com:yourusername/dwcaptchademo.git
   ```

2. Open the project in Android Studio

3. Update the backend URL in `RetrofitClient.kt`:
   ```kotlin
   private const val BASE_URL = "http://10.0.2.2:3001"  # Change this to your backend URL
   ```

4. Configure Tencent Captcha:
   - Open `LoginActivity.kt`
   - Replace `"YourCaptchaAppId"` with your actual Tencent Captcha App ID:
     ```kotlin
     .setCaptchaAppid("YourCaptchaAppId")
     ```

5. Build and run the project:
   - Click "Run" in Android Studio, or
   - Run using Gradle:
     ```bash
     ./gradlew assembleDebug
     ```

## API Endpoints

The app communicates with the following endpoints:

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
      "password": "password123"
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

1. **Password Requirements**:
   - Minimum 8 characters
   - At least one number
   - At least one letter

2. **Token Storage**:
   - Uses Android's EncryptedSharedPreferences
   - Automatic token cleanup on logout
   - Token expiration handling

3. **Captcha Verification**:
   - Tencent Captcha integration
   - Bot prevention
   - Human verification before login

4. **Input Validation**:
   - Email format validation
   - Password strength validation
   - Empty field validation

## Error Handling

The app handles various error scenarios:
- Network connectivity issues
- Invalid credentials
- Server errors
- Validation errors
- Token expiration

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Material Design Components for Android
- Retrofit for API communication
- Android Security Crypto library for secure storage
