package com.luckcat.utils;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Oriental
 * @version 1.0
 * @description MInio的初始化
 * @date 2023/3/30 14:27
 */

@Component
@Data
public class MinioInit {
    @Value("${minio.username}")
    private String user;
    @Value("${minio.key}")
    private String key;
    @Value("${minio.url}")
    private String minioUrl;
    @Value("${minio.buckNameOfPhoto}")
    private String buckNameOfPhoto;
    @Value("${minio.buckNameOfAvatar}")
    private String buckNameOfAvatar;

    @Bean
    public MinioClient createMinio() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = MinioClient.builder()
                        // minio服务端地址URL
                        .endpoint(minioUrl)
                        // 用户名及密码（访问密钥/密钥）
                        .credentials(user, key)
                        .build();
        boolean foundPhoto = minioClient.bucketExists(BucketExistsArgs.builder().bucket(buckNameOfPhoto).build());
        boolean foundAvatar = minioClient.bucketExists(BucketExistsArgs.builder().bucket(buckNameOfAvatar).build());
        if(!foundPhoto){
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(buckNameOfPhoto).build());
        }
        if(!foundAvatar){
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(buckNameOfAvatar).build());
        }
        return minioClient;
    }

}
