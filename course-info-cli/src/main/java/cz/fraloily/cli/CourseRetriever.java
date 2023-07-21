package cz.fraloily.cli;

import cz.fraloily.repository.CourseRepository;
import cz.fraloily.service.CourseRetrieverService;
import cz.fraloily.service.CourseStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CourseRetriever {
    private static final Logger log = LoggerFactory.getLogger(CourseRetriever.class);
    private static final CourseRetrieverService courseRetrieverService = new CourseRetrieverService();

    public static void main(String[] args) {
        log.info("CourseRetriever started!");

        if(args.length == 0) {
            log.warn("Please provide an author name as first argument.");
            return;
        }

        try {
            retrieveCourses(args[0]);
        } catch (Exception e) {
            log.error("Unexpected error", e);
        }
    }

    private static void retrieveCourses(String authorId) {
        log.info("Retrieving courses for author '{}'", authorId);
        var coursesToStore = courseRetrieverService.getCoursesFor(authorId);
        var courseRepository = CourseRepository.openCourseRepository("./courses.db");

        var courseStorageService = new CourseStorageService(courseRepository);
        log.info("Retrieving the following {} courses: {}", coursesToStore.size(), coursesToStore);
        courseStorageService.storePluralsightCourses(coursesToStore);
        log.info("Courses successfully stored.");
    }

}
