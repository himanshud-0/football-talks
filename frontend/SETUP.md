# Football Talks - Integrated Frontend Setup

## ✅ Your Original Frontend Design + Working Backend Connection

This frontend combines:
- ✨ Your beautiful UI design (orange theme, animations)
- 🔌 Full backend integration (login, register, posts, comments, likes)
- 🔐 JWT authentication
- ⚽ Ready to use with your existing backend

---

## 🚀 Quick Setup

### Step 1: Replace Frontend Folder

```bash
# Navigate to your project
cd football-talks-fullstack

# Backup old frontend (optional)
mv frontend frontend_old

# Extract and rename this folder to 'frontend'
# The folder structure should be:
# football-talks-fullstack/
#   ├── backend/
#   ├── frontend/  <-- This new folder
#   └── database/
```

### Step 2: Install Dependencies

```bash
cd frontend
npm install
```

This will install:
- React 18
- React Router
- Axios (API calls)
- Tailwind CSS
- Vite (dev server)

### Step 3: Make Sure Backend is Running

In a separate terminal:

```bash
cd backend
./mvnw spring-boot:run
```

Wait for the backend to start (you'll see the ASCII art banner).

### Step 4: Start Frontend

```bash
cd frontend
npm run dev
```

Browser opens to: **http://localhost:3000**

---

## 🎯 What Works Now

### ✅ Authentication
- **Login**: Uses your MySQL database users
  - Test account: `john_doe` / `password123`
- **Sign Up**: Creates new users in database
- **JWT Tokens**: Automatic token management
- **Protected Routes**: Login required for certain pages

### ✅ Posts (Discussions)
- View all posts on homepage
- Like/unlike posts
- Comment on posts  
- Delete your own posts
- Real-time updates

### ✅ User Profiles
- Display user info with posts
- Favorite team badges
- Profile pictures (URLs)

---

## 📁 Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── Components/
│   │   ├── Header.jsx       ← Navigation with auth status
│   │   └── Footer.jsx       ← Footer component
│   ├── Pages/
│   │   ├── HomePage.jsx     ← Shows all posts from backend
│   │   ├── LoginPage.jsx    ← Login with backend auth
│   │   ├── SignUpPage.jsx   ← Registration
│   │   ├── NewsPage.jsx     ← Your news page
│   │   ├── PlayersPage.jsx  ← Your players page
│   │   └── ...
│   ├── Services/
│   │   └── api.js           ← Backend API integration
│   ├── assets/              ← Images (your hero-bg.png, etc.)
│   ├── App.jsx              ← Main router
│   ├── main.jsx             ← Entry point
│   └── index.css            ← Your original styles
├── .env                     ← API configuration
├── vite.config.js          ← Dev server config
├── tailwind.config.js      ← Tailwind setup
└── package.json            ← Dependencies
```

---

## 🔧 Configuration Files

### `.env` - API Connection
```env
VITE_API_BASE=http://localhost:8080/api
VITE_API_TIMEOUT=5000
```

### `vite.config.js` - Proxy Setup
```javascript
server: {
  port: 3000,
  proxy: {
    '/api': 'http://localhost:8080'
  }
}
```

---

## 🎨 Customization

### Change Colors

Edit `tailwind.config.js`:

```javascript
theme: {
  extend: {
    colors: {
      primary: '#FF6B00',        // Main orange
      'primary-hover': '#FF8533', // Hover orange
      dark: '#0F0F0F',           // Background
      'dark-card': '#1A1A1A',    // Card background
    }
  },
}
```

### Add New API Endpoints

Edit `src/Services/api.js`:

```javascript
export const customAPI = {
  myNewEndpoint: () => api.get('/my-endpoint'),
};
```

---

## 🐛 Troubleshooting

### Issue: "Cannot connect to backend"

**Check:**
1. Backend is running on port 8080
2. Frontend is running on port 3000
3. Check console for CORS errors

**Fix:**
```bash
# Restart both servers
# Terminal 1:
cd backend
./mvnw spring-boot:run

# Terminal 2:
cd frontend
npm run dev
```

### Issue: "Login fails with 401"

**Check:**
1. Database has users
2. Password is correct: `password123`
3. Backend logs for errors

**Test:**
```bash
# Try logging in via curl
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"password123"}'
```

### Issue: "npm install fails"

**Fix:**
```bash
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

### Issue: "Vite errors"

**Fix:**
```bash
# Update Vite
npm install vite@latest

# Or use legacy OpenSSL (Mac/Linux)
export NODE_OPTIONS=--openssl-legacy-provider
npm run dev
```

---

## 📊 API Endpoints Available

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/login` | POST | Login user |
| `/api/auth/register` | POST | Register user |
| `/api/posts` | GET | Get all posts |
| `/api/posts` | POST | Create post |
| `/api/posts/{id}` | GET | Get single post |
| `/api/posts/{id}` | PUT | Update post |
| `/api/posts/{id}` | DELETE | Delete post |
| `/api/posts/{id}/like` | POST | Toggle like |
| `/api/posts/{id}/comments` | POST | Add comment |
| `/api/users/me` | GET | Get current user |
| `/api/users/me` | PUT | Update profile |

---

## 🚀 Development Commands

```bash
# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

---

## 🎯 Next Steps

1. **Test everything works**:
   - Login with `john_doe` / `password123`
   - View posts on homepage
   - Like and comment on posts
   - Create a new account

2. **Customize your pages**:
   - Edit `NewsPage.jsx` to create/view posts
   - Update `PlayersPage.jsx` with backend data
   - Add more features to `HomePage.jsx`

3. **Add new features**:
   - User profiles page
   - Search functionality
   - Post filtering by team
   - Image uploads

---

## 💡 Tips

- **Keep backend running** while developing frontend
- **Check browser console** (F12) for errors
- **Check network tab** to see API calls
- **Backend logs** show detailed errors
- **Use test account** to try features

---

## 📞 Quick Reference

| What | Command | Port |
|------|---------|------|
| Backend | `./mvnw spring-boot:run` | 8080 |
| Frontend | `npm run dev` | 3000 |
| Database | MySQL Workbench | 3306 |

**Test Login:**
- Username: `john_doe`
- Password: `password123`

**Other test accounts:**
- `jane_smith` / `password123`
- `mike_wilson` / `password123`
- `sarah_jones` / `password123`
- `alex_brown` / `password123`

---

## ✨ You're Ready!

Your frontend now has:
- ✅ Beautiful UI (your design)
- ✅ Working authentication
- ✅ Real posts from database
- ✅ Like/comment system
- ✅ User management

Start building! 🚀
