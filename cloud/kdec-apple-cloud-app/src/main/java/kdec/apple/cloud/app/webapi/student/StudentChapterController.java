package kdec.apple.cloud.app.webapi.student;

import io.swagger.annotations.ApiOperation;
import kd.bos.kwc.v1.annotation.DeleteMapping;
import kd.bos.kwc.v1.annotation.GetMapping;
import kd.bos.kwc.v1.annotation.PathVariable;
import kd.bos.kwc.v1.annotation.PostMapping;
import kd.bos.kwc.v1.annotation.PutMapping;
import kd.bos.kwc.v1.annotation.RequestBody;
import kd.bos.kwc.v1.annotation.RequestMapping;
import kd.bos.kwc.v1.annotation.RestController;
import kdec.apple.base.common.result.Result;
import kdec.apple.cloud.app.business.service.MaterialService;
import kdec.apple.cloud.app.business.service.TaskQueryService;
import kdec.apple.cloud.app.business.service.student.StudentChapterService;
import kdec.apple.cloud.app.common.dto.chapter.ChapterDetailVO;
import kdec.apple.cloud.app.common.dto.graph.GraphVO;
import kdec.apple.cloud.app.common.dto.materials.AudioParseResultVO;
import kdec.apple.cloud.app.common.dto.materials.MaterialDetailVO;
import kdec.apple.cloud.app.common.dto.materials.MaterialUploadDTO;
import kdec.apple.cloud.app.common.dto.materials.MaterialUploadVO;
import kdec.apple.cloud.app.common.dto.materials.MaterialVO;
import kdec.apple.cloud.app.common.dto.materials.NoteDTO;
import kdec.apple.cloud.app.common.dto.materials.NoteVO;
import kdec.apple.cloud.app.common.dto.materials.VideoParseResultVO;
import kdec.apple.cloud.app.common.dto.utils.TaskCreatedVO;
import kdec.apple.cloud.app.common.dto.utils.TaskStatusVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/student/courses/{courseId}/chapters/{chapterId}")
public class StudentChapterController {
    private final MaterialService materialService;
    private final StudentChapterService chapterService;
    private final TaskQueryService taskQueryService;

    @ApiOperation("Get chapter detail")
    @GetMapping
    public Result<ChapterDetailVO> getChapterDetail(@PathVariable Long courseId, @PathVariable Long chapterId) {
        return Result.ok(chapterService.getChapterDetail(courseId, chapterId));
    }

    @ApiOperation("List chapter materials")
    @GetMapping("/materials")
    public Result<List<MaterialVO>> getMaterialList(@PathVariable Long courseId, @PathVariable Long chapterId) {
        return Result.ok(materialService.listByChapter(courseId, chapterId));
    }

    @ApiOperation("Get material detail")
    @GetMapping("/materials/{materialId}")
    public Result<MaterialDetailVO> getMaterialDetailById(@PathVariable Long materialId) {
        return Result.ok(materialService.getById(materialId));
    }

    @ApiOperation("Upload material")
    @PostMapping("/materials/upload")
    public Result<MaterialUploadVO> uploadMaterial(@PathVariable Long courseId, @PathVariable Long chapterId,
                                                   @RequestPart("file") MultipartFile file,
                                                   @ModelAttribute MaterialUploadDTO uploadDTO) {
        uploadDTO.setCourseId(courseId);
        uploadDTO.setChapterId(chapterId);
        return Result.ok(materialService.upload(file, uploadDTO));
    }

    @ApiOperation("Delete material")
    @DeleteMapping("/materials/{materialId}")
    public Result<Void> deleteMaterial(@PathVariable Long materialId) {
        materialService.deleteById(materialId);
        return Result.ok();
    }

    @ApiOperation("Get AI task status")
    @GetMapping("/tasks/{taskId}/status")
    public Result<TaskStatusVO> getTaskStatus(@PathVariable Long taskId) {
        return Result.ok(taskQueryService.getTaskStatus(taskId));
    }

    @ApiOperation("Generate knowledge graph")
    @PostMapping("/materials/{materialId}/graph/generate")
    public Result<TaskCreatedVO> generateKnowledgeGraph(@PathVariable Long materialId) {
        return Result.ok(materialService.generateGraph(materialId));
    }

    @ApiOperation("Get knowledge graph")
    @GetMapping("/materials/{materialId}/graph")
    public Result<GraphVO> getKnowledgeGraph(@PathVariable Long materialId) {
        return Result.ok(materialService.getGraph(materialId));
    }

    @ApiOperation("Generate note")
    @PostMapping("/materials/{materialId}/note/generate")
    public Result<TaskCreatedVO> generateNote(@PathVariable Long materialId) {
        return Result.ok(materialService.generateNote(materialId));
    }

    @ApiOperation("Save note")
    @PutMapping("/materials/{materialId}/note")
    public Result<NoteVO> saveNote(@PathVariable Long materialId, @RequestBody NoteDTO noteDTO) {
        noteDTO.setMaterialId(materialId);
        return Result.ok(materialService.saveNote(noteDTO));
    }

    @ApiOperation("Get note")
    @GetMapping("/materials/{materialId}/note")
    public Result<NoteVO> getNote(@PathVariable Long materialId) {
        return Result.ok(materialService.getNote(materialId));
    }

    @ApiOperation("Parse audio")
    @PostMapping("/materials/{materialId}/audio/generate")
    public Result<TaskCreatedVO> parseAudio(@PathVariable Long materialId) {
        return Result.ok(materialService.parseAudio(materialId));
    }

    @ApiOperation("Get audio parse result")
    @GetMapping("/materials/{materialId}/audio/result")
    public Result<AudioParseResultVO> getAudioParseResult(@PathVariable Long materialId) {
        return Result.ok(materialService.getAudioResult(materialId));
    }

    @ApiOperation("Parse video")
    @PostMapping("/materials/{materialId}/video/generate")
    public Result<TaskCreatedVO> parseVideo(@PathVariable Long materialId) {
        return Result.ok(materialService.parseVideo(materialId));
    }

    @ApiOperation("Get video parse result")
    @GetMapping("/materials/{materialId}/video/result")
    public Result<VideoParseResultVO> getVideoParseResult(@PathVariable Long materialId) {
        return Result.ok(materialService.getVideoResult(materialId));
    }
}
