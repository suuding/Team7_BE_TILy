package com.example.tily.alarm;

import com.example.tily._core.errors.exception.Exception400;
import com.example.tily.user.User;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    public AlarmResponse.FindAllDTO findAll(User user) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        List<Alarm> alarms = alarmRepository.findAllByReceiverId(user.getId(), sort);
        return new AlarmResponse.FindAllDTO(alarms);
    }
}
