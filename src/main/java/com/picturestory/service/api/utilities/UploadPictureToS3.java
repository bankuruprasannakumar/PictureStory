package com.picturestory.service.api.utilities;

import java.io.File;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * Created by bankuru on 27/8/16.
 */
public class UploadPictureToS3 {
    private static final String SUFFIX = "/";

    public static String uploadImagesToS3(String fileUrl, String fileName, String userName, String prefix){
        try {

            // create a client connection based on credentials
            AmazonS3 s3client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new InstanceProfileCredentialsProvider())
                    .build();

            // create bucket - name must be unique for all S3 users
            String bucketName = "pixtory-uploaded-content";

            if (userName != null) {
                fileName = prefix + SUFFIX + userName + SUFFIX + fileName;
            } else {
                fileName = prefix + SUFFIX + fileName;
            }

            // upload file to folder and set it to public
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");
            s3client.putObject(new PutObjectRequest(bucketName, fileName+".jpeg",
                    new File(fileUrl))
                    .withCannedAcl(CannedAccessControlList.PublicRead)
                    .withMetadata(metadata));
            return fileName;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
