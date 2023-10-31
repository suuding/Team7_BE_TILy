package com.example.tily.image;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AwsS3Config {
    @Value("${cloud.aws.credentials.access-key}") // application.yml 에 명시한 내용
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}

/*
// applcation.yml 파일 설정 필요 -> 하지 않으면 서버 실행이 안됨
cloud:
  aws:
    credentials:
      access-key: AWIUEJWQIUWJEJIOWJIO  // IAM 계정의 accessKey
      secret-key: BqwulzzZqiZw+wwW0ifeweqDmiz+LfAlp  // IAM 계정의 secretKey
    region:
      static: ap-northeast-2  // 버킷의 리전
    s3:
      bucket: my-bucket   //  버킷 이름
    stack:
      auto: false

spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
 */
