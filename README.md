# 🏙️ UrbanUp MVP - Complete Backend & Frontend Integration Guide

UrbanUp is a comprehensive task marketplace platform that connects task posters with task fulfillers in urban areas. This document provides complete details for integrating the backend with frontend applications.

## 📋 Table of Contents

- [Backend Overview](#-backend-overview)
- [Database Schema & Data Models](#-database-schema--data-models)
- [Complete API Endpoints](#-complete-api-endpoints--data-flow)
- [Frontend UI Components Structure](#-frontend-ui-components-structure)
- [State Management](#️-state-management-structure)
- [Implementation Roadmap](#-implementation-priority--roadmap)
- [Getting Started](#-getting-started)

---

## 🚀 Backend Overview

### **Base Configuration**
- **Base URL**: `http://localhost:8080/api`
- **Authentication**: JWT Bearer tokens
- **Content-Type**: `application/json`
- **Database**: PostgreSQL
- **Cache**: Redis (for real-time features)

### **Technology Stack**
- **Framework**: Spring Boot 3.5.4
- **Security**: Spring Security with JWT
- **Database**: PostgreSQL with Hibernate JPA
- **Build Tool**: Maven
- **Java Version**: 17+

---

## 📊 Database Schema & Data Models

### **1. User Data Structure**
```javascript
const UserModel = {
  id: number,
  email: string,
  password: string, // (encrypted, not returned in API)
  firstName: string,
  lastName: string,
  phoneNumber: string,
  isEmailVerified: boolean,
  isPhoneVerified: boolean,
  isActive: boolean,
  theme: "LIGHT" | "DARK",
  profilePictureUrl: string,
  dateOfBirth: string, // ISO date
  ratingAsPoster: number, // 0.0-5.0
  ratingAsFulfiller: number, // 0.0-5.0
  ratingsAsPostCount: number,
  ratingsAsFulfillerCount: number,
  totalTasksPosted: number,
  totalTasksCompleted: number,
  totalEarnings: number, // BigDecimal
  accountCreatedFrom: string,
  lastLogin: string, // ISO datetime
  createdAt: string, // ISO datetime
  updatedAt: string, // ISO datetime
  
  // Relationships
  userProfile: UserProfile,
  postedTasks: Task[],
  fulfilledTasks: Task[],
  sentNotifications: Notification[],
  chatParticipations: Chat[]
};
```

### **2. UserProfile Data Structure**
```javascript
const UserProfileModel = {
  id: number,
  bio: string,
  address: string,
  city: string,
  state: string,
  pincode: string,
  latitude: number,
  longitude: number,
  gender: "MALE" | "FEMALE" | "OTHER" | "PREFER_NOT_TO_SAY",
  isKycVerified: boolean,
  kycDocumentType: "AADHAAR" | "PAN" | "PASSPORT" | "DRIVING_LICENSE",
  kycDocumentNumber: string,
  kycVerificationDate: string,
  skills: string[], // JSON array
  interests: string[], // JSON array
  languagesSpoken: string[], // JSON array
  emergencyContactName: string,
  emergencyContactPhone: string,
  badges: string[], // JSON array
  totalTasksPosted: number,
  totalTasksCompleted: number,
  totalEarnings: number,
  averageResponseTime: number, // in minutes
  accountVerificationLevel: "BASIC" | "VERIFIED" | "PREMIUM",
  preferredWorkRadius: number, // in km
  isAvailableForWork: boolean,
  workingHours: string, // JSON object
  profileCompletionPercentage: number,
  createdAt: string,
  updatedAt: string
};
```

### **3. Task Data Structure**
```javascript
const TaskModel = {
  id: number,
  title: string,
  description: string,
  category: "PERSONAL_ERRANDS" | "PROFESSIONAL_TASKS" | "HOUSEHOLD_HELP" | "MICRO_GIGS",
  price: number, // BigDecimal
  pricingType: "FIXED" | "HOURLY",
  location: string,
  address: string,
  latitude: number,
  longitude: number,
  deadline: string, // ISO datetime
  isUrgent: boolean,
  status: "OPEN" | "ACCEPTED" | "IN_PROGRESS" | "COMPLETED" | "CONFIRMED" | "CANCELLED",
  requirements: string,
  images: string[], // JSON array of URLs
  files: string[], // JSON array of URLs
  estimatedDuration: number, // in hours
  skillsRequired: string[], // JSON array
  acceptedAt: string,
  startedAt: string,
  completedAt: string,
  confirmedAt: string,
  createdAt: string,
  updatedAt: string,
  
  // Relationships
  poster: User,
  fulfiller: User,
  applications: TaskApplication[],
  chat: Chat,
  review: Review,
  payment: Payment
};
```

### **4. Chat & Message Data Structure**
```javascript
const ChatModel = {
  id: number,
  isActive: boolean,
  lastMessageAt: string,
  createdAt: string,
  updatedAt: string,
  
  // Relationships
  task: Task,
  poster: User,
  fulfiller: User,
  messages: Message[]
};

const MessageModel = {
  id: number,
  content: string,
  type: "TEXT" | "IMAGE" | "FILE" | "LOCATION" | "SYSTEM",
  isRead: boolean,
  attachmentUrl: string,
  metadata: string, // JSON
  createdAt: string,
  
  // Relationships
  chat: Chat,
  sender: User
};
```

### **5. TaskApplication Data Structure**
```javascript
const TaskApplicationModel = {
  id: number,
  proposalText: string,
  proposedPrice: number,
  estimatedCompletionTime: string,
  status: "PENDING" | "ACCEPTED" | "REJECTED",
  createdAt: string,
  
  // Relationships
  task: Task,
  applicant: User
};
```

---

## 🌐 Complete API Endpoints & Data Flow

### **Authentication Endpoints**

#### **1. User Registration**
```javascript
// POST /api/auth/register
const registrationData = {
  firstName: "John",
  lastName: "Doe", 
  email: "john@example.com",
  password: "password123",
  phoneNumber: "+919876543210"
};

// Response
const registrationResponse = {
  success: true,
  message: "User registered successfully",
  data: {
    user: UserModel, // without password
    accessToken: "jwt_token_here",
    refreshToken: "refresh_token_here"
  }
};
```

#### **2. User Login**
```javascript
// POST /api/auth/login
const loginData = {
  email: "john@example.com",
  password: "password123"
};

// Response
const loginResponse = {
  success: true,
  message: "Login successful",
  data: {
    user: UserModel,
    accessToken: "jwt_token_here",
    refreshToken: "refresh_token_here"
  }
};
```

#### **3. Refresh Token**
```javascript
// POST /api/auth/refresh
const refreshData = {
  refreshToken: "refresh_token_here"
};

// Response
const refreshResponse = {
  success: true,
  data: {
    accessToken: "new_jwt_token_here",
    refreshToken: "new_refresh_token_here"
  }
};
```

### **User Management Endpoints**

#### **4. Get User Profile**
```javascript
// GET /api/users/{id}
// Headers: Authorization: Bearer {token}

// Response
const userProfileResponse = {
  success: true,
  data: {
    ...UserModel,
    userProfile: UserProfileModel
  }
};
```

#### **5. Update User Profile**
```javascript
// PUT /api/users/{id}/profile
const updateData = {
  firstName: "John Updated",
  lastName: "Doe Updated",
  phoneNumber: "+919876543211",
  bio: "Updated bio",
  address: "New address",
  city: "New city",
  skills: ["JavaScript", "React", "Node.js"],
  interests: ["Technology", "Travel"]
};

// Response: Updated user object
```

#### **6. Update Theme**
```javascript
// PUT /api/users/{id}/theme
const themeData = {
  theme: "DARK" // or "LIGHT"
};
```

#### **7. Get User Count**
```javascript
// GET /api/users/count
// Response: { success: true, data: 1250 }
```

#### **8. Search Users**
```javascript
// GET /api/users/search?query=john&page=0&size=20
```

### **Task Management Endpoints**

#### **9. Get All Tasks**
```javascript
// GET /api/tasks
// Optional query params: ?category=PERSONAL_ERRANDS&status=OPEN&page=0&size=20

// Response
const tasksResponse = {
  success: true,
  data: {
    content: Task[], // Array of tasks
    page: {
      size: 20,
      number: 0,
      totalElements: 150,
      totalPages: 8
    }
  }
};
```

#### **10. Create Task**
```javascript
// POST /api/tasks
const taskData = {
  title: "Grocery Shopping",
  description: "Need someone to buy groceries from nearby store",
  category: "PERSONAL_ERRANDS",
  price: 500.00,
  pricingType: "FIXED",
  location: "Bangalore, Karnataka",
  address: "123 MG Road, Bangalore",
  latitude: 12.9716,
  longitude: 77.5946,
  deadline: "2024-12-25T18:00:00Z",
  isUrgent: false,
  requirements: "Need someone with vehicle",
  estimatedDuration: 2,
  skillsRequired: ["Shopping", "Local Area Knowledge"]
};

// Response: Created task object
```

#### **11. Get Task by ID**
```javascript
// GET /api/tasks/{id}
// Response: Task object with full details
```

#### **12. Update Task**
```javascript
// PUT /api/tasks/{id}
// Body: Updated task data (same as create)
```

#### **13. Delete Task**
```javascript
// DELETE /api/tasks/{id}
// Response: { success: true, message: "Task deleted successfully" }
```

#### **14. Search Tasks**
```javascript
// GET /api/tasks/search?keyword=grocery&category=PERSONAL_ERRANDS&minPrice=100&maxPrice=1000&latitude=12.9716&longitude=77.5946&radius=10

// Response: Array of matching tasks
```

#### **15. Apply for Task**
```javascript
// POST /api/tasks/{taskId}/apply
const applicationData = {
  proposalText: "I can complete this task efficiently",
  proposedPrice: 450.00, // Optional price negotiation
  estimatedCompletionTime: "2024-12-25T16:00:00Z"
};

// Response
const applicationResponse = {
  success: true,
  message: "Application submitted successfully",
  data: {
    id: 123,
    status: "PENDING",
    proposalText: "I can complete this task efficiently",
    proposedPrice: 450.00,
    createdAt: "2024-12-24T10:00:00Z",
    applicant: UserModel,
    task: TaskModel
  }
};
```

#### **16. Get Task Applications**
```javascript
// GET /api/tasks/{taskId}/applications
// Response: Array of TaskApplication objects
```

#### **17. Update Task Status**
```javascript
// PUT /api/tasks/{id}/status
const statusData = {
  status: "IN_PROGRESS" // or other valid status
};
```

### **Chat System Endpoints**

#### **18. Get User Chats**
```javascript
// GET /api/chats/user/{userId}

// Response
const chatsResponse = {
  success: true,
  data: [
    {
      id: 1,
      task: TaskModel,
      poster: UserModel,
      fulfiller: UserModel,
      lastMessage: MessageModel,
      unreadCount: 3,
      isActive: true,
      lastMessageAt: "2024-12-24T15:30:00Z"
    }
  ]
};
```

#### **19. Create Task Chat**
```javascript
// POST /api/chats/task/{taskId}
// Response: Created chat object
```

#### **20. Get Chat Messages**
```javascript
// GET /api/chats/{chatId}/messages?page=0&size=50

// Response
const messagesResponse = {
  success: true,
  data: {
    content: Message[], // Array of messages
    page: {
      size: 50,
      number: 0,
      totalElements: 25,
      totalPages: 1
    }
  }
};
```

#### **21. Send Message**
```javascript
// POST /api/chats/{chatId}/messages
const messageData = {
  content: "Hello! I'm interested in your task.",
  type: "TEXT",
  attachmentUrl: null // Optional for images/files
};

// Response: Created message object
```

#### **22. Mark Messages as Read**
```javascript
// PUT /api/chats/{chatId}/messages/read
// Response: { success: true, message: "Messages marked as read" }
```

---

## 🎨 Frontend UI Components Structure

### **Page-by-Page UI Requirements**

#### **1. Authentication Pages**

##### **Registration Page**
```jsx
const RegistrationPage = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '', 
    email: '',
    password: '',
    confirmPassword: '',
    phoneNumber: '',
    agreeToTerms: false
  });

  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  // Validation rules:
  // - firstName: Required, min 2 chars
  // - lastName: Required, min 2 chars  
  // - email: Required, valid email format
  // - password: Required, min 8 chars, must contain uppercase, lowercase, number
  // - phoneNumber: Required, valid phone format (+91XXXXXXXXXX)
  // - agreeToTerms: Must be true

  return (
    <div className="registration-page">
      <form onSubmit={handleSubmit}>
        <input name="firstName" placeholder="First Name" required />
        <input name="lastName" placeholder="Last Name" required />
        <input name="email" type="email" placeholder="Email" required />
        <input name="password" type="password" placeholder="Password" required />
        <input name="confirmPassword" type="password" placeholder="Confirm Password" required />
        <input name="phoneNumber" placeholder="Phone Number" required />
        <checkbox name="agreeToTerms" required>I agree to Terms & Conditions</checkbox>
        <button type="submit" disabled={loading}>
          {loading ? 'Creating Account...' : 'Sign Up'}
        </button>
      </form>
    </div>
  );
};
```

##### **Login Page**
```jsx
const LoginPage = () => {
  const [credentials, setCredentials] = useState({
    email: '',
    password: ''
  });

  // Handle login response:
  // - Store accessToken in localStorage/sessionStorage
  // - Store refreshToken securely
  // - Store user data in Redux/Context
  // - Redirect to dashboard
};
```

#### **2. Dashboard Page**
```jsx
const Dashboard = () => {
  const user = useSelector(state => state.auth.user);
  
  return (
    <div className="dashboard">
      {/* Header with user info */}
      <header>
        <div className="user-info">
          <img src={user.profilePictureUrl} alt="Profile" />
          <span>{user.firstName} {user.lastName}</span>
          <span className="rating">★ {user.ratingAsPoster.toFixed(1)}</span>
        </div>
        <nav>
          <button>Post Task</button>
          <button>Browse Tasks</button>
          <button>My Chats</button>
        </nav>
      </header>

      {/* Stats Cards */}
      <div className="stats-grid">
        <div className="stat-card">
          <h3>Tasks Posted</h3>
          <span>{user.totalTasksPosted}</span>
        </div>
        <div className="stat-card">
          <h3>Tasks Completed</h3>
          <span>{user.totalTasksCompleted}</span>
        </div>
        <div className="stat-card">
          <h3>Total Earnings</h3>
          <span>₹{user.totalEarnings}</span>
        </div>
      </div>

      {/* Recent Activities */}
      <RecentActivities />
      <NearbyTasks />
    </div>
  );
};
```

#### **3. Task Listing Page**
```jsx
const TaskListing = () => {
  const [tasks, setTasks] = useState([]);
  const [filters, setFilters] = useState({
    category: '',
    minPrice: '',
    maxPrice: '',
    location: '',
    isUrgent: false,
    sortBy: 'createdAt'
  });
  const [pagination, setPagination] = useState({
    page: 0,
    size: 20,
    totalPages: 0
  });

  return (
    <div className="task-listing">
      {/* Filters */}
      <FilterPanel filters={filters} onFilterChange={setFilters} />
      
      {/* Task Grid */}
      <div className="tasks-grid">
        {tasks.map(task => (
          <TaskCard 
            key={task.id} 
            task={task}
            onApply={handleApplyForTask}
            onViewDetails={handleViewTaskDetails}
          />
        ))}
      </div>

      {/* Pagination */}
      <Pagination 
        currentPage={pagination.page}
        totalPages={pagination.totalPages}
        onPageChange={handlePageChange}
      />
    </div>
  );
};
```

#### **4. Task Creation Page**
```jsx
const CreateTask = () => {
  const [taskData, setTaskData] = useState({
    title: '',
    description: '',
    category: '',
    price: '',
    pricingType: 'FIXED',
    location: '',
    address: '',
    deadline: '',
    isUrgent: false,
    requirements: '',
    estimatedDuration: '',
    skillsRequired: [],
    images: []
  });

  const [locationData, setLocationData] = useState({
    latitude: null,
    longitude: null
  });

  return (
    <div className="create-task">
      <form onSubmit={handleSubmit}>
        <input name="title" placeholder="Task Title" required />
        <textarea name="description" placeholder="Describe your task..." required />
        
        <select name="category" required>
          <option value="">Select Category</option>
          <option value="PERSONAL_ERRANDS">Personal Errands</option>
          <option value="PROFESSIONAL_TASKS">Professional Tasks</option>
          <option value="HOUSEHOLD_HELP">Household Help</option>
          <option value="MICRO_GIGS">Micro Gigs</option>
        </select>

        <div className="price-section">
          <input name="price" type="number" placeholder="Price (₹)" required />
          <select name="pricingType">
            <option value="FIXED">Fixed Price</option>
            <option value="HOURLY">Hourly Rate</option>
          </select>
        </div>

        <LocationPicker 
          onLocationSelect={setLocationData}
          onAddressSelect={(address) => setTaskData({...taskData, address})}
        />

        <datetime name="deadline" placeholder="Deadline" required />
        
        <label>
          <input name="isUrgent" type="checkbox" />
          This is urgent
        </label>

        <ImageUpload 
          maxImages={5}
          onImagesChange={(images) => setTaskData({...taskData, images})}
        />

        <button type="submit">Post Task</button>
      </form>
    </div>
  );
};
```

#### **5. Task Details Page**
```jsx
const TaskDetails = ({ taskId }) => {
  const [task, setTask] = useState(null);
  const [applications, setApplications] = useState([]);
  const [showApplicationForm, setShowApplicationForm] = useState(false);

  return (
    <div className="task-details">
      {task && (
        <>
          <div className="task-header">
            <h1>{task.title}</h1>
            <div className="task-meta">
              <span className="category">{task.category}</span>
              <span className="price">₹{task.price}</span>
              <span className="deadline">Due: {formatDate(task.deadline)}</span>
              {task.isUrgent && <span className="urgent">URGENT</span>}
            </div>
          </div>

          <div className="task-content">
            <div className="description">
              <h3>Description</h3>
              <p>{task.description}</p>
            </div>

            <div className="requirements">
              <h3>Requirements</h3>
              <p>{task.requirements}</p>
            </div>

            <div className="location">
              <h3>Location</h3>
              <p>{task.address}</p>
              <MapView latitude={task.latitude} longitude={task.longitude} />
            </div>

            {task.images.length > 0 && (
              <div className="images">
                <h3>Images</h3>
                <ImageGallery images={task.images} />
              </div>
            )}
          </div>

          <div className="task-actions">
            {task.status === 'OPEN' && (
              <button onClick={() => setShowApplicationForm(true)}>
                Apply for This Task
              </button>
            )}
          </div>

          {showApplicationForm && (
            <ApplicationForm 
              taskId={task.id}
              onSubmit={handleApplicationSubmit}
              onCancel={() => setShowApplicationForm(false)}
            />
          )}

          {/* Show applications if user is the poster */}
          {task.poster.id === currentUser.id && (
            <ApplicationsList 
              applications={applications}
              onAccept={handleAcceptApplication}
              onReject={handleRejectApplication}
            />
          )}
        </>
      )}
    </div>
  );
};
```

#### **6. Chat Interface**
```jsx
const ChatInterface = () => {
  const [chats, setChats] = useState([]);
  const [selectedChat, setSelectedChat] = useState(null);
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');

  return (
    <div className="chat-interface">
      {/* Chat List Sidebar */}
      <div className="chat-list">
        <h3>Messages</h3>
        {chats.map(chat => (
          <div 
            key={chat.id}
            className={`chat-item ${selectedChat?.id === chat.id ? 'active' : ''}`}
            onClick={() => setSelectedChat(chat)}
          >
            <div className="chat-avatar">
              <img src={chat.otherUser.profilePictureUrl} alt="User" />
            </div>
            <div className="chat-info">
              <div className="chat-title">{chat.task.title}</div>
              <div className="last-message">{chat.lastMessage?.content}</div>
              <div className="chat-time">{formatTime(chat.lastMessageAt)}</div>
            </div>
            {chat.unreadCount > 0 && (
              <div className="unread-badge">{chat.unreadCount}</div>
            )}
          </div>
        ))}
      </div>

      {/* Chat Messages */}
      <div className="chat-messages">
        {selectedChat ? (
          <>
            <div className="chat-header">
              <h3>{selectedChat.task.title}</h3>
              <span>with {selectedChat.otherUser.firstName}</span>
            </div>

            <div className="messages-container">
              {messages.map(message => (
                <div 
                  key={message.id}
                  className={`message ${message.sender.id === currentUser.id ? 'sent' : 'received'}`}
                >
                  <div className="message-content">{message.content}</div>
                  <div className="message-time">{formatTime(message.createdAt)}</div>
                </div>
              ))}
            </div>

            <div className="message-input">
              <input 
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
                placeholder="Type a message..."
                onKeyPress={handleKeyPress}
              />
              <button onClick={handleSendMessage}>Send</button>
            </div>
          </>
        ) : (
          <div className="no-chat-selected">
            Select a chat to start messaging
          </div>
        )}
      </div>
    </div>
  );
};
```

#### **7. User Profile Page**
```jsx
const UserProfile = () => {
  const [user, setUser] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editData, setEditData] = useState({});

  return (
    <div className="user-profile">
      <div className="profile-header">
        <div className="profile-picture">
          <img src={user.profilePictureUrl} alt="Profile" />
          <button className="change-picture">Change Photo</button>
        </div>
        
        <div className="profile-info">
          <h1>{user.firstName} {user.lastName}</h1>
          <div className="ratings">
            <span>Poster Rating: ★ {user.ratingAsPoster.toFixed(1)} ({user.ratingsAsPostCount})</span>
            <span>Fulfiller Rating: ★ {user.ratingAsFulfiller.toFixed(1)} ({user.ratingsAsFulfillerCount})</span>
          </div>
          <div className="verification-status">
            {user.isEmailVerified && <span className="verified">✓ Email</span>}
            {user.isPhoneVerified && <span className="verified">✓ Phone</span>}
            {user.userProfile?.isKycVerified && <span className="verified">✓ KYC</span>}
          </div>
        </div>
      </div>

      <div className="profile-content">
        {isEditing ? (
          <EditProfileForm 
            userData={user}
            onSave={handleSaveProfile}
            onCancel={() => setIsEditing(false)}
          />
        ) : (
          <ViewProfile user={user} onEdit={() => setIsEditing(true)} />
        )}
      </div>

      <div className="profile-stats">
        <div className="stat-section">
          <h3>Activity Statistics</h3>
          <div className="stats-grid">
            <div className="stat">
              <span className="label">Tasks Posted</span>
              <span className="value">{user.totalTasksPosted}</span>
            </div>
            <div className="stat">
              <span className="label">Tasks Completed</span>
              <span className="value">{user.totalTasksCompleted}</span>
            </div>
            <div className="stat">
              <span className="label">Total Earnings</span>
              <span className="value">₹{user.totalEarnings}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
```

---

## 🛠️ State Management Structure

### **Redux Store Structure**
```javascript
const reduxStore = {
  auth: {
    user: UserModel | null,
    isAuthenticated: boolean,
    tokens: {
      accessToken: string,
      refreshToken: string
    },
    loading: boolean,
    error: string | null
  },
  
  tasks: {
    allTasks: Task[],
    myPostedTasks: Task[],
    myCompletedTasks: Task[],
    currentTask: Task | null,
    filters: {
      category: string,
      location: string,
      priceRange: [number, number],
      isUrgent: boolean
    },
    pagination: {
      page: number,
      size: number,
      totalPages: number
    },
    loading: boolean,
    error: string | null
  },
  
  chat: {
    chats: Chat[],
    activeChat: Chat | null,
    messages: { [chatId]: Message[] },
    unreadCounts: { [chatId]: number },
    typing: { [chatId]: boolean },
    loading: boolean,
    error: string | null
  },
  
  notifications: {
    notifications: Notification[],
    unreadCount: number,
    loading: boolean
  },
  
  ui: {
    theme: "light" | "dark",
    sidebarOpen: boolean,
    loadingStates: { [key]: boolean },
    modals: { [key]: boolean }
  }
};
```

### **API Service Layer**
```javascript
// src/services/api.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
          refreshToken: refreshToken,
        });

        const { accessToken } = response.data.data;
        localStorage.setItem('accessToken', accessToken);

        return api(originalRequest);
      } catch (refreshError) {
        // Refresh failed, redirect to login
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
```

### **Authentication Service**
```javascript
// src/services/authService.js
import api from './api';

export const authService = {
  // Register new user
  register: async (userData) => {
    const response = await api.post('/auth/register', userData);
    return response.data;
  },

  // Login user
  login: async (credentials) => {
    const response = await api.post('/auth/login', credentials);
    const { accessToken, refreshToken, user } = response.data.data;
    
    // Store tokens
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify(user));
    
    return response.data;
  },

  // Logout user
  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  },

  // Get current user
  getCurrentUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  // Check if user is authenticated
  isAuthenticated: () => {
    return !!localStorage.getItem('accessToken');
  },
};
```

### **Task Service**
```javascript
// src/services/taskService.js
import api from './api';

export const taskService = {
  // Get all tasks
  getAllTasks: async (params = {}) => {
    const response = await api.get('/tasks', { params });
    return response.data;
  },

  // Create new task
  createTask: async (taskData) => {
    const response = await api.post('/tasks', taskData);
    return response.data;
  },

  // Get task by ID
  getTaskById: async (taskId) => {
    const response = await api.get(`/tasks/${taskId}`);
    return response.data;
  },

  // Update task
  updateTask: async (taskId, taskData) => {
    const response = await api.put(`/tasks/${taskId}`, taskData);
    return response.data;
  },

  // Delete task
  deleteTask: async (taskId) => {
    const response = await api.delete(`/tasks/${taskId}`);
    return response.data;
  },

  // Search tasks
  searchTasks: async (searchParams) => {
    const response = await api.get('/tasks/search', { params: searchParams });
    return response.data;
  },

  // Apply for task
  applyForTask: async (taskId, applicationData) => {
    const response = await api.post(`/tasks/${taskId}/apply`, applicationData);
    return response.data;
  },

  // Get task applications
  getTaskApplications: async (taskId) => {
    const response = await api.get(`/tasks/${taskId}/applications`);
    return response.data;
  },

  // Update task status
  updateTaskStatus: async (taskId, status) => {
    const response = await api.put(`/tasks/${taskId}/status`, { status });
    return response.data;
  },
};
```

### **Chat Service**
```javascript
// src/services/chatService.js
import api from './api';

export const chatService = {
  // Get user chats
  getUserChats: async (userId) => {
    const response = await api.get(`/chats/user/${userId}`);
    return response.data;
  },

  // Create task chat
  createTaskChat: async (taskId) => {
    const response = await api.post(`/chats/task/${taskId}`);
    return response.data;
  },

  // Get chat messages
  getChatMessages: async (chatId, params = {}) => {
    const response = await api.get(`/chats/${chatId}/messages`, { params });
    return response.data;
  },

  // Send message
  sendMessage: async (chatId, messageData) => {
    const response = await api.post(`/chats/${chatId}/messages`, messageData);
    return response.data;
  },

  // Mark messages as read
  markMessagesAsRead: async (chatId) => {
    const response = await api.put(`/chats/${chatId}/messages/read`);
    return response.data;
  },
};
```

---

## 🎯 Implementation Priority & Roadmap

### **Phase 1: Core MVP (2-3 weeks)**
1. **Authentication System** ✅
   - Registration/Login forms with validation
   - JWT token management with refresh
   - Protected routes and auth guards
   - User context/Redux setup

2. **Task Management** ✅
   - Task listing with pagination
   - Task creation form with validation
   - Task details page with apply functionality
   - Task filtering and search

3. **Basic User Profile** ✅
   - View/edit profile forms
   - Profile picture upload
   - Verification status display
   - User statistics dashboard

### **Phase 2: Communication (1-2 weeks)**
1. **Chat System**
   - Real-time messaging interface
   - Chat list with unread counts
   - File/image sharing capability
   - WebSocket integration for real-time updates

2. **Notifications**
   - In-app notification center
   - Real-time notification updates
   - Push notifications setup (optional)

### **Phase 3: Advanced Features (2-3 weeks)**
1. **Location Integration**
   - Interactive maps for task locations
   - Location-based task filtering
   - GPS integration for mobile apps
   - Radius-based search

2. **Payment System**
   - Stripe payment integration
   - Payment history and tracking
   - Earnings dashboard
   - Transaction management

3. **Rating & Review System**
   - Review submission forms
   - Rating display components
   - Trust badges and verification
   - Reputation system

### **Phase 4: Polish & Optimization (1-2 weeks)**
1. **UI/UX Enhancements**
   - Dark/light theme toggle
   - Responsive design for all devices
   - Loading states and animations
   - Error handling and user feedback

2. **Performance Optimization**
   - Lazy loading for components
   - API response caching
   - Bundle size optimization
   - SEO improvements

---

## 🚀 Getting Started

### **Backend Setup**
1. **Prerequisites**
   - Java 17 or higher
   - PostgreSQL database
   - Redis server (optional for development)
   - Maven 3.6+

2. **Database Setup**
   ```sql
   CREATE DATABASE urbanup;
   CREATE USER root WITH PASSWORD 'sanjanRoot';
   GRANT ALL PRIVILEGES ON DATABASE urbanup TO root;
   ```

3. **Run the Application**
   ```bash
   cd urbanup
   ./mvnw spring-boot:run
   ```

4. **Verify Installation**
   ```bash
   curl -X GET http://localhost:8080/api/users/count
   # Should return: {"success":false,"message":"Unauthorized access..."}
   
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"firstName":"Test","lastName":"User","email":"test@example.com","password":"password123","phoneNumber":"+1234567890"}'
   ```

### **Frontend Setup Options**

#### **React.js Setup**
```bash
npx create-react-app urbanup-frontend
cd urbanup-frontend
npm install axios @reduxjs/toolkit react-redux react-router-dom
```

#### **Vue.js Setup**
```bash
npm create vue@latest urbanup-frontend
cd urbanup-frontend
npm install axios pinia vue-router
```

#### **React Native Setup**
```bash
npx react-native init UrbanUpMobile
cd UrbanUpMobile
npm install @react-native-async-storage/async-storage react-navigation
```

### **Testing the Integration**
```javascript
// Test authentication flow
const testAuth = async () => {
  try {
    // 1. Register a new user
    const registerResponse = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        firstName: 'Test',
        lastName: 'User',
        email: 'test@example.com',
        password: 'password123',
        phoneNumber: '+1234567890',
      }),
    });

    console.log('Register:', await registerResponse.json());

    // 2. Login with the user
    const loginResponse = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: 'test@example.com',
        password: 'password123',
      }),
    });

    const loginData = await loginResponse.json();
    console.log('Login:', loginData);

    // 3. Use the token to access protected endpoint
    const token = loginData.data.accessToken;
    const profileResponse = await fetch(`http://localhost:8080/api/users/${loginData.data.user.id}`, {
      headers: { 'Authorization': `Bearer ${token}` },
    });

    console.log('Profile:', await profileResponse.json());

  } catch (error) {
    console.error('Test failed:', error);
  }
};

// Run the test
testAuth();
```

---

## 📝 API Response Format

All API responses follow this consistent format:

```javascript
// Success Response
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { /* response data */ },
  "timestamp": "2024-12-24T10:30:00Z"
}

// Error Response
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2024-12-24T10:30:00Z",
  "path": "/api/endpoint"
}

// Paginated Response
{
  "success": true,
  "data": {
    "content": [ /* array of items */ ],
    "page": {
      "size": 20,
      "number": 0,
      "totalElements": 150,
      "totalPages": 8
    }
  }
}
```

---

## 🔐 Security Considerations

1. **JWT Token Management**
   - Store access tokens in memory or short-lived storage
   - Store refresh tokens securely (httpOnly cookies recommended)
   - Implement automatic token refresh
   - Clear tokens on logout

2. **API Security**
   - Always use HTTPS in production
   - Validate all input data
   - Implement rate limiting
   - Use CORS properly

3. **Data Validation**
   - Client-side validation for UX
   - Server-side validation for security
   - Sanitize all user inputs
   - Handle errors gracefully

---

## 📱 Mobile Development Notes

### **React Native Specific Considerations**
1. **Navigation**
   - Use React Navigation for routing
   - Implement deep linking for tasks
   - Handle back button behavior

2. **Storage**
   - Use AsyncStorage for token storage
   - Implement secure storage for sensitive data
   - Cache frequently accessed data

3. **Location Services**
   - Request location permissions
   - Implement background location updates
   - Handle location accuracy and availability

4. **Push Notifications**
   - Integrate Firebase Cloud Messaging
   - Handle notification permissions
   - Implement notification actions

---

## 🛠️ Development Tools & Resources

### **Recommended Tools**
1. **API Testing**: Postman or Insomnia
2. **State Management**: Redux Toolkit or Zustand
3. **UI Libraries**: 
   - React: Material-UI, Ant Design, or Chakra UI
   - Vue: Vuetify or Quasar
4. **Maps**: Google Maps API or Mapbox
5. **Real-time**: Socket.IO or native WebSockets

### **Useful Libraries**
```json
{
  "dependencies": {
    "axios": "^1.6.0",
    "react-query": "^3.39.0",
    "date-fns": "^2.29.0",
    "react-hook-form": "^7.43.0",
    "react-router-dom": "^6.8.0",
    "@reduxjs/toolkit": "^1.9.0",
    "react-redux": "^8.0.0"
  }
}
```

---

## 📞 Support & Documentation

### **API Documentation**
- Base URL: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html` (when implemented)
- Health Check: `http://localhost:8080/api/actuator/health`

### **Common Issues & Solutions**
1. **CORS Issues**: Configure CORS in SecurityConfig
2. **Token Expiry**: Implement refresh token logic
3. **Database Connection**: Check PostgreSQL service and credentials
4. **Port Conflicts**: Change server port in application.yaml

---

## 🎉 Conclusion

This comprehensive guide provides everything needed to build a full-featured frontend for the UrbanUp platform. The backend is fully functional with:

✅ **Complete REST API** with 22+ endpoints  
✅ **JWT Authentication** with refresh tokens  
✅ **Real-time Chat** capabilities  
✅ **Location-based** task management  
✅ **File Upload** support  
✅ **Payment Integration** ready  
✅ **Comprehensive** user management  

Start with Phase 1 (Core MVP) and gradually implement advanced features. The modular structure allows for incremental development and easy maintenance.

Happy coding! 🚀

---

*Last updated: August 4, 2025*
