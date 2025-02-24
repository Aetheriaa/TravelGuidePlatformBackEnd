package top.aetheria.travelguideplatform.user.vo;

import lombok.Data;

@Data
public class LoginVO {

    private Long id;

    private String username;

    private String token;
}
