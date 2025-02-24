package top.aetheria.travelguideplatform.user.dto;

import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;
//import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserUpdateDTO {
    private String nickname;
    private String avatar;
    private String gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    private String phone_number;
    private String bio;
}
