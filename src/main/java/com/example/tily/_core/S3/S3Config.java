package com.example.tily._core.S3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;

import java.net.URI;

@Configuration
public class S3Config {
    @Value("${cloud.aws.credentials.access-key}") // application.yml 에 명시한 내용
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("krmp-proxy.9rum.cc")
    private String proxyHost;

    @Value("3128")
    private int proxyPort;


//    @Bean
//    @Profile({"local", "prod"})
//    public AmazonS3Client amazonS3Client() {
//        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
//        return (AmazonS3Client) AmazonS3ClientBuilder
//                .standard()
//                .withRegion(region)
//                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//                .build();
//    }

//    @Bean
//    @Profile({"local", "product", "test"})
//    public AmazonS3 amazonS3Client() {
//        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
//
//        return AmazonS3ClientBuilder
//                .standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withRegion(region)
//                .build();
//    }
//
//    @Bean
//    @Profile("deploy")
//    public AmazonS3Client amazonS3ClientForDeploy() {
//        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
//
//        ClientConfiguration clientConfiguration = new ClientConfiguration();
//        clientConfiguration.setConnectionTimeout(60000);  // 연결 타임아웃 시간 60000ms = 60s 설정
//        clientConfiguration.setSocketTimeout(60000);  // 소켓 타임아웃 시간 60000ms = 60s 설정
//        clientConfiguration.setProxyHost(proxyHost);
//        clientConfiguration.setProxyPort(proxyPort);
//        clientConfiguration.setProxyProtocol(Protocol.HTTP);
//
//        return (AmazonS3Client) AmazonS3ClientBuilder
//                .standard()
//                .withRegion(region)
//                .withClientConfiguration(clientConfiguration)
//                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//                .build();
//    }

    @Bean
    public S3Client s3Client() {
        SdkHttpClient sdkHttpClient = ApacheHttpClient.builder()
                .proxyConfiguration(ProxyConfiguration.builder().endpoint(URI.create("http://krmp-proxy.9rum.cc:3128"))
                        .build())
                .build();
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .httpClient(sdkHttpClient)
                .build();
    }
}
