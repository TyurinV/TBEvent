package ru.testikov.tbot.event_checker.checkers;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SCVChecker implements EventChecker {
    @Override
    public List<String> notHeppendEvent(List<String> ourEvent, List<String> happenedEvent) {
        List<String> result = new ArrayList<>();
        for (String ourString : ourEvent) {
            if (!happenedEvent.contains(ourString)) {
                result.add(ourString);
            }
        }
        return result;
    }
}
