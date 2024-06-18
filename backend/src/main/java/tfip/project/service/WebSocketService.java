package tfip.project.service;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    // @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessage(String topicSuffix, String messagePayload) {
        messagingTemplate.convertAndSend("/topic/" + topicSuffix, messagePayload);
    }

    public void sendMessagePlayerAdded(String topicSuffix) {
        messagingTemplate.convertAndSend("/topic/" + topicSuffix, "Player added");
    }
    
}
