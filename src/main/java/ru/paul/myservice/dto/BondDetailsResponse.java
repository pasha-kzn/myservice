package ru.paul.myservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class BondDetailsResponse {
    public List<BondsResponse> rates;
    public List<RatesCursor> ratesСursor;

    @Data
    public static class RatesCursor {
        private Integer INDEX;
        private Integer TOTAL;
        private Integer PAGESIZE;
    }
}
