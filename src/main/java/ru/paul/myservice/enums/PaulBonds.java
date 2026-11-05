package ru.paul.myservice.enums;

import lombok.Getter;

public enum PaulBonds {
    RU000A109TJ6("RU000A109TJ6", "30", "Банк ПСБ 003P-09"),
    RU000A10FVD7("RU000A10FVD7", "30", "ВТБ Лизинг 001P-МБ-07"),
    RU000A10BS76("RU000A10BS76", "5", "ВУШ БО 001P-04"),
    RU000A104Z48("RU000A104Z48", "28", "ВЭБ.РФ ПБО-002P-33"),
    RU000A107RZ0("RU000A107RZ0", "3", "ГК Самолет БО-П13"),
    RU000A10CR50("RU000A10CR50", "20", "ГТЛК БО 002P-10"),
    RU000A10B1Q6("RU000A10B1Q6", "3", "Джи-групп 002P-06"),
    RU000A109JW0("RU000A109JW0", "20", "КАМАЗ БО-П12"),
    RU000A10CCG7("RU000A10CCG7", "20", "Каршеринг Руссия 001P-07"),
    RU000A10DSC0("RU000A10DSC0", "36", "Кредитный поток 4.1"),
    RU000A10EAQ6("RU000A10EAQ6", "7", "КРОНОС БО-01"),
    RU000A1053H4("RU000A1053H4", "10", "Лизинг-Трейд 001P-05"),
    RU000A10BFP3("RU000A10BFP3", "20", "МВ Финанс 001P-06"),
    RU000A10DIA3("RU000A10DIA3", "24", "Нижегородская область 3..."),
    RU000A1082W2("RU000A1082W2", "24", "Новые Технологии БО-03"),
    RU000A10CCN3("RU000A10CCN3", "21", "О'КЕЙ БО 001P-08"),
    RU000A0JXS59("RU000A0JXS59", "13", "Почта России БО-001P-03"),
    RU000A1055Y4("RU000A1055Y4", "9", "Почта России БО-002P-04"),
    RU000A10CDZ5("RU000A10CDZ5", "30", "РЖД БО 001P-45R"),
    RU000A10BQ60("RU000A10BQ60", "5", "РОЛЬФ БО 001P-08"),
    RU000A10DS74("RU000A10DS74", "20", "Сбербанк 001P-SBER51"),
    RU000A10DY50("RU000A10DY50", "3", "Сережа 003P-08R"),
    RU000A10D3S6("RU000A10D3S6", "23", "Селигдар 001P-06"),
    RU000A1099V8("RU000A1099V8", "28", "Совкомбанк Лизинг БО-П07"),
    RU000A10EA08("RU000A10EA08", "11", "Софтлайн 002P-02"),
    RU000A10CKS5("RU000A10CKS5", "35", "СФО Альфа Фабрика ПК-1 ..."),
    RU000A10CW87("RU000A10CW87", "20", "СФО ВТБ РКС Эталон 06"),
    RU000A108BX7("RU000A108BX7", "25", "Татнефтехим 001P-02"),
    RU000A109E71("RU000A109E71", "20", "ТрансКонтейнер П02-01");

    @Getter
    public final String code;

    @Getter
    public final String count;

    @Getter
    public final String codeWithName;

    PaulBonds(String code, String count, String codeWithName) {
        this.code = code;
        this.count = count;
        this.codeWithName = codeWithName;
    }
}
