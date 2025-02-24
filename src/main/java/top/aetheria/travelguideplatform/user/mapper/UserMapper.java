package top.aetheria.travelguideplatform.user.mapper;

import top.aetheria.travelguideplatform.user.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO users(username, password, email, avatar, registration_time, status) " +
            "VALUES(#{username}, #{password}, #{email}, #{avatar}, #{registrationTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);


    @Select("SELECT * FROM users WHERE username = #{usernameOrEmail} OR email = #{usernameOrEmail}")
    User findByUsernameOrEmail(String usernameOrEmail);

    @Update("UPDATE users SET nickname = #{nickname}, avatar = #{avatar}, " +
            "gender = #{gender}, birthday = #{birthday},phone_number = #{phone_number}, bio = #{bio}, last_login_time = #{lastLoginTime} WHERE id = #{id}")
    void update(User user);

    @Update("update users set last_login_time = #{lastLoginTime} where id = #{id}")
    void updateLastLoginTime(@Param("id") Long id, @Param("lastLoginTime") LocalDateTime now);

    @Select("SELECT tag FROM user_tags WHERE user_id = #{userId}")
    List<String> findUserTags(Long userId);

    @Insert("<script>" +
            "INSERT INTO user_tags (user_id, tag) VALUES " +
            "<foreach collection='tags' item='tag' separator=','>" +
            "(#{userId}, #{tag})" +
            "</foreach>" +
            "</script>")
    void insertUserTags(@Param("userId") Long userId, @Param("tags") List<String> tags);

    @Delete("DELETE FROM user_tags WHERE user_id = #{userId}")
    void deleteUserTags(Long userId);
}
