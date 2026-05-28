package kdec.apple.cloud.app.webapi.teacher;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kdec.apple.base.common.result.Result;
import kdec.apple.cloud.app.business.service.teacher.TeacherMessageService;
import kdec.apple.cloud.app.common.dto.message.*;
import kdec.apple.cloud.app.common.dto.utils.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 教师端 - 公告消息 & 私信
 */
@Api(tags = "教师端-消息中心")
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherMessageController {

    private final TeacherMessageService messageService;

    /**
     * 发布班级公告
     */
    @ApiOperation("发布班级公告")
    @PostMapping("/classes/{classId}/announcements")
    public Result<AnnouncementVO> createAnnouncement(@PathVariable Long classId,
                                                     @RequestBody AnnouncementDTO dto) {
        dto.setClassId(classId);
        return Result.ok(messageService.createAnnouncement(dto));
    }

    /**
     * 修改公告
     */
    @ApiOperation("修改公告")
    @PutMapping("/classes/{classId}/announcements/{announcementId}")
    public Result<Void> updateAnnouncement(@PathVariable Long classId,
                                           @PathVariable Long announcementId,
                                           @RequestBody AnnouncementDTO dto) {
        messageService.updateAnnouncement(announcementId, dto);
        return Result.ok();
    }

    /**
     * 删除公告
     */
    @ApiOperation("删除公告")
    @DeleteMapping("/classes/{classId}/announcements/{announcementId}")
    public Result<Void> deleteAnnouncement(@PathVariable Long classId,
                                           @PathVariable Long announcementId) {
        messageService.deleteAnnouncement(announcementId);
        return Result.ok();
    }

    /**
     * 获取班级公告列表
     */
    @ApiOperation("获取班级公告列表")
    @GetMapping("/classes/{classId}/announcements")
    public Result<PageResult<AnnouncementVO>> listAnnouncements(@PathVariable Long classId) {
        return Result.ok(messageService.listAnnouncements(classId));
    }

}