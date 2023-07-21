package cz.fraloily.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class PluralSightCourseTest {


    @ParameterizedTest
    @CsvSource(textBlock =
    """
    01:08:54.96233, 68
    00:05:37, 5
    00:00:00.0, 0
    """)
    void durationInMinutes(String input, long expected) {
        var pluralSightCourse = new PluralSightCourse(
                "id", "Test course", input, "url", false
        );

        assertEquals(expected, pluralSightCourse.durationInMinutes());
    }

}