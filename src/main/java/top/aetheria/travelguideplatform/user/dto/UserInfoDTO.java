package top.aetheria.travelguideplatform.user.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserInfoDTO {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
    private String gender;
    private Date birthday;
    private String phone_number;
    private String bio;
    private LocalDateTime registrationTime;
    private LocalDateTime lastLoginTime;
}