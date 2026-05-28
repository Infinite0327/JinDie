package kdec.apple.cloud.app.business.service;

import kdec.apple.cloud.app.common.dto.chapter.ChapterDetailVO;
import kdec.apple.cloud.app.common.dto.graph.GraphVO;
import kdec.apple.cloud.app.common.dto.materials.*;
import kdec.apple.cloud.app.common.dto.utils.TaskCreatedVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//课件解析任务（调AI抽知识点）

public interface MaterialService {


    MaterialUploadVO upload(MultipartFile file, MaterialUploadDTO dto);

    void delete(Long materialId);

    void update(Long materialId, MaterialUpdateDTO dto);

    List<MaterialVO> listByChapter(Long courseId, Long chapterId);

    void deleteById(Long materialId);


    TaskCreatedVO generateGraph(Long materialId);

    GraphVO getGraph(Long materialId);

    TaskCreatedVO generateNote(Long materialId);

    NoteVO saveNote(NoteDTO noteDTO);

    NoteVO getNote(Long materialId);

    TaskCreatedVO parseAudio(Long materialId);

    AudioParseResultVO getAudioResult(Long materialId);

    TaskCreatedVO parseVideo(Long materialId);

    VideoParseResultVO getVideoResult(Long materialId);

    MaterialDetailVO getById(Long materialId);
}
