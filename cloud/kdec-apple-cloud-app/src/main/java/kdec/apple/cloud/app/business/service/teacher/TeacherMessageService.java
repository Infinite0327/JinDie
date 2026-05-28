package kdec.apple.cloud.app.business.service.teacher;

import kdec.apple.cloud.app.common.dto.message.AnnouncementDTO;
import kdec.apple.cloud.app.common.dto.message.AnnouncementVO;
import kdec.apple.cloud.app.common.dto.utils.PageResult;

public interface TeacherMessageService {
    AnnouncementVO createAnnouncement(AnnouncementDTO dto);

    void updateAnnouncement(Long announcementId, AnnouncementDTO dto);

    void deleteAnnouncement(Long announcementId);

    PageResult<AnnouncementVO> listAnnouncements(Long classId);
}
