package ru.paul.myservice.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;
import ru.paul.myservice.dto.BondsResponse;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MoexBondGridClient {

    private static final int PAGE_SIZE = 100;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public MoexBondGridClient(RestClient restClient) {
        this.restClient = restClient;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Получает все облигации через эндпоинт rates.json с пагинацией
     */
    public List<BondsResponse> getAllBondsWithRate() {
        List<BondsResponse> allBonds = new ArrayList<>();
        int start = 0;
        int total = Integer.MAX_VALUE;

        log.info("🚀 Начинаем загрузку всех облигаций через rates.json...");

        while (start < total) {
            try {
                log.info("Загрузка порции с start={}", start);

                String url = buildUrl(start);
                String rawJson = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(String.class);

                if (rawJson == null || rawJson.isEmpty()) {
                    log.warn("Пустой ответ от MOEX на start={}", start);
                    break;
                }

                // Парсим JSON
                JsonNode root = objectMapper.readTree(rawJson);

                // 1. Получаем данные rates
                JsonNode ratesNode = findRatesNode(root);
                if (ratesNode == null || !ratesNode.isArray()) {
                    log.warn("Не найден узел rates на start={}", start);
                    break;
                }

                // 2. Получаем курсор
                JsonNode cursorNode = findCursorNode(root);
                if (cursorNode != null && cursorNode.isArray() && cursorNode.size() > 0) {
                    JsonNode cursor = cursorNode.get(0);
                    total = cursor.path("TOTAL").asInt(Integer.MAX_VALUE);
                    log.info("Всего бумаг: {}, загружено: {}", total, allBonds.size());
                }

                // 3. Парсим каждую облигацию
                for (JsonNode item : ratesNode) {
                    BondsResponse bond = parseBond(item);
                    allBonds.add(bond);
                }

                start += PAGE_SIZE;

            } catch (Exception e) {
                log.error("❌ Ошибка при загрузке порции start={}: {}", start, e.getMessage(), e);
                break;
            }
        }

        log.info("✅ Всего загружено {} облигаций", allBonds.size());
        return allBonds;
    }

    /**
     * Парсит одну облигацию из JSON узла
     */
    private BondsResponse parseBond(JsonNode item) {
        BondsResponse bond = new BondsResponse();

        // Основные идентификаторы
        bond.setSecId(getString(item, "SECID"));
        bond.setShortName(getString(item, "SHORTNAME"));
        bond.setName(getString(item, "NAME"));
        bond.setIsin(getString(item, "ISIN"));

        // Классификация
        bond.setBondType(getString(item, "BOND_TYPE"));
        bond.setBondSubType(getString(item, "BOND_SUBTYPE"));
        bond.setListLevel(getInt(item, "LISTLEVEL"));
        bond.setStatus(getString(item, "STATUS"));
        bond.setSuspensionListing(getString(item, "SUSPENSION_LISTING"));

        // Доступность для инвесторов
        bond.setIsQualifiedInvestors(getInt(item, "IS_QUALIFIED_INVESTORS"));
        bond.setHighRisk(getInt(item, "HIGH_RISK"));
        bond.setIsCollateral(getInt(item, "IS_COLLATERAL"));

        // Номинал и объем
        bond.setFaceValue(getDouble(item, "FACEVALUE"));
        bond.setFaceUnit(getString(item, "FACEUNIT"));
        bond.setIssueSize(getLong(item, "ISSUESIZE"));

        // Цена и доходность
        bond.setPrice(getDouble(item, "PRICE"));
        bond.setPriceRub(getDouble(item, "PRICE_RUB"));
        bond.setWaprice(getDouble(item, "WAPRICE"));
        bond.setYieldAtWap(getDouble(item, "YIELDATWAP"));

        // Купонные параметры
        bond.setCouponValue(getDouble(item, "COUPONVALUE"));
        bond.setCouponPercent(getDouble(item, "COUPONPERCENT"));
        bond.setCouponPeriod(getInt(item, "COUPONPERIOD"));
        bond.setNextCoupon(getString(item, "NEXTCOUPON"));
        bond.setAccruedInt(getDouble(item, "ACCRUEDINT"));

        // Даты
        bond.setMatDate(getString(item, "MATDATE"));
        bond.setOfferDate(getString(item, "OFFERDATE"));
        bond.setBuybackDate(getString(item, "BUYBACKDATE"));
        bond.setSettleDate(getString(item, "SETTLEDATE"));
        bond.setCallOptionDate(getString(item, "CALLOPTIONDATE"));
        bond.setPutOptionDate(getString(item, "PUTOPTIONDATE"));

        // Эмитент
        bond.setEmitentName(getString(item, "EMITENTNAME"));
        bond.setInn(getString(item, "INN"));

        return bond;
    }

    /**
     * Строит URL для запроса с параметрами
     */
    private String buildUrl(int start) {
        return "https://iss.moex.com/iss/apps/infogrid/stock/rates.json" +
               "?iss.meta=off" +
               "&iss.json=extended" +
               "&rates.columns=" +
               "SECID,SHORTNAME,NAME,ISIN,BOND_TYPE,BOND_SUBTYPE,LISTLEVEL,STATUS," +
               "SUSPENSION_LISTING,IS_QUALIFIED_INVESTORS,HIGH_RISK,IS_COLLATERAL," +
               "FACEVALUE,FACEUNIT,ISSUESIZE,PRICE,PRICE_RUB,WAPRICE,YIELDATWAP," +
               "COUPONVALUE,COUPONPERCENT,COUPONPERIOD,NEXTCOUPON,ACCRUEDINT," +
               "MATDATE,OFFERDATE,BUYBACKDATE,SETTLEDATE,CALLOPTIONDATE,PUTOPTIONDATE," +
               "EMITENTNAME,INN" +
               "&listname=1,2,3" +
//               "&sec_type=stock_corporate_bond,stock_exchange_bond" +
               "&lang=ru" +
               "&sort_column=SHORTNAME" +
               "&sort_order=asc" +
               "&start=" + start +
               "&limit=" + PAGE_SIZE +
               "&faceunit=rub";
    }

    /**
     * Ищет узел rates в ответе
     */
    private JsonNode findRatesNode(JsonNode root) {
        if (root.isArray()) {
            for (JsonNode node : root) {
                JsonNode rates = node.path("rates");
                if (!rates.isMissingNode()) {
                    return rates;
                }
            }
        }
        return root.path("rates");
    }

    /**
     * Ищет узел rates.cursor в ответе
     */
    private JsonNode findCursorNode(JsonNode root) {
        if (root.isArray()) {
            for (JsonNode node : root) {
                JsonNode cursor = node.path("rates.cursor");
                if (!cursor.isMissingNode()) {
                    return cursor;
                }
            }
        }
        return root.path("rates.cursor");
    }

    // --- Вспомогательные методы для парсинга ---

    private String getString(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isNull() ? null : value.asText();
    }

    private Integer getInt(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isNull() ? null : value.asInt();
    }

    private Double getDouble(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isNull() ? null : value.asDouble();
    }

    private Long getLong(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isNull() ? null : value.asLong();
    }
}