package cz.fraloily.service;

import cz.fraloily.domain.Course;
import cz.fraloily.repository.CourseRepository;

import java.util.List;
import java.util.Optional;

public class CourseStorageService {
    private final CourseRepository courseRepository;
    private final String PS_BASE_URL = "https://app.pluralsight.com";

    public CourseStorageService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public void storePluralsightCourses(List<PluralSightCourse> courses) {
        for (var course : courses) {
            var dbCourse = new Course(
                    course.id(),
                    course.title(),
                    course.durationInMinutes(),
                    PS_BASE_URL + course.contentUrl(),
                    Optional.empty()
            );
            courseRepository.saveCourse(dbCourse);
        }
    }
}
