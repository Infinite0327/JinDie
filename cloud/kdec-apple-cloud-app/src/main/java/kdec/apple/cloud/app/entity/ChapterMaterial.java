package kdec.apple.cloud.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import kdec.apple.cloud.app.entity.enums.FileType;
import kdec.apple.cloud.app.entity.enums.ParseStatus;
import kdec.apple.cloud.app.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDateTime;

/**
 * 上传的资料
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("chapter_material")
public class ChapterMaterial {
    @TableId
    private Long id;
    private Long chapterId;
    private Long uploaderId;
    private UserRole uploaderRole;  // TEACHER / STUDENT
    private String title;
    private String description;
    private String fileName;
    private FileType fileType;      // DOCUMENT / AUDIO / VIDEO / IMAGE
    private String fileUrl;
    private Long fileSize;
    private ParseStatus parseStatus; // PENDING / PROCESSING / DONE / FAILED
    private String failReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}