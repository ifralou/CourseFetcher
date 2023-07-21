package cz.fraloily;

import cz.fraloily.repository.CourseRepository;
import cz.fraloily.domain.Course;
import cz.fraloily.repository.RepositoryException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Path("/courses")
public class CourseResource {
    private static final Logger log = LoggerFactory.getLogger(CourseResource.class);

    private final CourseRepository courseRepository;

    public CourseResource(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Stream<Course> getCourses() {
        try {
            return courseRepository.getAllCourses().stream()
                    .sorted(Comparator.comparing(Course::id));
        } catch (RepositoryException e) {
            log.error("Could not retrieve courses from the database", e);
            throw new NotFoundException();
        }
    }

    @POST
    @Path("/{id}/notes")
    @Consumes(MediaType.TEXT_PLAIN)
    public void addNotes(
            @PathParam("id") String id,
            String notes
    ) {
        courseRepository.addNotes(id, notes);
    }

}