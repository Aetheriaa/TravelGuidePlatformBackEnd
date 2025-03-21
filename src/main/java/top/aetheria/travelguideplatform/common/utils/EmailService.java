package top.aetheria.travelguideplatform.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.AddressException; // 导入 AddressException
import jakarta.mail.internet.InternetAddress; // 导入 InternetAddress

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}") // 从配置文件中读取发件人邮箱
    private String from;

    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);

        try {
            // 验证邮箱地址格式


            InternetAddress emailAddr = new InternetAddress(to);
            emailAddr.validate(); // 如果邮箱地址无效，会抛出 AddressException

            message.setTo(to); // 设置收件人
            message.setSubject("Travel Guide Platform - 验证码");
            message.setText("您的验证码是：" + code + "，请在5分钟内使用。");
            mailSender.send(message);

        } catch (AddressException e) {
            // 处理邮箱地址无效的情况
            throw new IllegalArgumentException("Invalid email address: " + to, e);
        } catch (Exception e) { // 捕获其他可能的异常
            // 处理邮件发送失败的情况
            throw new RuntimeException("Failed to send verification code to: " + to, e);
        }
    }
}
