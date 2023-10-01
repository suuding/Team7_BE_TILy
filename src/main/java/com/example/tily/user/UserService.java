package com.example.tily.user;

import com.example.tily._core.errors.exception.Exception400;
import com.example.tily._core.errors.exception.Exception404;
import com.example.tily._core.errors.exception.Exception500;
import com.example.tily._core.security.JWTProvider;
import com.example.tily._core.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final RedisUtils redisUtils;

    // (회원가입) 이메일 중복 체크 후 인증코드 전송
    @Transactional
    public void checkEmail(UserRequest.CheckEmailDTO requestDTO) {
        Optional<User> user = userRepository.findByEmail(requestDTO.getEmail());
        if (user.isPresent()) {
            throw new Exception400("이미 존재하는 이메일입니다 : " +requestDTO.getEmail());
        }

        sendCode(requestDTO.getEmail());
    }

    // 인증코드 전송
    @Transactional
    public void sendEmailCode(UserRequest.SendEmailCodeDTO requestDTO) {

        User user = userRepository.findByEmail(requestDTO.getEmail()).orElseThrow(
                () -> new Exception404("해당 이메일을 찾을 수 없습니다 : "+requestDTO.getEmail())
        );

        sendCode(requestDTO.getEmail());
    }

    // 인증코드 확인
    @Transactional
    public UserResponse.CheckEmailCodeDTO checkEmailCode(UserRequest.CheckEmailCodeDTO requestDTO) {
        String code = redisUtils.getData(requestDTO.getEmail()); // 이메일로 찾은 코드

        if (code==null) {
            throw new Exception400("유효기간이 만료되었습니다.");
        }

        if (!code.equals(requestDTO.getCode())) {
            throw new Exception400("잘못된 인증코드입니다.");
        }

        redisUtils.deleteData(requestDTO.getEmail()); // 인증 완료 후 인증코드 삭제

        return new UserResponse.CheckEmailCodeDTO(requestDTO.getEmail());
    }

    @Transactional
    public void join(UserRequest.JoinDTO requestDTO) {
        Optional<User> user = userRepository.findByEmail(requestDTO.getEmail());
        if (user.isPresent()) {
            throw new Exception400("이미 존재하는 이메일입니다 : " +requestDTO.getEmail());
        }

        requestDTO.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        try {
            userRepository.save(requestDTO.toEntity());
        } catch (Exception e) {
            throw new Exception500("unknown server error");
        }
    }

    @Transactional
    public String login(UserRequest.LoginDTO requestDTO) {
        User user = userRepository.findByEmail(requestDTO.getEmail()).orElseThrow(
                () -> new Exception404("해당 이메일을 찾을 수 없습니다 : "+requestDTO.getEmail())
        );

        if(!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new Exception400("비밀번호가 일치하지 않습니다. ");
        }

        return JWTProvider.create(user);
    }

    @Transactional
    public void changePassword(UserRequest.ChangePwdDTO requestDTO) {
        User user = userRepository.findByEmail(requestDTO.getEmail()).orElseThrow(
                () -> new Exception404("해당 이메일을 찾을 수 없습니다 : "+requestDTO.getEmail())
        );

        String enPassword = passwordEncoder.encode(requestDTO.getPassword());
        user.updatePassword(enPassword);
    }

    // 해당 이메일로 인증코드 전송
    public void sendCode(String email) {
        try {
            if (redisUtils.existData(email)) { // 이메일에 대한 인증코드 존재하면 예전 인증코드 삭제
                redisUtils.deleteData(email);
            }
            // 랜덤한 인증코드 생성
            String code = createCode();

            // 이메일로 인증코드 전송
            MimeMessage message = javaMailSender.createMimeMessage();
            message.addRecipients(Message.RecipientType.TO, email);
            message.setSubject("[TIL-y] 인증코드가 도착했습니다.");
            message.setText(getEmailContent(code), "utf-8", "html");
            javaMailSender.send(message);

            // redis에 인증코드 저장 (유효기간: 5분)
            redisUtils.setDataExpire(email, code, 60*5L);
        } catch (Exception e) {
            throw new Exception500("인증코드를 전송하지 못했습니다.");
        }
    }

    // 인증코드 랜덤 생성
    public String createCode() {
        int start = 48;
        int end = 90;
        int codeLength = 8;

        Random random = new Random();

        return random.ints(start, end + 1).filter(i -> (i <=57 || i >=65)).limit(codeLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    public String getEmailContent(String code) {

        String content = "<div style='background-color: #f8f9fa; padding: 10px 20px 50px 20px; margin: 20px'>" +
                "<h1 style='margin: 20px'>이메일 인증</h1>" +
                "<p>안녕하세요!<br>본인 인증을 위해 아래의 코드를 복사해 인증코드 입력 칸에 입력해주세요.<br>" +
                "<div style='background-color:gainsboro; font-size: 30px; margin: 10px; padding: 5px; width: 250px' >"+code+"</div><br>" +
                "TIL-y 서비스를 이용해주셔서 감사합니다. <br></p></div>";
        return content;
    }

}
