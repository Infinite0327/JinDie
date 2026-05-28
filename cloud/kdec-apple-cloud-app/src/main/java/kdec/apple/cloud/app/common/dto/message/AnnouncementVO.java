package kdec.apple.cloud.app.common.dto.message;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementVO {
    private Long announcementId;
    private Long classId;
    private String title;
    private LocalDateTime createdAt;
}
