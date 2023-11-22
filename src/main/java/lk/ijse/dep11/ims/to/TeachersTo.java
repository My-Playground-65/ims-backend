package lk.ijse.dep11.ims.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeachersTo {
    @Null(message = "ID should be empty")
    private Integer id;
    @NotBlank(message = "Name should not be empty")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Invalid name")
    private String name;
    @NotBlank(message = "Contact should not be empty")
    @Pattern(regexp = "^\\d{3}-\\d{7}$", message = "Invalid contact")
    private String contact;

}
