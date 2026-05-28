package kdec.apple.cloud.app.webapi.teacher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kdec.apple.base.common.result.Result;
import kdec.apple.cloud.app.business.service.teacher.StudentService;
import kdec.apple.cloud.app.business.service.teacher.TeacherClassService;
import kdec.apple.cloud.app.common.dto.teacherClassManagement.*;
import kdec.apple.cloud.app.common.dto.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 教师端 - 班级与学生管理
 */
@Slf4j
@Api(tags = "教师端-班级管理")
@RestController
@RequestMapping("/teacher/classes")
@RequiredArgsConstructor
public class TeacherClassController {
    private final TeacherClassService classService;
    private final StudentService studentService;

    // ========================================================
    //  一、班级管理
    // ========================================================

    /**
     * 创建班级
     * @param dto
     * @return
     */
    @ApiOperation("创建班级")
    @PostMapping
    public Result<ClassVO> createClass(@RequestBody ClassDTO dto) {
        return Result.ok(classService.createClass(dto));
    }

    /**
     * 修改班级信息
     * @param dto
     * @return
     */
    @ApiOperation("修改班级信息")
    @PutMapping("/{classId}")
    public Result<Void> updateClass(@PathVariable Long classId, @RequestBody ClassDTO dto) {
        dto.setClassId(classId);
        classService.updateClass(dto);
        return Result.ok();
    }

    /**
     * 删除班级
     * @param classId
     * @return
     */
    @ApiOperation("删除班级")
    @DeleteMapping("/{classId}")
    public Result<Void> deleteClass(@PathVariable Long classId) {
        classService.deleteClass(classId);
        return Result.ok();
    }

    /**
     * 获取教师的班级列表
     */
    @ApiOperation("获取班级列表")
    @GetMapping
    public Result<List<ClassVO>> listClasses() {
        log.info("获取班级列表");
        return Result.ok(classService.getClassList());
    }

    /**
     * 获取班级详情
     */
    @ApiOperation("获取班级详情")
    @GetMapping("/{classId}")
    public Result<ClassVO> getClassDetail(@PathVariable Long classId) {
        return Result.ok(classService.getClassDetail(classId));
    }

    // ========================================================
    //  二、学生管理
    // ========================================================

    /**
     * 添加学生到班级
     */
    @ApiOperation("添加学生")
    @PostMapping("/{classId}/students")
    public Result<String> addStudent(@PathVariable Long classId,
                                   @RequestBody StudentDTO dto) {
        classService.addStudent(classId, dto);
        return Result.ok();
    }

    /**
     * 批量添加学生（导入名单）
     */
    @ApiOperation("批量添加学生")
    @PostMapping("/{classId}/students/batch")
    public Result<StudentBatchAddResultVO> batchAddStudents(@PathVariable Long classId,
                                                            @RequestPart("file") MultipartFile file) {
        return Result.ok(classService.batchAddStudents(classId, file));
    }

    /**
     * 移除学生
     */
    @ApiOperation("移除学生")
    @DeleteMapping("/{classId}/students/{studentId}")
    public Result<String> removeStudent(@PathVariable Long classId,
                                      @PathVariable Long studentId) {
        classService.removeStudent(classId, studentId);
        return Result.ok();
    }

    /**
     * 修改学生信息
     */
    @ApiOperation("修改学生信息")
    @PutMapping("/{classId}/students/{studentId}")
    public Result<Void> updateStudent(@PathVariable Long classId,
                                      @PathVariable Long studentId,
                                      @RequestBody StudentDTO dto) {
        studentService.updateStudent(classId, studentId, dto);
        return Result.ok();
    }

    /**
     * 获取学生列表
     * @param classId
     * @return
     */
    @ApiOperation("获取学生列表")
    @GetMapping("/{classId}/students")
    public Result<List<StudentVO>> listStudents(@PathVariable Long classId) {
        return Result.ok(classService.listStudents(classId));
    }

    /**
     * 分页查询学生
     * @param queryDTO
     * @return
     */
    @ApiOperation("分页查询学生")
    @GetMapping("/{classId}/students/page")
    public Result<PageResult> pageQueryStudents(
            @PathVariable Long classId,
            StudentPageQueryDTO queryDTO) {
        queryDTO.setClassId(classId);
        PageResult pageResult = studentService.pageQuery(queryDTO);
        return Result.ok(pageResult);
    }

}