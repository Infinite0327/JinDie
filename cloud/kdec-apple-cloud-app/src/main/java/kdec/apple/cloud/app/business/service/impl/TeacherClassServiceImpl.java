package kdec.apple.cloud.app.business.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import kdec.apple.base.common.exception.BusinessException;
import kdec.apple.base.common.exception.ErrorCode;
import kdec.apple.cloud.app.business.mapper.ClassMapper;
import kdec.apple.cloud.app.business.mapper.ClassStudentMapper;
import kdec.apple.cloud.app.business.mapper.StudentMapper;
import kdec.apple.cloud.app.business.mapper.UserMapper;
import kdec.apple.cloud.app.business.service.teacher.TeacherClassService;
import kdec.apple.cloud.app.common.context.UserContext;
import kdec.apple.cloud.app.common.dto.teacherClassManagement.*;
import kdec.apple.cloud.app.entity.ClassStudent;
import kdec.apple.cloud.app.entity.Course;
import kdec.apple.cloud.app.entity.Student;
import kdec.apple.cloud.app.entity.User;
import kdec.apple.cloud.app.entity.enums.CourseStatus;
import kdec.apple.cloud.app.entity.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherClassServiceImpl implements TeacherClassService {


    private final ClassMapper classMapper;
    private final ClassStudentMapper classStudentMapper;
    private final StudentMapper studentMapper;
    private final UserMapper userMapper;

    @Override
    public ClassVO createClass(ClassDTO dto) {
        // 1. 从登录态取teacherId
        Long teacherId = UserContext.getCurrentUserId();
        // 2. 构建实体
        Course course = Course.builder()
                .teacherId(teacherId)
                .name(dto.getName())
                .description(dto.getDescription())
                .status(CourseStatus.DRAFT)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        // 3. 插入
        classMapper.insert(course);
        // 4. 组装VO
        return toClassVO(course);
    }

    @Override
    public void updateClass(ClassDTO dto) {
        // 1. 查是否存在
        Course course = classMapper.selectById(dto.getClassId());
        if (course == null) {
            throw new BusinessException(ErrorCode.CLASS_NOT_FOUND);
        }
        // 2. 更新字段
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setUpdateTime(LocalDateTime.now());
        classMapper.updateById(course);
    }

    @Override
    public void deleteClass(Long classId) {
        // 1. 查是否存在
        Course course = classMapper.selectById(classId);
        if (course == null) {
            throw new BusinessException(ErrorCode.CLASS_NOT_FOUND);
        }
        // 2. 删除班级
        classMapper.deleteById(classId);
        // 3. 删除班级下所有学生关联关系
        LambdaQueryWrapper<ClassStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassStudent::getClassId, classId);
        classStudentMapper.delete(wrapper);
    }

    @Override
    public List<ClassVO> getClassList() {
        // 1. 从登录态取teacherId
        Long teacherId = UserContext.getCurrentUserId();
        // 2. 查该教师的所有班级
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getTeacherId, teacherId);
        List<Course> courses = classMapper.selectList(wrapper);
        // 3. 组装VO
        return courses.stream().map(this::toClassVO).collect(Collectors.toList());
    }

    @Override
    public ClassVO getClassDetail(Long classId) {
        // 1. 查班级
        Course course = classMapper.selectById(classId);
        if (course == null) {
            throw new BusinessException(ErrorCode.CLASS_NOT_FOUND);
        }
        // 2. 查学生数
        LambdaQueryWrapper<ClassStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassStudent::getClassId, classId);
        Long studentCount = classStudentMapper.selectCount(wrapper);
        // 3. 组装VO
        ClassVO vo = new ClassVO();
        vo.setClassId(course.getId());
        vo.setName(course.getName());
        vo.setDescription(course.getDescription());
        vo.setStudentCount(studentCount.intValue());
        vo.setCreateTime(course.getCreateTime());
        return vo;
    }

    @Override
    public void addStudent(Long classId, StudentDTO dto) {
        // 1. 查班级是否存在
        Course course = classMapper.selectById(classId);
        if (course == null) {
            throw new BusinessException(ErrorCode.CLASS_NOT_FOUND);
        }
        // 2. 查user是否已存在（按email
        //TODO:拿哪个字段作为唯一性数据，如何标记？？
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getEmail, dto.getEmail());
        User user = userMapper.selectOne(userWrapper);

        if (user == null) {
            // 3a. 不存在则创建user
            user = User.builder()
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .password(encryptPassword(dto.getStudentNo())) // 默认密码为学号
                    .role(UserRole.STUDENT)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
            // 3b. 创建student
            Student student = Student.builder()
                    .userId(user.getId())
                    .studentNo(dto.getStudentNo())
                    .createTime(LocalDateTime.now())
                    .build();
            studentMapper.insert(student);
        }

        // 4. 查是否已在班级里
        LambdaQueryWrapper<ClassStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(ClassStudent::getClassId, classId)
                .eq(ClassStudent::getStudentId, user.getId());
        if (classStudentMapper.selectCount(csWrapper) > 0) {
            throw new BusinessException( ErrorCode.STUDENT_ALREADY_IN_CLASS);
        }

        // 5. 创建关联关系
        ClassStudent classStudent = ClassStudent.builder()
                .classId(classId)
                .studentId(user.getId())
                .joinedAt(LocalDateTime.now())
                .build();
        classStudentMapper.insert(classStudent);
    }

    @Override
    public StudentBatchAddResultVO batchAddStudents(Long classId, MultipartFile file) {
        List<StudentDTO> list;
        try {
            list = EasyExcel.read(file.getInputStream())
                    .head(StudentDTO.class)
                    .sheet()
                    .doReadSync();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_PARSE_ERROR);
        }
        if (list.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }
        // 逐个添加学生
        int successCount = 0;
        int failCount = 0;
        List<String> failReasons = new ArrayList<>();
        for (StudentDTO dto : list) {
            try {
                addStudent(classId, dto);
                successCount++;
            } catch (BusinessException e) {
                failCount++;
                failReasons.add(dto.getName() + "：" + e.getMessage());
            }
        }
        StudentBatchAddResultVO vo = new StudentBatchAddResultVO();
        vo.setSuccessCount(successCount);
        vo.setFailCount(failCount);
        vo.setFailReasons(failReasons);
        return vo;
    }

    @Override
    public void removeStudent(Long classId, Long studentId) {
        // 只删关联关系，不删student账号
        LambdaQueryWrapper<ClassStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassStudent::getClassId, classId)
                .eq(ClassStudent::getStudentId, studentId);
        int deleted = classStudentMapper.delete(wrapper);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.STUDENT_NOT_IN_CLASS);
        }
    }

    @Override
    public List<StudentVO> listStudents(Long classId) {
        // 1. 查班级下所有关联的studentId
        LambdaQueryWrapper<ClassStudent> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(ClassStudent::getClassId, classId);

        List<ClassStudent> classStudents =
                classStudentMapper.selectList(wrapper);
        if (classStudents.isEmpty()) {
            return Collections.emptyList();
        }
        // 2. 取出studentId列表
        List<Long> studentIds = classStudents.stream()
                .map(ClassStudent::getStudentId)
                .collect(Collectors.toList());
        // 3. 查student表
        List<Student> students = studentMapper.selectBatchIds(studentIds);
        // 4. 查user表拿name
        List<Long> userIds = students.stream()
                .map(Student::getUserId)
                .collect(Collectors.toList());
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        // 5. 组装VO
        return students.stream().map(student -> {
            StudentVO vo = new StudentVO();
            vo.setStudentId(student.getUserId());
            vo.setStudentNo(student.getStudentNo());
            User user = userMap.get(student.getUserId());
            if (user != null) {
                vo.setName(user.getName());
                vo.setEmail(user.getEmail());
            }
            // 6. 取joinedAt从classStudent里拿
            classStudents.stream()
                    .filter(cs -> cs.getStudentId().equals(student.getUserId()))
                    .findFirst()
                    .ifPresent(cs -> vo.setJoinedAt(cs.getJoinedAt()));
            return vo;
        }).collect(Collectors.toList());
    }


    private ClassVO toClassVO(Course course) {
        ClassVO vo = new ClassVO();
        vo.setClassId(course.getId());
        vo.setName(course.getName());
        vo.setDescription(course.getDescription());
        vo.setCreateTime(course.getCreateTime());
        return vo;
    }

    private String encryptPassword(String raw) {
        // 实际项目用BCrypt
        return BCrypt.hashpw(raw, BCrypt.gensalt());
    }
}
