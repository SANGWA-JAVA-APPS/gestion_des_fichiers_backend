# MAGERWA Document Management System

A comprehensive document management system for MAGERWA organization, allowing users to manage documents across departments, countries, modules, entities, and sections.

## Features

- **Hierarchical Organization Structure**: Countries → Departments → Entities → Modules → Sections
- **User Management**: Users can be assigned to different levels of the organization
- **Document Upload & Management**: Upload, view, download, archive, and delete documents
- **Security**: Role-based access control (Admin, Manager, User)
- **Web Interface**: Responsive web interface built with Bootstrap
- **Database**: H2 in-memory database for development

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. Clone the repository:
```bash
git clone <repository-url>
cd gestion_des_fichiers
```

2. Run the application:
```bash
mvn spring-boot:run
```

3. Open your browser and navigate to: `http://localhost:8080`

### Default Demo Credentials

The application comes with pre-configured demo users:

- **Admin User**: 
  - Username: `admin` 
  - Password: `admin123`
  - Access: Full admin access including organization structure management

- **Regular User**: 
  - Username: `user` 
  - Password: `user123`
  - Access: Document management within assigned section

- **Manager User**: 
  - Username: `manager` 
  - Password: `manager123`
  - Access: Manager-level permissions

## Organizational Structure

The system uses a hierarchical structure:

1. **Countries** (e.g., Rwanda, Uganda)
2. **Departments** (e.g., Information Technology, Human Resources)
3. **Entities** (e.g., IT Operations, HR Operations)
4. **Modules** (e.g., Development, Support)
5. **Sections** (e.g., Backend Development, Frontend Development, Help Desk)

Users can be assigned to any level of this hierarchy and can manage documents within their assigned section.

## Key Features

### Document Management
- Upload documents with title, description, and file
- Download documents
- Archive/delete documents (soft delete)
- View documents by section or user
- Search functionality

### Admin Panel
- Manage organizational structure (Countries, Departments, Entities, Modules, Sections)
- Add new organizational units
- View statistics dashboard

### Security
- Spring Security integration
- Password encryption (BCrypt)
- Role-based access control
- Session management

## Technical Stack

- **Backend**: Spring Boot 3.2.0
- **Database**: H2 (in-memory for development)
- **ORM**: Hibernate/JPA
- **Security**: Spring Security
- **Frontend**: Thymeleaf + Bootstrap 5
- **Build Tool**: Maven

## Database Access

For development purposes, the H2 console is available at:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## File Storage

Uploaded documents are stored in the `./uploads` directory relative to the application root. In production, this should be configured to a more appropriate location.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License.
