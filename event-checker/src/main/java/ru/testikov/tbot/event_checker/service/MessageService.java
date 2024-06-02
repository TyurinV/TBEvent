package ru.testikov.tbot.event_checker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {


    public SendMessage processTextMessage(Update updateEvent) {
        var defaultMessage = new SendMessage();
        defaultMessage.setText("Мне нечего ответить");
        String text = updateEvent.getMessage().getText();
        Long chatId = updateEvent.getMessage().getChatId();
        String name = updateEvent.getMessage().getChat().getFirstName();

        String responseText;
        switch (text) {
            default -> responseText = "Кидай сюда свой scv";
        }
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(responseText);
        return message;
    }
}


