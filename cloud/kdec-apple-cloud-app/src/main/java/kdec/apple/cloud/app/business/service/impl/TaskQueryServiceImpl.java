package kdec.apple.cloud.app.business.service.impl;

import kdec.apple.cloud.app.business.mapper.TaskMapper;
import kdec.apple.cloud.app.business.service.TaskQueryService;
import kdec.apple.cloud.app.common.dto.utils.TaskStatusVO;
import kdec.apple.cloud.app.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskQueryServiceImpl implements TaskQueryService {
    private final TaskMapper taskMapper;

    @Override
    public TaskStatusVO getTaskStatus(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("task not found");
        }
        return TaskStatusVO.builder()
                .taskId(task.getTaskId())
                .bizId(task.getBizId())
                .taskType(task.getTaskType())
                .status(task.getStatus())
                .failReason(task.getFailReason())
                .build();
    }
}
