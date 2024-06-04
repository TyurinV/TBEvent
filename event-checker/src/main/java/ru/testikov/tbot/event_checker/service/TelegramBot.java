package ru.testikov.tbot.event_checker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.testikov.tbot.event_checker.checkers.EventChecker;
import ru.testikov.tbot.event_checker.checkers.SCVChecker;
import ru.testikov.tbot.event_checker.config.properties.BotProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotProperties bp;
    private final MessageService ms;
    private final EventChecker scvChecker;

    @Override
    public String getBotUsername() {
        return bp.name();
    }

    @Override
    public String getBotToken() {
        return bp.token();
    }

    @Override
    public void onUpdateReceived(Update updateEvent) {
        try {
            if (updateEvent.hasMessage() && updateEvent.getMessage().hasText()) {
                execute(ms.processTextMessage(updateEvent));
            }
            if (updateEvent.hasMessage() && updateEvent.getMessage().hasDocument()) {
                String fileId = updateEvent.getMessage().getDocument().getFileId();
                GetFile getFile = new GetFile();
                getFile.setFileId(fileId);
                org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
                File localFile = downloadFile(file);
                execute(ms.processSCV(localFile, updateEvent.getMessage(), scvChecker));
            }
        } catch (TelegramApiException e) {
            log.error("При ответе пользоватю возникла проблема");
        }
    }
}
