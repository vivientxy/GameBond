package tfip.project.controller;

import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

import tfip.project.service.TelegramBotService;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final TelegramBotService telegramBot;

    public WebhookController(TelegramBotService telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostMapping
    public void onUpdateReceived(@RequestBody Update update) {
        telegramBot.onUpdateReceived(update);
    }
}