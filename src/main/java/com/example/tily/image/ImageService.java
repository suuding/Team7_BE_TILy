package com.example.tily.image;

import com.example.tily._core.errors.exception.CustomException;
import com.example.tily._core.errors.exception.ExceptionCode;
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
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final RoadmapRepository roadmapRepository;
    private final S3Service s3Service;

    @Transactional
    public ImageResponse.UserImageDTO findUserImage(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        String url = s3Service.getFileUrl(user.getImage());

        return new ImageResponse.UserImageDTO(url);
    }

    public void updateUserImage(Long userId, MultipartFile multipartFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        String storageFileName = s3Service.uploadFile(multipartFile, FileFolder.USER_IMAGE);

        imageRepository.save(new Image(multipartFile.getOriginalFilename(), storageFileName, s3Service.getFileUrl(storageFileName)));
        user.updateImage(storageFileName); // user의 image 필드는 파일명을 가진다
    }

    @Transactional
    public ImageResponse.RoadmapImageDTO findRoadmapImage(Long userId){
        Roadmap roadmap = roadmapRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));

        String url = s3Service.getFileUrl(roadmap.getImage());

        return new ImageResponse.RoadmapImageDTO(url);
    }

    public void updateRoadmapImage(Long roadmapId, MultipartFile multipartFile) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));

        String storageFileName = s3Service.uploadFile(multipartFile, FileFolder.ROADMAP_IMAGE);

        imageRepository.save(new Image(multipartFile.getOriginalFilename(), storageFileName, s3Service.getFileUrl(storageFileName)));
        roadmap.updateImage(storageFileName);
    }
}