package cz.fraloily.domain;

import java.util.Optional;

public record Course(
        String id,
        String name,
        long length,
        String url,
        Optional<String> notes
) {
    public Course {
        checkFilled(id, name, url);
        notes.ifPresent(Course::checkFilled);
    }

    private static void checkFilled(String... strings) {
        for (var s : strings) {
            if(s == null || s.isBlank()) {
                throw new IllegalArgumentException("No value present!");
            }
        }
    }
}
