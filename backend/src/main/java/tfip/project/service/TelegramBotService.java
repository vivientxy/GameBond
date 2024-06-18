package tfip.project.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import tfip.project.repo.RedisRepository;

@SuppressWarnings("deprecation")
@Component
public class TelegramBotService extends TelegramLongPollingBot {

    @Value("${telegram.username}")
    private String username;

    @Value("${telegram.token}")
    private String token;

    // @Value("${telegram.webhook-url}")
    // private String webhookUrl;

    @Value("${project.url}")
    private String projectUrl;

    @Autowired
    private RedisRepository redisRepo;

    // @PostConstruct
    // public void init() {
    // SetWebhook setWebhook = SetWebhook.builder()
    // .url(webhookUrl)
    // .build();
    // try {
    // this.execute(setWebhook);
    // } catch (TelegramApiException e) {
    // e.printStackTrace();
    // }
    // }

    // @Override
    // public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method
    // 'onWebhookUpdateReceived'");
    // }

    // @Override
    // public String getBotPath() {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'getBotPath'");
    // }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();

        if (update.hasMessage() && update.getMessage().hasText()) {
            var receivedMessage = update.getMessage();
            var user = receivedMessage.getFrom();
            Long chatId = receivedMessage.getChatId();
            String message = receivedMessage.getText();

            System.out.println(">>> msg: " + message);

            // get host ID and team ID
            if (message.startsWith("/start")) {
                sendMessage = handleStartCommand(chatId, message);
            } else {
                // replace message symbols with Strings --> these should correspond with emulator input
                switch (message) {
                    case "⬆":
                        message = "UP";
                        break;
                    case "⬅":
                        message = "LEFT";
                        break;
                    case "➡":
                        message = "RIGHT";
                        break;
                    case "⬇":
                        message = "DOWN";
                        break;
                    default:
                        break;
                }
                String responseText = "Input received from " + user.getFirstName() + ": " + message;
                ReplyKeyboardMarkup replyKeyboardMarkup = generateControllerKeyboardMarkup();
                sendMessage = createMessage(chatId, responseText, replyKeyboardMarkup);

                // TODO: if msg == input command --> send to kafka queue

            }
        } else if (update.hasCallbackQuery()) {
            // USER JUST SELECTED TEAM
            sendMessage = selectedTeam(update);
        }

        try {
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* HANDLE MESSAGE SCENARIOS */

    private SendMessage handleStartCommand(Long chatId, String messageText) {
        if (messageText.length() <= 6) {
            // no hostId:
            return redirectToJoinGame(chatId);
        } else {
            String payload = messageText.substring(7);
            String decodedUrl = new String(Base64.getDecoder().decode(payload), StandardCharsets.UTF_8);
            Map<String, String> params = getQueryParams(decodedUrl);
            String hostId = params.get("hostId");
            Integer numOfTeams = Integer.parseInt(hostId.substring(7));

            // hostId + numOfTeams obtained -- prompt team selection
            // TODO: IF numOfTeams == 1, DON'T NEED INLINE KEYBOARD. ASSIGN TEAM VALUE TO A

            String responseText = "Please select your team!";
            InlineKeyboardMarkup teamKeyboardMarkup = generateTeamKeyboardMarkup(numOfTeams, hostId);
            return createMessage(chatId, responseText, teamKeyboardMarkup);
        }
    }

    private SendMessage redirectToJoinGame(Long chatId) {
        String responseText = "Welcome to GameBond! Click here to join a game:";
        InlineKeyboardMarkup joinKeyboardMarkup = generateJoinKeyboardMarkup();
        return createMessage(chatId, responseText, joinKeyboardMarkup);
    }
    
    private SendMessage selectedTeam(Update update) {
        System.out.println("========== USER JUST SELECTED TEAM =============");
        String data = update.getCallbackQuery().getData();
        var msg = (Message) update.getCallbackQuery().getMessage();
        String hostId = data.split(",")[0];
        String teamId = data.split(",")[1];
        String firstname = msg.getChat().getFirstName();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (redisRepo.playerExists(chatId))
            redisRepo.deletePlayer(chatId);
        redisRepo.savePlayerInfo(chatId, hostId, teamId);

        String responseText = "Welcome to " + teamId + ", " + firstname + "! Please check the game screen and wait for the game to start :)";
        ReplyKeyboardMarkup replyKeyboardMarkup = generateControllerKeyboardMarkup();
        return createMessage(chatId, responseText, replyKeyboardMarkup);
    }

    /* KEYBOARD MARKUPS */

    private ReplyKeyboardMarkup generateControllerKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new LinkedList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();

        KeyboardButton buttonUp = new KeyboardButton("⬆");
        KeyboardButton buttonLeft = new KeyboardButton("⬅");
        KeyboardButton buttonRight = new KeyboardButton("➡");
        KeyboardButton buttonDown = new KeyboardButton("⬇");
        KeyboardButton buttonA = new KeyboardButton("A");
        KeyboardButton buttonB = new KeyboardButton("B");
        KeyboardButton buttonL = new KeyboardButton("L");
        KeyboardButton buttonR = new KeyboardButton("R");
        KeyboardButton buttonStart = new KeyboardButton("START");
        KeyboardButton buttonSelect = new KeyboardButton("SELECT");

        row1.add(buttonL);
        row1.add(buttonUp);
        row1.add(buttonR);

        row2.add(buttonA);
        row2.add(buttonLeft);
        row2.add(buttonRight);
        row2.add(buttonB);

        row3.add(buttonStart);
        row3.add(buttonDown);
        row3.add(buttonSelect);

        keyboardRowList.add(row1);
        keyboardRowList.add(row2);
        keyboardRowList.add(row3);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        return replyKeyboardMarkup;
    }

    private InlineKeyboardMarkup generateTeamKeyboardMarkup(Integer numOfTeams, String hostId) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        List<String> teams = Arrays.asList("Team A", "Team B", "Team C", "Team D");
        for (int i = 0; i < numOfTeams; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton(teams.get(i));
            button.setCallbackData(hostId + "," + teams.get(i));
            rowInline.add(button);
        }

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    private InlineKeyboardMarkup generateJoinKeyboardMarkup() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton("Join Game");
        button.setUrl(projectUrl + "/join");

        rowInline.add(button);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    /* MISC UTILITY METHODS */

    private Map<String, String> getQueryParams(String url) {
        Map<String, String> queryParams = new HashMap<>();
        String[] pairs = url.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length > 1)
                queryParams.put(keyValue[0], keyValue[1]);
        }
        return queryParams;
    }

    private SendMessage createMessage(Long chatId, String text, ReplyKeyboard keyboardMarkup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(keyboardMarkup);
        return msg;
    }


    // // UNUSABLE CODE -- telegram limitations (30 msges per sec)
    // public void broadcastMessage(String hostId, String text) {
    //     List<String> playerList = redisRepo.getPlayersInHost(hostId);
    //     for (String chatIdString : playerList) {
    //         Long chatId = Long.valueOf(chatIdString);
    //         SendMessage msg = new SendMessage();
    //         msg.setChatId(chatId);
    //         msg.setText(text);

    //         try {
    //             execute(msg);
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //         }
    //     }
    // }

}
