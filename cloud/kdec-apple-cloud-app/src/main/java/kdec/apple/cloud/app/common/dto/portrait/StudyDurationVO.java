package kdec.apple.cloud.app.common.dto.portrait;

import lombok.Data;

import java.util.Map;

@Data
public class StudyDurationVO {
    // DAY: 每天是否有学习记录（日历打圈用）
    // MONTH: 每天学习时长（热度图用）
    // YEAR: 每月学习时长汇总
    private String granularity;
    // key: 日期字符串，value: 学习分钟数（DAY模式下>0即打圈）
    private Map<String, Integer> data;
    // 周期内总时长
    private Integer totalMinutes;

}
