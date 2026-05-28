package kdec.apple.base.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import kdec.apple.base.common.exception.BusinessException;
import kdec.apple.base.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
//TODO:是不是用oss还是金蝶有自己的
@Component
@RequiredArgsConstructor
public class OssUtil {

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.accessKeyId}")
    private String accessKeyId;

    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${oss.bucketName}")
    private String bucketName;

    @Value("${oss.domain}")
    private String domain;  // CDN域名或OSS域名，用于拼接访问URL

    /**
     * 上传文件，返回访问URL
     */
    public String upload(MultipartFile file) {
        // 1. 生成唯一文件名，避免重名覆盖
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = UUID.randomUUID().toString().replace("-", "") + ext;

        // 2. 上传到OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.putObject(bucketName, objectName, file.getInputStream());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OSS_UPLOAD_FAILED);
        } finally {
            ossClient.shutdown();
        }

        // 3. 拼接返回访问URL
        return domain + "/" + objectName;
    }

    /**
     * 删除文件
     */
    public void delete(String ossUrl) {
        // 从URL里截取objectName
        String objectName = ossUrl.replace(domain + "/", "");
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.deleteObject(bucketName, objectName);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OSS_DELETE_FAILED);
        } finally {
            ossClient.shutdown();
        }
    }
}