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

import java.time.LocalDateTime;
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
                .isPublic(requestDTO.roadmap().isPublic())
                .currentNum(1L)
                .code(generateRandomCode())
                .isRecruit(true)
                .stepNum(requestDTO.steps().size())
                .build();
        roadmapRepository.save(roadmap);

        List<RoadmapRequest.StepDTO> stepDTOS = requestDTO.steps();

        for(RoadmapRequest.StepDTO stepDTO : stepDTOS){
            // step 저장
            String title = stepDTO.title() ;
            String stepDescription = stepDTO.description();
            LocalDateTime dueDate =  stepDTO.dueDate();

            Step step = Step.builder().roadmap(roadmap).title(title).description(stepDescription).dueDate(dueDate).build();
            stepRepository.save(step);

            UserStep userStep = UserStep.builder().roadmap(roadmap).step(step).user(user).isSubmit(false).build();
            userStepRepository.save(userStep);

            // reference 저장
            RoadmapRequest.ReferenceDTOs referenceDTOs = stepDTO.references();
            List<Reference> referenceList = new ArrayList<>();

            // (1) youtube
            List<RoadmapRequest.ReferenceDTO> youtubeDTOs = referenceDTOs.youtube();
            for(RoadmapRequest.ReferenceDTO youtubeDTO : youtubeDTOs){
                String link = youtubeDTO.link();

                Reference reference = Reference.builder().step(step).category("youtube").link(link).build();
                referenceList.add(reference);
            }

            // (2) reference
            List<RoadmapRequest.ReferenceDTO> webDTOs = referenceDTOs.web();
            for(RoadmapRequest.ReferenceDTO webDTO : webDTOs){
                String link = webDTO.link();

                Reference reference = Reference.builder().step(step).category("web").link(link).build();
                referenceList.add(reference);
            }

            referenceRepository.saveAll(referenceList);
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
        Roadmap roadmap = roadmapRepository.findById(id).orElseThrow(
                () -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND)
        );

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

        Til latestTil = tilRepository.findFirstByOrderBySubmitDateDesc();
        Long recentTilId = latestTil != null ? latestTil.getId() : null;

        List<RoadmapResponse.FindGroupRoadmapDTO.StepDTO> steps = stepList.stream()
                .map(step -> new RoadmapResponse.FindGroupRoadmapDTO.StepDTO(step
                        , youtubeMap.get(step.getId()).stream()
                        .map(reference -> new RoadmapResponse.ReferenceDTOs.ReferenceDTO(reference))
                        .collect(Collectors.toList())
                        , webMap.get(step.getId()).stream()
                        .map(reference -> new RoadmapResponse.ReferenceDTOs.ReferenceDTO(reference))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());

        return new RoadmapResponse.FindGroupRoadmapDTO(roadmap, steps, user, recentTilId);
    }

    @Transactional
    public void updateGroupRoadmap(Long id, RoadmapRequest.UpdateGroupRoadmapDTO requestDTO, User user){
        checkManagerPermission(id ,user);

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

    @Transactional
    public RoadmapResponse.FindAllMyRoadmapDTO findAllMyRoadmaps(User user) {

        List<Roadmap> roadmaps = userRoadmapRepository.findByUserId(user.getId(), true);      // 내가 속한 로드맵 조회

        List<RoadmapResponse.FindAllMyRoadmapDTO.CategoryDTO> categories = roadmaps.stream()
                .filter(roadmap -> roadmap.getCategory().equals(Category.CATEGORY_INDIVIDUAL))
                .map(RoadmapResponse.FindAllMyRoadmapDTO.CategoryDTO::new)
                .collect(Collectors.toList());


        List<RoadmapResponse.TilyDTO> tilys = roadmaps.stream()
                .filter(roadmap -> roadmap.getCategory().equals(Category.CATEGORY_TILY))
                .map(RoadmapResponse.TilyDTO::new)
                .collect(Collectors.toList());

        List<RoadmapResponse.GroupDTO> groups = roadmaps.stream()
                .filter(roadmap -> roadmap.getCategory().equals(Category.CATEGORY_GROUP))
                .map(RoadmapResponse.GroupDTO::new)
                .collect(Collectors.toList());

        RoadmapResponse.FindAllMyRoadmapDTO.RoadmapDTO roadmapDTO =  new RoadmapResponse.FindAllMyRoadmapDTO.RoadmapDTO(tilys, groups);

        return new RoadmapResponse.FindAllMyRoadmapDTO(categories, roadmapDTO);
    }

    @Transactional
    public RoadmapResponse.FindRoadmapByQueryDTO findAll(String category, String name, int page, int size) {

        // 생성일자를 기준으로 내림차순
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Slice<Roadmap> roadmaps = roadmapRepository.findAllByOrderByCreatedDateDesc(Category.getCategory(category), name, pageable);

        List<RoadmapResponse.FindRoadmapByQueryDTO.RoadmapDTO> roadmapDTOS = roadmaps.getContent().stream().map(RoadmapResponse.FindRoadmapByQueryDTO.RoadmapDTO::new).collect(Collectors.toList());
        Boolean hasNext = roadmaps.hasNext();

        return new RoadmapResponse.FindRoadmapByQueryDTO(Category.getCategory(category), roadmapDTOS, hasNext);
    }

    @Transactional
    public void applyRoadmap(RoadmapRequest.ApplyRoadmapDTO requestDTO, Long id, User user){
        Roadmap roadmap = roadmapRepository.findById(id).
                orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));

        // 최초로 한 번만 신청 가능
        Optional<UserRoadmap> ur = userRoadmapRepository.findByRoadmapIdAndUserId(id, user.getId());
        if (ur.isPresent()) {
            if (ur.get().getRole().equals(GroupRole.ROLE_NONE))
                throw new CustomException(ExceptionCode.ROADMAP_REJECT);
            else if (ur.get().getIsAccept().equals(true))
                throw new CustomException(ExceptionCode.ROADMAP_ALREADY_MEMBER);
            else
                throw new CustomException(ExceptionCode.ROADMAP_ALREADY_APPLY);
        }

        // 신청하면 ROLE_MEMBER, isAccept=false이다. 즉 예비 맴버라는 의미
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

    @Transactional
    public RoadmapResponse.FindRoadmapMembersDTO findRoadmapMembers(Long groupsId, User user){
        checkManagerPermission(groupsId, user);

        List<UserRoadmap> userRoadmaps = userRoadmapRepository.findByRoadmapIdAndIsAcceptTrue(groupsId);

        if (userRoadmaps.isEmpty()) {
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        }

        List<RoadmapResponse.FindRoadmapMembersDTO.UserDTO> users = userRoadmaps.stream()
                .map(userRoadmap -> new RoadmapResponse.FindRoadmapMembersDTO.UserDTO(userRoadmap.getUser().getId(), userRoadmap.getUser().getName(), userRoadmap.getUser().getImage(), userRoadmap.getRole()))
                .collect(Collectors.toList());

        Optional<GroupRole> myRole = userRoadmaps.stream()
                .filter(userRoadmap -> userRoadmap.getUser().getId().equals(user.getId()))
                .map(UserRoadmap::getRole)
                .findFirst();

        return new RoadmapResponse.FindRoadmapMembersDTO(users, myRole.get());
    }

    @Transactional
    public void changeMemberRole(RoadmapRequest.ChangeMemberRoleDTO requestDTO, Long groupsId, Long membersId, User user){
        checkManagerPermission(groupsId, user);

        UserRoadmap userRoadmap = userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(groupsId, membersId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        userRoadmap.updateRole(requestDTO.role());
    }

    @Transactional
    public void dismissMember(Long groupsId, Long membersId, User user){
        checkManagerPermission(groupsId, user);

        UserRoadmap userRoadmap = userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(groupsId, membersId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 방출하면 Role을 NONE으로
        userRoadmap.updateRole(GroupRole.ROLE_NONE);
    }

    @Transactional
    public RoadmapResponse.FindAppliedUsersDTO findAppliedUsers(Long groupsId, User user){
        checkManagerPermission(groupsId, user);

        List<UserRoadmap> userRoadmaps = userRoadmapRepository.findByRoadmapIdAndIsAcceptFalse(groupsId);

        // 해당 페이지로 들어온 사용자 찾기
        UserRoadmap currentUser = userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(groupsId, user.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        List<RoadmapResponse.FindAppliedUsersDTO.UserDTO> users = userRoadmaps.stream()
                .map(userRoadmap -> new RoadmapResponse.FindAppliedUsersDTO.UserDTO(userRoadmap.getUser().getId(), userRoadmap.getUser().getName(), userRoadmap.getUser().getImage(), userRoadmap.getCreatedDate().toLocalDate(), userRoadmap.getContent()))
                .collect(Collectors.toList());

        return new RoadmapResponse.FindAppliedUsersDTO(users, currentUser.getRole());
    }

    @Transactional
    public void acceptApplication(Long groupsId, Long membersId, User user){
        checkManagerPermission(groupsId, user);

        UserRoadmap userRoadmap = userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptFalse(groupsId, membersId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        userRoadmap.updateIsAccept(true);

        // 허가하면 수강생이 해당 roadmap에 속한다 -> 제출 여부 관리를 위한 userstep에 다 넣어줘야 함
        // 로드맵의 모든 step에 대해 userstep에 넣어줘야 한다
        List<Step> steps = stepRepository.findByRoadmapId(groupsId);
        for (Step step : steps) {
            UserStep userStep = UserStep.builder().roadmap(step.getRoadmap()).step(step).user(userRoadmap.getUser()).isSubmit(false).build();
            userStepRepository.save(userStep);
        }
    }

    @Transactional
    public void rejectApplication(Long groupsId, Long membersId, User user){
        checkManagerPermission(groupsId, user);

        UserRoadmap userRoadmap = userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptFalse(groupsId, membersId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        userRoadmap.updateRole(GroupRole.ROLE_NONE);
    }

    @Transactional
    public RoadmapResponse.FindTilOfStepDTO findTilOfStep(Long groupsId, Long stepsId, Boolean isSubmit, Boolean isMember, String name){
        List<Pair<Til, User>> pairs = new ArrayList<>();

        List<Til> tils = tilRepository.findByStep_Id(stepsId);
        for(Til til : tils){
            User user = til.getWriter();

            // 어떤 틸이 존재한다면, 해당 틸은 반드시 틸이 속한 Step과 Roadmap 그리고 User를 가진다 => userStep 관계와 userRoadmap 관계는 반드시 존재한다. => 존재하지 않은 것에 대한 예외처리 필요 X
            UserStep userStep = userStepRepository.findByUserIdAndStepId(user.getId(), stepsId).get();
            UserRoadmap userRoadmap = userRoadmapRepository.findByRoadmapIdAndUserId(groupsId, user.getId()).get();

            if((isSubmit == userStep.getIsSubmit())  && (name == null || name.equals(user.getName()))){
                // isMember가 false => 운영자를 포함해서 모든 til을 반환, isMember가 true => 운영자의 til을 제외하고 반환한다
                if(!isMember || (isMember && GroupRole.ROLE_MEMBER.equals(userRoadmap.getRole()))){
                    Pair<Til, User> pair = Pair.of(til, user);
                    pairs.add(pair);
                }
            }
        }

        List<RoadmapResponse.FindTilOfStepDTO.MemberDTO> members;

        if(isSubmit) {
            members = pairs.stream()
                    .map(pair -> new RoadmapResponse.FindTilOfStepDTO.MemberDTO(pair.getFirst().getId(), pair.getSecond().getId(),pair.getSecond().getName(), pair.getSecond().getImage(), pair.getFirst().getContent(), pair.getFirst().getSubmitDate().toLocalDate(), pair.getFirst().getCommentNum()))
                    .collect(Collectors.toList());
        }
        else{
            members = pairs.stream()
                    .map(pair -> new RoadmapResponse.FindTilOfStepDTO.MemberDTO(null, pair.getSecond().getId(), pair.getSecond().getName(), null, null, null, 0))
                    .collect(Collectors.toList());
        }

        return new RoadmapResponse.FindTilOfStepDTO(members);
    }

    private void checkManagerPermission(Long groupsId, User user) { // 매니저급만 접근
        UserRoadmap currentUserRoadmap = userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(groupsId, user.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_BELONG));

        if(currentUserRoadmap.getRole() != GroupRole.ROLE_MASTER && currentUserRoadmap.getRole() != GroupRole.ROLE_MANAGER){
            throw new CustomException(ExceptionCode.ROADMAP_FORBIDDEN);
        }
    }

    private void checkUserPermission(Long groupsId, User user) { // 추후에 사용할지 몰라 남겨둠, 유저만 접근
        UserRoadmap currentUserRoadmap = userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(groupsId, user.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_BELONG));

        if(currentUserRoadmap.getRole() == GroupRole.ROLE_NONE){
            throw new CustomException(ExceptionCode.ROADMAP_FORBIDDEN);
        }
    }
}
