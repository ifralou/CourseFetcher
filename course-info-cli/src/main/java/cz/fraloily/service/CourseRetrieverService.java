package cz.fraloily.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

import static java.util.function.Predicate.not;

public class CourseRetrieverService {
    private static final String coursesURL = "https://app.pluralsight.com/profile/data/author/%s/all-content";
    private static final HttpClient client = HttpClient.
            newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    private static final ObjectMapper mapper = new ObjectMapper();

    public List<PluralSightCourse> getCoursesFor(String authorId) {
        try {

            HttpResponse<String> response = client.send(
                    getServiceURL(authorId),
                    HttpResponse.BodyHandlers.ofString()
            );

            return switch (response.statusCode()) {
                case 200 -> mapToCourses(response).stream()
                        .filter(not(PluralSightCourse::isRetired))
                        .toList();
                case 404 -> Collections.emptyList();
                default -> throw new RuntimeException("Pluralsight API call failed with status code " + response.statusCode());
            };

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Could not call Pluralsight API");
        }
    }

    private static List<PluralSightCourse> mapToCourses(HttpResponse<String> response) throws JsonProcessingException {
        JavaType returnType = mapper.getTypeFactory()
                        .constructCollectionType(List.class, PluralSightCourse.class);
        return mapper.readValue(response.body(), returnType);
    }

    public HttpRequest getServiceURL(String authorId) {
        return HttpRequest
                .newBuilder(URI.create(coursesURL.formatted(authorId)))
                .GET().build();
    }

}
