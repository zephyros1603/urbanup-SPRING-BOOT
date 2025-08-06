package com.zephyros.urbanup.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.zephyros.urbanup.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * WebSocket configuration for real-time chat messaging
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple message broker to handle subscriptions and broadcast messages
        config.enableSimpleBroker("/topic", "/queue");
        
        // Define prefix for messages bound for @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app");
        
        // Define prefix for user-specific destinations
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register STOMP over WebSocket endpoint
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Configure based on your frontend domain
                .withSockJS(); // Enable SockJS fallback
        
        // Additional endpoint without SockJS for native WebSocket clients
        registry.addEndpoint("/ws-native")
                .setAllowedOriginPatterns("*");
                
        // Add endpoint for token-based authentication via query parameter
        registry.addEndpoint("/ws-auth")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Try to extract JWT token from headers first
                    String authToken = accessor.getFirstNativeHeader("Authorization");
                    
                    // If not in headers, try to get from query parameters (for SockJS compatibility)
                    if (authToken == null || !authToken.startsWith("Bearer ")) {
                        authToken = accessor.getFirstNativeHeader("token");
                        if (authToken != null && !authToken.startsWith("Bearer ")) {
                            authToken = "Bearer " + authToken;
                        }
                    }
                    
                    if (authToken != null && authToken.startsWith("Bearer ")) {
                        String token = authToken.substring(7);
                        
                        try {
                            // Validate JWT token
                            if (jwtUtil.validateToken(token)) {
                                String username = jwtUtil.extractUsername(token);
                                
                                // Set authentication in WebSocket session
                                UsernamePasswordAuthenticationToken auth = 
                                    new UsernamePasswordAuthenticationToken(username, null, null);
                                accessor.setUser(auth);
                                SecurityContextHolder.getContext().setAuthentication(auth);
                            }
                        } catch (Exception e) {
                            // Invalid token - connection will be rejected
                            System.err.println("WebSocket authentication failed: " + e.getMessage());
                        }
                    }
                }
                
                return message;
            }
        });
    }
}
