package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {
    @Test
    void whenMultiCallHasNextThenTrue() {
        String date = "2025-04-13T10:28:00+03:00";
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        assertThat(parser.parse(date)).isEqualTo(LocalDateTime.of(2025, 4, 13, 10, 28, 0));
    }
}