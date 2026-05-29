package kdec.apple.cloud.app.business.mapper;

import kdec.apple.cloud.app.common.entity.ExerciseItem;
import kdec.apple.cloud.app.common.enums.ExerciseType;

import java.util.ArrayList;
import java.util.List;

public class ExerciseItemMapper {
    private static final List<ExerciseItem> ITEM_LIST = new ArrayList<>();

    static {
        ITEM_LIST.add(new ExerciseItem(
                1L,
                1001L,
                ExerciseType.SINGLE_CHOICE,
                "下列哪一项最能体现本章的核心概念？",
                "[{\"key\":\"A\",\"value\":\"只记忆结论\"},{\"key\":\"B\",\"value\":\"理解概念、条件和适用场景\"},{\"key\":\"C\",\"value\":\"跳过基础定义\"},{\"key\":\"D\",\"value\":\"只刷题不复盘\"}]",
                "[\"B\"]",
                "本题考查对核心概念的理解，而不是机械记忆。",
                "[\"概念理解\"]"
        ));

        ITEM_LIST.add(new ExerciseItem(
                2L,
                1001L,
                ExerciseType.SINGLE_CHOICE,
                "当题目条件发生变化时，最应该先判断什么？",
                "[{\"key\":\"A\",\"value\":\"答案是否一样\"},{\"key\":\"B\",\"value\":\"方法适用条件是否成立\"},{\"key\":\"C\",\"value\":\"选项是否最长\"},{\"key\":\"D\",\"value\":\"题干是否熟悉\"}]",
                "[\"B\"]",
                "条件变化后，应先判断原方法或公式的适用条件是否仍成立。",
                "[\"条件辨析\",\"方法迁移\"]"
        ));

        ITEM_LIST.add(new ExerciseItem(
                3L,
                2001L,
                ExerciseType.TRUE_FALSE,
                "学习一个新知识点时，定义通常比例题更优先。",
                "[{\"key\":\"true\",\"value\":\"正确\"},{\"key\":\"false\",\"value\":\"错误\"}]",
                "[\"true\"]",
                "定义决定对象边界，是后续推理和做题的基础。",
                "[\"定义理解\"]"
        ));
    }

    public List<ExerciseItem> selectByCondition(Long batchId, String keyword, String type) {
        List<ExerciseItem> result = new ArrayList<>();

        for (ExerciseItem item : ITEM_LIST) {
            if (batchId != null && !batchId.equals(item.getBatchId())) {
                continue;
            }

            if (type != null && !type.isEmpty() && !type.equals(item.getType().name())) {
                continue;
            }

            if (keyword != null && !keyword.isEmpty()) {
                boolean hitContent = item.getContent() != null && item.getContent().contains(keyword);
                boolean hitTags = item.getKnowledgeTags() != null && item.getKnowledgeTags().contains(keyword);

                if (!hitContent && !hitTags) {
                    continue;
                }
            }

            result.add(item);
        }

        return result;
    }

    public ExerciseItem selectById(Long exerciseItemId) {
        if (exerciseItemId == null) {
            return null;
        }

        for (ExerciseItem item : ITEM_LIST) {
            if (exerciseItemId.equals(item.getId())) {
                return item;
            }
        }

        return null;
    }
}