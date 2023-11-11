package com.example.tily.user;

import com.example.tily._core.errors.ExceptionCode;
import com.example.tily._core.errors.CustomException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.tily._core.security.JWTProvider;
import com.example.tily._core.utils.RedisUtils;
import com.example.tily.alarm.AlarmRepository;
import com.example.tily.comment.Comment;
import com.example.tily.comment.CommentRepository;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.roadmap.relation.UserRoadmapRepository;
import com.example.tily.step.Step;
import com.example.tily.step.StepRepository;
import com.example.tily.step.reference.ReferenceRepository;
import com.example.tily.step.relation.UserStep;
import com.example.tily.step.relation.UserStepRepository;
import com.example.tily.til.Til;
import com.example.tily.til.TilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final RedisUtils redisUtils;
    private final TilRepository tilRepository;
    private final UserRoadmapRepository userRoadmapRepository;
    private final UserStepRepository userStepRepository;
    private final RoadmapRepository roadmapRepository;
    private final CommentRepository commentRepository;
    private final StepRepository stepRepository;
    private final ReferenceRepository referenceRepository;
    private final AlarmRepository alarmRepository;

    private String defaultImage = "/assets/icons/ic_profile";

    // (회원가입) 이메일 중복 체크 후 인증코드 전송
    @Transactional
    public void checkEmail(UserRequest.CheckEmailDTO requestDTO) {
        checkEmail(requestDTO.email());
        //sendCode(requestDTO.email());
    }

    // 인증코드 전송
    @Transactional
    public void sendEmailCode(UserRequest.SendEmailCodeDTO requestDTO) {
        findByEmail(requestDTO.email());
        //sendCode(requestDTO.email());
    }

    // 인증코드 확인
    @Transactional
    public UserResponse.CheckEmailCodeDTO checkEmailCode(UserRequest.CheckEmailCodeDTO requestDTO) {
        String code = redisUtils.getData(requestDTO.email()); // 이메일로 찾은 코드

        if (code==null)
            throw new CustomException(ExceptionCode.CODE_EXPIRED);
        else if (!code.equals(requestDTO.code()))
            throw new CustomException(ExceptionCode.CODE_WRONG);

        redisUtils.deleteData(requestDTO.email()); // 인증 완료 후 인증코드 삭제

        return new UserResponse.CheckEmailCodeDTO(requestDTO.email());
    }


    // 회원가입
    @Transactional
    public void join(UserRequest.JoinDTO requestDTO) {
        checkEmail(requestDTO.email());
        
        if (!requestDTO.password().equals(requestDTO.passwordConfirm()))
            throw new CustomException(ExceptionCode.USER_PASSWORD_WRONG);

        User user = User.builder()
                .email(requestDTO.email())
                .name(requestDTO.name())
                .password(passwordEncoder.encode(requestDTO.password()))
                .image(createDefaultImage(defaultImage))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
    }

    // 로그인
    @Transactional
    public UserResponse.TokenDTO login(UserRequest.LoginDTO requestDTO) {
        User user = findByEmail(requestDTO.email());

        if(!passwordEncoder.matches(requestDTO.password(), user.getPassword()))
            throw new CustomException(ExceptionCode.USER_PASSWORD_WRONG);

        return createToken(user);
    }

    // 토큰 재발급
    @Transactional
    public UserResponse.TokenDTO refresh(String refreshToken) {
        DecodedJWT decodedJWT = JWTProvider.verify(refreshToken);
        Long userId = decodedJWT.getClaim("id").asLong();

        if (!redisUtils.existData(userId.toString()))
            throw new CustomException(ExceptionCode.TOKEN_EXPIRED);

        User user = findById(userId);

        return createToken(user);
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(UserRequest.ChangePwdDTO requestDTO) {
        User user = findByEmail(requestDTO.email());

        String enPassword = passwordEncoder.encode(requestDTO.password());
        user.updatePassword(enPassword);
    }

    // 사용자 정보 조회
    public UserResponse.UserDTO findUser(User user) {
        return new UserResponse.UserDTO(findById(user.getId()));
    }

    //  사용자 정보 수정 - 비밀번호 수정
    @Transactional
    public void updatePassword(UserRequest.UpdateUserDTO requestDTO, Long userId) {
        User user = findById(userId);

        if (!user.getId().equals(userId))
            throw new CustomException(ExceptionCode.USER_UPDATE_FORBIDDEN);

        if (!passwordEncoder.matches(requestDTO.curPassword(), user.getPassword()))
            throw new CustomException(ExceptionCode.USER_CURPASSWORD_WRONG);

        if (!requestDTO.newPassword().equals(requestDTO.newPasswordConfirm()))
            throw new CustomException(ExceptionCode.USER_PASSWORD_WRONG);

        String enPassword = passwordEncoder.encode(requestDTO.newPassword());
        user.updatePassword(enPassword);
    }

    // 장미밭 조회
    public UserResponse.ViewGardensDTO viewGardens(User user) {
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime beginDateTime = endDateTime.minusYears(1).plusDays(1);

        Long userId = user.getId();
        HashMap<String, Integer> maps = new HashMap<>();

        List<Til> tils = tilRepository.findTilsByUserIdAndDateRange(userId, beginDateTime, endDateTime);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 모든 날에 대한 작성여부 0으로 초기화.
        for (LocalDate date = beginDateTime.toLocalDate(); !date.isAfter(endDateTime.toLocalDate()); date = date.plusDays(1)) {
            maps.put(date.format(formatter), 0);
        }

        // TIL 존재하는 날의 작성여부만 1로 변경.
        for (Til til : tils) {
            LocalDate tilDate = til.getCreatedDate().toLocalDate();
            maps.put(tilDate.format(formatter), 1);
        }

        List<UserResponse.ViewGardensDTO.GardenDTO> gardens = maps.entrySet().stream()
                .map(garden -> new UserResponse.ViewGardensDTO.GardenDTO(garden.getKey(),garden.getValue()))
                .collect(Collectors.toList());

        return new UserResponse.ViewGardensDTO(gardens);
    }

    // 회원 탈퇴하기
    public void withdrawMembership(User user){
        // 1. 유저가 작성한 Comment들 삭제
        List<Comment> comments = getCommentByUserId(user.getId());
        List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        commentRepository.softDeleteCommentsByIds(commentIds);

        // 2. Comment들과 관련된 Alarm 삭제
        alarmRepository.deleteByCommentIds(commentIds);

        // 3. 유저가 작성한 Til 삭제
        List<Til> tils = getTilByUserId(user.getId());
        List<Long> tilIds = tils.stream()
                .map(Til::getId)
                .collect(Collectors.toList());

        tilRepository.softDeleteTilsByTilIds(tilIds);

        // 4. UserStep들을 삭제
        List<UserStep> userSteps = getUserStepByUserId(user.getId());
        List<Long> userStepIds = userSteps.stream()
                .map(UserStep::getId)
                .collect(Collectors.toList());

        userStepRepository.softDeleteUserStepByUserStepIds(userStepIds);

        // 5. 유저가 만든 Step들을 삭제
        List<Step> steps = userSteps.stream()
                .map(userStep -> userStep.getStep())
                .collect(Collectors.toList());

        List<Long> stepIds = steps.stream()
                .map(Step::getId)
                .collect(Collectors.toList());

        stepRepository.softDeleteStepByStepIds(stepIds);

        // 6. 유저가 작성한 Reference들을 삭제
        referenceRepository.softDeleteReferenceByStepIds(stepIds);

        // 7. UserRoadmap 삭제
        List<UserRoadmap> userRoadmaps = getUserRoadmapByUserId(user.getId());
        List<Long> userRoadmapIds = userRoadmaps.stream()
                .map(UserRoadmap::getId)
                .collect(Collectors.toList());

        userRoadmapRepository.softDeleteUserRoadmapByUserRoadmapIds(userRoadmapIds);

        // 8. 유저가 만든 로드맵 삭제
        List<Roadmap> roadmaps = userRoadmaps.stream()
                .map(userRoadmap -> userRoadmap.getRoadmap())
                .filter(roadmap -> roadmap.getCreator().getId().equals(user.getId()))
                .collect(Collectors.toList());

        List<Long> roadmapIds = roadmaps.stream()
                .map(Roadmap::getId)
                .collect(Collectors.toList());

        roadmapRepository.softDeleteRoadmapByRoadmapIds(roadmapIds);

        // 9. 유저 삭제
        userRepository.softDeleteUserById(user.getId());
    }

    //////////////

    // 해당 이메일로 인증코드 전송
    private void sendCode(String email) {
        try {
            // 이메일에 대한 인증코드 존재하면 예전 인증코드 삭제
            if (redisUtils.existData(email))
                redisUtils.deleteData(email);

            String code = createCode();

            // 이메일로 인증코드 전송
            MimeMessage message = javaMailSender.createMimeMessage();
            message.addRecipients(Message.RecipientType.TO, email);
            message.setSubject("[TIL-y] 인증코드가 도착했습니다.");
            message.setText(getEmailContent(code), "utf-8", "html");
            javaMailSender.send(message);

            // redis에 인증코드 저장 (유효기간: 5분)
            redisUtils.setDataExpire(email, code, 60 * 5L);
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.CODE_NOT_SEND);
        }
    }

    // 인증코드 랜덤 생성
    private String createCode() {
        int start = 48;
        int end = 90;
        int codeLength = 8;

        Random random = new Random();

        return random.ints(start, end + 1).filter(i -> (i <= 57 || i >= 65)).limit(codeLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    private String getEmailContent(String code) {

        String content = "<div style='background-color: #f8f9fa; padding: 10px 20px 50px 20px; margin: 20px'>" +
                "<h1 style='margin: 20px'>이메일 인증</h1>" +
                "<p>안녕하세요!<br>본인 인증을 위해 아래의 코드를 복사해 인증코드 입력 칸에 입력해주세요.<br>" +
                "<div style='background-color:gainsboro; font-size: 30px; margin: 10px; padding: 5px; width: 250px' >" + code + "</div><br>" +
                "TIL-y 서비스를 이용해주셔서 감사합니다. <br></p></div>";

        return content;
    }

    // 토큰 생성 및 재발급
    private UserResponse.TokenDTO createToken(User user) {

        String newRefreshToken = JWTProvider.createRefreshToken(user);
        String newAccessToken = JWTProvider.createAccessToken(user);
        redisUtils.deleteData(user.getId().toString());
        redisUtils.setDataExpire(user.getId().toString(), newRefreshToken, JWTProvider.REFRESH_EXP);

        return new UserResponse.TokenDTO(newAccessToken, newRefreshToken);
    }

    // email 중복 체크
    private void checkEmail (String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) throw new CustomException(ExceptionCode.USER_EMAIL_EXIST);
    }

    // email로 사용자 조회
    private User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new CustomException(ExceptionCode.USER_EMAIL_NOT_FOUND));
    }

    // id로 사용자 조회
    private User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()->new CustomException(ExceptionCode.USER_NOT_FOUND));
    }

    private List<UserRoadmap> getUserRoadmapByUserId(Long userId) {
        return userRoadmapRepository.findByUserId(userId);
    }

    private List<UserStep> getUserStepByUserId(Long userId) {
        return userStepRepository.findByUserId(userId);
    }

    private List<Til> getTilByUserId(Long userId){
        return tilRepository.findByWriterId(userId);
    }

    private List<Comment> getCommentByUserId(Long userId){return commentRepository.findByWriterId(userId);}
    
    private String createDefaultImage(String defaultImage) {
        int random = (int)(Math.random()*6)+1;
        return defaultImage+random+".svg";
    }
}
