package com.example.tily._core.S3;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class S3Component {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.folder.folderName1}")
    private String userFolder;

    @Value("${cloud.aws.s3.folder.folderName2}")
    private String roadmapFolder;

    @Value("${cloud.aws.s3.folder.folderName3}")
    private String postFolder;
}
