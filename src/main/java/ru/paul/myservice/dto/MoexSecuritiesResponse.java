package ru.paul.myservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MoexSecuritiesResponse {
    @JsonProperty("securities")
    private SecuritiesData securities;

    @Data
    public class SecuritiesData {
        private Map<String, ColumnMetadata> metadata;  // метаданные колонок
        private List<String> columns;                  // названия колонок
        private List<List<Object>> data;               // сами данные
    }

    @Data
    public class ColumnMetadata {
        private String type;
        private Integer bytes;
        private Integer maxSize;
    }
}
