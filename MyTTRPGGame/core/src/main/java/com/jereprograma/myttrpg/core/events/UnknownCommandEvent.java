package com.jereprograma.myttrpg.core.events;

public record UnknownCommandEvent(String input) implements ResultEvent {
}
