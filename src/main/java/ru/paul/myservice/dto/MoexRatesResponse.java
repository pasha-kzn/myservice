package ru.paul.myservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Корневой объект ответа - массив, но мы будем использовать список
 */
@Data
public class MoexRatesResponse {
    private CharsetInfo charsetinfo;
    private List<Rate> rates;

    @JsonProperty("rates.cursor")
    private RatesCursor ratesCursor;

    @JsonProperty("rates.emitters")
    private RatesEmitters ratesEmitters;

    /**
     * Информация о кодировке
     */
    @Data
    public static class CharsetInfo {
        private String name;
    }

    /**
     * Данные по облигации
     */
    @Data
    public static class Rate {
        private String SECID;
        private String SHORTNAME;
        private String ISIN;
        private Double FACEVALUE;
        private String FACEUNIT;
        private Long ISSUESIZE;
        private String PRIMARY_BOARDID;
        private String MATDATE;
        private Double YIELDATWAP;
    }

    /**
     * Информация о курсоре (пагинация)
     */
    @Data
    public static class RatesCursor {
        private Integer INDEX;
        private Integer TOTAL;
        private Integer PAGESIZE;
    }

    /**
     * Информация о количестве эмитентов
     */
    @Data
    public static class RatesEmitters {
        private Integer TOTAL;
    }
}


