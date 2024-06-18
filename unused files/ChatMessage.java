package tfip.project.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String gameId;
    private String sender;
    private String team;
    private String content;
    private ChatMessageType type;
}
