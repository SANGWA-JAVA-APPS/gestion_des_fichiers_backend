# User Role Differentiation & Dashboard Routing Implementation

## Overview
Implemented role-based dashboard system that automatically routes users to appropriate dashboards based on their role after login.

## Backend Changes

### 1. LoginResponseDto.java
**File**: `src/main/java/com/bar/gestiondesfichier/dto/LoginResponseDto.java`

**Added Fields**:
- `userId` (Long) - User ID for tracking
- `email` (String) - User email address

**Purpose**: Provide complete user information to frontend for role-based routing and personalization.

### 2. AuthController.java
**File**: `src/main/java/com/bar/gestiondesfichier/controller/AuthController.java`

**Changes**: Updated `/api/auth/login` endpoint to populate new fields:
```java
response.setUserId(account.getId());
response.setEmail(account.getEmail());
```

## Frontend Changes

### 1. Role-Based Dashboard Components

#### AdminDashboard.jsx
**File**: `src/components/AdminDashboard.jsx`

**Features**:
- Full system access
- User management (Accounts & Roles)
- Document management
- Location management (Country, Entity, Modules, Sections)
- System settings
- Color scheme: Primary (Blue)

**Menu Items**:
- Dashboard Overview
- Users
- Documents
- Locations
- Roles

#### ManagerDashboard.jsx
**File**: `src/components/ManagerDashboard.jsx`

**Features**:
- Document management (full CRUD)
- Location viewing (read-only)
- Report generation
- Document approval
- Color scheme: Success (Green)

**Menu Items**:
- Dashboard Overview
- Documents
- Locations

#### UserDashboard.jsx
**File**: `src/components/UserDashboard.jsx`

**Features**:
- Personal documents only
- Profile viewing
- Document upload/download
- Limited permissions
- Color scheme: Info (Cyan)

**Menu Items**:
- Dashboard Overview
- My Documents
- Profile

### 2. Authentication Utilities
**File**: `src/services/authUtils.js`

**New Functions**:
```javascript
// Store user info after login
setUserInfo(userData)

// Get user role
getUserRole()

// Check multiple roles
hasAnyRole(roles)

// Role-specific checks
isAdmin()
isManager()
isUser()

// Permission checking
hasPermission(permission)
```

**Permission Matrix**:
- **ADMIN**: Full access (`*`)
- **MANAGER**: `view_documents`, `create_documents`, `edit_documents`, `delete_documents`, `view_locations`
- **USER**: `view_own_documents`, `create_own_documents`, `edit_own_documents`

### 3. App.jsx Routing
**File**: `src/App.jsx`

**Changes**:
- Imports all three dashboard components
- Checks user role on app load
- Routes to appropriate dashboard based on role:
  - Admin roles → AdminDashboard
  - Manager roles → ManagerDashboard
  - User roles → UserDashboard (default)

**Role Detection Logic**:
```javascript
const role = userRole?.toUpperCase();
if (role?.includes('ADMIN')) return <AdminDashboard />;
if (role?.includes('MANAGER')) return <ManagerDashboard />;
return <UserDashboard />; // default
```

### 4. Login Component
**File**: `src/components/Login.jsx`

**Changes**:
- Imports `setUserInfo` utility
- Stores complete user data in localStorage after successful login:
  - userId
  - username
  - fullName
  - email
  - role
  - token
  - refreshToken
- Redirects to appropriate dashboard automatically

## User Flow

### Login Process
1. User enters credentials
2. Backend validates and returns user data with role
3. Frontend stores user info in localStorage
4. App.jsx reads role and renders appropriate dashboard
5. User sees role-specific interface

### Role-Based Features

#### Admin Users See:
- Full system management
- User account creation/management
- Role assignment
- All documents across system
- Location configuration
- System settings

#### Manager Users See:
- Document management (all documents)
- Document approval workflow
- Location information (view-only)
- Report generation
- Team oversight capabilities

#### Regular Users See:
- Personal documents only
- Profile information
- Document upload/download
- Limited to own data

## Database Requirements

### AccountCategory Table
Must contain role names matching the system:
- `ADMIN` or `Admin` or `Administrator`
- `MANAGER` or `Manager`
- `USER` or `User`

**Example SQL**:
```sql
INSERT INTO account_categories (name, description) VALUES
('ADMIN', 'System Administrator with full access'),
('MANAGER', 'Manager with document and team management'),
('USER', 'Regular user with personal document access');
```

## Testing the Implementation

### Test Admin Login
1. Login with admin account
2. Should see blue-themed dashboard
3. Menu should show: Dashboard, Users, Documents, Locations, Roles

### Test Manager Login
1. Login with manager account
2. Should see green-themed dashboard
3. Menu should show: Dashboard, Documents, Locations

### Test User Login
1. Login with regular user account
2. Should see cyan-themed dashboard
3. Menu should show: Dashboard, My Documents, Profile

## Security Features

1. **Token-based Authentication**: JWT tokens stored in localStorage
2. **Role-based Access Control**: Frontend checks roles before rendering components
3. **Permission System**: Granular permission checking for actions
4. **Auto-logout**: Clears all auth data on logout
5. **Session Persistence**: User stays logged in across browser refreshes

## Next Steps (Optional Enhancements)

1. **Backend Authorization**: Add `@PreAuthorize` annotations to endpoints
2. **Route Guards**: Create ProtectedRoute component for sensitive routes
3. **Audit Logging**: Log user actions by role
4. **Dynamic Permissions**: Load permissions from database instead of hardcoded
5. **Role Hierarchy**: Implement role inheritance (Admin inherits Manager permissions)
6. **Email Notifications**: Send alerts based on user role and actions
7. **Dashboard Analytics**: Show role-specific statistics and charts

## API Response Example

```json
{
  "success": true,
  "message": "Login successful",
  "userId": 1,
  "username": "admin",
  "fullName": "System Administrator",
  "email": "admin@example.com",
  "role": "ADMIN",
  "token": "eyJhbGc...",
  "refreshToken": "eyJhbGc..."
}
```

## Summary

✅ Backend returns complete user information including userId and email
✅ Three distinct dashboard components for Admin, Manager, and User roles
✅ Enhanced authentication utilities with permission checking
✅ Automatic dashboard routing based on user role
✅ Login component stores user data and triggers dashboard display
✅ Role-based UI with different colors and menu options
✅ Permission matrix for granular access control
✅ Secure token-based authentication system

The system now automatically shows the appropriate dashboard immediately after login, with features tailored to each user's role and permissions.
