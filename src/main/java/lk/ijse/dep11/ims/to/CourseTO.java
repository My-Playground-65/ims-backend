package lk.ijse.dep11.ims.to;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;
import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseTO implements Serializable {
    @Null(message = "Id should be empty!")
    private Integer courseId;
    @NotBlank(message = "can't be a blank")
    @Pattern(regexp = "^[A-Za-z0-9 ]+$",message = "Invalid course name")
    private String courseName;
    @NotNull(message = "can't be null!")
    private Integer durationInMonths;

}
