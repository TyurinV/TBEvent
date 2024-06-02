package ru.testikov.tbot.event_checker.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.testikov.tbot.event_checker.service.TelegramBot;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BotConfig {

    private final TelegramBot telegramBot;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        try {
            TelegramBotsApi tbpa = new TelegramBotsApi(DefaultBotSession.class);
            tbpa.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            log.error("Не удалось создать бота");
        }
    }

}
