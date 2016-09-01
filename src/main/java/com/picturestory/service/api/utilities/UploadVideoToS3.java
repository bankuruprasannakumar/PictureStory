package com.picturestory.service.api.utilities;

import java.io.File;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * Created by bankuru on 27/8/16.
 */
public class UploadVideoToS3 {
    private static final String SUFFIX = "/";

    public static String uploadImagesToS3(String fileUrl, String fileName, String userName){
        try {
            // credentials object identifying user for authentication
            // user must have AWSConnector and AmazonS3FullAccess for
            // this to work

            AWSCredentials credentials = new BasicAWSCredentials(
                    "AKIAJBJ5XROOYK3HGAPQ",
                    "dg7mxOMuGO6yzzoJeiDnjerXXdiRiZrnNn68EtSn");

            // create a client connection based on credentials
            AmazonS3 s3client = new AmazonS3Client(credentials);

            // create bucket - name must be unique for all S3 users
            String bucketName = "pixtorycontent";

            fileName = "contributor" + SUFFIX + userName + SUFFIX + fileName;

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
