package kdec.apple.cloud.app.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import kdec.apple.base.common.exception.BusinessException;
import kdec.apple.base.common.exception.ErrorCode;
import kdec.apple.cloud.app.business.mapper.ChapterMapper;
import kdec.apple.cloud.app.business.mapper.ExerciseBatchMapper;
import kdec.apple.cloud.app.business.mapper.MaterialMapper;
import kdec.apple.cloud.app.business.service.student.StudentChapterService;
import kdec.apple.cloud.app.common.dto.chapter.ChapterDetailVO;
import kdec.apple.cloud.app.common.dto.chapter.ChapterListVO;
import kdec.apple.cloud.app.entity.Chapter;
import kdec.apple.cloud.app.entity.ChapterMaterial;
import kdec.apple.cloud.app.entity.ExerciseBatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class StudentChapterServiceImpl implements StudentChapterService {
    private final ChapterMapper chapterMapper;
    private final MaterialMapper materialMapper;
    private final ExerciseBatchMapper exerciseMapper;

    @Override
    public List<ChapterListVO> listChapters(Long courseId) {
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chapter::getClassId, courseId)
                .orderByAsc(Chapter::getSortIndex);
        return chapterMapper.selectList(wrapper)
                .stream()
                .map(this::toChapterListVO)
                .collect(Collectors.toList());
    }

    @Override
    public ChapterDetailVO getChapterDetail(Long courseId, Long chapterId) {
        // 1. 查章节是否存在
        Chapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_FOUND);
        }
        // 2. 校验章节是否属于该课程
        if (!chapter.getClassId().equals(courseId)) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_BELONG_TO_CLASS);
        }
        // 3. 直接复用toChapterVO
        return toChapterVO(chapter);
    }

    private ChapterListVO toChapterListVO(Chapter chapter) {
        ChapterListVO vo = new ChapterListVO();
        vo.setChapterId(chapter.getChapterId());
        vo.setTitle(chapter.getTitle());
        vo.setSortIndex(chapter.getSortIndex());
        return vo;
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
