package ru.testikov.tbot.event_checker.checkers;

import java.util.List;

public interface EventChecker {
    List<String> notHeppendEvent(List<String> ourEvent, List<String> heppendEvent);
}
