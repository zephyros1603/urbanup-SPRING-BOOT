// Real-time Chat Component for React Frontend
// This component demonstrates full integration with the WebSocket chat API

import React, { useState, useEffect, useRef, useCallback } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { 
    Paper, 
    TextField, 
    Button, 
    List, 
    ListItem, 
    ListItemText, 
    Typography, 
    Avatar, 
    Box, 
    Chip, 
    IconButton,
    CircularProgress,
    Alert
} from '@mui/material';
import { 
    Send as SendIcon, 
    AttachFile as AttachFileIcon,
    Circle as CircleIcon
} from '@mui/icons-material';
import { format } from 'date-fns';
import { authService } from '../services/authService';
import { apiClient } from '../services/apiClient';

const RealtimeChatComponent = ({ chatId, taskId, fulfillerId }) => {
    // State management
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [isConnected, setIsConnected] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [typingUsers, setTypingUsers] = useState(new Set());
    const [onlineUsers, setOnlineUsers] = useState(new Set());
    const [currentUser, setCurrentUser] = useState(null);
    
    // Refs
    const stompClient = useRef(null);
    const messagesEndRef = useRef(null);
    const typingTimeoutRef = useRef(null);
    const messageInputRef = useRef(null);
    
    // Initialize component
    useEffect(() => {
        initializeChat();
        return () => {
            disconnect();
        };
    }, [chatId]);
    
    // Auto-scroll to bottom when new messages arrive
    useEffect(() => {
        scrollToBottom();
    }, [messages]);
    
    const initializeChat = async () => {
        try {
            setIsLoading(true);
            setError(null);
            
            // Get current user
            const user = authService.getCurrentUser();
            setCurrentUser(user);
            
            // Create or get chat if needed
            if (!chatId && taskId && fulfillerId) {
                await createChat();
            }
            
            // Load existing messages
            await loadMessages();
            
            // Connect to WebSocket
            await connectWebSocket();
            
        } catch (err) {
            console.error('Failed to initialize chat:', err);
            setError('Failed to initialize chat. Please try again.');
        } finally {
            setIsLoading(false);
        }
    };
    
    const createChat = async () => {
        try {
            const response = await apiClient.post(
                `/realtime-chat/create/${taskId}?fulfillerId=${fulfillerId}`
            );
            
            if (response.data.success) {
                // Chat created successfully, chatId should be updated by parent component
                console.log('Chat created:', response.data.data);
            } else {
                throw new Error(response.data.message);
            }
        } catch (err) {
            console.error('Failed to create chat:', err);
            throw err;
        }
    };
    
    const loadMessages = async () => {
        try {
            const response = await apiClient.get(`/realtime-chat/${chatId}/messages`);
            
            if (response.data.success) {
                setMessages(response.data.data);
                // Mark messages as read
                await markMessagesAsRead();
            } else {
                throw new Error(response.data.message);
            }
        } catch (err) {
            console.error('Failed to load messages:', err);
            throw err;
        }
    };
    
    const markMessagesAsRead = async () => {
        try {
            await apiClient.post(`/realtime-chat/${chatId}/mark-read`);
        } catch (err) {
            console.error('Failed to mark messages as read:', err);
        }
    };
    
    const connectWebSocket = () => {
        return new Promise((resolve, reject) => {
            try {
                const token = authService.getToken();
                const socket = new SockJS(`${process.env.REACT_APP_API_URL}/ws`);
                
                stompClient.current = Stomp.over(socket);
                
                // Connect with authentication
                stompClient.current.connect(
                    { Authorization: `Bearer ${token}` },
                    (frame) => {
                        console.log('Connected to WebSocket:', frame);
                        setIsConnected(true);
                        setupSubscriptions();
                        resolve();
                    },
                    (error) => {
                        console.error('WebSocket connection error:', error);
                        setIsConnected(false);
                        reject(error);
                    }
                );
            } catch (err) {
                console.error('Failed to create WebSocket connection:', err);
                reject(err);
            }
        });
    };
    
    const setupSubscriptions = () => {
        if (!stompClient.current || !stompClient.current.connected) {
            return;
        }
        
        // Subscribe to chat messages
        stompClient.current.subscribe(`/topic/chat/${chatId}`, (message) => {
            const newMessage = JSON.parse(message.body);
            setMessages(prev => [...prev, newMessage]);
            
            // Mark as read if not from current user
            if (newMessage.senderId !== currentUser?.id) {
                markMessagesAsRead();
            }
        });
        
        // Subscribe to typing indicators
        stompClient.current.subscribe(`/topic/chat/${chatId}/typing`, (message) => {
            const typingData = JSON.parse(message.body);
            handleTypingIndicator(typingData);
        });
        
        // Subscribe to presence updates
        stompClient.current.subscribe(`/topic/chat/${chatId}/presence`, (message) => {
            const presenceData = JSON.parse(message.body);
            handlePresenceUpdate(presenceData);
        });
        
        // Subscribe to read status updates
        stompClient.current.subscribe(`/topic/chat/${chatId}/read`, (message) => {
            const readData = JSON.parse(message.body);
            handleReadStatusUpdate(readData);
        });
        
        // Send initial presence
        sendPresenceUpdate('online');
    };
    
    const disconnect = () => {
        if (stompClient.current) {
            sendPresenceUpdate('offline');
            stompClient.current.disconnect();
            setIsConnected(false);
        }
    };
    
    const sendMessage = async () => {
        if (!newMessage.trim() || !isConnected) {
            return;
        }
        
        try {
            // Send via REST API for reliability
            const response = await apiClient.post(`/realtime-chat/${chatId}/send`, {
                content: newMessage.trim()
            });
            
            if (response.data.success) {
                setNewMessage('');
                // Also send via WebSocket for real-time updates
                if (stompClient.current && stompClient.current.connected) {
                    stompClient.current.send(`/app/chat/${chatId}/send`, {}, JSON.stringify({
                        content: newMessage.trim(),
                        messageType: 'TEXT'
                    }));
                }
            } else {
                setError('Failed to send message');
            }
        } catch (err) {
            console.error('Failed to send message:', err);
            setError('Failed to send message. Please try again.');
        }
    };
    
    const handleTypingIndicator = (typingData) => {
        if (typingData.userId === currentUser?.id) {
            return; // Ignore own typing
        }
        
        setTypingUsers(prev => {
            const newSet = new Set(prev);
            if (typingData.isTyping) {
                newSet.add(typingData.userId);
            } else {
                newSet.delete(typingData.userId);
            }
            return newSet;
        });
    };
    
    const handlePresenceUpdate = (presenceData) => {
        if (presenceData.userId === currentUser?.id) {
            return; // Ignore own presence
        }
        
        setOnlineUsers(prev => {
            const newSet = new Set(prev);
            if (presenceData.status === 'online') {
                newSet.add(presenceData.userId);
            } else {
                newSet.delete(presenceData.userId);
            }
            return newSet;
        });
    };
    
    const handleReadStatusUpdate = (readData) => {
        // Update message read status in UI if needed
        console.log('Messages read by user:', readData.userId);
    };
    
    const sendTypingIndicator = (isTyping) => {
        if (stompClient.current && stompClient.current.connected) {
            stompClient.current.send(`/app/chat/${chatId}/typing`, {}, JSON.stringify({
                isTyping: isTyping
            }));
        }
    };
    
    const sendPresenceUpdate = (status) => {
        if (stompClient.current && stompClient.current.connected) {
            stompClient.current.send(`/app/chat/${chatId}/presence`, {}, JSON.stringify({
                status: status
            }));
        }
    };
    
    const handleInputChange = (e) => {
        setNewMessage(e.target.value);
        
        // Send typing indicator
        sendTypingIndicator(true);
        
        // Clear previous timeout
        if (typingTimeoutRef.current) {
            clearTimeout(typingTimeoutRef.current);
        }
        
        // Set timeout to stop typing indicator
        typingTimeoutRef.current = setTimeout(() => {
            sendTypingIndicator(false);
        }, 1000);
    };
    
    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    };
    
    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };
    
    const formatMessageTime = (timestamp) => {
        return format(new Date(timestamp), 'HH:mm');
    };
    
    const formatMessageDate = (timestamp) => {
        return format(new Date(timestamp), 'MMM dd, yyyy');
    };
    
    const handleFileUpload = async (event) => {
        const file = event.target.files[0];
        if (!file) return;
        
        try {
            const formData = new FormData();
            formData.append('file', file);
            formData.append('caption', `Shared ${file.name}`);
            
            const response = await apiClient.post(
                `/realtime-chat/${chatId}/send-media`,
                formData,
                {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                }
            );
            
            if (!response.data.success) {
                setError('Failed to upload file');
            }
        } catch (err) {
            console.error('Failed to upload file:', err);
            setError('Failed to upload file. Please try again.');
        }
        
        // Clear the input
        event.target.value = '';
    };
    
    const renderMessage = (message, index) => {
        const isOwnMessage = message.senderId === currentUser?.id;
        const isSystemMessage = message.messageType === 'SYSTEM';
        
        return (
            <ListItem
                key={message.id || index}
                sx={{
                    flexDirection: isSystemMessage ? 'column' : 'row',
                    justifyContent: isOwnMessage && !isSystemMessage ? 'flex-end' : 'flex-start',
                    alignItems: isSystemMessage ? 'center' : 'flex-start',
                    mb: 1
                }}
            >
                {isSystemMessage ? (
                    <Chip
                        label={message.content}
                        size="small"
                        variant="outlined"
                        sx={{ bgcolor: 'grey.100' }}
                    />
                ) : (
                    <Box
                        sx={{
                            maxWidth: '70%',
                            bgcolor: isOwnMessage ? 'primary.main' : 'grey.100',
                            color: isOwnMessage ? 'primary.contrastText' : 'text.primary',
                            borderRadius: 2,
                            p: 1.5,
                            ml: isOwnMessage ? 0 : 1,
                            mr: isOwnMessage ? 1 : 0
                        }}
                    >
                        {!isOwnMessage && (
                            <Typography variant="caption" color="text.secondary">
                                {message.senderName}
                            </Typography>
                        )}
                        <Typography variant="body2">
                            {message.content}
                        </Typography>
                        {message.attachmentUrl && (
                            <Box sx={{ mt: 1 }}>
                                {message.messageType === 'IMAGE' ? (
                                    <img
                                        src={message.attachmentUrl}
                                        alt="Attachment"
                                        style={{
                                            maxWidth: '100%',
                                            maxHeight: '200px',
                                            borderRadius: '8px'
                                        }}
                                    />
                                ) : (
                                    <Button
                                        size="small"
                                        href={message.attachmentUrl}
                                        target="_blank"
                                        startIcon={<AttachFileIcon />}
                                    >
                                        Download File
                                    </Button>
                                )}
                            </Box>
                        )}
                        <Typography
                            variant="caption"
                            sx={{
                                display: 'block',
                                textAlign: 'right',
                                mt: 0.5,
                                opacity: 0.7
                            }}
                        >
                            {formatMessageTime(message.createdAt)}
                            {isOwnMessage && message.isRead && ' ✓✓'}
                        </Typography>
                    </Box>
                )}
            </ListItem>
        );
    };
    
    if (isLoading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" height="400px">
                <CircularProgress />
            </Box>
        );
    }
    
    return (
        <Paper elevation={2} sx={{ height: '500px', display: 'flex', flexDirection: 'column' }}>
            {/* Header */}
            <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider' }}>
                <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Typography variant="h6">Chat</Typography>
                    <Box display="flex" alignItems="center" gap={1}>
                        <CircleIcon
                            sx={{
                                fontSize: 12,
                                color: isConnected ? 'success.main' : 'error.main'
                            }}
                        />
                        <Typography variant="caption">
                            {isConnected ? 'Connected' : 'Disconnected'}
                        </Typography>
                    </Box>
                </Box>
                {typingUsers.size > 0 && (
                    <Typography variant="caption" color="text.secondary">
                        Someone is typing...
                    </Typography>
                )}
            </Box>
            
            {/* Error Alert */}
            {error && (
                <Alert severity="error" onClose={() => setError(null)} sx={{ m: 1 }}>
                    {error}
                </Alert>
            )}
            
            {/* Messages List */}
            <Box sx={{ flexGrow: 1, overflow: 'auto', p: 1 }}>
                <List sx={{ pb: 0 }}>
                    {messages.map((message, index) => renderMessage(message, index))}
                </List>
                <div ref={messagesEndRef} />
            </Box>
            
            {/* Message Input */}
            <Box sx={{ p: 2, borderTop: 1, borderColor: 'divider' }}>
                <Box display="flex" gap={1} alignItems="flex-end">
                    <input
                        type="file"
                        hidden
                        onChange={handleFileUpload}
                        id="file-upload"
                        accept="image/*,.pdf,.doc,.docx,.txt"
                    />
                    <label htmlFor="file-upload">
                        <IconButton component="span" size="small">
                            <AttachFileIcon />
                        </IconButton>
                    </label>
                    
                    <TextField
                        ref={messageInputRef}
                        fullWidth
                        multiline
                        maxRows={3}
                        placeholder="Type a message..."
                        value={newMessage}
                        onChange={handleInputChange}
                        onKeyPress={handleKeyPress}
                        disabled={!isConnected}
                        size="small"
                    />
                    
                    <Button
                        variant="contained"
                        onClick={sendMessage}
                        disabled={!newMessage.trim() || !isConnected}
                        sx={{ minWidth: 'auto', px: 2 }}
                    >
                        <SendIcon />
                    </Button>
                </Box>
            </Box>
        </Paper>
    );
};

export default RealtimeChatComponent;
