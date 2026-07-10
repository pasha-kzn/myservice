package ru.paul.myservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import ru.paul.myservice.dto.BondsResponse;
import ru.paul.myservice.dto.MoexSecuritiesResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MoexSecuritiesClient {

    private final RestClient restClient;

    public MoexSecuritiesClient(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Получает все облигации с Московской биржи
     * Использует эндпоинт /iss/engines/stock/markets/bonds/securities.json
     */
    public List<BondsResponse> getAllBonds() {
        String url = UriComponentsBuilder.fromPath("/iss/engines/stock/markets/bonds/securities.json")
                .queryParam("iss.meta", "off")
//                .queryParam("iss.json", "extended")
//                .queryParam("securities.columns",
//                        "SECID,BOARDID,SHORTNAME,PREVWAPRICE,YIELDATPREVWAPRICE,COUPONVALUE," +
//                        "NEXTCOUPON,ACCRUEDINT,PREVPRICE,LOTSIZE,FACEVALUE,BOARDNAME,STATUS," +
//                        "MATDATE,DECIMALS,COUPONPERIOD,ISSUESIZE,PREVLEGALCLOSEPRICE,PREVDATE," +
//                        "SECNAME,REMARKS,MARKETCODE,INSTRID,SECTORID,MINSTEP,FACEUNIT," +
//                        "BUYBACKPRICE,BUYBACKDATE,ISIN,LATNAME,REGNUMBER,CURRENCYID," +
//                        "ISSUESIZEPLACED,LISTLEVEL,SECTYPE,COUPONPERCENT,OFFERDATE,SETTLEDATE," +
//                        "LOTVALUE,FACEVALUEONSETTLEDATE,CALLOPTIONDATE,PUTOPTIONDATE," +
//                        "DATEYIELDFROMISSUER,BONDTYPE,BONDSUBTYPE")
                .build()
                .toUriString();

        log.debug("Запрос к MOEX: {}", url);

        try {
            MoexSecuritiesResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(MoexSecuritiesResponse.class);

            if (response == null || response.getSecurities() == null) {
                log.warn("Пустой ответ от MOEX");
                return List.of();
            }

            return convertToBondsResponse(response.getSecurities());

        } catch (RestClientException e) {
            log.error("Ошибка при запросе к MOEX: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Преобразует сырые данные в список BondsResponse
     */
    private List<BondsResponse> convertToBondsResponse(MoexSecuritiesResponse.SecuritiesData securitiesData) {
        List<String> columns = securitiesData.getColumns();
        List<List<Object>> data = securitiesData.getData();

        if (columns == null || data == null || data.isEmpty()) {
            return List.of();
        }

        Map<String, Integer> columnIndex = columns.stream()
                .collect(Collectors.toMap(
                        col -> col,
                        columns::indexOf,
                        (existing, replacement) -> existing
                ));

        List<BondsResponse> result = new ArrayList<>();

        for (List<Object> row : data) {
            BondsResponse bond = new BondsResponse();

            // Основные идентификаторы
            bond.setSecId(getString(row, columnIndex, "SECID"));
            bond.setBoardId(getString(row, columnIndex, "BOARDID"));
            bond.setShortName(getString(row, columnIndex, "SHORTNAME"));
            bond.setSecName(getString(row, columnIndex, "SECNAME"));
            bond.setIsin(getString(row, columnIndex, "ISIN"));
            bond.setRegNumber(getString(row, columnIndex, "REGNUMBER"));
            bond.setLatName(getString(row, columnIndex, "LATNAME"));

            // Ценовые показатели
            bond.setPrevWapPrice(getDouble(row, columnIndex, "PREVWAPRICE"));
            bond.setYieldAtPrevWapPrice(getDouble(row, columnIndex, "YIELDATPREVWAPRICE"));
            bond.setPrevPrice(getDouble(row, columnIndex, "PREVPRICE"));
            bond.setPrevLegalClosePrice(getDouble(row, columnIndex, "PREVLEGALCLOSEPRICE"));

            // Купон
            bond.setCouponValue(getDouble(row, columnIndex, "COUPONVALUE"));
            bond.setCouponPercent(getDouble(row, columnIndex, "COUPONPERCENT"));
            bond.setCouponPeriod(getInteger(row, columnIndex, "COUPONPERIOD"));
            bond.setNextCoupon(getString(row, columnIndex, "NEXTCOUPON"));

            // Номинал
            bond.setFaceValue(getDouble(row, columnIndex, "FACEVALUE"));
            bond.setFaceUnit(getString(row, columnIndex, "FACEUNIT"));
            bond.setIssueSize(getLong(row, columnIndex, "ISSUESIZE"));
            bond.setIssueSizePlaced(getLong(row, columnIndex, "ISSUESIZEPLACED"));
            bond.setLotValue(getDouble(row, columnIndex, "LOTVALUE"));
            bond.setLotSize(getInteger(row, columnIndex, "LOTSIZE"));

            // Даты
            bond.setMatDate(getString(row, columnIndex, "MATDATE"));
            bond.setPrevDate(getString(row, columnIndex, "PREVDATE"));
            bond.setBuybackDate(getString(row, columnIndex, "BUYBACKDATE"));
            bond.setOfferDate(getString(row, columnIndex, "OFFERDATE"));
            bond.setSettleDate(getString(row, columnIndex, "SETTLEDATE"));
            bond.setCallOptionDate(getString(row, columnIndex, "CALLOPTIONDATE"));
            bond.setPutOptionDate(getString(row, columnIndex, "PUTOPTIONDATE"));
            bond.setDateYieldFromIssuer(getString(row, columnIndex, "DATEYIELDFROMISSUER"));

            // Начисленные проценты
            bond.setAccruedInt(getDouble(row, columnIndex, "ACCRUEDINT"));

            // Статусы и классификация
            bond.setStatus(getString(row, columnIndex, "STATUS"));
            bond.setBoardName(getString(row, columnIndex, "BOARDNAME"));
            bond.setMarketCode(getString(row, columnIndex, "MARKETCODE"));
            bond.setSectorId(getString(row, columnIndex, "SECTORID"));
            bond.setSecType(getString(row, columnIndex, "SECTYPE"));
            bond.setBondType(getString(row, columnIndex, "BONDTYPE"));
            bond.setBondSubType(getString(row, columnIndex, "BONDSUBTYPE"));
            bond.setCurrencyId(getString(row, columnIndex, "CURRENCYID"));
            bond.setListLevel(getInteger(row, columnIndex, "LISTLEVEL"));
            bond.setDecimals(getInteger(row, columnIndex, "DECIMALS"));
            bond.setMinStep(getDouble(row, columnIndex, "MINSTEP"));
            bond.setRemarks(getString(row, columnIndex, "REMARKS"));
            bond.setInstrId(getString(row, columnIndex, "INSTRID"));
            bond.setBuybackPrice(getDouble(row, columnIndex, "BUYBACKPRICE"));
            bond.setFaceValueOnSettleDate(getDouble(row, columnIndex, "FACEVALUEONSETTLEDATE"));

            result.add(bond);
        }

        log.info("Получено {} облигаций с MOEX", result.size());
        return result;
    }

    // --- Вспомогательные методы для извлечения данных ---

    private String getString(List<Object> row, Map<String, Integer> indexMap, String column) {
        Integer idx = indexMap.get(column);
        if (idx == null || idx >= row.size()) return null;
        Object value = row.get(idx);
        return value != null ? value.toString() : null;
    }

    private Double getDouble(List<Object> row, Map<String, Integer> indexMap, String column) {
        Integer idx = indexMap.get(column);
        if (idx == null || idx >= row.size()) return null;
        Object value = row.get(idx);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer getInteger(List<Object> row, Map<String, Integer> indexMap, String column) {
        Integer idx = indexMap.get(column);
        if (idx == null || idx >= row.size()) return null;
        Object value = row.get(idx);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long getLong(List<Object> row, Map<String, Integer> indexMap, String column) {
        Integer idx = indexMap.get(column);
        if (idx == null || idx >= row.size()) return null;
        Object value = row.get(idx);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate getLocalDate(List<Object> row, Map<String, Integer> indexMap, String column) {
        String dateStr = getString(row, indexMap, column);
        if (dateStr == null || dateStr.isEmpty() || "0000-00-00".equals(dateStr)) return null;
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            log.debug("Не удалось распарсить дату: {}", dateStr);
            return null;
        }
    }
}
