package top.aetheria.travelguideplatform.user.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String avatar;
    private String gender;
    private Date birthday;
    private String phone_number;
    private String bio;
    private LocalDateTime registrationTime;
    private LocalDateTime lastLoginTime;
    private Integer status;
}