package lk.ijse.dep11.ims.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lk.ijse.dep11.ims.to.TeachersTo;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/teachers")
@CrossOrigin
public class TeacherHttpController {

    private final HikariDataSource pool;
    public TeacherHttpController() {
        HikariConfig config = new HikariConfig();
        config.setUsername("root");
        config.setPassword("root");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/dep11_ims");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.addDataSourceProperty("maximumPoolSize", 10);
        pool = new HikariDataSource(config);
    }

    //Create a teacher
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public TeachersTo createTeacher(@RequestBody @Validated TeachersTo teacher){

        try {
            Connection connection = pool.getConnection();
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO teachers (name, contact) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1,teacher.getName() );
            pstm.setString(2, teacher.getContact());
            pstm.executeUpdate();
            ResultSet generatedKeys = pstm.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            teacher.setId(id);
            return teacher;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    //Update a teacher
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = "/{id}", consumes ="application/json")
    public void updateTeacher(@PathVariable String id, @RequestBody @Validated TeachersTo teacher){

        try(Connection connection = pool.getConnection()){
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM teachers WHERE id=?");
            pstm.setInt(1, Integer.parseInt(id));
            ResultSet resultSet = pstm.executeQuery();
            if(!resultSet.next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher Not Found");
            }
            PreparedStatement stm = connection.prepareStatement("UPDATE teachers SET name=?, contact=? WHERE id=?");
            stm.setString(1, teacher.getName());
            stm.setString(2, teacher.getContact());
            stm.setInt(3, Integer.parseInt(id));
            stm.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    //Delete teacher
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteTeacher(@PathVariable String id){

        try(Connection connection = pool.getConnection()){

            // Check if the teacher has any associated courses
            PreparedStatement checkCources = connection.prepareStatement("SELECT * FROM teacher_course WHERE teacher_id=?");
            checkCources.setInt(1, Integer.parseInt(id));
            ResultSet courseResultSet = checkCources.executeQuery();

            if (courseResultSet.next()) {
                // If there are associated courses, do not allow deletion
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Teacher is associated with one or more courses. Cannot delete.");
            }
            // If no associated courses, proceed with deletion
            PreparedStatement deleteTeacher = connection.prepareStatement("DELETE FROM teachers WHERE id=?");
            deleteTeacher.setInt(1, Integer.parseInt(id));
            int affectedRows = deleteTeacher.executeUpdate();

            if (affectedRows == 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher Not Found");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    //Get a teacher
    @GetMapping(value = "/{id}", produces = "application/json")
    public TeachersTo getTeacherDetails(@PathVariable String id){

        try(Connection connection = pool.getConnection()){
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM teachers WHERE id=?");
            pstm.setInt(1, Integer.parseInt(id));
            ResultSet resultSet = pstm.executeQuery();
            if(!resultSet.next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher Not Found");
            }
            int id1 = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String contact = resultSet.getString("contact");
            TeachersTo teacher = new TeachersTo(id1, name, contact);
            return teacher;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    //Get all teachers
    @GetMapping(produces = "application/json")
    public List<TeachersTo> getAllTeachers(){
        try(Connection connection = pool.getConnection()){
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM teachers ORDER BY id");
            ResultSet resultSet = pstm.executeQuery();
            List<TeachersTo> teachers = new LinkedList<>();
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String contact = resultSet.getString("contact");
                TeachersTo teacher = new TeachersTo(id, name, contact);
                teachers.add(teacher);
            }
            return teachers;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
