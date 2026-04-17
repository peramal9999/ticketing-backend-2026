REST API DESIGN

AuthController
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
GET  /api/auth/me


USER APIs (ADMIN only)
GET    /api/users
GET    /api/users/{id}
POST   /api/users
PUT    /api/users/{id}
DELETE /api/users/{id}

CLIENT APIs
GET    /api/clients
POST   /api/clients
GET    /api/clients/{id}
PUT    /api/clients/{id}
DELETE /api/clients/{id}

PROJECT APIs
GET    /api/projects
POST   /api/projects
GET    /api/projects/{id}
PUT    /api/projects/{id}
DELETE /api/projects/{id}
GET    /api/clients/{clientId}/projects


TICKET APIs (CORE)
POST   /api/tickets
GET    /api/tickets
GET    /api/tickets/{id}
PUT    /api/tickets/{id}
DELETE /api/tickets/{id}

PATCH  /api/tickets/{id}/assign
PATCH  /api/tickets/{id}/status


Filters (VERY important)
GET /api/tickets?status=OPEN&priority=HIGH&clientId=xxx&projectId=xxx


COMMENT APIs
POST   /api/tickets/{ticketId}/comments
GET    /api/tickets/{ticketId}/comments
DELETE /api/comments/{id}
📎 ATTACHMENT APIs
POST   /api/tickets/{ticketId}/attachments
GET    /api/tickets/{ticketId}/attachments
DELETE /api/attachments/{id}


CATEGORY APIs (internal)
GET    /api/categories
POST   /api/categories
PUT    /api/categories/{id}
DELETE /api/categories/{id}
📊 STATUS HISTORY (read-only)
GET /api/tickets/{id}/history


SPRING BOOT FOLDER STRUCTURE (PRODUCTION MVP)

Clean layered architecture:


src/main/java/com/yourapp/ticketing
│
├── config
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   └── WebConfig.java
│
├── controller
│   ├── AuthController.java
│   ├── UserController.java
│   ├── TicketController.java
│   ├── ClientController.java
│   ├── ProjectController.java
│   ├── CommentController.java
│   └── AttachmentController.java
│
├── service
│   ├── AuthService.java
│   ├── UserService.java
│   ├── TicketService.java
│   ├── ClientService.java
│   ├── ProjectService.java
│   ├── CommentService.java
│   └── AttachmentService.java
│
├── repository
│   ├── UserRepository.java
│   ├── TicketRepository.java
│   ├── ClientRepository.java
│   ├── ProjectRepository.java
│   ├── CommentRepository.java
│   └── AttachmentRepository.java
│
├── entity
│   ├── User.java
│   ├── Ticket.java
│   ├── Client.java
│   ├── Project.java
│   ├── Comment.java
│   ├── Attachment.java
│   └── enums
│       ├── Role.java
│       ├── TicketStatus.java
│       ├── Priority.java
│       └── ContextType.java
│
├── dto
│   ├── request
│   │   ├── CreateTicketRequest.java
│   │   ├── CreateUserRequest.java
│   │   └── LoginRequest.java
│   │
│   ├── response
│   │   ├── TicketResponse.java
│   │   └── UserResponse.java
│
├── security
│   ├── JwtAuthenticationFilter.java
│   ├── JwtService.java
│   ├── CustomUserDetailsService.java
│   └── TicketSecurity.java
│
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── AccessDeniedException.java
│
└── util
├── TicketNumberGenerator.java
└── DateUtils.java


SPRING SECURITY IMPLEMENTATION (HOW YOU APPLY IT)

You will use:

1. JWT Authentication Filter
   Validate token
   Set SecurityContext
2. Method Level Security
   @PreAuthorize("hasRole('ADMIN')")
   @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
3. Custom Permission Checks (IMPORTANT)

Example:

@PreAuthorize("@ticketSecurity.canViewTicket(authentication, #ticketId)")