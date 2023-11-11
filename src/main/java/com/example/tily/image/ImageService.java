package com.example.tily.image;

import com.example.tily._core.S3.FileFolder;
import com.example.tily._core.S3.S3Service;
import com.example.tily._core.errors.CustomException;
import com.example.tily._core.errors.ExceptionCode;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
import com.example.tily.user.User;
import com.example.tily.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class ImageService {
    private final UserRepository userRepository;
    private final RoadmapRepository roadmapRepository;
    private final S3Service s3Service;

    @Transactional
    public ImageResponse.UserImageDTO findUserImage(Long userId){
        User user = getUserById(userId);

        String url = s3Service.getFileUrl(user.getImage());

        return new ImageResponse.UserImageDTO(url);
    }

    @Transactional
    public void updateUserImageS3(Long userId, MultipartFile multipartFile, User userDetails) {
        if (!userDetails.getId().equals(userId))
            throw new CustomException(ExceptionCode.USER_UPDATE_FORBIDDEN);

        User user = getUserById(userId);

        String storageFileName = s3Service.uploadFile(multipartFile, FileFolder.USER_IMAGE);
        if(user.getImage() != null) {
            s3Service.deleteFile(user.getImage()); // s3에 업로드하고, 기존에 있던것은 지운다
        }

        user.updateImage(storageFileName); // user의 image 필드는 파일명을 가진다
    }

    @Transactional
    public void updateUserImage(Long userId, ImageRequest.UpdateUserImageDTO requestDTO, User userDetails) {
        if (!userDetails.getId().equals(userId))
            throw new CustomException(ExceptionCode.USER_UPDATE_FORBIDDEN);

        User user = getUserById(userId);

        user.updateImage(requestDTO.url());
    }

    @Transactional
    public ImageResponse.RoadmapImageDTO findRoadmapImage(Long roadmapId){
        Roadmap roadmap = getRoadmapById(roadmapId);

        String url = s3Service.getFileUrl(roadmap.getImage());

        return new ImageResponse.RoadmapImageDTO(url);
    }

    @Transactional
    public void updateRoadmapImage(Long roadmapId, MultipartFile multipartFile) {
        Roadmap roadmap = getRoadmapById(roadmapId);

        String storageFileName = s3Service.uploadFile(multipartFile, FileFolder.ROADMAP_IMAGE);
        if(roadmap.getImage() != null) {
            s3Service.deleteFile(roadmap.getImage());
        }
        roadmap.updateImage(storageFileName);
    }

    @Transactional
    public ImageResponse.PostImageDTO postImage(MultipartFile multipartFile){
        String storageFileName = s3Service.uploadFile(multipartFile, FileFolder.POST_IMAGE);

        String url = s3Service.getFileUrl(storageFileName);

        return new ImageResponse.PostImageDTO(url);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }

    private Roadmap getRoadmapById(Long roadmapId) {
        return roadmapRepository.findById(roadmapId).orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));
    }
}