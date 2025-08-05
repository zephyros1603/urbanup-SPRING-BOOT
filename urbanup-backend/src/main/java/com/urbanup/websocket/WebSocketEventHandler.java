package com.urbanup.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
public class WebSocketEventHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void handleSessionConnect(SessionConnectEvent event) {
        // Handle session connect event
        String sessionId = event.getSessionId();
        messagingTemplate.convertAndSend("/topic/connect", "User connected: " + sessionId);
    }

    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        // Handle session disconnect event
        String sessionId = event.getSessionId();
        messagingTemplate.convertAndSend("/topic/disconnect", "User disconnected: " + sessionId);
    }

    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        // Handle session subscribe event
        String sessionId = event.getSessionId();
        messagingTemplate.convertAndSend("/topic/subscribe", "User subscribed: " + sessionId);
    }

    public void handleSessionUnsubscribe(SessionUnsubscribeEvent event) {
        // Handle session unsubscribe event
        String sessionId = event.getSessionId();
        messagingTemplate.convertAndSend("/topic/unsubscribe", "User unsubscribed: " + sessionId);
    }
}