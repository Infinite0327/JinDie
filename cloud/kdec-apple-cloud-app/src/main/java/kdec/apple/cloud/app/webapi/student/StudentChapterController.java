package kdec.apple.cloud.app.webapi.student;

import io.swagger.annotations.ApiOperation;
import kd.bos.kwc.v1.annotation.*;
import kd.bos.workflow.engine.TaskService;
import kdec.apple.base.common.result.Result;
import kdec.apple.cloud.app.business.service.AiService;
import kdec.apple.cloud.app.business.service.MaterialService;
import kdec.apple.cloud.app.business.service.student.StudentChapterService;
import kdec.apple.cloud.app.common.dto.chapter.ChapterDetailVO;
import kdec.apple.cloud.app.common.dto.graph.GraphVO;
import kdec.apple.cloud.app.common.dto.materials.*;
import kdec.apple.cloud.app.common.dto.utils.TaskCreatedVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
//TODO：
import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("student/courses/{courseId}/{chapterId}")
public class StudentChapterController {

    private final MaterialService  materialService;
    private final StudentChapterService chapterService;
    private final TaskService taskService;


    /**
     * 获取章节基本信息（资料库 + 习题库概览）
     *
     * @param courseId  课程ID
     * @param chapterId 章节ID
     * @return 章节详情VO
     */
    @ApiOperation("获取章节基本信息")
    @GetMapping()
    public Result<ChapterDetailVO> getChapterDetail(
            @PathVariable Long courseId,
            @PathVariable Long chapterId) {
        ChapterDetailVO vo = chapterService.getChapterDetail(courseId, chapterId);
        return Result.ok(vo);
    }


    // ========================================================
    //  二、资料库模块
    // ========================================================

    /**
     * 获取章节资料库列表
     *
     * @param courseId  课程ID
     * @param chapterId 章节ID
     * @return 资料列表
     */
    @ApiOperation("获取章节资料库列表")
    @GetMapping("/materials")
    public Result<List<MaterialVO>> getMaterialList(
            @PathVariable Long courseId,
            @PathVariable Long chapterId) {
        List<MaterialVO> list = materialService.listByChapter(courseId, chapterId);
        return Result.ok(list);
    }

    /**
     * 获取资料详情
     *
     * @param courseId   课程ID
     * @param materialId 资料ID
     * @return 资料详情VO
     */
    @ApiOperation("获取资料详情")
    @GetMapping("/materials/{materialId}")
    public Result<MaterialDetailVO> getMaterialDetailById(
            @PathVariable Long courseId,
            @PathVariable Long materialId) {
        MaterialDetailVO materialDetailVO = materialService.getById(materialId);
        return Result.ok(materialDetailVO);
    }

    /**
     * 上传资料（支持音频、视频、文档）
     *
     * @param courseId   课程ID
     * @param chapterId  章节ID
     * @param file       上传文件
     * @param uploadDTO  上传附加信息
     * @return 上传结果（含资料ID及解析状态）
     */
    @ApiOperation("上传资料")
    @PostMapping("/materials/upload")
    public Result<MaterialUploadVO> uploadMaterial(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @RequestPart("file") MultipartFile file,
            @ModelAttribute MaterialUploadDTO uploadDTO) {
        uploadDTO.setCourseId(courseId);
        uploadDTO.setChapterId(chapterId);
        MaterialUploadVO vo = materialService.upload(file, uploadDTO);
        return Result.ok(vo);
    }

    /**
     * 删除资料
     *
     * @param courseId   课程ID
     * @param materialId 资料ID
     * @return 操作结果
     */
    @ApiOperation("删除资料")
    @DeleteMapping("/{courseId}/material/{materialId}")
    public Result<Void> deleteMaterial(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @PathVariable Long materialId) {
        materialService.deleteById(materialId);
        return Result.ok();
    }

    // ---------- AI 解析 ----------

    /**
     * AI 状态轮询
     *
     * @param taskId 资料ID
     * @return 知识图谱VO（节点 + 边）
     */
    @ApiOperation("AI生成知识图谱")
    @PostMapping("/{courseId}/material/{materialId}")
    public Result<MaterialAnalysisStatusVO> getTaskStatus(@PathVariable Long taskId) {
        MaterialAnalysisStatusVO vo = taskService.getTaskStatus(taskId);
        return Result.ok(vo);
    }

    // ---------- AI 图谱生成 ----------

