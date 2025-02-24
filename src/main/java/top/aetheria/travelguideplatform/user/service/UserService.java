package top.aetheria.travelguideplatform.user.service;

import top.aetheria.travelguideplatform.user.dto.UserLoginDTO;
import top.aetheria.travelguideplatform.user.dto.UserRegisterDTO;
import top.aetheria.travelguideplatform.user.dto.UserUpdateDTO;
import top.aetheria.travelguideplatform.user.entity.User;
import top.aetheria.travelguideplatform.user.vo.LoginVO;

public interface UserService {
    void register(UserRegisterDTO userRegisterDTO);
    LoginVO login(UserLoginDTO userLoginDTO);
    User getById(Long id);
    void update(Long id, UserUpdateDTO userUpdateDTO);
}
