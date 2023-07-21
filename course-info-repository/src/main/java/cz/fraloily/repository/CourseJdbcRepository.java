package cz.fraloily.repository;

import cz.fraloily.domain.Course;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class CourseJdbcRepository implements CourseRepository {
    private final DataSource dataSource;
    private final String H2_DATABASE_URL = "jdbc:h2:file:%s;AUTO_SERVER=TRUE;INIT=RUNSCRIPT from './db.init.sql'";

    private static final String INSERT_COURSE = """
        MERGE INTO Courses(id, name, length, url)
        VALUES ( ?, ?, ?, ? )
        """;

    private static final String ADD_NOTES = """
            UPDATE Courses SET notes = ?
            WHERE id = ?
            """;

    public CourseJdbcRepository(String databaseFile) {
        var jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setURL(H2_DATABASE_URL.formatted(databaseFile));
        this.dataSource = jdbcDataSource;
    }

    @Override
    public void saveCourse(Course course) {
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT_COURSE);
        ) {
            statement.setString(1, course.id());
            statement.setString(2, course.name());
            statement.setString(3, String.valueOf(course.length()));
            statement.setString(4, course.url());
            statement.execute();
        } catch (SQLException e) {
            throw new RepositoryException("Failed to save entity to database: " + course, e);
        }
    }

    @Override
    public List<Course> getAllCourses() {
        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
        ) {
            var resultSet = statement.executeQuery("SELECT * FROM COURSES");
            List<Course> courses = new ArrayList<>();
            while(resultSet.next()) {
                var course = new Course(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getLong(3),
                        resultSet.getString(4),
                        Optional.ofNullable(resultSet.getString(5))
                );
                courses.add(course);
            }
            return Collections.unmodifiableList(courses);
        } catch (SQLException e) {
            throw new RepositoryException("Failed retrieve courses.", e);
        }
    }

    @Override
    public void addNotes(String id, String notes) {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(ADD_NOTES);
        ) {
            statement.setString(1, notes);
            statement.setString(2, id);
            statement.execute();
        } catch (SQLException e) {
            throw new RepositoryException("Failed to add notes to course with id " + id , e);
        }
    }
}
