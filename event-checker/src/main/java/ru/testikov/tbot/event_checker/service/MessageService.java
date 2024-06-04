package ru.testikov.tbot.event_checker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.testikov.tbot.event_checker.checkers.EventChecker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.testikov.tbot.event_checker.service.TelegramBotConstantContent.NEW_EVENT;
import static ru.testikov.tbot.event_checker.service.TelegramBotConstantContent.OLD_EVENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {


    public SendMessage processTextMessage(Update updateEvent) {
        SendMessage defaultMessage = new SendMessage();
        defaultMessage.setText("Мне нечего ответить");
        String text = updateEvent.getMessage().getText();
        Long chatId = updateEvent.getMessage().getChatId();

        String responseText;
        switch (text) {
            default -> responseText = "Кидай сюда свой scv";
        }
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(responseText);
        return message;
    }

    public SendMessage processSCV(File localFile, Message message, EventChecker scvChecker) {
        List<String> eventsFromFile = new ArrayList<>();
        List<String> oldList = List.of(OLD_EVENT.split(","));
        List<String> newList = List.of(NEW_EVENT.split(","));

        try {
            eventsFromFile = Files.readAllLines(Path.of(localFile.getPath()))
                    .stream()
                    .map(line -> Arrays.asList(line.split(",")))
                    .flatMap(List::stream)
                    .map(str -> str.replaceAll(("^\"|\"$"), ""))
                    .collect(Collectors.toList());

        } catch (IOException ex) {
            log.error("Не смогли прочитать файл");
        }
        // Отправка результата пользователю
        return new SendMessage(message.getChatId().toString(),
                "❗Это не выпало в старых событиях: \n" +
                        scvChecker.notHeppendEvent(oldList, eventsFromFile).toString() + "\n \n" +
                        "❗ Это не выпало в новых событиях: \n" +
                        scvChecker.notHeppendEvent(newList, eventsFromFile).toString());
    }
}


