#!/bin/bash

echo "═══════════════════════════════════════════════════════════════════════════"
echo "           FOOTBALL TALKS - FULL STACK SETUP SCRIPT"
echo "═══════════════════════════════════════════════════════════════════════════"
echo ""

# Check prerequisites
echo "📋 Checking prerequisites..."

command -v java >/dev/null 2>&1 || { echo "❌ Java not found. Please install Java 17+"; exit 1; }
command -v node >/dev/null 2>&1 || { echo "❌ Node.js not found. Please install Node.js 18+"; exit 1; }
command -v mysql >/dev/null 2>&1 || { echo "❌ MySQL not found. Please install MySQL 8+"; exit 1; }

echo "✅ Prerequisites check passed!"
echo ""

# Setup database
echo "🗄️  Setting up database..."
echo "Please enter your MySQL root password:"
mysql -u root -p < database/schema.sql
mysql -u root -p < database/sample-data.sql
echo "✅ Database setup complete!"
echo ""

# Setup backend
echo "🔧 Setting up backend..."
cd backend
./mvnw clean install -DskipTests
echo "✅ Backend build complete!"
cd ..
echo ""

# Setup frontend
echo "⚛️  Setting up frontend..."
cd frontend
npm install
echo "✅ Frontend dependencies installed!"
cd ..
echo ""

echo "═══════════════════════════════════════════════════════════════════════════"
echo "                    🎉 SETUP COMPLETE!"
echo "═══════════════════════════════════════════════════════════════════════════"
echo ""
echo "📝 To start the application:"
echo ""
echo "   Terminal 1 (Backend):"
echo "   cd backend"
echo "   ./mvnw spring-boot:run"
echo ""
echo "   Terminal 2 (Frontend):"
echo "   cd frontend"
echo "   npm start"
echo ""
echo "🌐 Access the application:"
echo "   Frontend: http://localhost:3000"
echo "   Backend:  http://localhost:8080"
echo "   Swagger:  http://localhost:8080/swagger-ui/index.html"
echo ""
echo "👤 Test accounts:"
echo "   Username: john_doe    Password: password123"
echo "   Username: jane_smith  Password: password123"
echo ""
echo "═══════════════════════════════════════════════════════════════════════════"
