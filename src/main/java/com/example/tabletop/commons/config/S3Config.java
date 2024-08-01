package com.example.tabletop.commons.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {
   @Value("${cloud.aws.credentials.access-key}")
   private String awsAccessKey;
   @Value("${cloud.aws.credentials.secret-key}")
   private String awsSecretKey;
   @Value("${cloud.aws.region.static}")
   private String region;
   @Bean
   public AmazonS3 s3client() {
       BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);  // AWS 자격 증명
       // AmazonS3 클라이언트 생성 : AWS S3 서비스와 상호 작용에 필요
       return AmazonS3ClientBuilder.standard()
               .withRegion(region)
               .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
               .build();
   }
}
