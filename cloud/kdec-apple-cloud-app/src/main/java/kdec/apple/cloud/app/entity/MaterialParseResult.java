package kdec.apple.cloud.app.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("material_parse_result")
public class MaterialParseResult {
    @TableId
    private Long id;
    private Long materialId;
    // GRAPH / SUMMARY / AUDIO / VIDEO
    private String resultType;
    // 直接存AI返回的JSON结果，不拆字段
    private String resultData;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
