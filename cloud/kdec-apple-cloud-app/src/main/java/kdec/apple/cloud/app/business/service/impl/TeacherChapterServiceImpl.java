package kdec.apple.cloud.app.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import kdec.apple.base.common.exception.BusinessException;
import kdec.apple.base.common.exception.ErrorCode;
import kdec.apple.cloud.app.business.mapper.ChapterMapper;
import kdec.apple.cloud.app.business.mapper.MaterialMapper;
import kdec.apple.cloud.app.business.mapper.ClassMapper;
import kdec.apple.cloud.app.business.mapper.ExerciseMapper;
import kdec.apple.cloud.app.business.service.teacher.TeacherChapterService;
import kdec.apple.cloud.app.common.dto.chapter.ChapterDTO;
import kdec.apple.cloud.app.common.dto.chapter.ChapterDetailVO;
import kdec.apple.cloud.app.common.dto.chapter.ChapterSortDTO;
import kdec.apple.cloud.app.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherChapterServiceImpl implements TeacherChapterService {
    private final ChapterMapper chapterMapper;
    private final ClassMapper classMapper;
    private final MaterialMapper materialMapper;
    private final ExerciseMapper exerciseMapper;

    @Override
    public ChapterDetailVO createChapter(ChapterDTO dto) {
        // 1. 校验班级是否存在
        Course course = classMapper.selectById(dto.getCourseId());
        if (course == null) {
            throw new BusinessException(ErrorCode.CLASS_NOT_FOUND);
        }
        // 2. 查当前最大sortIndex，新章节排在最后
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chapter::getClassId, dto.getCourseId())
                .orderByDesc(Chapter::getSortIndex)
                .last("LIMIT 1");
        Chapter last = chapterMapper.selectOne(wrapper);
        int nextSort = (last == null) ? 1 : last.getSortIndex() + 1;
        // 3. 构建实体
        Chapter chapter = Chapter.builder()
                .classId(dto.getCourseId())
                .title(dto.getTitle())
                .sortIndex(nextSort)
                .createTime(LocalDateTime.now())
                .build();
        chapterMapper.insert(chapter);
        // 4. 组装VO
        return toChapterVO(chapter);
    }

    @Override
    public void updateChapter(Long chapterId, ChapterDTO dto) {
        Chapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_FOUND);
        }
        chapter.setTitle(dto.getTitle());
        chapterMapper.updateById(chapter);
    }

    @Override
    public void deleteChapter(Long chapterId) {
        Chapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_FOUND);
        }
        chapterMapper.deleteById(chapterId);
    }

    @Override
    public List<ChapterDetailVO> listChapters(Long courseId) {
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chapter::getClassId, courseId)
                .orderByAsc(Chapter::getSortIndex);
        return chapterMapper.selectList(wrapper)
                .stream()
                .map(this::toChapterVO)
                .collect(Collectors.toList());
    }

    @Override
    public void sortChapters(Long courseId, ChapterSortDTO dto) {
        // 1. 校验班级是否存在
        Course course = classMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(ErrorCode.CLASS_NOT_FOUND);
        }
        // 2. 按传入的chapterIds顺序更新sortIndex
        List<Long> chapterIds = dto.getChapterIds();
        for (int i = 0; i < chapterIds.size(); i++) {
            Chapter chapter = chapterMapper.selectById(chapterIds.get(i));
            if (chapter == null) {
                throw new BusinessException(ErrorCode.CHAPTER_NOT_FOUND);
            }
            // 校验章节是否属于该课程
            if (!chapter.getClassId().equals(courseId)) {
                throw new BusinessException(ErrorCode.CHAPTER_NOT_BELONG_TO_CLASS);
            }
            chapter.setSortIndex(i + 1);
            chapterMapper.updateById(chapter);
        }
    }


    private ChapterDetailVO toChapterVO(Chapter chapter) {
        ChapterDetailVO vo = new ChapterDetailVO();
        vo.setChapterId(chapter.getChapterId());
        vo.setTitle(chapter.getTitle());
        vo.setSortIndex(chapter.getSortIndex());
        vo.setCreateTime(chapter.getCreateTime());

        // 统计资料数
        LambdaQueryWrapper<ChapterMaterial> materialWrapper = new LambdaQueryWrapper<>();
        materialWrapper.eq(ChapterMaterial::getChapterId, chapter.getChapterId());
        vo.setMaterialCount(materialMapper.selectCount(materialWrapper).intValue());

        // 统计习题数
        LambdaQueryWrapper<ExerciseBatch> exerciseWrapper = new LambdaQueryWrapper<>();
        exerciseWrapper.eq(ExerciseBatch::getChapterId, chapter.getChapterId());
        vo.setExerciseCount(exerciseMapper.selectCount(exerciseWrapper).intValue());

        return vo;
    }
}
