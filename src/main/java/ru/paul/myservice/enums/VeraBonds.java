package ru.paul.myservice.enums;

import lombok.Getter;

public enum VeraBonds {
    RU000A109TJ6("RU000A109TJ6", "25", "Банк ПСБ 003P-09"),
    RU000A10FVD7("RU000A10FVD7", "25", "ВТБ Лизинг 001P-МБ-07"),
    RU000A104Z48("RU000A104Z48", "30", "ВЭБ.РФ ПБО-002P-33"),
    RU000A10E747("RU000A10E747", "2", "ГК Сережа 003P-10R"),
    RU000A10B1Q6("RU000A10B1Q6", "3", "Джи-групп 002P-06"),
    RU000A109JW0("RU000A109JW0", "25", "КАМАЗ БО-П12"),
    RU000A10CCG7("RU000A10CCG7", "15", "Каршеринг Руссия 001P-07"),
    RU000A10DSC0("RU000A10DSC0", "40", "Кредитный поток 4.1"),
    RU000A10EAQ6("RU000A10EAQ6", "6", "КРОНОС БО-01"),
    RU000A1053H4("RU000A1053H4", "10", "Лизинг-Трейд 001P-05"),
    RU000A10BFP3("RU000A10BFP3", "18", "МВ Финанс 001P-06"),
    RU000A10DIA3("RU000A10DIA3", "75", "Нижегородская область 34017"),
    RU000A1082W2("RU000A1082W2", "15", "Новые Технологии БО-03"),
    RU000A10ES32("RU000A10ES32", "9", "ОДК 001P-01"),
    RU000A10CCN3("RU000A10CCN3", "19", "О'КЕЙ БО 001P-08"),
    RU000A1055Y4("RU000A1055Y4", "11", "Почта России БО-002P-04"),
    RU000A10CDZ5("RU000A10CDZ5", "20", "РЖД БО 001P-45R"),
    RU000A10ASE2("RU000A10ASE2", "5", "РОЛЬФ БО 001P-07"),
    RU000A106ZU6("RU000A106ZU6", "15", "РусГидро БО-П12"),
    RU000A10D3S6("RU000A10D3S6", "39", "Селигдар 001P-06"),
    RU000A1099V8("RU000A1099V8", "2", "Совкомбанк Лизинг БО-П07"),
    RU000A10EA08("RU000A10EA08", "8", "Софтлайн 002P-02"),
    RU000A10CKS5("RU000A10CKS5", "49", "СФО Альфа Фабрика ПК-1 класс А"),
    RU000A10CW87("RU000A10CW87", "57", "СФО ВТБ РКС Эталон 06"),
    RU000A10AAQ4("RU000A10AAQ4", "54", "СФО Сплит Финанс 1 01"),
    RU000A108BX7("RU000A108BX7", "25", "Татнефтехим 001P-02");

    @Getter
    private final String code;

    @Getter
    private final String count;

    @Getter
    private final String codeWithName;

    VeraBonds(String code, String count, String codeWithName) {
        this.code = code;
        this.count = count;
        this.codeWithName = codeWithName;
    }
}
