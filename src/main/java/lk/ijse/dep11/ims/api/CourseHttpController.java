package lk.ijse.dep11.ims.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import lk.ijse.dep11.ims.to.CourseTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PreDestroy;
import javax.annotation.security.PermitAll;
import java.sql.*;
import java.util.ArrayList;



@RestController
@RequestMapping("/courses")
@CrossOrigin
public class CourseHttpController {

    static HikariDataSource pool = null;
    public CourseHttpController(){
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/dep11_ims");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("nwBDK@4112");
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.addDataSourceProperty("maximumPoolSize",10);
        pool = new HikariDataSource(hikariConfig);
    }
    @PreDestroy
    public void destroy(){
        pool.close();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "application/json" ,produces = "application/json")
    public CourseTO createCourse(@RequestBody @Validated CourseTO course){
        try {
            Connection connection = pool.getConnection();
            PreparedStatement createStm = connection.prepareStatement("INSERT INTO course (name,duration_in_months) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            createStm.setString(1,course.getCourseName());
            createStm.setInt(2,course.getDurationInMonths());
            createStm.executeUpdate();
            ResultSet generatedKeys = createStm.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            course.setCourseId(id);
            return course;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = "/{courseId}",consumes = "application/json")
    public void UpdateCourse(@PathVariable int courseId,@RequestBody @Validated CourseTO course){
        try {
            Connection connection = pool.getConnection();
            PreparedStatement getCourseStm = connection.prepareStatement("SELECT * FROM course WHERE id=?");
            getCourseStm.setInt(1,courseId);
            ResultSet resultSet = getCourseStm.executeQuery();
            if(resultSet.next()){
                PreparedStatement deleteStm = connection.prepareStatement("UPDATE course SET name=?,duration_in_months=? WHERE id=?");
                deleteStm.setString(1,course.getCourseName());
                deleteStm.setInt(2,course.getDurationInMonths());
                deleteStm.setInt(3,courseId);
                deleteStm.executeUpdate();
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("update");
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{courseId}")
    public void deleteCourse( @PathVariable int courseId){
        try {
            Connection connection = pool.getConnection();
            PreparedStatement getCourseStm = connection.prepareStatement("SELECT * FROM course WHERE id=?");
            getCourseStm.setInt(1,courseId);
            ResultSet resultSet = getCourseStm.executeQuery();
            if(resultSet.next()){
                PreparedStatement deleteStm = connection.prepareStatement("DELETE FROM course WHERE id=?");
                deleteStm.setInt(1,courseId);
                deleteStm.executeUpdate();
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("delete");
    }
    @GetMapping(value = "/{courseId}" , produces = "application/json")
    public CourseTO getCourseDetail(@PathVariable int courseId){
        try {
            Connection connection = pool.getConnection();
            PreparedStatement getCourseStm = connection.prepareStatement("SELECT * FROM course WHERE id=?");
            getCourseStm.setInt(1,courseId);
            ResultSet resultSet = getCourseStm.executeQuery();
            if(resultSet.next()){
                int id = resultSet.getInt("id");
                String courseName = resultSet.getString("name");
                int durationInMonths = resultSet.getInt("duration_in_months");
                return  new CourseTO(id,courseName,durationInMonths);
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping(produces = "application/json")
    public ArrayList<CourseTO> getAllCourses(){
        try {
            Connection connection = pool.getConnection();
            PreparedStatement getAllStm = connection.prepareStatement("SELECT * FROM course ORDER BY id");
            ResultSet resultSet = getAllStm.executeQuery();
            ArrayList<CourseTO> coursesList = new ArrayList<>();

            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int duration = resultSet.getInt("duration_in_months");
                coursesList.add(new CourseTO(id,name,duration));
            }
            return coursesList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
