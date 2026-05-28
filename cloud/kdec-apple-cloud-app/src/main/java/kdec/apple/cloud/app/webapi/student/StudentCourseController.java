package kdec.apple.cloud.app.webapi.student;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import kd.bos.context.RequestContext;
import kdec.apple.base.common.result.Result;
import kdec.apple.cloud.app.business.service.student.StudentCourseService;
import kdec.apple.cloud.app.business.service.StudyRecordService;
import kdec.apple.cloud.app.common.dto.studentCourse.CourseDetailVO;
import kdec.apple.cloud.app.common.dto.studentCourse.CourseVO;
import kdec.apple.cloud.app.common.dto.studentCourse.StudyRecordDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生端 - 课程
 */
@Api(tags = "学生端-课程")
@RestController
@RequestMapping("/student/courses")
@RequiredArgsConstructor
public class StudentCourseController {

    private final StudentCourseService courseService;
    private final StudyRecordService studyRecordService;
    private final RequestContext requestContext;

    /**
     * 获取我的课程列表
     * @return
     */
    @ApiOperation("获取我的课程列表")
    @GetMapping
    public Result<List<CourseVO>> listMyCourses() {

        Long studentId = requestContext.getCurrUserId();
        return Result.ok(courseService.listMyCourses(studentId));
    }

    /**
     * 获取课程详情
     * @param courseId
     * @return
     */
    @ApiOperation("获取课程详情")
    @GetMapping("/{courseId}")
    public Result<CourseDetailVO> getCourseDetail(@PathVariable Long courseId) {
        return Result.ok(courseService.getCourseDetail(courseId));
    }

    /**
     * 上报课程学习时长
     */
    @PostMapping("/{courseId}/study-record")
    public Result<Void> recordStudy(
            @PathVariable Long courseId,
            @RequestBody StudyRecordDTO dto) {
        studyRecordService.record(courseId, dto);
        return Result.ok();
    }


}