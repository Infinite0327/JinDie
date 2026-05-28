package kdec.apple.cloud.app.common.dto.enums;

/**
 * 节点颜色（掌握度）
 */
public enum MasteryLevel {
    GREEN,   // 已掌握 (mastery >= 80)
    YELLOW,  // 部分掌握 (50 <= mastery < 80)
    RED      // 薄弱 (mastery < 50)
}
