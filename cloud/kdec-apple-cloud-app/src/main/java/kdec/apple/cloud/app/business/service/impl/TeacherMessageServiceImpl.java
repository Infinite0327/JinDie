package kdec.apple.cloud.app.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import kdec.apple.base.common.exception.BusinessException;
import kdec.apple.base.common.exception.ErrorCode;
import kdec.apple.cloud.app.business.mapper.AnnouncementMapper;
import kdec.apple.cloud.app.business.service.teacher.TeacherMessageService;
import kdec.apple.cloud.app.common.context.UserContext;
import kdec.apple.cloud.app.common.dto.message.AnnouncementDTO;
import kdec.apple.cloud.app.common.dto.message.AnnouncementVO;
import kdec.apple.cloud.app.common.dto.utils.PageResult;
import kdec.apple.cloud.app.entity.Announcement;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherMessageServiceImpl implements TeacherMessageService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public AnnouncementVO createAnnouncement(AnnouncementDTO dto) {
        // 1. 获取当前教师
        Long teacherId = UserContext.getCurrentUserId();

        // 2. 构建公告实体
        Announcement announcement = Announcement.builder()
                .teacherId(teacherId)
                .classId(dto.getClassId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 3. 插入公告
        announcementMapper.insert(announcement);

        // 4. 返回VO
        return toAnnouncementVO(announcement);
    }

    @Override
    public void updateAnnouncement(Long announcementId, AnnouncementDTO dto) {
        // 1. 查询公告
        Announcement announcement = announcementMapper.selectById(announcementId);
        if (announcement == null) {
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }

        // 2. 更新字段
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setUpdateTime(LocalDateTime.now());

        // 3. 更新数据库
        announcementMapper.updateById(announcement);
    }

    @Override
    public void deleteAnnouncement(Long announcementId) {
        // 1. 查询公告
        Announcement announcement = announcementMapper.selectById(announcementId);
        if (announcement == null) {
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }

        // 2. 删除公告
        announcementMapper.deleteById(announcementId);
    }

    @Override
    public PageResult<AnnouncementVO> listAnnouncements(Long classId) {
        // 1. 查询公告
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getClassId, classId)
                .orderByDesc(Announcement::getCreateTime);

        List<Announcement> announcements = announcementMapper.selectList(wrapper);

        // 2. 转VO
        List<AnnouncementVO> list = announcements.stream()
                .map(this::toAnnouncementVO)
                .collect(Collectors.toList());

        // 3. 返回分页结果
        return new PageResult<>(
                (long) list.size(),
                list
        );
    }

    /**
     * 实体转VO
     */
    private AnnouncementVO toAnnouncementVO(Announcement announcement) {
        AnnouncementVO vo = new AnnouncementVO();
        vo.setAnnouncementId(announcement.getId());
        vo.setClassId(announcement.getClassId());
        vo.setTitle(announcement.getTitle());
        vo.setCreatedAt(announcement.getCreateTime());
        return vo;
    }
}