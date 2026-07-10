package ru.paul.myservice.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Rate {
    private String secId;
    private String shortName;
    private String name;
    private String isin;        // идент код - RU000A10BY52
    private String bondType;        // Тип облигации: с фиксированным купоном, с переменным купоном, амортизируемая
    private String bondSubType;     // Подтип облигации, уточняет особенности (например, "с амортизацией").
    private String listLevel;   //Уровень листинга
    private String status;   //Статус бумаги: A — активна, T — торгуется, D — делистинг.
    private String suspensionListing;   //Приостановлен ли листинг: 0 — нет, 1 — да.
    private Integer isQualifiedInvestors;  // 0 - для всех, 1 - только для квалов
    private Integer highRisk;
    private Integer isCollateral;  //Является ли бумага обеспечением (для сделок РЕПО и т.д.).
    private Double faceValue;  //номинал
    private String faceUnit;    //валюта
    private Long issueSize;     //Объем выпуска в штуках
    private Double price;     //Цена последней сделки в процентах от номинала
    private Double priceRub;     //Цена последней сделки в рублях (с учетом НКД).
    private Double waprice;     //Средневзвешенная цена за сессию (в % от номинала).
    private Double yieldAtWap;     //Доходность к погашению, рассчитанная по средневзвешенной цене (в % годовых).
    private Double couponValue;     //Абсолютный размер купона в валюте номинала.
    private Double couponPercent;     //Ставка купона в процентах годовых.
    private Double couponPeriod;     //Период выплаты купона в днях.
    private Double nextCoupon;     //Дата следующей купонной выплаты.
    private Double accruedInt;     //Накопленный купонный доход (НКД) на текущую дату.
    private String matDate;     //Дата погашения облигации.
    private String offerDate;     //Дата оферты (возможность досрочного погашения).
    private String buybackDate;     //Альтернативная дата оферты.
    private String settleDate;     //Дата расчетов по сделке.
    private String callOptionDate;     //Дата call-опциона (досрочное погашение эмитентом).
    private String putOptionDate;     //Дата put-опциона (досрочное погашение инвестором).
    private String emitentName;
    private String inn;
}
