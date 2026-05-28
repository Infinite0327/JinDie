package kdec.apple.cloud.app.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import kdec.apple.base.common.exception.BusinessException;
import kdec.apple.base.common.exception.ErrorCode;
import kdec.apple.cloud.app.business.mapper.ChapterMapper;
import kdec.apple.cloud.app.business.mapper.ClassMapper;
import kdec.apple.cloud.app.business.mapper.ClassStudentMapper;
import kdec.apple.cloud.app.business.mapper.UserMapper;
import kdec.apple.cloud.app.business.service.student.StudentCourseService;
import kdec.apple.cloud.app.common.dto.chapter.ChapterListVO;
import kdec.apple.cloud.app.common.dto.studentCourse.CourseDetailVO;
import kdec.apple.cloud.app.common.dto.studentCourse.CourseVO;
import kdec.apple.cloud.app.entity.Chapter;
import kdec.apple.cloud.app.entity.ClassStudent;
import kdec.apple.cloud.app.entity.Course;
import kdec.apple.cloud.app.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentCourseServiceImpl implements StudentCourseService {
    private final ClassStudentMapper classStudentMapper;
    private final ChapterMapper chapterMapper;
    private final UserMapper userMapper;
    private final ClassMapper classMapper;

    @Override
    public List<CourseVO> listMyCourses(Long studentId) {
        // 1. 查学生加入的所有班级
        LambdaQueryWrapper<ClassStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(ClassStudent::getStudentId, studentId);
        List<Long> classIds = classStudentMapper.selectList(csWrapper)
                .stream()
                .map(ClassStudent::getClassId)
                .collect(Collectors.toList());

        if (classIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 查班级信息
        List<Course> courses = classMapper.selectBatchIds(classIds);

        // 3. 组装VO
        return courses.stream().map(course -> {
            CourseVO vo = new CourseVO();
            vo.setCourseId(course.getId());
            vo.setName(course.getName());
            vo.setDescription(course.getDescription());

            // 4. 查章节数
            LambdaQueryWrapper<Chapter> chapterWrapper = new LambdaQueryWrapper<>();
            chapterWrapper.eq(Chapter::getClassId, course.getId());
            vo.setChapterCount(chapterMapper.selectCount(chapterWrapper).intValue());

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public CourseDetailVO getCourseDetail(Long courseId) {
        // 1. 查班级是否存在
        Course course = classMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(ErrorCode.CLASS_NOT_FOUND);
        }

        // 2. 查章节列表
        LambdaQueryWrapper<Chapter> chapterWrapper = new LambdaQueryWrapper<>();
        chapterWrapper.eq(Chapter::getClassId, courseId)
                .orderByAsc(Chapter::getSortIndex);
        List<Chapter> chapters = chapterMapper.selectList(chapterWrapper);

        // 3. 组装章节简要列表
        List<ChapterListVO> chapterListVOs = chapters.stream().map(chapter -> {
            ChapterListVO chapterVO = new ChapterListVO();
            chapterVO.setChapterId(chapter.getId());
            chapterVO.setTitle(chapter.getTitle());
            chapterVO.setSortIndex(chapter.getSortIndex());
            return chapterVO;
        }).collect(Collectors.toList());

        // 4. 查教师名
        // teacher信息通过course.teacherId → user表取name
        User teacher = userMapper.selectById(course.getTeacherId());


        // 5. 组装VO
        CourseDetailVO vo = new CourseDetailVO();
        vo.setCourseId(course.getId());
        vo.setName(course.getName());
        vo.setDescription(course.getDescription());
        vo.setTeacherName(teacher != null ? teacher.getName() : "");
        vo.setChapters(chapterListVOs);
        return vo;

    }
}
