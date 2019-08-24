package com.nexters.teambuilder.s3uploader.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {

    /**
     * Amazon S3 client Configuration for TransferManager.
     */
    @Bean
    public TransferManager amazonS3Client() {
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().withRegion("ap-northeast-2").build();
        return TransferManagerBuilder.standard().withS3Client(amazonS3).build();
    }
}