package top.aetheria.travelguideplatform.private_message.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PrivateMessageCreateDTO {
    @NotNull
    private Long receiverId;
    @NotBlank
    private String content;
}
