package com.example.tily.user;

import com.example.tily._core.errors.exception.Exception400;
import com.example.tily._core.errors.exception.Exception500;
import com.example.tily._core.security.JWTProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void join(UserRequest.JoinDTO requestDTO) {
        Optional<User> user = userRepository.findByEmail(requestDTO.getEmail());
        if (user.isPresent()) {
            throw new Exception400("이미 존재하는 이메일입니다 : " + requestDTO.getEmail());
        }

        requestDTO.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        try {
            userRepository.save(requestDTO.toEntity());
        } catch (Exception e) {
            throw new Exception500("unknown server error");
        }
    }

    public void checkEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new Exception400("이미 존재하는 이메일입니다 : " +email);
        }
    }

}
