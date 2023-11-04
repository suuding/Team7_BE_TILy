package com.example.tily.roadmap;

import com.example.tily._core.errors.exception.CustomException;
import com.example.tily._core.errors.exception.Exception403;
import com.example.tily._core.errors.exception.Exception404;
import com.example.tily._core.errors.exception.ExceptionCode;
import com.example.tily.roadmap.relation.GroupRole;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.roadmap.relation.UserRoadmapRepository;
import com.example.tily.step.Step;
import com.example.tily.step.StepRepository;
import com.example.tily.step.reference.Reference;
import com.example.tily.step.reference.ReferenceRepository;
import com.example.tily.step.relation.UserStep;
import com.example.tily.step.relation.UserStepRepository;
import com.example.tily.til.Til;
import com.example.tily.til.TilRepository;
import com.example.tily.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RoadmapService {
    private final RoadmapRepository roadmapRepository;
    private final StepRepository stepRepository;
    private final ReferenceRepository referenceRepository;
    private final TilRepository tilRepository;
    private final UserRoadmapRepository userRoadmapRepository;
    private final UserStepRepository userStepRepository;

    @Transactional
    public RoadmapResponse.CreateRoadmapDTO createIndividualRoadmap(RoadmapRequest.CreateIndividualRoadmapDTO requestDTO, User user){

        Roadmap roadmap = Roadmap.builder()
                .creator(user)
                .category(Category.CATEGORY_INDIVIDUAL)
                .name(requestDTO.name())
                .stepNum(0)
                .build();
        roadmapRepository.save(roadmap);

        UserRoadmap userRoadmap = UserRoadmap.builder()
                .roadmap(roadmap)
                .user(user)
                .role(GroupRole.ROLE_MASTER)
                .isAccept(true)
                .build();
        userRoadmapRepository.save(userRoadmap);

        return new RoadmapResponse.CreateRoadmapDTO(roadmap);
    }

    @Transactional
    public RoadmapResponse.CreateRoadmapDTO createGroupRoadmap(RoadmapRequest.CreateGroupRoadmapDTO requestDTO, User user){
        
        Roadmap roadmap = Roadmap.builder()
                .creator(user)
                .category(Category.CATEGORY_GROUP)
                .name(requestDTO.roadmap().name())
                .description(requestDTO.roadmap().description())
                .isPublic(requestDTO.roadmap().isPublic()) // 공개여부
                .currentNum(1L)
                .code(generateRandomCode())
                .isRecruit(true)    // 모집여부
                .stepNum(requestDTO.steps().size())
                .build();
        roadmapRepository.save(roadmap);

        List<RoadmapRequest.StepDTO> stepDTOS = requestDTO.steps();
        for(RoadmapRequest.StepDTO stepDTO : stepDTOS){
            // step 저장
            Step step = Step.builder()
                    .roadmap(roadmap)
                    .title(stepDTO.title())
                    .description(stepDTO.description())
                    .dueDate(stepDTO.dueDate()!=null ? stepDTO.dueDate() : null)
                    .build();
            stepRepository.save(step);

            UserStep userStep = UserStep.builder()
                    .roadmap(roadmap)
                    .step(step)
                    .user(user)
                    .isSubmit(false)
                    .build();
            userStepRepository.save(userStep);

            // reference 저장
            RoadmapRequest.ReferenceDTOs referenceDTOs = stepDTO.references();
            List<Reference> references = new ArrayList<>();

            // (1) youtube
            List<RoadmapRequest.ReferenceDTO> youtubeDTOs = referenceDTOs.youtube();
            for(RoadmapRequest.ReferenceDTO youtubeDTO : youtubeDTOs){
                Reference reference = Reference.builder().step(step).category("youtube").link(youtubeDTO.link()).build();
                references.add(reference);
            }

            // (2) reference
            List<RoadmapRequest.ReferenceDTO> webDTOs = referenceDTOs.web();
            for(RoadmapRequest.ReferenceDTO webDTO : webDTOs){
                Reference reference = Reference.builder().step(step).category("web").link(webDTO.link()).build();
                references.add(reference);
            }

            referenceRepository.saveAll(references);
        }

        UserRoadmap userRoadmap = UserRoadmap.builder()
                .roadmap(roadmap)
                .user(user)
                .role(GroupRole.ROLE_MASTER)
                .progress(0)
                .isAccept(true)
                .build();
        userRoadmapRepository.save(userRoadmap);

        return new RoadmapResponse.CreateRoadmapDTO(roadmap);
    }

    public RoadmapResponse.FindGroupRoadmapDTO findGroupRoadmap(Long id, User user){
        Roadmap roadmap = roadmapRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));

        List<Step> stepList = stepRepository.findByRoadmapId(id);

        Map<Long, List<Reference>> youtubeMap = new HashMap<>();
        Map<Long, List<Reference>> webMap = new HashMap<>();

        for(Step step : stepList){
            List<Reference> referenceList = referenceRepository.findByStepId(step.getId());

            List<Reference> youtubeList = new ArrayList<>();
            List<Reference> webList = new ArrayList<>();

            for(Reference reference : referenceList){
                if(reference.getCategory().equals("youtube")){
                    youtubeList.add(reference);
                }
                else if(reference.getCategory().equals("web")){
                    webList.add(reference);
                }
            }

            youtubeMap.put(step.getId(), youtubeList);
            webMap.put(step.getId(), webList);
        }

        List<RoadmapResponse.FindGroupRoadmapDTO.StepDTO> steps = stepList.stream()
                .map(step -> new RoadmapResponse.FindGroupRoadmapDTO.StepDTO(step
                        , youtubeMap.get(step.getId()).stream()
                        .map(RoadmapResponse.ReferenceDTOs.ReferenceDTO::new).collect(Collectors.toList())
                        , webMap.get(step.getId()).stream()
                        .map(RoadmapResponse.ReferenceDTOs.ReferenceDTO::new).collect(Collectors.toList())))
                .collect(Collectors.toList());

        Optional<UserRoadmap> userRoadmap = userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(id, user.getId());
        String myRole;
        Long recentTilId;
        Long recentStepId;

        if (userRoadmap.isPresent()) {
            myRole = userRoadmap.get().getRole();
            List<Til> tils = tilRepository.findByUserIdByOrderByUpdatedDateDesc(id, user.getId());
            recentTilId = !tils.isEmpty() ? tils.get(0).getId() : null;
            recentStepId = !tils.isEmpty() ? tils.get(0).getStep().getId() : null;
        } else {
            myRole = "none";
            recentTilId = null;
            recentStepId = null;
        }

        return new RoadmapResponse.FindGroupRoadmapDTO(roadmap, steps, roadmap.getCreator(), recentTilId, recentStepId, myRole);
    }

    // 그룹 로드맵 정보 수정하기 수정필요!
    @Transactional
    public void updateGroupRoadmap(Long id, RoadmapRequest.UpdateGroupRoadmapDTO requestDTO, User user){
        checkMasterAndManagerPermission(id ,user);

        // 로드맵 업데이트
        Roadmap roadmap = roadmapRepository.findById(id).orElseThrow(
                () -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND)
        );

        String name = requestDTO.roadmap().name();
        String description = requestDTO.roadmap().description();
        String code = requestDTO.roadmap().code();
        Boolean isPublic = requestDTO.roadmap().isPublic();
        Boolean isRecruit = requestDTO.roadmap().isRecruit();

        roadmap.update(name, description, code, isPublic, isRecruit);

        // 스텝 업데이트
        List<RoadmapRequest.StepDTO> stepDTOs = requestDTO.steps();

        for(RoadmapRequest.StepDTO stepDTO : stepDTOs){
            Step step;

            step = stepRepository.findById(stepDTO.id()).orElseThrow(
                    () -> new CustomException(ExceptionCode.STEP_NOT_FOUND)
            );

            String title = stepDTO.title() ;
            String stepDescription = stepDTO.description();

            step.update(title,stepDescription);

            // reference 업데이트
            List<RoadmapRequest.ReferenceDTO> referenceDTOs = new ArrayList<>();
            referenceDTOs.addAll(stepDTO.references().web());
            referenceDTOs.addAll(stepDTO.references().youtube());

            for(RoadmapRequest.ReferenceDTO referenceDTO : referenceDTOs){
                Reference reference;

                reference = referenceRepository.findById(referenceDTO.id()).orElseThrow(
                        () -> new CustomException(ExceptionCode.REFERENCE_NOT_FOUND)
                );

                String link = referenceDTO.link();

                reference.update(link);
            }
        }
    }

    public RoadmapResponse.FindAllMyRoadmapDTO findAllMyRoadmaps(User user) {

        List<Roadmap> roadmaps = userRoadmapRepository.findByUserIdAndIsAccept(user.getId(), true);      // 내가 속한 로드맵 조회

        List<RoadmapResponse.FindAllMyRoadmapDTO.CategoryDTO> categories = roadmaps.stream()
                .filter(roadmap -> roadmap.getCategory().equals(Category.CATEGORY_INDIVIDUAL))
                .map(RoadmapResponse.FindAllMyRoadmapDTO.CategoryDTO::new).collect(Collectors.toList());

        List<RoadmapResponse.TilyDTO> tilys = roadmaps.stream()
                .filter(roadmap -> roadmap.getCategory().equals(Category.CATEGORY_TILY))
                .map(RoadmapResponse.TilyDTO::new).collect(Collectors.toList());

        List<RoadmapResponse.GroupDTO> groups = roadmaps.stream()
                .filter(roadmap -> roadmap.getCategory().equals(Category.CATEGORY_GROUP))
                .map(roadmap -> {
                    String groupRole = userRoadmapRepository.findByRoadmapIdAndUserId(roadmap.getId(), user.getId()).get().getRole();
                    return (groupRole == "master" || groupRole == "manager") ? new RoadmapResponse.GroupDTO(roadmap, true) : new RoadmapResponse.GroupDTO(roadmap, false);
                }).collect(Collectors.toList());

        return new RoadmapResponse.FindAllMyRoadmapDTO(categories, new RoadmapResponse.FindAllMyRoadmapDTO.RoadmapDTO(tilys, groups));
    }

    public RoadmapResponse.FindRoadmapByQueryDTO findAll(String category, String name, int page, int size) {

        // 생성일자를 기준으로 내림차순
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Slice<Roadmap> roadmaps = roadmapRepository.findAllByOrderByCreatedDateDesc(Category.getCategory(category), name, pageable);

        List<RoadmapResponse.FindRoadmapByQueryDTO.RoadmapDTO> roadmapDTOS = roadmaps.getContent().stream().map(RoadmapResponse.FindRoadmapByQueryDTO.RoadmapDTO::new).collect(Collectors.toList());
        boolean hasNext = roadmaps.hasNext();

        return new RoadmapResponse.FindRoadmapByQueryDTO(Category.getCategory(category), roadmapDTOS, hasNext);
    }

    @Transactional
    public void applyRoadmap(RoadmapRequest.ApplyRoadmapDTO requestDTO, Long id, User user){
        Roadmap roadmap = getRoadmapById(id);

        // 최초로 한 번만 신청 가능
        Optional<UserRoadmap> ur = userRoadmapRepository.findByRoadmapIdAndUserId(id, user.getId());
        if (ur.isPresent()) {
            if (ur.get().getRole().equals(GroupRole.ROLE_NONE.getValue()))
                throw new CustomException(ExceptionCode.ROADMAP_REJECT);
            else if (ur.get().getIsAccept().equals(true))
                throw new CustomException(ExceptionCode.ROADMAP_ALREADY_MEMBER);
            else
                throw new CustomException(ExceptionCode.ROADMAP_ALREADY_APPLY);
        }

        // 신청하면 ROLE_MEMBER, isAccept=false, 즉 예비 맴버라는 의미
        UserRoadmap userRoadmap = UserRoadmap.builder()
                .roadmap(roadmap)
                .user(user)
                .role(GroupRole.ROLE_MEMBER)
                .content(requestDTO.content())
                .isAccept(false)
                .progress(0)
                .build();
        userRoadmapRepository.save(userRoadmap);
    }

    @Transactional
    public RoadmapResponse.ParticipateRoadmapDTO participateRoadmap(RoadmapRequest.ParticipateRoadmapDTO requestDTO, User user){
        String code = requestDTO.code();
        Roadmap roadmap = roadmapRepository.findByCode(code)
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));

        Optional<UserRoadmap> ur = userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmap.getId(), user.getId());
        if (ur.isPresent())
            throw new CustomException(ExceptionCode.ROADMAP_ALREADY_MEMBER);

        // 코드로 참여시 승인없이 바로 맴버가 된다
        UserRoadmap userRoadmap = UserRoadmap.builder()
                .roadmap(roadmap)
                .user(user)
                .role(GroupRole.ROLE_MEMBER)
                .content(null)
                .isAccept(true)
                .progress(0)
                .build();

        userRoadmapRepository.save(userRoadmap);

        return new RoadmapResponse.ParticipateRoadmapDTO(roadmap);
    }

    public RoadmapResponse.FindRoadmapMembersDTO findRoadmapMembers(Long groupsId, User user){
        checkMasterAndManagerPermission(groupsId, user);

        List<UserRoadmap> userRoadmaps = userRoadmapRepository.findByRoadmapIdAndIsAcceptTrue(groupsId);

        List<RoadmapResponse.FindRoadmapMembersDTO.UserDTO> users = userRoadmaps.stream()
                .map(userRoadmap -> new RoadmapResponse.FindRoadmapMembersDTO.UserDTO(userRoadmap.getUser().getId(), userRoadmap.getUser().getName(), userRoadmap.getUser().getImage(), userRoadmap.getRole()))
                .collect(Collectors.toList());

        return new RoadmapResponse.FindRoadmapMembersDTO(users);
    }

    @Transactional
    public void changeMemberRole(RoadmapRequest.ChangeMemberRoleDTO requestDTO, Long groupsId, Long membersId, User user){
        checkMasterPermission(groupsId, user); // master만 수정 가능

        UserRoadmap userRoadmap = getUserBelongRoadmap(groupsId, membersId);

        userRoadmap.updateRole(requestDTO.role());
    }

    @Transactional
    public void dismissMember(Long groupsId, Long membersId, User user){
        String role = checkMasterAndManagerPermission(groupsId, user);

        UserRoadmap userRoadmap = getUserBelongRoadmap(groupsId, membersId);

        // master는 다 강퇴 가능, manager는 member만
        if (role.equals(GroupRole.ROLE_MANAGER.getValue()) & userRoadmap.getRole().equals(GroupRole.ROLE_MASTER.getValue()))
            throw new CustomException(ExceptionCode.ROADMAP_DISMISS_FORBIDDEN);

        userRoadmap.updateRole(GroupRole.ROLE_NONE.getValue());
    }

    public RoadmapResponse.FindAppliedUsersDTO findAppliedUsers(Long groupsId, User user){
        checkMasterAndManagerPermission(groupsId, user);

        List<UserRoadmap> userRoadmaps = userRoadmapRepository.findByRoadmapIdAndIsAcceptFalseAndRole(groupsId, GroupRole.ROLE_MEMBER.getValue());

        List<RoadmapResponse.FindAppliedUsersDTO.UserDTO> users = userRoadmaps.stream()
                .map(userRoadmap -> new RoadmapResponse.FindAppliedUsersDTO.UserDTO(userRoadmap.getUser(), userRoadmap))
                .collect(Collectors.toList());

        return new RoadmapResponse.FindAppliedUsersDTO(users);
    }

    @Transactional
    public void acceptApplication(Long groupId, Long memberId, User user){
        checkMasterAndManagerPermission(groupId, user);

        UserRoadmap userRoadmap = getUserNotBelongRoadmap(groupId, memberId);

        // 이미 거절당한 사람(NONE) 이면
        if (userRoadmap.getRole().equals(GroupRole.ROLE_NONE.getValue()))
            throw new CustomException(ExceptionCode.ROADMAP_REJECT);

        userRoadmap.updateIsAccept(true);

        // 승인하면 수강생이 해당 roadmap에 속한다 -> 제출 여부 관리를 위해 모든 step에 대해 userstep에 다 저장
        List<Step> steps = stepRepository.findByRoadmapId(groupId);
        for (Step step : steps) {
            UserStep userStep = UserStep.builder()
                    .roadmap(step.getRoadmap())
                    .step(step)
                    .user(userRoadmap.getUser())
                    .isSubmit(false)
                    .build();
            userStepRepository.save(userStep);
        }
    }

    @Transactional
    public void rejectApplication(Long groupId, Long memberId, User user){
        checkMasterAndManagerPermission(groupId, user);

        UserRoadmap userRoadmap = getUserNotBelongRoadmap(groupId, memberId);

        userRoadmap.updateRole(GroupRole.ROLE_NONE.getValue());
    }

    @Transactional
    public RoadmapResponse.FindTilOfStepDTO findTilOfStep(Long groupsId, Long stepId, Boolean isSubmit, Boolean isMember, String name){
//        List<Pair<Til, User>> pairs = new ArrayList<>();
//
//        List<Til> tils = tilRepository.findByStepId(stepId);
//        for(Til til : tils){
//            User user = til.getWriter();
//
//            // 어떤 틸이 존재한다면, 해당 틸은 반드시 틸이 속한 Step과 Roadmap 그리고 User를 가진다 => userStep 관계와 userRoadmap 관계는 반드시 존재한다. => 존재하지 않은 것에 대한 예외처리 필요 X
//            UserStep userStep = userStepRepository.findByUserIdAndStepId(user.getId(), stepId).get();
//            UserRoadmap userRoadmap = userRoadmapRepository.findByRoadmapIdAndUserId(groupsId, user.getId()).get();
//
//            if((isSubmit == userStep.getIsSubmit())  && (name == null || name.equals(user.getName()))){
//                // isMember가 false => 운영자를 포함해서 모든 til을 반환, isMember가 true => 운영자의 til을 제외하고 반환한다
//                if(!isMember || (isMember && GroupRole.ROLE_MEMBER.equals(userRoadmap.getRole()))){
//                    Pair<Til, User> pair = Pair.of(til, user);
//                    pairs.add(pair);
//                }
//            }
//        }
//
//        List<RoadmapResponse.FindTilOfStepDTO.MemberDTO> members;
//
//        if(isSubmit) {
//            members = pairs.stream()
//                    .map(pair -> new RoadmapResponse.FindTilOfStepDTO.MemberDTO(pair.getFirst().getId(), pair.getSecond().getId(),pair.getSecond().getName(), pair.getSecond().getImage(), pair.getFirst().getContent(), pair.getFirst().getSubmitDate().toLocalDate(), pair.getFirst().getCommentNum()))
//                    .collect(Collectors.toList());
//        }
//        else {
//            members = pairs.stream()
//                    .map(pair -> new RoadmapResponse.FindTilOfStepDTO.MemberDTO(null, pair.getSecond().getId(), pair.getSecond().getName(), null, null, null, 0))
//                    .collect(Collectors.toList());
//        }
//
//        return new RoadmapResponse.FindTilOfStepDTO(members);

        // 특정 로드맵에 속한 UserRoadmap list
        List<UserRoadmap> userRoadmaps = userRoadmapRepository.findByRoadmapIdAndIsAcceptTrue(groupsId);
        List<User> users = userStepRepository.findAllByStepIdAndIsSubmitAndName(stepId, isSubmit, name)
                .stream().map(UserStep::getUser).toList(); // 특정 step에 대해 제출 여부, 사용자 이름으로 user 조회

        List<RoadmapResponse.FindTilOfStepDTO.MemberDTO> members = new ArrayList<>();

        if (isMember) { // 로드맵에 속한 member만 대해
            for (User user : users) {
                // 로드맵에서의 사용자의 role을 알기 위해 사용자의 userRoadmap 조회
                Optional<UserRoadmap> userRoadmap = userRoadmaps.stream().filter(u -> u.getUser().equals(user)).findFirst();

                if (userRoadmap.isPresent() & userRoadmap.get().getRole().equals(GroupRole.ROLE_MEMBER.getValue())) {
                    Til til = tilRepository.findByStepIdAndUserId(stepId, user.getId());
                    if (til==null) members.add(new RoadmapResponse.FindTilOfStepDTO.MemberDTO(null, user));
                    else members.add(new RoadmapResponse.FindTilOfStepDTO.MemberDTO(til, user));
                }
            }
        } else { // 로드맵에 속한 모든 사용자에 대해
            for (User user : users) {
                Til til = tilRepository.findByStepIdAndUserId(stepId, user.getId());
                if (til==null) members.add(new RoadmapResponse.FindTilOfStepDTO.MemberDTO(null, user));
                else members.add(new RoadmapResponse.FindTilOfStepDTO.MemberDTO(til, user));
            }
        }

        return new RoadmapResponse.FindTilOfStepDTO(members);
    }

    private static String generateRandomCode() {
        String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";

        String combinedChars = upperAlphabet + lowerAlphabet + numbers;

        Random random = new Random();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(combinedChars.length());
            char randomChar = combinedChars.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    private String checkMasterAndManagerPermission(Long roadmapId, User user) { // 매니저급만 접근
        UserRoadmap userRoadmap = getUserBelongRoadmap(roadmapId, user.getId());

        if(!userRoadmap.getRole().equals(GroupRole.ROLE_MASTER.getValue()) && !userRoadmap.getRole().equals(GroupRole.ROLE_MANAGER.getValue())){
            throw new CustomException(ExceptionCode.ROADMAP_FORBIDDEN);
        }
        return userRoadmap.getRole();
    }

    private UserRoadmap checkMasterPermission(Long roadmapId, User user) {
        UserRoadmap userRoadmap = getUserBelongRoadmap(roadmapId, user.getId());

        if(!userRoadmap.getRole().equals(GroupRole.ROLE_MASTER.getValue()))
            throw new CustomException(ExceptionCode.ROADMAP_ONLY_MASTER);

        return userRoadmap;
    }

    private void checkUserPermission(Long groupId, User user) { // 추후에 사용할지 몰라 남겨둠, 유저만 접근
        UserRoadmap userRoadmap = getUserBelongRoadmap(groupId, user.getId());

        if(userRoadmap.getRole().equals(GroupRole.ROLE_NONE.getValue()))
            throw new CustomException(ExceptionCode.ROADMAP_FORBIDDEN);
    }

    // 해당 로드맵에 속한 user
    private UserRoadmap getUserBelongRoadmap(Long roadmapId, Long userId) {
        return userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmapId, userId).orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_BELONG));
    }

    private Roadmap getRoadmapById(Long roadmapId) {
        return roadmapRepository.findById(roadmapId).orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));
    }

    // 해당 로드맵에 속하지 않은 user
    private UserRoadmap getUserNotBelongRoadmap(Long roadmapId, Long userId) {
        return userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptFalse(roadmapId, userId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }

    private LocalDateTime parseDate(String date) {
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            throw new CustomException(ExceptionCode.DATE_WRONG);
        }
    }
}
