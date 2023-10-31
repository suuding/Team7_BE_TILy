package com.example.tily.alarm;

import com.example.tily._core.errors.exception.ExceptionCode;
import com.example.tily._core.errors.exception.CustomException;
import com.example.tily.user.User;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    public AlarmResponse.FindAllDTO findAll(User user) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        List<Alarm> alarms = alarmRepository.findAllByReceiverId(user.getId(), sort);
        List<AlarmResponse.AlarmDTO> alarmDTOS = alarms.stream().map(AlarmResponse.AlarmDTO::new).collect(Collectors.toList());

        return new AlarmResponse.FindAllDTO(alarmDTOS);
    }
    
    @Transactional
    public void readAlarm(AlarmRequest.ReadAlarmDTO requestDTO) {
        // 요청DTO에 있는 모든 알림 id에 대해 true로 처리
        List<AlarmRequest.ReadAlarmDTO.AlarmDTO> alarms = requestDTO.alarms();
        for (AlarmRequest.ReadAlarmDTO.AlarmDTO alarm : alarms) {
            Alarm a = alarmRepository.findById(alarm.id())
                    .orElseThrow(() -> new CustomException(ExceptionCode.ALARM_NOT_FOUND));
            a.readAlarm();
        }
    }
}
