package vttp5.batcha.travelgoeasy.server.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3Service 
{
    @Autowired 
    private AmazonS3 amazonS3;

    @Value("${do.storage.bucket}")
    private String bucketName;

    @Value("${do.storage.endpoint}")
    private String endPoint;

    public String uploadProfilePic(MultipartFile file, Integer userId) throws IOException
    {
        String fileExtension = "";
        String contentType = file.getContentType();
        System.out.println("contentType: " + contentType);

        switch (contentType) {
            case "image/png": fileExtension = ".png";
                break;
                
            case "image/jpeg": fileExtension = ".jpg";
                break;
        }

        Map<String, String> metadata = Map.of(
            "userId", userId.toString(),
            "uploadDatetime", String.valueOf(System.currentTimeMillis()),
            "contentType", contentType
        );

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setUserMetadata(metadata);

        String finalFilename = "profile-" + userId + fileExtension;

        // String origFilename = file.getOriginalFilename();
        // if(origFilename.equals("blob")){
        //     finalFilename = postId + ".png";
        // }

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, 
                                                                finalFilename, 
                                                                file.getInputStream(), 
                                                                objectMetadata);

        putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3.putObject(putObjectRequest);

        String fileUrl = "https://%s.%s/%s".formatted(bucketName, endPoint, finalFilename);
        System.out.println("Uploaded file url: " + fileUrl);
        return fileUrl;
    }

    
    // Delete file from S3
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        
        // Extract key from URL
        String key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1); // extract after the blahblah.com/
        try {
            amazonS3.deleteObject(bucketName, key);
            System.out.println("Deleted file from S3: " + key);
        } catch (Exception e) {
            System.err.println("Failed to delete file from S3: " + e.getMessage());
        }
    }
}
