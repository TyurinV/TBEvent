package ru.testikov.tbot.event_checker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.testikov.tbot.event_checker.checkers.EventChecker;
import ru.testikov.tbot.event_checker.config.properties.BotProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotProperties bp;
    private final MessageService ms;
    private final EventChecker scvChecker;

    List<String> oldList = List.of(TelegramBotConstantContent.OLD_EVENT.split(","));
    List<String> newList = List.of(TelegramBotConstantContent.NEW_EVENT.split(","));

    List<String> eventsFromFile = new ArrayList<>();

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
                Message message = updateEvent.getMessage();
                String fileId = message.getDocument().getFileId();
                String fileName = message.getDocument().getFileName();
                if (fileName.endsWith(".csv")) {
                    try {
                        GetFile getFile = new GetFile();
                        getFile.setFileId(fileId);
                        org.telegram.telegrambots.meta.api.objects.File file = execute(getFile); // Получение файла
                        File localFile = downloadFile(file); // Загрузка файла
                        eventsFromFile = Files.readAllLines(Path.of(localFile.getPath()))
                                .stream()
                                .map(line -> Arrays.asList(line.split(",")))
                                .flatMap(List::stream)
                                .map(str -> str.replaceAll(("^\"|\"$"), ""))
                                .collect(Collectors.toList());
                        // Отправка результата пользователю
                        execute(new SendMessage(message.getChatId().toString(), "Это не выпало в старых событиях: \n" + scvChecker.notHeppendEvent(oldList, eventsFromFile).toString()));
                        execute(new SendMessage(message.getChatId().toString(), "Это не выпало в новых событиях: \n" + scvChecker.notHeppendEvent(newList, eventsFromFile).toString()));
                    } catch (TelegramApiException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        execute(new SendMessage(message.getChatId().toString(), "Я принимаю только файлы CSV."));
                    } catch (TelegramApiException e) {
                        log.error("Не смогли прочитать файл");
                    }
                }
            }
        } catch (TelegramApiException e) {
            log.error("При ответе пользоватю возникла проблема");
        }
    }
}
