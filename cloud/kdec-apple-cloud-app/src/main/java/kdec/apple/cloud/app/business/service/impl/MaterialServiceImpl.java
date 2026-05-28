package kdec.apple.cloud.app.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import kdec.apple.base.common.exception.BusinessException;
import kdec.apple.base.common.exception.ErrorCode;
import kdec.apple.base.common.utils.OssUtil;
import kdec.apple.cloud.app.business.mapper.*;
import kdec.apple.cloud.app.business.service.AiService;
import kdec.apple.cloud.app.business.service.MaterialService;
import kdec.apple.cloud.app.common.context.UserContext;
import kdec.apple.cloud.app.common.dto.enums.TaskType;
import kdec.apple.cloud.app.common.dto.graph.GraphVO;
import kdec.apple.cloud.app.common.dto.materials.*;
import kdec.apple.cloud.app.common.dto.utils.TaskCreatedVO;
import kdec.apple.cloud.app.entity.*;
import kdec.apple.cloud.app.entity.enums.FileType;
import kdec.apple.cloud.app.entity.enums.ParseStatus;
import kdec.apple.cloud.app.entity.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialMapper materialMapper;
    private final OssUtil ossUtil;
    private final ChapterMapper  chapterMapper;
    private final TaskMapper taskMapper;
    private final AiService aiService;
    private final NoteMapper noteMapper;
    private final MaterialParseResultMapper parseResultMapper;


    @Override
    public MaterialUploadVO upload(MultipartFile file, MaterialUploadDTO dto) {
        // 1. 校验章节是否存在
        Chapter chapter = chapterMapper.selectById(dto.getChapterId());
        if (chapter == null) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_FOUND);
        }
        // 2. 识别文件类型
        FileType fileType = detectFileType(file.getOriginalFilename());
        // 3. 上传到OSS
        String ossUrl = ossUtil.upload(file);
        // 4. 从登录态取uploaderId
        Long uploaderId = UserContext.getCurrentUserId();
        // 5. 构建实体
        ChapterMaterial material = ChapterMaterial.builder()
                .chapterId(dto.getChapterId())
                .uploaderId(uploaderId)
                .uploaderRole(UserRole.TEACHER)
                .title(dto.getTitle())
                .fileName(file.getOriginalFilename())
                .fileType(fileType)
                .fileUrl(ossUrl)
                .fileSize(file.getSize())
                .parseStatus(ParseStatus.PENDING)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        materialMapper.insert(material);
        // 6. 组装VO
        MaterialUploadVO vo = new MaterialUploadVO();
        vo.setMaterialId(material.getId());
        vo.setOssUrl(ossUrl);
        return vo;
    }

    @Override
    public void delete(Long materialId) {
        ChapterMaterial material = materialMapper.selectById(materialId);
        if (material == null) {
            throw new BusinessException(ErrorCode.MATERIAL_NOT_FOUND);
        }
        // 校验是否是自己上传的
        Long currentUserId = UserContext.getCurrentUserId();
        if (!material.getUploaderId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        ossUtil.delete(material.getFileUrl());
        materialMapper.deleteById(materialId);
    }

    @Override
    public void update(Long materialId, MaterialUpdateDTO dto) {
        ChapterMaterial material = materialMapper.selectById(materialId);
        if (material == null) {
            throw new BusinessException(ErrorCode.MATERIAL_NOT_FOUND);
        }
        material.setTitle(dto.getTitle());
        material.setDescription(dto.getDescription());
        material.setUpdateTime(LocalDateTime.now());
        materialMapper.updateById(material);
    }


    @Override
    public MaterialDetailVO getById(Long materialId) {
        ChapterMaterial material = materialMapper.selectById(materialId);
        if (material == null) {
            throw new BusinessException(ErrorCode.MATERIAL_NOT_FOUND);
        }
        MaterialDetailVO vo = new MaterialDetailVO();
        vo.setMaterialId(material.getId());
        vo.setTitle(material.getTitle());
        vo.setFileName(material.getFileName());
        vo.setFileType(material.getFileType());
        vo.setOssUrl(material.getFileUrl());
        vo.setChapterId(material.getChapterId());
        vo.setUploaderRole(material.getUploaderRole());
        vo.setParseStatus(material.getParseStatus());
        vo.setCreatedAt(material.getCreateTime());
        return vo;
    }

    @Override
    public List<MaterialVO> listByChapter(Long courseId, Long chapterId){
        LambdaQueryWrapper<ChapterMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChapterMaterial::getChapterId, chapterId)
            .orderByDesc(ChapterMaterial::getCreateTime);
        return materialMapper.selectList(wrapper)
            .stream()
                .map(this::toMaterialVO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long materialId) {
        ChapterMaterial material = materialMapper.selectById(materialId);
        if (material == null) {
            throw new BusinessException(ErrorCode.MATERIAL_NOT_FOUND);
        }
        // 校验是否是自己上传的
        Long currentUserId = UserContext.getCurrentUserId();
        if (!material.getUploaderId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        ossUtil.delete(material.getFileUrl());
        materialMapper.deleteById(materialId);
    }


    @Override
    public TaskCreatedVO generateGraph(Long materialId) {
        ChapterMaterial material = materialMapper.selectById(materialId);
        if (material == null) {
            throw new BusinessException(ErrorCode.MATERIAL_NOT_FOUND);
        }
        Task task = Task.builder()
                .taskType(TaskType.GRAPH)
                .status(ParseStatus.PENDING)
                .bizId(materialId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        taskMapper.insert(task);
        aiService.generateGraphAsync(task.getTaskId(), material.getFileUrl(), materialId);

        TaskCreatedVO vo = new TaskCreatedVO();
        vo.setTaskId(task.getTaskId());
        vo.setTaskType(TaskType.GRAPH);
        vo.setBizId(materialId);
        return vo;
    }

    @Override
    public GraphVO getGraph(Long materialId) {
        LambdaQueryWrapper<MaterialParseResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialParseResult::getMaterialId, materialId)
                .eq(MaterialParseResult::getResultType, TaskType.GRAPH.name());
        MaterialParseResult result = parseResultMapper.selectOne(wrapper);
        if (result == null) {
            throw new BusinessException(ErrorCode.PARSE_RESULT_NOT_FOUND);
        }
        return JSON.parseObject(result.getResultData(), GraphVO.class);
    }

    @Override
    public TaskCreatedVO generateNote(Long materialId) {
        ChapterMaterial material = materialMapper.selectById(materialId);
        if (material == null) {
            throw new BusinessException(ErrorCode.MATERIAL_NOT_FOUND);
        }
        Task task = Task.builder()
                .taskType(TaskType.NOTE)
                .status(ParseStatus.PENDING)
                .bizId(materialId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        taskMapper.insert(task);
        aiService.generateNoteAsync(task.getTaskId(), material.getFileUrl(), materialId);

        TaskCreatedVO vo = new TaskCreatedVO();
        vo.setTaskId(task.getTaskId());
        vo.setTaskType(TaskType.NOTE);
        vo.setBizId(materialId);
        return vo;
    }

    @Override
    public NoteVO saveNote(NoteDTO dto) {
        Long studentId = UserContext.getCurrentUserId();
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getMaterialId, dto.getMaterialId())
                .eq(Note::getStudentId, studentId);
        Note note = noteMapper.selectOne(wrapper);

        if (note == null) {
            // 新建笔记
            note = Note.builder()
                    .materialId(dto.getMaterialId())
                    .studentId(studentId)
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            noteMapper.insert(note);
        } else {
            // 更新笔记
            note.setTitle(dto.getTitle());
            note.setContent(dto.getContent());
            note.setUpdateTime(LocalDateTime.now());
            noteMapper.updateById(note);
        }

        return toNoteVO(note);
    }

    @Override
    public NoteVO getNote(Long materialId) {
        Long studentId = UserContext.getCurrentUserId();
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getMaterialId, materialId)
                .eq(Note::getStudentId, studentId);
        Note note = noteMapper.selectOne(wrapper);
        if (note == null) {
            throw new BusinessException(ErrorCode.NOTE_NOT_FOUND);
        }
        return toNoteVO(note);
    }

    @Override
    public TaskCreatedVO parseAudio(Long materialId) {
        ChapterMaterial material = materialMapper.selectById(materialId);
        if (material == null) {
            throw new BusinessException(ErrorCode.MATERIAL_NOT_FOUND);
        }
        if (material.getFileType() != FileType.AUDIO) {
            throw new BusinessException(ErrorCode.FILE_TYPE_NOT_SUPPORT);
        }
        Task task = Task.builder()
                .taskType(TaskType.AUDIO)
                .status(ParseStatus.PENDING)
                .bizId(materialId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        taskMapper.insert(task);
        aiService.parseAudioAsync(task.getTaskId(), material.getFileUrl(), materialId);

        TaskCreatedVO vo = new TaskCreatedVO();
        vo.setTaskId(task.getTaskId());
        vo.setTaskType(TaskType.AUDIO);
        vo.setBizId(materialId);
        return vo;
    }

    @Override
    public AudioParseResultVO getAudioResult(Long materialId) {
        LambdaQueryWrapper<MaterialParseResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialParseResult::getMaterialId, materialId)
                .eq(MaterialParseResult::getResultType, TaskType.AUDIO.name());
        MaterialParseResult result = parseResultMapper.selectOne(wrapper);
        if (result == null) {
            throw new BusinessException(ErrorCode.PARSE_RESULT_NOT_FOUND);
        }
        return JSON.parseObject(result.getResultData(), AudioParseResultVO.class);

    }

    @Override
    public TaskCreatedVO parseVideo(Long materialId) {
        ChapterMaterial material = materialMapper.selectById(materialId);
        if (material == null) {
            throw new BusinessException(ErrorCode.MATERIAL_NOT_FOUND);
        }
        if (material.getFileType() != FileType.VIDEO) {
            throw new BusinessException(ErrorCode.FILE_TYPE_NOT_SUPPORT);
        }
        Task task = Task.builder()
                .taskType(TaskType.VIDEO)
                .status(ParseStatus.PENDING)
                .bizId(materialId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        taskMapper.insert(task);
        aiService.parseVideoAsync(task.getTaskId(), material.getFileUrl(), materialId);

        TaskCreatedVO vo = new TaskCreatedVO();
        vo.setTaskId(task.getTaskId());
        vo.setTaskType(TaskType.VIDEO);
        vo.setBizId(materialId);
        return vo;
    }

    @Override
    public VideoParseResultVO getVideoResult(Long materialId) {
        LambdaQueryWrapper<MaterialParseResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialParseResult::getMaterialId, materialId)
                .eq(MaterialParseResult::getResultType, TaskType.VIDEO.name());
        MaterialParseResult result = parseResultMapper.selectOne(wrapper);
        if (result == null) {
            throw new BusinessException(ErrorCode.PARSE_RESULT_NOT_FOUND);
        }
        return JSON.parseObject(result.getResultData(), VideoParseResultVO.class);

    }



    private MaterialVO toMaterialVO(ChapterMaterial material) {
        MaterialVO vo = new MaterialVO();
        vo.setMaterialId(material.getId());
        vo.setTitle(material.getTitle());
        vo.setFileType(material.getFileType());
        vo.setFileUrl(material.getFileUrl());
        vo.setParseStatus(material.getParseStatus());
        vo.setCreatedAt(material.getCreateTime());
        return vo;
    }

    private NoteVO toNoteVO(Note note) {
        NoteVO vo = new NoteVO();
        vo.setNoteId(note.getId());
        vo.setMaterialId(note.getMaterialId());
        vo.setTitle(note.getTitle());
        vo.setContent(note.getContent());
        vo.setUpdatedAt(note.getUpdateTime());
        return vo;
    }


    //TODO:FileType确定
    private FileType detectFileType(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        switch (ext) {
            case ".pdf":
            case ".ppt":
            case ".pptx":
            case ".doc":
            case ".docx": return FileType.DOCUMENT;
            case ".mp3":
            case ".wav":
            case ".m4a":  return FileType.AUDIO;
            case ".mp4":
            case ".mov":  return FileType.VIDEO;
            default: throw new BusinessException(ErrorCode.FILE_TYPE_NOT_SUPPORT);
        }
    }
}
