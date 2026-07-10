package ru.paul.myservice.dto;

import lombok.Data;

@Data
public class BondsResponse {
    // Основные идентификаторы
    private String secId;              // SECID - код инструмента
    private String boardId;            // BOARDID - код режима торгов
    private String shortName;          // SHORTNAME - краткое наименование
    private String name;
    private String secName;            // SECNAME - полное наименование
    private String isin;               // ISIN - международный код
    private String regNumber;          // REGNUMBER - регистрационный номер
    private String latName;            // LATNAME - латинское наименование
    private String suspensionListing;   //Приостановлен ли листинг: 0 — нет, 1 — да.
    private String emitentName;
    private String inn;

    // Ценовые и доходностные показатели
    private Double prevWapPrice;       // PREVWAPRICE - средневзвешенная цена
    private Double yieldAtPrevWapPrice; // YIELDATPREVWAPRICE - доходность по средневзвешенной
    private Double prevPrice;          // PREVPRICE - цена последней сделки
    private Double prevLegalClosePrice; // PREVLEGALCLOSEPRICE - юридическая цена закрытия
    private Double yieldAtWap;          // YIELDATWAP - доходность к погашению
    private Integer highRisk;
    private Integer isCollateral;  //Является ли бумага обеспечением (для сделок РЕПО и т.д.).
    private Double price;     //Цена последней сделки в процентах от номинала
    private Double priceRub;     //Цена последней сделки в рублях (с учетом НКД).
    private Double waprice;     //Средневзвешенная цена за сессию (в % от номинала).

    // Номинальные значения
    private Double faceValue;          // FACEVALUE - номинал
    private String faceUnit;           // FACEUNIT - валюта номинала
    private Long issueSize;            // ISSUESIZE - объем выпуска
    private Long issueSizePlaced;      // ISSUESIZEPLACED - размещенный объем
    private Double lotValue;           // LOTVALUE - стоимость лота
    private Integer lotSize;           // LOTSIZE - размер лота

    // Купонные параметры
    private Double couponPercent;      // COUPONPERCENT - процентная ставка купона
    private Double couponValue;        // COUPONVALUE - размер купона
    private Integer couponPeriod;      // COUPONPERIOD - период купона в днях
    private String nextCoupon;      // NEXTCOUPON - дата следующей выплаты купона
    private Double accruedInt;         // ACCRUEDINT - НКД (накопленный купонный доход)

    // Даты
    private String matDate;         // MATDATE - дата погашения
    private String prevDate;        // PREVDATE - дата предыдущей сделки
    private String buybackDate;     // BUYBACKDATE - дата оферты
    private String offerDate;       // OFFERDATE - дата оферты (альтернативная)
    private String settleDate;      // SETTLEDATE - дата расчетов
    private String callOptionDate;  // CALLOPTIONDATE - дата call-опциона
    private String putOptionDate;   // PUTOPTIONDATE - дата put-опциона
    private String dateYieldFromIssuer; // DATEYIELDFROMISSUER

    // Статусы и классификация
    private String status;             // STATUS - статус (A - активная)
    private String bondType;           // BONDTYPE - тип облигации
    private String boardName;          // BOARDNAME - наименование режима торгов
    private String marketCode;         // MARKETCODE - код рынка
    private String sectorId;           // SECTORID - код сектора
    private String secType;            // SECTYPE - тип бумаги
    private String bondSubType;        // BONDSUBTYPE - подтип облигации
    private String currencyId;         // CURRENCYID - валюта
    private Integer listLevel;         // LISTLEVEL - уровень листинга
    private Integer decimals;          // DECIMALS - количество знаков после запятой
    private Double minStep;            // MINSTEP - минимальный шаг цены
    private String remarks;            // REMARKS - примечания
    private String instrId;            // INSTRID - идентификатор инструмента
    private Double buybackPrice;       // BUYBACKPRICE - цена оферты
    private Double faceValueOnSettleDate; // FACEVALUEONSETTLEDATE

    // Доступность для инвесторов
    private Integer isQualifiedInvestors;  // IS_QUALIFIED_INVESTORS - 0 для всех, 1 только для квал
}
