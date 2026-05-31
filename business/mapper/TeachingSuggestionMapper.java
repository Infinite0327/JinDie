package kdec.apple.cloud.app.business.mapper;

import kdec.apple.cloud.app.common.entity.TeachingSuggestion;

import java.util.HashMap;
import java.util.Map;

public class TeachingSuggestionMapper {
    private static final Map<Long, TeachingSuggestion> SUGGESTIONS = new HashMap<>();

    public void save(TeachingSuggestion suggestion) {
        SUGGESTIONS.put(suggestion.getClassId(), suggestion);
    }

    public TeachingSuggestion selectByClassId(Long classId) {
        return SUGGESTIONS.get(classId);
    }
}