    /**
     *
     * @param materialId
     * @return
     */
    @PostMapping("/materials/{materialId}/graph/generate")
    public Result<TaskCreatedVO> generateKnowledgeGraph(@PathVariable Long materialId) {
        TaskCreatedVO vo = materialService.generateGraph(materialId);
        return Result.ok(vo);
    }



    /**
     * 获取已生成的知识图谱
     *
     * @param courseId   课程ID
     * @param materialId 资料ID
     * @return 知识图谱VO
     */
    @ApiOperation("获取已生成的知识图谱")
    @GetMapping("/materials/{materialId}/graph")
    public Result<GraphVO> getKnowledgeGraph(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @PathVariable Long materialId) {
        GraphVO vo = materialService.getGraph(materialId);
        return Result.ok(vo);
    }

    // ---------- 笔记生成 ----------

    /**
     * AI 生成笔记
     *
     * @param courseId   课程ID
     * @param materialId 资料ID
     * @return 笔记内容VO
     */
    @ApiOperation("AI生成笔记")
    @PostMapping("/materials/{materialId}/note/generate")
    public Result<TaskCreatedVO> generateNote(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @PathVariable Long materialId) {
        TaskCreatedVO vo = materialService.generateNote(materialId);
        return Result.ok(vo);
    }

    /**
     * 保存/更新笔记
     *
     * @param courseId   课程ID
     * @param materialId 资料ID
     * @param noteDTO    笔记内容DTO
     * @return 保存后的笔记VO
     */
    @ApiOperation("保存/更新笔记")
    @PutMapping("/materials/{materialId}/note")
    public Result<NoteVO> saveNote(
            @PathVariable Long courseId,
            @PathVariable Long materialId,
            @RequestBody NoteDTO noteDTO) {
        noteDTO.setMaterialId(materialId);
        NoteVO vo = materialService.saveNote(noteDTO);
        return Result.ok(vo);
    }

    /**
     * 获取笔记详情
     *
     * @param courseId   课程ID
     * @param materialId 资料ID
     * @return 笔记内容VO
     */
    @ApiOperation("获取笔记详情")
    @GetMapping("/materials/{materialId}/note")
    public Result<NoteVO> getNote(
            @PathVariable Long courseId,
            @PathVariable Long materialId) {
        NoteVO vo = materialService.getNote(materialId);
        return Result.ok(vo);
    }

    // ---------- 语音解析 ----------

    /**
     * 触发语音资料解析（异步）
     *
     * @param courseId   课程ID
     * @param materialId 资料ID（需为音频类型）
     * @return 解析任务ID
     */
    @ApiOperation("触发语音解析")
    @PostMapping("/materials/{materialId}/audio/generate")
    public Result<TaskCreatedVO> parseAudio(
            @PathVariable Long courseId,
            @PathVariable Long materialId) {
        TaskCreatedVO vo = materialService.parseAudio(materialId);
        return Result.ok(vo);
    }

    /**
     * 获取语音解析结果
     *
     * @param courseId   课程ID
     * @param materialId 资料ID
     * @return 语音解析结果VO（含文字转录、时间轴）
     */
    @ApiOperation("获取语音解析结果")
    @GetMapping("/materials/{materialId}/audio/result")
    public Result<AudioParseResultVO> getAudioParseResult(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @PathVariable Long materialId) {
        AudioParseResultVO vo = materialService.getAudioResult(materialId);
        return Result.ok(vo);
    }

    // ---------- 视频解析 ----------

    /**
     * 触发视频资料解析（异步）
     *
     * @param courseId   课程ID
     * @param materialId 资料ID（需为视频类型）
     * @return 解析任务ID
     */
    @ApiOperation("触发视频解析")
    @PostMapping("/{courseId}/material/{materialId}/video/parse")
    public Result<TaskCreatedVO> parseVideo(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @PathVariable Long materialId) {
        TaskCreatedVO vo = materialService.parseVideo(materialId);
        return Result.ok(vo);
    }

    /**
     * 获取视频解析结果
     *
     * @param courseId   课程ID
     * @param materialId 资料ID
     * @return 视频解析结果VO（含帧关键词、字幕、章节摘要）
     */
    @ApiOperation("获取视频解析结果")
    @GetMapping("/{courseId}/material/{materialId}/video/result")
    public Result<VideoParseResultVO> getVideoParseResult(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @PathVariable Long materialId) {
        VideoParseResultVO vo = materialService.getVideoResult(materialId);
        return Result.ok(vo);
    }


}
