# MR Quiz - Authentication System

This document describes the authentication system implemented for the MR Quiz platform.

## Overview

The authentication system uses JWT (JSON Web Tokens) for stateless authentication with the following features:

- User registration (signup)
- User login with JWT token generation
- Token refresh mechanism
- Role-based access control
- Account security features (account locking, failed attempt tracking)
- Password validation and encryption

## Architecture

### Components

1. **DTOs (Data Transfer Objects)**
   - `SignupRequestDto` - User registration data
   - `LoginRequestDto` - Login credentials
   - `LoginResponseDto` - Login response with JWT tokens
   - `RefreshTokenRequestDto` - Token refresh request
   - `UserResponseDto` - User information response

2. **Services**
   - `AuthenticationService` - Main authentication logic
   - `JwtService` - JWT token generation and validation
   - `CustomUserDetailsService` - Spring Security user details implementation

3. **Security Configuration**
   - `SecurityConfig` - Spring Security configuration
   - `JwtAuthenticationFilter` - JWT token validation filter
   - `JwtAuthenticationEntryPoint` - Unauthorized access handler

4. **Controllers**
   - `AuthController` - Authentication endpoints
   - `TestController` - Test endpoints for verification

## API Endpoints

### Authentication Endpoints

#### 1. User Registration
```http
POST /api/auth/signup
Content-Type: application/json

{
  "email": "user@example.com",
  "username": "username",
  "password": "Password123",
  "confirmPassword": "Password123",
  "firstName": "John",
  "lastName": "Doe",
  "role": "STUDENT",
  "phone": "+1234567890",
  "timezone": "UTC",
  "language": "en",
  "acceptTerms": true
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully. Please verify your email.",
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "username": "username",
    "firstName": "John",
    "lastName": "Doe",
    "role": "STUDENT",
    "status": "PENDING_VERIFICATION",
    "emailVerified": false,
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

#### 2. User Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password123",
  "rememberMe": false
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userId": "uuid",
    "email": "user@example.com",
    "username": "username",
    "firstName": "John",
    "lastName": "Doe",
    "role": "STUDENT",
    "status": "ACTIVE",
    "emailVerified": true,
    "lastLogin": "2024-01-01T00:00:00"
  }
}
```

#### 3. Token Refresh
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userId": "uuid",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "STUDENT"
  }
}
```

#### 4. Logout
```http
POST /api/auth/logout
Authorization: Bearer {accessToken}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

### Test Endpoints

#### 1. Public Endpoint (No Authentication Required)
```http
GET /api/test/public
```

#### 2. Protected Endpoint (Authentication Required)
```http
GET /api/test/protected
Authorization: Bearer {accessToken}
```

#### 3. User Information
```http
GET /api/test/user-info
Authorization: Bearer {accessToken}
```

#### 4. Admin Only Endpoint
```http
GET /api/test/admin
Authorization: Bearer {accessToken}
```
*Requires ADMIN role*

#### 5. Teacher Endpoint
```http
GET /api/test/teacher
Authorization: Bearer {accessToken}
```
*Requires TEACHER or ADMIN role*

## User Roles

The system supports the following user roles:

- `STUDENT` - Regular students
- `TEACHER` - Teachers/instructors
- `ADMIN` - Administrators
- `SUPER_ADMIN` - Super administrators
- `FACULTY` - Faculty members
- `DEPARTMENT_ADMIN` - Department administrators

## User Status

- `ACTIVE` - Active user account
- `INACTIVE` - Inactive account
- `SUSPENDED` - Suspended account
- `PENDING_VERIFICATION` - Awaiting email verification

## Security Features

### Password Requirements
- Minimum 8 characters
- At least one lowercase letter
- At least one uppercase letter
- At least one digit

### Account Security
- Account locking after 5 failed login attempts
- Locked accounts are automatically unlocked after 30 minutes
- Failed login attempt tracking
- JWT token expiration (24 hours for access tokens, 7 days for refresh tokens)

### JWT Configuration
- Access Token Expiration: 24 hours (86400000 ms)
- Refresh Token Expiration: 7 days (604800000 ms)
- Secret Key: Configured in application.properties

## Configuration

### Application Properties
```properties
# JWT Configuration
jwt.secret=bXJxdWl6X3NlY3JldF9rZXlfZm9yX2p3dF90b2tlbl9nZW5lcmF0aW9uX2FuZF92YWxpZGF0aW9u
jwt.access-token-expiration=86400000
jwt.refresh-token-expiration=604800000

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/mrquiz
spring.datasource.username=mrquiz_user
spring.datasource.password=mrquiz_password
```

## Usage Examples

### Frontend Integration

#### 1. User Registration
```javascript
const signup = async (userData) => {
  const response = await fetch('/api/auth/signup', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(userData)
  });
  
  const result = await response.json();
  return result;
};
```

#### 2. User Login
```javascript
const login = async (email, password) => {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ email, password })
  });
  
  const result = await response.json();
  
  if (result.success) {
    // Store tokens in localStorage or secure storage
    localStorage.setItem('accessToken', result.data.accessToken);
    localStorage.setItem('refreshToken', result.data.refreshToken);
  }
  
  return result;
};
```

#### 3. Making Authenticated Requests
```javascript
const makeAuthenticatedRequest = async (url) => {
  const token = localStorage.getItem('accessToken');
  
  const response = await fetch(url, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    }
  });
  
  return response.json();
};
```

#### 4. Token Refresh
```javascript
const refreshToken = async () => {
  const refreshToken = localStorage.getItem('refreshToken');
  
  const response = await fetch('/api/auth/refresh', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ refreshToken })
  });
  
  const result = await response.json();
  
  if (result.success) {
    localStorage.setItem('accessToken', result.data.accessToken);
  }
  
  return result;
};
```

## Error Handling

### Common Error Responses

#### 400 Bad Request
```json
{
  "success": false,
  "message": "Passwords do not match"
}
```

#### 401 Unauthorized
```json
{
  "success": false,
  "status": 401,
  "error": "Unauthorized",
  "message": "You need to be authenticated to access this resource",
  "path": "/api/test/protected",
  "timestamp": 1640995200000
}
```

#### 403 Forbidden
```json
{
  "success": false,
  "message": "Access denied. Insufficient privileges."
}
```

## Testing

### Using cURL

#### Register a new user
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "username": "testuser",
    "password": "Password123",
    "confirmPassword": "Password123",
    "firstName": "Test",
    "lastName": "User",
    "role": "STUDENT",
    "acceptTerms": true
  }'
```

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123"
  }'
```

#### Access protected endpoint
```bash
curl -X GET http://localhost:8080/api/test/protected \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## Dependencies

The authentication system requires the following dependencies:

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

## Next Steps

1. Implement email verification
2. Add password reset functionality
3. Implement two-factor authentication (2FA)
4. Add OAuth2 integration (Google, Facebook, etc.)
5. Implement token blacklisting for logout
6. Add rate limiting for authentication endpoints
7. Implement audit logging for security events

## Support

For questions or issues related to the authentication system, please refer to the project documentation or contact the development team.