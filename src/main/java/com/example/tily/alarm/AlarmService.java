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
    
    @Transactional
    public void readAlarm(AlarmRequest.ReadAlarmDTO requestDTO) {
        // 요청DTO에 있는 모든 알림 id에 대해 true로 처리
        List<AlarmRequest.ReadAlarmDTO.AlarmDTO> alarms = requestDTO.getAlarms();
        for (AlarmRequest.ReadAlarmDTO.AlarmDTO alarm : alarms) {
            Alarm a = alarmRepository.findById(alarm.getId()).orElseThrow(
                    () -> new Exception400("해당 알림이 존재하지 않습니다.")
            );
            a.readAlarm();
        }
    }
}
