package kdec.apple.cloud.app.common.dto.chapter;

import lombok.Data;

import java.util.List;

@Data
public class ChapterSortDTO {
    // 章节ID有序列表，按列表顺序重新排序
    private List<Long> chapterIds;
}