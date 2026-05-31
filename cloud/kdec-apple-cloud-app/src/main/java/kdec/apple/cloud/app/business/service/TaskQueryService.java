package kdec.apple.cloud.app.business.service;

import kdec.apple.cloud.app.common.dto.utils.TaskStatusVO;

public interface TaskQueryService {
    TaskStatusVO getTaskStatus(Long taskId);
}
