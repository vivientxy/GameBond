package tfip.project.model;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import jakarta.annotation.PostConstruct;

@SuppressWarnings("deprecation")
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.username}")
    private String username;

    @Value("${telegram.token}")
    private String token;

    @Value("${telegram.webhook-url}")
    private String webhookUrl;

    @PostConstruct
    public void init() {
        SetWebhook setWebhook = SetWebhook.builder()
                .url(webhookUrl)
                .build();
        try {
            this.execute(setWebhook);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

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
        var msg = update.getMessage();
        var user = msg.getFrom();
        var chatId = msg.getChatId();
        String messageText = msg.getText();

        // Handle /start command with payload
        if (messageText.startsWith("/start")) {
            handleStartCommand(chatId, messageText);
            return;
        }

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

        String message = msg.getText();
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

        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setText(user.getFirstName() + " wrote " + message);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(msg.getChatId());

        // sendText(id, user.getFirstName() + " wrote " + msg.getText());

        System.out.println(user.getFirstName() + " wrote " + message);

        try {
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                            .chatId(who.toString()) //Who are we sending a message to
                            .text(what).build();    //Message content
        try {
                execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
                throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleStartCommand(Long chatId, String messageText) {
        String payload = "";
        if (messageText.length() > 6)
            payload = messageText.substring(7);
        if (!payload.isEmpty()) {
            String decodedUrl = new String(Base64.getDecoder().decode(payload), StandardCharsets.UTF_8);
            Map<String, String> params = getQueryParams(decodedUrl);
            System.out.println(params); // {hostId=a1b2c3d4}
            sendMessage(chatId, "Parsed parameters: " + params.toString());
        } else {
            sendMessage(chatId, "Welcome to the bot! No payload received.");
        }
    }

    private Map<String, String> getQueryParams(String url) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = url.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length > 1) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }
    
}
