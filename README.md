# Football Talks - Full Stack Application

A complete football discussion platform built with React, Spring Boot, and MySQL.

## 🏗️ Project Structure

```
football-talks-fullstack/
├── backend/          # Spring Boot REST API
├── frontend/         # React application
└── database/         # MySQL schema and sample data
```

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- MySQL 8+
- Maven 3.6+

### 1. Database Setup

```bash
# Start MySQL and run the schema
mysql -u root -p < database/schema.sql
mysql -u root -p < database/sample-data.sql
```

### 2. Backend Setup

```bash
cd backend
# Optional: export env vars from backend/.env.example values
mvn spring-boot:run
# Backend will start on http://localhost:8080
```

### 3. Frontend Setup

```bash
cd frontend
npm install
# Optional: set API URL explicitly
cp .env.example .env
npm run dev
# Frontend will start on http://localhost:3000
```

## 📋 Features

### Current Features (Ready to Use)
- ✅ User authentication (login/register)
- ✅ Create, read, update, delete posts
- ✅ Comment on posts
- ✅ Like/unlike posts
- ✅ User profiles
- ✅ Real-time post feed
- ✅ Responsive design

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user |
| GET | `/api/posts` | Get all posts |
| POST | `/api/posts` | Create new post |
| GET | `/api/posts/{id}` | Get single post |
| PUT | `/api/posts/{id}` | Update post |
| DELETE | `/api/posts/{id}` | Delete post |
| POST | `/api/posts/{id}/like` | Like a post |
| POST | `/api/posts/{id}/comments` | Add comment |
| GET | `/api/users/{id}` | Get user profile |

## 🎨 Customization Guide

### Add New Features

1. **Backend**: Add new controller in `backend/src/main/java/.../controller/`
2. **Frontend**: Add new component in `frontend/src/components/`
3. **Database**: Add new tables in `database/migrations/`

### Modify Styling

- Edit `frontend/src/App.css` for global styles
- Component styles are in respective `.css` files

### Change Database

- Update `backend/src/main/resources/application.properties`
- Run migrations from `database/migrations/`

## 🔧 Tech Stack

- **Frontend**: React 18, React Router, Axios
- **Backend**: Spring Boot 3.3, Spring Security, JPA/Hibernate
- **Database**: MySQL 8
- **Build Tools**: Maven, npm

## 📝 Default Test Accounts

```
Username: john_doe
Password: password123

Username: jane_smith  
Password: password123
```

## 🐛 Common Issues

**Frontend can't connect to backend**
- Check backend is running on port 8080
- Check CORS settings in `CorsConfig.java`

**Database connection failed**
- Verify MySQL is running
- Check credentials in `application.properties`
- Ensure database `football_talks` exists

**Build errors**
- Run `mvn clean install` in backend
- Run `npm install` in frontend
- Check Java and Node versions

## 📖 Documentation

- Swagger API Docs: http://localhost:8080/swagger-ui/index.html
- Frontend Dev Server: http://localhost:3000

## 👥 Team Development

1. **Pull latest changes**: `git pull origin main`
2. **Create feature branch**: `git checkout -b feature/your-feature`
3. **Make changes and test**
4. **Commit**: `git commit -m "Add: your feature"`
5. **Push**: `git push origin feature/your-feature`
6. **Create Pull Request**

## 📦 Production Build

### Backend
```bash
cd backend
./mvnw clean package
java -jar target/football-talks-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
cd frontend
npm run build
# Deploy the 'dist' folder to your hosting service
```

## 📄 License

MIT License - Feel free to modify for your needs!
