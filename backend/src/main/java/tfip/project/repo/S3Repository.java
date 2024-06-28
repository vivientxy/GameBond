package tfip.project.repo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

@Repository
public class S3Repository {

    @Autowired
    AmazonS3 s3;

    @Value("${s3.bucket}")
	private String bucket;

    public String saveToS3(MultipartFile uploadFile, String id) throws IOException {
        Map<String, String> userData = new HashMap<>();
        userData.put("filename", uploadFile.getOriginalFilename());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(uploadFile.getContentType());
        metadata.setContentLength(uploadFile.getSize());
        metadata.setUserMetadata(userData);

        PutObjectRequest putRequest = new PutObjectRequest(bucket, id, uploadFile.getInputStream(), metadata);
        putRequest.withCannedAcl(CannedAccessControlList.PublicRead);

        s3.putObject(putRequest);
        return s3.getUrl(bucket, id).toString();
    }

    public S3Object getFileFromS3(String fileId) {
        try {
            GetObjectRequest getRequest = new GetObjectRequest(bucket, fileId);
            S3Object result = s3.getObject(getRequest);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

}
