package kdec.apple.cloud.app.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import kdec.apple.base.common.exception.BusinessException;
import kdec.apple.base.common.exception.ErrorCode;
import kdec.apple.cloud.app.business.mapper.ClassStudentMapper;
import kdec.apple.cloud.app.business.mapper.StudentMapper;
import kdec.apple.cloud.app.business.mapper.UserMapper;
import kdec.apple.cloud.app.business.service.teacher.StudentService;
import kdec.apple.cloud.app.common.dto.teacherClassManagement.StudentDTO;
import kdec.apple.cloud.app.common.dto.teacherClassManagement.StudentPageQueryDTO;
import kdec.apple.cloud.app.common.dto.teacherClassManagement.StudentVO;
import kdec.apple.cloud.app.common.dto.utils.PageResult;
import kdec.apple.cloud.app.entity.ClassStudent;
import kdec.apple.cloud.app.entity.Student;
import kdec.apple.cloud.app.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentMapper studentMapper;
    private final ClassStudentMapper classStudentMapper;
    private final UserMapper userMapper;

    @Override
    public void updateStudent(Long classId, Long studentId, StudentDTO dto) {
        // 1. 查user是否存在
        User user = userMapper.selectById(studentId);
        if (user == null) {
            throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND);
        }
        // 2. 更新user表里的name和email
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public PageResult pageQuery(StudentPageQueryDTO queryDTO) {
        // 1. 分页查询该班级下符合条件的学生
        Page<Student> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());

        LambdaQueryWrapper<ClassStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(ClassStudent::getClassId, queryDTO.getClassId());
        List<Long> studentIds = classStudentMapper.selectList(csWrapper)
                .stream()
                .map(ClassStudent::getStudentId)
                .collect(Collectors.toList());

        if (studentIds.isEmpty()) {
            return new PageResult<>(0L, Collections.emptyList());
        }

        // 2. 根据keyword模糊查询user表（name或email）
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getId, studentIds);
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            userWrapper.and(w -> w
                    .like(User::getName, queryDTO.getKeyword())
                    .or()
                    .like(User::getEmail, queryDTO.getKeyword()));
        }
        Page<User> userPage = userMapper.selectPage(page, userWrapper);

        // 3. 查student表拿studentNo
        List<Long> pagedUserIds = userPage.getRecords()
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());
        Map<Long, Student> studentMap = studentMapper.selectBatchIds(pagedUserIds)
                .stream()
                .collect(Collectors.toMap(Student::getUserId,
                        student -> student));

        // 4. 组装VO
        List<StudentVO> records = userPage.getRecords().stream().map(user -> {
            StudentVO vo = new StudentVO();
            vo.setStudentId(user.getId());
            vo.setName(user.getName());
            vo.setEmail(user.getEmail());
            Student student = studentMap.get(user.getId());
            if (student != null) {
                vo.setStudentNo(student.getStudentNo());
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(userPage.getTotal(), records);
    }
}
