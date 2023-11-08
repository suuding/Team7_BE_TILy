package com.example.tily.roadmap;

import com.example.tily._core.errors.exception.CustomException;
import com.example.tily._core.errors.exception.ExceptionCode;
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
    private final CommentRepository commentRepository;

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
                .name(requestDTO.name())
                .description(requestDTO.description())
                .isPublic(requestDTO.isPublic()) // 공개여부
                .currentNum(1L)
                .code(generateRandomCode())
                .isRecruit(true)    // 모집여부
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

    @Transactional
    public RoadmapResponse.CreateRoadmapDTO createTilyRoadmap(RoadmapRequest.CreateTilyRoadmapDTO requestDTO, User user){

        Roadmap roadmap = Roadmap.builder()
                .creator(user)
                .category(Category.CATEGORY_TILY)
                .name(requestDTO.roadmap().name())
                .description(requestDTO.roadmap().description())
                .isPublic(requestDTO.roadmap().isPublic()) // 공개여부
                .currentNum(1L)
                .code(generateRandomCode())
                .isRecruit(true)    // 모집여부
                .stepNum(requestDTO.steps().size())
                .image("tily.png")
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

    // 그룹 로드맵 정보 수정하기
    @Transactional
    public void updateGroupRoadmap(Long id, RoadmapRequest.UpdateGroupRoadmapDTO requestDTO, User user){
        checkMasterAndManagerPermission(id ,user);

        Roadmap roadmap = roadmapRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));

        // roadmap update
        roadmap.update(requestDTO);
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
                    boolean isManager = groupRole.equals(GroupRole.ROLE_MASTER.getValue()) || groupRole.equals(GroupRole.ROLE_MANAGER.getValue());
                    return new RoadmapResponse.GroupDTO(roadmap, isManager);
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
    public void applyGroupRoadmap(RoadmapRequest.ApplyRoadmapDTO requestDTO, Long id, User user){
        Roadmap roadmap = getRoadmapById(id);

        // 모집을 중단했을 때
        if (!roadmap.isRecruit())
            throw new CustomException(ExceptionCode.ROADMAP_END_RECRUIT);

        // 최초로 한 번만 신청 가능
        Optional<UserRoadmap> ur = userRoadmapRepository.findByRoadmapIdAndUserId(id, user.getId());
        if (ur.isPresent()) {
            if (ur.get().getRole().equals(GroupRole.ROLE_NONE.getValue()))
                throw new CustomException(ExceptionCode.ROADMAP_REJECT);
            else if (ur.get().isAccept())
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
    public void applyTilyRoadmap(Long id, User user) {
        Roadmap roadmap = getRoadmapById(id);

        // 모집을 중단했을 때
        if (!roadmap.isRecruit())
            throw new CustomException(ExceptionCode.ROADMAP_END_RECRUIT);

        // 이미 로드맵에 속한 경우
        Optional<UserRoadmap> ur = userRoadmapRepository.findByRoadmapIdAndUserId(id, user.getId());
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
        List<Step> steps = stepRepository.findByRoadmapId(id);
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

        // 해당 roadmap에 속한다 -> 제출 여부 관리를 위해 모든 step에 대해 userstep에 다 저장
        List<Step> steps = stepRepository.findByRoadmapId(roadmap.getId());
        for (Step step : steps) {
            UserStep userStep = UserStep.builder()
                    .roadmap(step.getRoadmap())
                    .step(step)
                    .user(userRoadmap.getUser())
                    .isSubmit(false)
                    .build();
            userStepRepository.save(userStep);
        }

        return new RoadmapResponse.ParticipateRoadmapDTO(roadmap);
    }

    public RoadmapResponse.FindRoadmapMembersDTO findRoadmapMembers(Long groupsId, User user){
        String myRole = checkMasterAndManagerPermission(groupsId, user);

        List<UserRoadmap> userRoadmaps = userRoadmapRepository.findByRoadmapIdAndIsAcceptTrue(groupsId);

        List<RoadmapResponse.FindRoadmapMembersDTO.UserDTO> users = userRoadmaps.stream()
                .map(userRoadmap -> new RoadmapResponse.FindRoadmapMembersDTO.UserDTO(userRoadmap.getUser(), userRoadmap.getRole()))
                .collect(Collectors.toList());

        return new RoadmapResponse.FindRoadmapMembersDTO(myRole, users);
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
    public void deleteRoadmap(Long roadmapId, User user){
        Roadmap roadmap = getRoadmapById(roadmapId);

        checkMasterAndManagerPermission(roadmapId, user);

        // 1. Til과 연관된 Comment들을 삭제한다.
        List<Til> tils = getTilsByRoadmapId(roadmapId);
        List<Long> tilIds = tils.stream()
                .map(Til::getId)
                .collect(Collectors.toList());

        commentRepository.softDeleteCommentsByTilIds(tilIds);

        // 2. Til을 삭제한다.
        tilRepository.softDeleteTilsByTilIds(tilIds);

        // 3. Reference들을 삭제한다
        List<Step> steps = getStepsByRoadmapId(roadmapId);
        List<Long> stepIds = steps.stream()
                .map(Step::getId)
                .collect(Collectors.toList());

        referenceRepository.softDeleteReferenceByStepIds(stepIds);

        // 4. Step들을 삭제한다.
        stepRepository.softDeleteStepByStepIds(stepIds);

        // 5. UserStep들을 삭제한다.
        userStepRepository.softDeleteUserStepByStepIds(stepIds);

        // 6. UserRoadmap을 삭제한다
        userRoadmapRepository.softDeleteUserRoadmapByRoadmapId(roadmapId);

        // 7. Roadmap을 삭제한다
        roadmapRepository.delete(roadmap);
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

    private List<Step> getStepsByRoadmapId(Long roadmapId){
        return stepRepository.findByRoadmapId(roadmapId);
    }

    private List<Til> getTilsByRoadmapId(Long roadmapId){
        return tilRepository.findByRoadmapId(roadmapId);
    }

    // 해당 로드맵에 속하지 않은 user
    private UserRoadmap getUserNotBelongRoadmap(Long roadmapId, Long userId) {
        return userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptFalse(roadmapId, userId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }
}
