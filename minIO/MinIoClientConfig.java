package com.msun.admin.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Component
public class MinIoClientConfig {
//  mc.exe  config host add minio http://47.107.61.79:19001 myminio KKSK12345678
//  mc.exe policy  set  download  minio/wido-pictures-bucket
//  mc.exe policy  set  public  minio/wido-pictures-bucket

    String endpoint = "http://localhost:19000";
    String accessKey = "";
    String secretKey = "";

    /**
     * 注入minio 客户端
     * @return
     */
    @Bean
    public MinioClient minioClient(){

        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
