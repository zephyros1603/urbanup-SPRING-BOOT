#!/bin/bash

echo "🚀 UrbanUp React Frontend Setup"
echo "==============================="

# Check if we're in the right directory
if [ ! -f "pom.xml" ]; then
    echo "❌ Error: Please run this script from the UrbanUp backend directory"
    exit 1
fi

FRONTEND_DIR="../urbanup-frontend"

echo "📁 Creating React frontend in: $FRONTEND_DIR"

# Create React app
if [ ! -d "$FRONTEND_DIR" ]; then
    echo "🔧 Creating new React app..."
    cd ..
    npx create-react-app urbanup-frontend
    cd urbanup-frontend
else
    echo "📂 Frontend directory already exists, navigating to it..."
    cd "$FRONTEND_DIR"
fi

echo ""
echo "📦 Installing required dependencies..."

# Install core dependencies
npm install axios @stomp/stompjs sockjs-client react-router-dom

# Optional but recommended
echo "🔄 Installing optional dependencies..."
npm install @reduxjs/toolkit react-redux react-query

echo ""
echo "📁 Creating project structure..."

# Create directory structure
mkdir -p src/components/auth
mkdir -p src/components/chat
mkdir -p src/components/notifications
mkdir -p src/components/tasks
mkdir -p src/services
mkdir -p src/hooks
mkdir -p src/context
mkdir -p src/utils

echo ""
echo "📝 Creating configuration files..."

# Create .env files
cat > .env.development << EOF
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_WS_URL=http://localhost:8080/ws
REACT_APP_ENVIRONMENT=development
EOF

cat > .env.production << EOF
REACT_APP_API_URL=https://your-api-domain.com/api
REACT_APP_WS_URL=https://your-api-domain.com/ws
REACT_APP_ENVIRONMENT=production
EOF

echo ""
echo "🔧 Creating basic API service..."

# Create basic API service
cat > src/services/api.js << 'EOF'
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - Add auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - Handle errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
EOF

echo ""
echo "🔐 Creating authentication service..."

# Create auth service
cat > src/services/auth.js << 'EOF'
import apiClient from './api';

export const authService = {
  register: async (userData) => {
    const response = await apiClient.post('/auth/register', userData);
    const { accessToken, refreshToken, user } = response.data.data;
    
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify(user));
    
    return response.data;
  },

  login: async (credentials) => {
    const response = await apiClient.post('/auth/login', credentials);
    const { accessToken, refreshToken, user } = response.data.data;
    
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify(user));
    
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  },

  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('accessToken');
  },
};
EOF

echo ""
echo "💬 Creating WebSocket service..."

# Create WebSocket service
cat > src/services/websocket.js << 'EOF'
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
  constructor() {
    this.client = null;
    this.connected = false;
    this.subscriptions = new Map();
  }

  connect(token) {
    return new Promise((resolve, reject) => {
      const socket = new SockJS(process.env.REACT_APP_WS_URL || 'http://localhost:8080/api/ws');
      
      this.client = new Client({
        webSocketFactory: () => socket,
        connectHeaders: {
          Authorization: `Bearer ${token}`,
        },
        onConnect: () => {
          console.log('WebSocket connected');
          this.connected = true;
          resolve();
        },
        onStompError: (error) => {
          console.error('WebSocket error:', error);
          this.connected = false;
          reject(error);
        },
      });

      this.client.activate();
    });
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.connected = false;
      this.subscriptions.clear();
    }
  }

  subscribeToChat(chatId, onMessage) {
    if (!this.connected) {
      throw new Error('WebSocket not connected');
    }

    const subscription = this.client.subscribe(
      `/topic/chat/${chatId}`,
      (message) => {
        const data = JSON.parse(message.body);
        onMessage(data);
      }
    );

    this.subscriptions.set(`chat-${chatId}`, subscription);
    return subscription;
  }

  sendMessage(chatId, message) {
    if (!this.connected) {
      throw new Error('WebSocket not connected');
    }

    this.client.publish({
      destination: `/app/chat/${chatId}/send`,
      body: JSON.stringify(message),
    });
  }
}

