package ru.paul.myservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import ru.paul.myservice.dto.BondDetailsResponse;

@Slf4j

public class MoexBondGridClientOld {

    private final RestClient restClient;

    public MoexBondGridClientOld(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Запрашивает облигации партиями пока не выберет все облигации
     * Использует эндпоинт /iss/apps/infogrid/stock/rates.json
     */

//    public List<Rate> getAllBondsWithRate() throws InterruptedException {
//        Integer start = 0;
//        Integer total = Integer.MAX_VALUE;
//        List<Rate> rates = new ArrayList<>();
//
//        while (start < 100) {
//            BondDetailsResponse response = getPartOfBonds(start);
//            total = response.getRatesСursor().getFirst().getTOTAL();
//            rates.addAll(response.getRates());
//            Thread.sleep(2000);
//            start += 100;
//        }
//        return rates;
//    }

    /**
     * Получает облигации с Московской биржи партиями по 100 шт
     * Использует эндпоинт /iss/apps/infogrid/stock/rates.json
     */
    public BondDetailsResponse getPartOfBonds(Integer start) {
        String url = UriComponentsBuilder.fromPath("/iss/apps/infogrid/stock/rates.json")
                .queryParam("iss.meta", "off")
                .queryParam("iss.json", "extended")
                .queryParam("sec_type", "stock_corporate_bond%2Cstock_exchange_bond")
                .queryParam("sort_column", "SHORTNAME")
                .queryParam("sort_order", "asc")
                .queryParam("start", start)
                .queryParam("limit", "100")
                .build()
                .toUriString();

        log.debug("Запрос к MOEX стартовая позиция - {}", start);

        try {
            BondDetailsResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(BondDetailsResponse.class);

            if (response == null || response.getRates().isEmpty()) {
                log.warn("Пустой ответ от MOEX");
                return null;
            }

            return response;

        } catch (RestClientException e) {
            log.error("Ошибка при запросе к MOEX: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Преобразует сырые данные в список BondsResponse
     */
//    private List<Rate> convertToBondsResponse(List<Rate> rates) {
//        List<String> columns = securitiesData.getColumns();
//        List<List<Object>> data = securitiesData.getData();
//
//        if (columns == null || data == null || data.isEmpty()) {
//            return List.of();
//        }
//
//        Map<String, Integer> columnIndex = columns.stream()
//                .collect(Collectors.toMap(
//                        col -> col,
//                        columns::indexOf,
//                        (existing, replacement) -> existing
//                ));
//
//        List<BondsResponse> result = new ArrayList<>();
//
//        for (List<Object> row : data) {
//            BondsResponse bond = new BondsResponse();
//
//            // Основные идентификаторы
//            bond.setSecId(getString(row, columnIndex, "SECID"));
//            bond.setBoardId(getString(row, columnIndex, "BOARDID"));
//            bond.setShortName(getString(row, columnIndex, "SHORTNAME"));
//            bond.setSecName(getString(row, columnIndex, "SECNAME"));
//            bond.setIsin(getString(row, columnIndex, "ISIN"));
//            bond.setRegNumber(getString(row, columnIndex, "REGNUMBER"));
//            bond.setLatName(getString(row, columnIndex, "LATNAME"));
//
//            // Ценовые показатели
//            bond.setPrevWapPrice(getDouble(row, columnIndex, "PREVWAPRICE"));
//            bond.setYieldAtPrevWapPrice(getDouble(row, columnIndex, "YIELDATPREVWAPRICE"));
//            bond.setPrevPrice(getDouble(row, columnIndex, "PREVPRICE"));
//            bond.setPrevLegalClosePrice(getDouble(row, columnIndex, "PREVLEGALCLOSEPRICE"));
//
//            // Купон
//            bond.setCouponValue(getDouble(row, columnIndex, "COUPONVALUE"));
//            bond.setCouponPercent(getDouble(row, columnIndex, "COUPONPERCENT"));
//            bond.setCouponPeriod(getInteger(row, columnIndex, "COUPONPERIOD"));
//            bond.setNextCoupon(getLocalDate(row, columnIndex, "NEXTCOUPON"));
//
//            // Номинал
//            bond.setFaceValue(getDouble(row, columnIndex, "FACEVALUE"));
//            bond.setFaceUnit(getString(row, columnIndex, "FACEUNIT"));
//            bond.setIssueSize(getLong(row, columnIndex, "ISSUESIZE"));
//            bond.setIssueSizePlaced(getLong(row, columnIndex, "ISSUESIZEPLACED"));
//            bond.setLotValue(getDouble(row, columnIndex, "LOTVALUE"));
//            bond.setLotSize(getInteger(row, columnIndex, "LOTSIZE"));
//
//            // Даты
//            bond.setMatDate(getLocalDate(row, columnIndex, "MATDATE"));
//            bond.setPrevDate(getLocalDate(row, columnIndex, "PREVDATE"));
//            bond.setBuybackDate(getLocalDate(row, columnIndex, "BUYBACKDATE"));
//            bond.setOfferDate(getLocalDate(row, columnIndex, "OFFERDATE"));
//            bond.setSettleDate(getLocalDate(row, columnIndex, "SETTLEDATE"));
//            bond.setCallOptionDate(getLocalDate(row, columnIndex, "CALLOPTIONDATE"));
//            bond.setPutOptionDate(getLocalDate(row, columnIndex, "PUTOPTIONDATE"));
//            bond.setDateYieldFromIssuer(getLocalDate(row, columnIndex, "DATEYIELDFROMISSUER"));
//
//            // Начисленные проценты
//            bond.setAccruedInt(getDouble(row, columnIndex, "ACCRUEDINT"));
//
//            // Статусы и классификация
//            bond.setStatus(getString(row, columnIndex, "STATUS"));
//            bond.setBoardName(getString(row, columnIndex, "BOARDNAME"));
//            bond.setMarketCode(getString(row, columnIndex, "MARKETCODE"));
//            bond.setSectorId(getString(row, columnIndex, "SECTORID"));
//            bond.setSecType(getString(row, columnIndex, "SECTYPE"));
//            bond.setBondType(getString(row, columnIndex, "BONDTYPE"));
//            bond.setBondSubType(getString(row, columnIndex, "BONDSUBTYPE"));
//            bond.setCurrencyId(getString(row, columnIndex, "CURRENCYID"));
//            bond.setListLevel(getInteger(row, columnIndex, "LISTLEVEL"));
//            bond.setDecimals(getInteger(row, columnIndex, "DECIMALS"));
//            bond.setMinStep(getDouble(row, columnIndex, "MINSTEP"));
//            bond.setRemarks(getString(row, columnIndex, "REMARKS"));
//            bond.setInstrId(getString(row, columnIndex, "INSTRID"));
//            bond.setBuybackPrice(getDouble(row, columnIndex, "BUYBACKPRICE"));
//            bond.setFaceValueOnSettleDate(getDouble(row, columnIndex, "FACEVALUEONSETTLEDATE"));
//
//            result.add(bond);
//        }
//
//        log.info("Получено {} облигаций с MOEX", result.size());
//        return result;
//    }

    // --- Вспомогательные методы для извлечения данных ---

//    private String getString(List<Object> row, Map<String, Integer> indexMap, String column) {
//        Integer idx = indexMap.get(column);
//        if (idx == null || idx >= row.size()) return null;
//        Object value = row.get(idx);
//        return value != null ? value.toString() : null;
//    }
//
//    private Double getDouble(List<Object> row, Map<String, Integer> indexMap, String column) {
//        Integer idx = indexMap.get(column);
//        if (idx == null || idx >= row.size()) return null;
//        Object value = row.get(idx);
//        if (value == null) return null;
//        if (value instanceof Number) return ((Number) value).doubleValue();
//        try {
//            return Double.parseDouble(value.toString());
//        } catch (NumberFormatException e) {
//            return null;
//        }
//    }
//
//    private Integer getInteger(List<Object> row, Map<String, Integer> indexMap, String column) {
//        Integer idx = indexMap.get(column);
//        if (idx == null || idx >= row.size()) return null;
//        Object value = row.get(idx);
//        if (value == null) return null;
//        if (value instanceof Number) return ((Number) value).intValue();
//        try {
//            return Integer.parseInt(value.toString());
//        } catch (NumberFormatException e) {
//            return null;
//        }
//    }
//
//    private Long getLong(List<Object> row, Map<String, Integer> indexMap, String column) {
//        Integer idx = indexMap.get(column);
//        if (idx == null || idx >= row.size()) return null;
//        Object value = row.get(idx);
//        if (value == null) return null;
//        if (value instanceof Number) return ((Number) value).longValue();
//        try {
//            return Long.parseLong(value.toString());
//        } catch (NumberFormatException e) {
//            return null;
//        }
//    }
//
//    private LocalDate getLocalDate(List<Object> row, Map<String, Integer> indexMap, String column) {
//        String dateStr = getString(row, indexMap, column);
//        if (dateStr == null || dateStr.isEmpty() || "0000-00-00".equals(dateStr)) return null;
//        try {
//            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
//        } catch (Exception e) {
//            log.debug("Не удалось распарсить дату: {}", dateStr);
//            return null;
//        }
//    }
}
