# Football Talks - VS Code Setup Guide

## 📋 Prerequisites

Before starting, install these:

1. **Java Development Kit (JDK) 17+**
   - Download: https://adoptium.net/
   - Verify: `java -version`

2. **Node.js 18+**
   - Download: https://nodejs.org/
   - Verify: `node --version` and `npm --version`

3. **MySQL 8+**
   - Mac: Download from https://dev.mysql.com/downloads/mysql/
   - Windows: Use MySQL Installer
   - Verify: `mysql --version`

4. **VS Code**
   - Download: https://code.visualstudio.com/

## 🔌 Required VS Code Extensions

Install these extensions in VS Code:

### For Backend (Java/Spring Boot)
1. **Extension Pack for Java** (by Microsoft)
   - Includes: Language Support, Debugger, Test Runner, Maven
2. **Spring Boot Extension Pack** (by VMware)
   - Includes: Spring Boot Tools, Spring Initializr

### For Frontend (React)
1. **ES7+ React/Redux/React-Native snippets** (by dsznajder)
2. **ESLint** (by Microsoft)
3. **Prettier - Code formatter** (by Prettier)

### General
1. **GitLens** (by GitKraken) - Optional but helpful
2. **Thunder Client** (by Thunder Client) - For API testing

## 🚀 Step-by-Step Setup

### Step 1: Extract and Open Project

1. Extract `football-talks-fullstack.zip`
2. Open VS Code
3. **File → Open Folder** → Select `football-talks-fullstack` folder

Your workspace should look like this:
```
football-talks-fullstack/
├── backend/
├── frontend/
├── database/
├── README.md
└── setup.sh
```

### Step 2: Setup MySQL Database

#### Option A: Using VS Code Terminal

