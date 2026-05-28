package kdec.apple.cloud.app.common.dto.graph;

/** 图谱整体（节点 + 边） */

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


/** 知识图谱VO */
@Data
public class GraphVO {
    private Long materialId;
    private List<GraphNode> nodes;
    private List<GraphEdge> edges;

    @Data
    public static class GraphNode {
        private String id;
        private String name;
    }

    @Data
    public static class GraphEdge {
        private String sourceId;
        private String targetId;
    }
}