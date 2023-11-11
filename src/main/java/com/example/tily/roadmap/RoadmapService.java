package com.example.tily.roadmap;

import com.example.tily._core.errors.CustomException;
import com.example.tily._core.errors.ExceptionCode;
import com.example.tily.alarm.AlarmRepository;
import com.example.tily.comment.Comment;
import com.example.tily.comment.CommentRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final CommentRepository commentRepository;
    private final AlarmRepository alarmRepository;

    // 로드맵 생성하기(개인, 그룹)
    @Transactional
    public RoadmapResponse.CreateRoadmapDTO createRoadmap(RoadmapRequest.CreateRoadmapDTO requestDTO, User user){
        Roadmap roadmap = Roadmap.builder()
                .creator(user)
                .category(Category.getCategory(requestDTO.category()))
                .name(requestDTO.name())
                .description(requestDTO.description())
                .isPublic(requestDTO.isPublic()) // 공개여부
                .code(requestDTO.category().equals(Category.CATEGORY_GROUP.getValue()) ? generateRandomCode() : null)
                .isRecruit(!requestDTO.category().equals(Category.CATEGORY_INDIVIDUAL.getValue()))    // 모집여부
                .stepNum(0)
                .build();
        roadmapRepository.save(roadmap);

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

    // 틸리 로드맵 생성하기 - 임시 api
    @Transactional
    public RoadmapResponse.CreateRoadmapDTO createTilyRoadmap(RoadmapRequest.CreateTilyRoadmapDTO requestDTO, User user){
        Roadmap roadmap = Roadmap.builder()
                .creator(user)
                .category(Category.CATEGORY_TILY)
                .name(requestDTO.roadmap().name())
                .description(requestDTO.roadmap().description())
                .isPublic(requestDTO.roadmap().isPublic()) // 공개여부
                .code(generateRandomCode())
                .isRecruit(true)    // 모집여부
                .stepNum(requestDTO.steps().size())
                .image(requestDTO.roadmap().image())
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
                Reference reference = Reference.builder().
                        step(step).
                        category("youtube").
                        link(youtubeDTO.link()).
                        build();
                references.add(reference);
            }

            // (2) reference
            List<RoadmapRequest.ReferenceDTO> webDTOs = referenceDTOs.web();
            for(RoadmapRequest.ReferenceDTO webDTO : webDTOs){
                Reference reference = Reference.builder().
                        step(step).
                        category("web").
                        link(webDTO.link()).
                        build();
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

    // 로드맵 정보 조회하기
    public RoadmapResponse.FindRoadmapDTO findRoadmap(Long roadmapId, User user){
        Roadmap roadmap = getRoadmapById(roadmapId);

        List<Step> stepList = stepRepository.findByRoadmapId(roadmapId);

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

        List<RoadmapResponse.FindRoadmapDTO.StepDTO> steps = stepList.stream()
                .map(step -> new RoadmapResponse.FindRoadmapDTO.StepDTO(step
                        , youtubeMap.get(step.getId()).stream()
                        .map(RoadmapResponse.ReferenceDTOs.ReferenceDTO::new).collect(Collectors.toList())
                        , webMap.get(step.getId()).stream()
                        .map(RoadmapResponse.ReferenceDTOs.ReferenceDTO::new).collect(Collectors.toList())))
                .collect(Collectors.toList());

        String myRole = "none";
        Long recentTilId = null;
        Long recentStepId = null;

        // 해당 페이지(로드맵 상세 조회 페이지)에 들어온 사용자가
        // 로드맵에 속하고, 로드맵의 step에 저장한 til이 있다면
        // 최근에 저장한 til과 그에 대한 step 반환
        if(user != null){
            Optional<UserRoadmap> userRoadmap = userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmapId, user.getId());

            if (userRoadmap.isPresent()) {
                myRole = userRoadmap.get().getRole();
                List<Til> tils = tilRepository.findByUserIdByOrderByUpdatedDateDesc(roadmapId, user.getId());
                recentTilId = !tils.isEmpty() ? tils.get(0).getId() : null;
                recentStepId = !tils.isEmpty() ? tils.get(0).getStep().getId() : null;
            }
        }

        return new RoadmapResponse.FindRoadmapDTO(roadmap, steps, roadmap.getCreator(), recentTilId, recentStepId, myRole);
    }

    // 그룹 로드맵 정보 수정하기
    @Transactional
    public void updateRoadmap(Long roadmapId, RoadmapRequest.UpdateRoadmapDTO requestDTO, User user){
        checkMasterAndManagerPermission(roadmapId ,user);

        Roadmap roadmap = getRoadmapById(roadmapId);

        roadmap.update(requestDTO);
    }

    // 내가 속한 로드맵 전체 목록 조회하기
    public RoadmapResponse.FindAllMyRoadmapDTO findAllMyRoadmaps(User user) {
        List<Roadmap> roadmaps = userRoadmapRepository.findByUserIdAndIsAccept(user.getId(), true);      // 내가 속한 로드맵 조회

        List<RoadmapResponse.FindAllMyRoadmapDTO.CategoryDTO> categories = roadmaps.stream()
                .filter(roadmap -> roadmap.getCategory().equals(Category.CATEGORY_INDIVIDUAL))
                .map(RoadmapResponse.FindAllMyRoadmapDTO.CategoryDTO::new).collect(Collectors.toList());

        List<RoadmapResponse.TilyDTO> tilys = roadmaps.stream()
                .filter(roadmap -> roadmap.getCategory().equals(Category.CATEGORY_TILY))
                .map(RoadmapResponse.TilyDTO::new).collect(Collectors.toList());

        // 내가 속한 그룹 로드맵 조회할 때
        // 해당 로드맵내 사용자의 관리자(master 또는 manager) 여부 반환
        List<RoadmapResponse.GroupDTO> groups = roadmaps.stream()
                .filter(roadmap -> roadmap.getCategory().equals(Category.CATEGORY_GROUP))
                .map(roadmap -> {
                    String groupRole = userRoadmapRepository.findByRoadmapIdAndUserId(roadmap.getId(), user.getId()).get().getRole();
                    boolean isManager = groupRole.equals(GroupRole.ROLE_MASTER.getValue()) || groupRole.equals(GroupRole.ROLE_MANAGER.getValue());
                    return new RoadmapResponse.GroupDTO(roadmap, isManager);
                }).collect(Collectors.toList());

        return new RoadmapResponse.FindAllMyRoadmapDTO(categories, new RoadmapResponse.FindAllMyRoadmapDTO.RoadmapDTO(tilys, groups));
    }

    // 로드맵 조회하기
    public RoadmapResponse.FindRoadmapByQueryDTO findAll(String category, String name, int page, int size) {
        // 생성일자를 기준으로 내림차순
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Slice<Roadmap> roadmaps = roadmapRepository.findAllByOrderByCreatedDateDesc(Category.getCategory(category), name, pageable);

        List<RoadmapResponse.FindRoadmapByQueryDTO.RoadmapDTO> roadmapDTOS = roadmaps.getContent().stream().map(RoadmapResponse.FindRoadmapByQueryDTO.RoadmapDTO::new).collect(Collectors.toList());
        boolean hasNext = roadmaps.hasNext();

        return new RoadmapResponse.FindRoadmapByQueryDTO(Category.getCategory(category), roadmapDTOS, hasNext);
    }

    // 그룹 로드맵에 참여 신청하기
    @Transactional
    public void applyGroupRoadmap(RoadmapRequest.ApplyRoadmapDTO requestDTO, Long roadmapId, User user){
        Roadmap roadmap = getRoadmapById(roadmapId);

        // 로드맵이 모집을 중단한 경우
        if (!roadmap.isRecruit())
            throw new CustomException(ExceptionCode.ROADMAP_END_RECRUIT);

        // 로드맵에 최초로 한 번만 신청 가능
        Optional<UserRoadmap> ur = userRoadmapRepository.findByRoadmapIdAndUserId(roadmapId, user.getId());
        if (ur.isPresent()) {
            if (ur.get().getRole().equals(GroupRole.ROLE_NONE.getValue()))          // 이미 거절당한 경우
                throw new CustomException(ExceptionCode.ROADMAP_REJECT);
            else if (ur.get().isAccept())                                           // 이미 소속된 경우
                throw new CustomException(ExceptionCode.ROADMAP_ALREADY_MEMBER);
            else                                                                    // 이미 신청한 경우
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

    // tily 로드맵에 참여 신청하기
    @Transactional
    public void applyTilyRoadmap(Long roadmapId, User user) {
        Roadmap roadmap = getRoadmapById(roadmapId);

        // 로드맵이 모집을 중단한 경우
        if (!roadmap.isRecruit())
            throw new CustomException(ExceptionCode.ROADMAP_END_RECRUIT);

        // 이미 tily 로드맵에 속한 경우
        Optional<UserRoadmap> ur = userRoadmapRepository.findByRoadmapIdAndUserId(roadmapId, user.getId());
        if (ur.isPresent() && ur.get().isAccept())
                throw new CustomException(ExceptionCode.ROADMAP_ALREADY_MEMBER);

        UserRoadmap userRoadmap = UserRoadmap.builder()
                .roadmap(roadmap)
                .user(user)
                .role(GroupRole.ROLE_MEMBER)
                .isAccept(true)
                .progress(0)
                .build();
        userRoadmapRepository.save(userRoadmap);

        // 수강생이 해당 roadmap에 속한다 -> 제출 여부 관리를 위해 모든 step에 대해 userstep에 다 저장
        saveUserStep(roadmapId, userRoadmap);
    }

    // 참가 코드로 그룹 로드맵 참여하기
    @Transactional
    public RoadmapResponse.ParticipateRoadmapDTO participateRoadmap(RoadmapRequest.ParticipateRoadmapDTO requestDTO, User user){
        String code = requestDTO.code();
        Roadmap roadmap = roadmapRepository.findByCode(code)
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));

        // 이미 로드맵에 속한 경우
        Optional<UserRoadmap> ur = userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmap.getId(), user.getId());
        if (ur.isPresent())
            throw new CustomException(ExceptionCode.ROADMAP_ALREADY_MEMBER);

        UserRoadmap userRoadmap = UserRoadmap.builder()
                .roadmap(roadmap)
                .user(user)
                .role(GroupRole.ROLE_MEMBER)
                .content(null)
                .isAccept(true)     // 코드로 참여시 승인없이 수락되고 맴버가 된다
                .progress(0)
                .build();
        userRoadmapRepository.save(userRoadmap);

        // 해당 roadmap에 속한다 -> 제출 여부 관리를 위해 모든 step에 대해 userstep에 다 저장
        saveUserStep(roadmap.getId(), userRoadmap);

        return new RoadmapResponse.ParticipateRoadmapDTO(roadmap);
    }

    // 그룹 로드맵의 구성원 전체 조회하기
    public RoadmapResponse.FindRoadmapMembersDTO findRoadmapMembers(Long roadmapId, User user){
        String myRole = checkMasterAndManagerPermission(roadmapId, user);

        List<UserRoadmap> userRoadmaps = userRoadmapRepository.findByRoadmapIdAndIsAcceptTrue(roadmapId);

        List<RoadmapResponse.FindRoadmapMembersDTO.UserDTO> users = userRoadmaps.stream()
                .map(userRoadmap -> new RoadmapResponse.FindRoadmapMembersDTO.UserDTO(userRoadmap.getUser(), userRoadmap.getRole()))
                .collect(Collectors.toList());

        return new RoadmapResponse.FindRoadmapMembersDTO(myRole, users);
    }

    // 그룹 로드맵의 구성원 역할 바꾸기
    @Transactional
    public void changeMemberRole(RoadmapRequest.ChangeMemberRoleDTO requestDTO, Long roadmapId, Long membersId, User user){
        checkMasterPermission(roadmapId, user); // master만 역할 수정 가능

        UserRoadmap userRoadmap = getUserBelongRoadmap(roadmapId, membersId);

        userRoadmap.updateRole(requestDTO.role());
    }

    // 그룹 로드맵의 구성원 강퇴하기
    @Transactional
    public void dismissMember(Long roadmapId, Long membersId, User user){
        String role = checkMasterAndManagerPermission(roadmapId, user);

        UserRoadmap userRoadmap = getUserBelongRoadmap(roadmapId, membersId);

        // master는 다 강퇴 가능, manager는 member만 강퇴 가능
        if (role.equals(GroupRole.ROLE_MANAGER.getValue()) & userRoadmap.getRole().equals(GroupRole.ROLE_MASTER.getValue()))
            throw new CustomException(ExceptionCode.ROADMAP_DISMISS_FORBIDDEN);

        userRoadmap.updateRoleAndIsAccept(GroupRole.ROLE_NONE.getValue(), false);
    }

    // 그룹 로드맵에 신청한 사람들 목록 조회하기
    public RoadmapResponse.FindAppliedUsersDTO findAppliedUsers(Long roadmapId, User user){
        checkMasterAndManagerPermission(roadmapId, user);

        List<UserRoadmap> userRoadmaps = userRoadmapRepository.findByRoadmapIdAndIsAcceptFalseAndRole(roadmapId, GroupRole.ROLE_MEMBER.getValue());

        List<RoadmapResponse.FindAppliedUsersDTO.UserDTO> users = userRoadmaps.stream()
                .map(userRoadmap -> new RoadmapResponse.FindAppliedUsersDTO.UserDTO(userRoadmap.getUser(), userRoadmap))
                .collect(Collectors.toList());

        return new RoadmapResponse.FindAppliedUsersDTO(users);
    }

    // 그룹 로드맵 참여 신청 승인
    @Transactional
    public void acceptApplication(Long roadmapId, Long memberId, User user){
        checkMasterAndManagerPermission(roadmapId, user);

        UserRoadmap userRoadmap = getUserNotBelongRoadmap(roadmapId, memberId);

        // 이미 로드맵에 대해 신청 거절당한 사람(none)인 경우
        if (userRoadmap.getRole().equals(GroupRole.ROLE_NONE.getValue()))
            throw new CustomException(ExceptionCode.ROADMAP_REJECT);

        userRoadmap.updateIsAccept(true);

        saveUserStep(roadmapId, userRoadmap);
    }

    // 그룹 로드맵 참여 신청 거절
    @Transactional
    public void rejectApplication(Long roadmapId, Long memberId, User user){
        checkMasterAndManagerPermission(roadmapId, user);

        UserRoadmap userRoadmap = getUserNotBelongRoadmap(roadmapId, memberId);

        // 거절당한 사람은 none 으로 역할 수정
        userRoadmap.updateRole(GroupRole.ROLE_NONE.getValue());
    }

    // 로드맵 삭제하기
    @Transactional
    public void deleteRoadmap(Long roadmapId, User user){
        checkMasterAndManagerPermission(roadmapId, user);

        // Til 삭제
        List<Til> tils = tilRepository.findByRoadmapId(roadmapId);
        List<Long> tilIds = tils.stream()
                .map(Til::getId)
                .collect(Collectors.toList());

        tilRepository.softDeleteTilsByTilIds(tilIds);

        // Til과 연관된 Comment들 삭제
        List<Comment> comments = getCommentsByTilIds(tilIds);
        List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        commentRepository.softDeleteCommentsByIds(commentIds);

        // Comment와 관련된 알람 삭제
        alarmRepository.deleteByCommentIds(commentIds);

        // 로드맵의 Reference들을 삭제
        List<Step> steps = getStepsByRoadmapId(roadmapId);
        List<Long> stepIds = steps.stream()
                .map(Step::getId)
                .collect(Collectors.toList());

        referenceRepository.softDeleteReferenceByStepIds(stepIds);

        // 로드맵의 Step들을 삭제
        stepRepository.softDeleteStepByStepIds(stepIds);

        // UserStep들을 삭제
        userStepRepository.softDeleteUserStepByStepIds(stepIds);

        // UserRoadmap을 삭제
        userRoadmapRepository.softDeleteUserRoadmapByRoadmapId(roadmapId);

        // Roadmap을 삭제
        roadmapRepository.softDeleteRoadmapById(roadmapId);
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

        if (!userRoadmap.getRole().equals(GroupRole.ROLE_MASTER.getValue()))
            throw new CustomException(ExceptionCode.ROADMAP_ONLY_MASTER);

        return userRoadmap;
    }

    // 해당 로드맵에 속한 user
    private UserRoadmap getUserBelongRoadmap(Long roadmapId, Long userId) {
        getRoadmapById(roadmapId);

        return userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmapId, userId).orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_BELONG));
    }

    private Roadmap getRoadmapById(Long roadmapId) {
        return roadmapRepository.findById(roadmapId).orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));
    }

    private List<Step> getStepsByRoadmapId(Long roadmapId){
        return stepRepository.findByRoadmapId(roadmapId);
    }

    private List<Comment> getCommentsByTilIds( List<Long> tilIds){
        return commentRepository.findByTilIds(tilIds);
    }

    // 해당 로드맵에 속하지 않은 user
    private UserRoadmap getUserNotBelongRoadmap(Long roadmapId, Long userId) {
        return userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptFalse(roadmapId, userId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }

    // 로드맵의 모든 step에 대해 userstep에 다 저장
    private void saveUserStep(Long roadmapId, UserRoadmap userRoadmap) {
        List<Step> steps = stepRepository.findByRoadmapId(roadmapId);
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
}