1. Open terminal in VS Code: **Terminal → New Terminal** (or Ctrl+`)
2. Navigate to database folder:
   ```bash
   cd database
   ```

3. Run SQL scripts:
   ```bash
   mysql -u root -p < schema.sql
   # Enter your MySQL password
   
   mysql -u root -p < sample-data.sql
   # Enter your MySQL password again
   ```

#### Option B: Using MySQL Workbench

1. Open MySQL Workbench
2. Connect to your local MySQL server
3. **File → Open SQL Script** → Select `database/schema.sql`
4. Click Execute (⚡ button)
5. Repeat for `database/sample-data.sql`

#### Verify Database Setup

```bash
mysql -u root -p
# Then in MySQL:
USE football_talks;
SHOW TABLES;
SELECT COUNT(*) FROM users;
# Should show 5 users
exit
```

### Step 3: Configure Backend

1. In VS Code, open `backend/src/main/resources/application.properties`

2. Update MySQL password on line 4:
   ```properties
   spring.datasource.password=YOUR_ACTUAL_PASSWORD
   ```
   Replace `Football@00` with your MySQL root password

3. Save the file

### Step 4: Run Backend

#### Method 1: VS Code Terminal (Recommended)

1. Open terminal in VS Code
2. Navigate to backend:
   ```bash
   cd backend
   ```

3. First time: Install dependencies
   ```bash
   ./mvnw clean install -DskipTests
   ```

4. Start the backend:
   ```bash
   ./mvnw spring-boot:run
   ```

5. Wait for startup message showing the ASCII art banner
6. Backend is running when you see: `Started FootballTalksApplication`

#### Method 2: Spring Boot Dashboard (Alternative)

1. Click **Spring Boot Dashboard** icon in left sidebar
2. Right-click on `football-talks`
3. Select **Start**

### Step 5: Run Frontend

1. **Open a NEW terminal** in VS Code:
   - **Terminal → New Terminal** (or click + in terminal panel)

2. Navigate to frontend:
   ```bash
   cd frontend
   ```

3. First time: Install dependencies
   ```bash
   npm install
   ```
   (This takes 2-3 minutes)

4. Start the frontend:
   ```bash
   npm start
   ```

5. Browser should auto-open to http://localhost:3000

## ✅ Verify Everything Works

### Check Backend (Terminal 1)
You should see:
```
╔══════════════════════════════════════════════════════════════╗
║        🏟️  FOOTBALL TALKS BACKEND IS NOW RUNNING  ⚽         ║
║  🌐 API:     http://localhost:8080                          ║
╚══════════════════════════════════════════════════════════════╝
```

### Check Frontend (Terminal 2)
You should see:
```
Compiled successfully!

You can now view football-talks-frontend in the browser.

  Local:            http://localhost:3000
```

### Test the Application

1. **Browser opens automatically** to http://localhost:3000
2. Click **Sign Up** in top right
3. Create an account
4. Try creating a post
5. Like and comment on existing posts

### Test API Documentation

Open: http://localhost:8080/swagger-ui/index.html

## 🐛 Common Issues & Fixes

### Issue 1: Backend won't start - "Port 8080 already in use"

**Fix:** Kill the process using port 8080
```bash
# Mac/Linux:
lsof -ti:8080 | xargs kill -9

# Windows (Command Prompt):
netstat -ano | findstr :8080
taskkill /PID <PID_NUMBER> /F
```

### Issue 2: Frontend won't start - "Port 3000 already in use"

**Fix:** Either kill process on 3000, or run on different port
```bash
# Mac/Linux:
lsof -ti:3000 | xargs kill -9

# Or run on different port:
PORT=3001 npm start
```

### Issue 3: "Cannot connect to database"

**Fixes:**
1. Verify MySQL is running:
   ```bash
   # Mac:
   mysql.server status
   # Or check System Preferences → MySQL
   
   # Windows: Check Services (services.msc) for MySQL80
   ```

2. Check password in `application.properties`

3. Verify database exists:
   ```bash
   mysql -u root -p -e "SHOW DATABASES;"
   ```

### Issue 4: "Module not found" in React

**Fix:**
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
npm start
```

### Issue 5: JWT errors when logging in

**Fix:** Check that:
1. Backend is running on port 8080
2. Frontend `package.json` has `"proxy": "http://localhost:8080"`
3. Clear browser cache (Ctrl+Shift+Delete)

### Issue 6: "./mvnw: Permission denied"

**Fix:**
```bash
chmod +x backend/mvnw
```

## 📁 VS Code Workspace Tips

### Recommended VS Code Settings

Create `.vscode/settings.json` in project root:

```json
{
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.compile.nullAnalysis.mode": "automatic",
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "[java]": {
    "editor.defaultFormatter": "redhat.java"
  },
  "files.exclude": {
    "**/.git": true,
    "**/.DS_Store": true,
    "**/node_modules": true,
    "**/target": true
  }
}
```

### Multi-Root Workspace (Advanced)

For better organization, create `football-talks.code-workspace`:

```json
{
  "folders": [
    {
      "name": "Backend",
      "path": "backend"
    },
    {
      "name": "Frontend", 
      "path": "frontend"
    },
    {
      "name": "Database",
      "path": "database"
    }
  ],
  "settings": {
    "java.configuration.updateBuildConfiguration": "automatic"
  }
}
```

Then: **File → Open Workspace from File** → Select this file

### Keyboard Shortcuts

- **Ctrl+`** - Toggle Terminal
- **Ctrl+Shift+`** - New Terminal
- **Ctrl+P** - Quick file search
- **Ctrl+Shift+F** - Search across all files
- **F5** - Start debugging (works for Spring Boot)

## 🧪 Testing the API

### Using Thunder Client (VS Code Extension)

1. Install **Thunder Client** extension
2. Click Thunder Client icon in left sidebar
3. Create New Request:
   - Method: POST
   - URL: http://localhost:8080/api/auth/login
   - Body (JSON):
     ```json
     {
       "username": "john_doe",
       "password": "password123"
     }
     ```
4. Click **Send**
5. Copy the `token` from response
6. Use token in Authorization header for other requests

### Using Swagger UI (Recommended)

1. Open: http://localhost:8080/swagger-ui/index.html
2. Click on any endpoint
3. Click **Try it out**
4. Fill parameters and click **Execute**

## 📝 Development Workflow

### Making Changes to Backend

1. Edit Java files
2. Save (Ctrl+S)
3. Spring Boot DevTools will auto-reload
4. No need to restart server for most changes

### Making Changes to Frontend

1. Edit React files
2. Save (Ctrl+S)
3. Browser auto-refreshes with changes
4. Check terminal for any errors

### Debugging Backend

1. Open any Java file
2. Click left of line number to set breakpoint (red dot)
3. Press **F5** or go to **Run and Debug** panel
4. Select **Spring Boot App**
5. Breakpoints will pause execution

### Debugging Frontend

1. In Chrome, press F12 to open DevTools
2. Go to **Sources** tab
3. Open file and set breakpoints
4. Or use `console.log()` statements

## 🎨 Customizing the Application

### Change Colors (Frontend)

Edit `frontend/src/App.css` - look for `:root` section:
```css
:root {
  --primary: #2563eb;        /* Change main blue color */
  --primary-dark: #1d4ed8;   /* Darker blue for hovers */
  --secondary: #64748b;      /* Gray color */
  --danger: #ef4444;         /* Red for delete/errors */
}
```

### Change App Name

1. Frontend title: `frontend/public/index.html` (line with `<title>`)
2. Backend banner: `backend/src/main/java/.../FootballTalksApplication.java`
3. Database name: `backend/src/main/resources/application.properties`

### Add New Features

Example: Add a "Featured Post" badge

1. **Backend**: Add `featured` field to `Post.java`
2. **Database**: 
   ```sql
   ALTER TABLE posts ADD COLUMN featured BOOLEAN DEFAULT FALSE;
   ```
3. **Frontend**: Show badge in `PostCard` component

## 📚 Project Structure Reference

```
football-talks-fullstack/
│
├── backend/
│   ├── src/main/java/.../
│   │   ├── controller/      ← API endpoints
│   │   ├── service/         ← Business logic
│   │   ├── repository/      ← Database queries
│   │   ├── model/           ← Database entities
│   │   ├── dto/             ← Request/Response objects
│   │   ├── config/          ← Configuration (CORS, Security)
│   │   └── security/        ← JWT & Authentication
│   ├── src/main/resources/
│   │   └── application.properties  ← Database config
│   └── pom.xml              ← Dependencies
│
├── frontend/
│   ├── src/
│   │   ├── App.js           ← Main component with all pages
│   │   ├── App.css          ← All styles
│   │   └── index.js         ← Entry point
│   ├── public/
│   │   └── index.html       ← HTML template
│   └── package.json         ← Dependencies
│
└── database/
    ├── schema.sql           ← Database structure
    └── sample-data.sql      ← Test data
```

## 🚀 Next Steps

1. **Explore the code** - Read through files to understand structure
2. **Make small changes** - Try changing colors, text, etc.
3. **Add features** - Use existing code as examples
4. **Learn more**:
   - Spring Boot: https://spring.io/guides
   - React: https://react.dev/learn
   - MySQL: https://dev.mysql.com/doc/

## 💡 Pro Tips

1. **Keep both terminals open** - One for backend, one for frontend
2. **Use VS Code split view** - Drag files to right to split editor
3. **Save often** - Auto-reload only works after save
4. **Check terminals for errors** - Most issues show up there first
5. **Use Git** - Initialize with `git init` to track changes

## 🆘 Getting Help

If stuck:
1. Check terminal for error messages
2. Check browser console (F12 → Console tab)
3. Review this guide's "Common Issues" section
4. Check backend logs for detailed errors

---

**Ready to code!** Open VS Code, follow the steps, and start building! 🎉
