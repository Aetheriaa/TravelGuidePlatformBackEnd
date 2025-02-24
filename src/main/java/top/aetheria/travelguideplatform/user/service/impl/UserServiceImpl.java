package top.aetheria.travelguideplatform.user.service.impl;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import top.aetheria.travelguideplatform.common.constant.AppConstants;
import top.aetheria.travelguideplatform.common.exception.BusinessException;
import top.aetheria.travelguideplatform.common.utils.JwtUtils;
import top.aetheria.travelguideplatform.user.dto.UserLoginDTO;
import top.aetheria.travelguideplatform.user.dto.UserRegisterDTO;
import top.aetheria.travelguideplatform.user.dto.UserUpdateDTO;
import top.aetheria.travelguideplatform.user.entity.User;
import top.aetheria.travelguideplatform.user.mapper.UserMapper;
import top.aetheria.travelguideplatform.user.service.UserService;
import top.aetheria.travelguideplatform.user.vo.LoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.beans.FeatureDescriptor;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        // 1. 检查用户名是否已存在
        if (userMapper.findByUsername(userRegisterDTO.getUsername()) != null) {
            throw new BusinessException(400,"用户名已存在");
        }

        // 2. 检查邮箱是否已存在
        if (userMapper.findByEmail(userRegisterDTO.getEmail()) != null) {
            throw new BusinessException(400,"邮箱已存在");
        }

        // 3. 将DTO转换为实体类
        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO, user);

        // 4. 密码加密
        String hashedPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(hashedPassword);

        //5. 设置状态和注册时间
        user.setStatus(AppConstants.USER_STATUS_NORMAL);
        user.setRegistrationTime(LocalDateTime.now());
        user.setAvatar(String.format("https://api.dicebear.com/9.x/%s/svg?seed=%s","pixel-art", user.getUsername()));

        // 6. 插入数据库
        userMapper.insert(user);
    }

    @Override
    public LoginVO login(UserLoginDTO userLoginDTO) {
        // 1. 根据用户名或邮箱查询用户
        User user = userMapper.findByUsernameOrEmail(userLoginDTO.getUsernameOrEmail());

        // 2. 检查用户是否存在
        if (user == null) {
            throw new BusinessException(400,"用户名或密码错误");
        }

        // 3. 检查密码是否正确
        String hashedPassword = DigestUtils.md5DigestAsHex(userLoginDTO.getPassword().getBytes());
        if (!hashedPassword.equals(user.getPassword())) {
            throw new BusinessException(400,"用户名或密码错误");
        }
        //更新最后登录时间
        userMapper.updateLastLoginTime(user.getId(), LocalDateTime.now());

        // 4. 生成JWT
        String token = jwtUtils.generateToken(user.getId());

        //5. 封装VO
        LoginVO loginVO = new LoginVO();
        loginVO.setId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setToken(token);
        return loginVO;

    }
    @Override
    public User getById(Long id){
        User user = userMapper.findById(id);
        if (user == null){
            throw new BusinessException(404,"用户不存在");
        }
        return  user;
    }

    @Override
    public void update(Long id, UserUpdateDTO userUpdateDTO) {
        // 1. 查询用户
        User user = userMapper.findById(id);
        if(user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // 2. 更新字段，仅更新不为null的字段
        BeanUtils.copyProperties(userUpdateDTO, user, getNullPropertyNames(userUpdateDTO));

        // 3. 更新到数据库
        userMapper.update(user);
    }

    // 工具方法：获取对象中为null的属性名
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
}