export const webSocketService = new WebSocketService();
EOF

echo ""
echo "🎨 Creating basic login component..."

# Create basic login component
cat > src/components/auth/LoginForm.jsx << 'EOF'
import React, { useState } from 'react';
import { authService } from '../../services/auth';

const LoginForm = ({ onLoginSuccess }) => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await authService.login(formData);
      onLoginSuccess();
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '50px auto', padding: '20px' }}>
      <h2>Login to UrbanUp</h2>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '15px' }}>
          <input
            type="email"
            placeholder="Email"
            value={formData.email}
            onChange={(e) => setFormData({...formData, email: e.target.value})}
            style={{ width: '100%', padding: '10px', border: '1px solid #ddd' }}
            required
          />
        </div>
        
        <div style={{ marginBottom: '15px' }}>
          <input
            type="password"
            placeholder="Password"
            value={formData.password}
            onChange={(e) => setFormData({...formData, password: e.target.value})}
            style={{ width: '100%', padding: '10px', border: '1px solid #ddd' }}
            required
          />
        </div>

        {error && <div style={{ color: 'red', marginBottom: '15px' }}>{error}</div>}
        
        <button 
          type="submit" 
          disabled={loading}
          style={{ 
            width: '100%', 
            padding: '10px', 
            backgroundColor: '#007bff', 
            color: 'white', 
            border: 'none',
            cursor: loading ? 'not-allowed' : 'pointer'
          }}
        >
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>
    </div>
  );
};

export default LoginForm;
EOF

echo ""
echo "📱 Updating main App.js..."

# Update App.js
cat > src/App.js << 'EOF'
import React, { useState } from 'react';
import { authService } from './services/auth';
import LoginForm from './components/auth/LoginForm';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(
    authService.isAuthenticated()
  );

  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    authService.logout();
    setIsAuthenticated(false);
  };

  return (
    <div className="App">
      {isAuthenticated ? (
        <div style={{ padding: '20px' }}>
          <nav style={{ 
            display: 'flex', 
            justifyContent: 'space-between', 
            alignItems: 'center',
            borderBottom: '1px solid #ddd',
            paddingBottom: '10px',
            marginBottom: '20px'
          }}>
            <h1>UrbanUp Dashboard</h1>
            <button 
              onClick={handleLogout}
              style={{ 
                padding: '8px 15px', 
                backgroundColor: '#dc3545', 
                color: 'white', 
                border: 'none',
                cursor: 'pointer'
              }}
            >
              Logout
            </button>
          </nav>
          
          <div>
            <h2>Welcome to UrbanUp!</h2>
            <p>Backend integration successful! 🎉</p>
            <p>Current user: {authService.getCurrentUser()?.email}</p>
            
            <div style={{ marginTop: '30px' }}>
              <h3>Next Steps:</h3>
              <ul>
                <li>✅ Authentication working</li>
                <li>🔄 Add task management components</li>
                <li>🔄 Add chat functionality</li>
                <li>🔄 Add notifications</li>
                <li>🔄 Style with CSS framework</li>
              </ul>
            </div>
          </div>
        </div>
      ) : (
        <LoginForm onLoginSuccess={handleLoginSuccess} />
      )}
    </div>
  );
}

export default App;
EOF

echo ""
echo "✅ Setup Complete!"
echo "=================="
echo ""
echo "🎯 Next Steps:"
echo "1. Start your Spring Boot backend:"
echo "   cd $(pwd | sed 's/urbanup-frontend/urbanup/')"
echo "   mvn spring-boot:run"
echo ""
echo "2. Start the React frontend:"
echo "   cd $(pwd)"
echo "   npm start"
echo ""
echo "3. Test the integration:"
echo "   • Open http://localhost:3000"
echo "   • Register/login with the backend"
echo "   • Verify authentication works"
echo ""
echo "📚 Documentation:"
echo "   • See FRONTEND_INTEGRATION_GUIDE.md for detailed examples"
echo "   • Check the websocket_chat_test.html for WebSocket testing"
echo ""
echo "🚀 Happy coding!"
