package ru.paul.myservice.client;

import ru.paul.myservice.dto.BondsResponse;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class BondMerger {

    /**
     * Объединяет два списка облигаций по secId
     * @param list1 первый список
     * @param list2 второй список
     * @return объединенный список
     */
    public static List<BondsResponse> mergeBonds(List<BondsResponse> list1, List<BondsResponse> list2) {
        if (list1 == null || list1.isEmpty()) {
            return list2 != null ? new ArrayList<>(list2) : new ArrayList<>();
        }
        if (list2 == null || list2.isEmpty()) {
            return new ArrayList<>(list1);
        }

        // Создаем карту для быстрого доступа: secId -> BondsResponse из первого списка
        Map<String, BondsResponse> map1 = list1.stream()
                .filter(b -> b.getSecId() != null)
                .collect(Collectors.toMap(
                        BondsResponse::getSecId,
                        b -> b,
                        (existing, replacement) -> existing
                ));

        // Создаем карту для второго списка
        Map<String, BondsResponse> map2 = list2.stream()
                .filter(b -> b.getSecId() != null)
                .collect(Collectors.toMap(
                        BondsResponse::getSecId,
                        b -> b,
                        (existing, replacement) -> existing
                ));

        // Собираем все уникальные secId
        Set<String> allSecIds = new HashSet<>(map1.keySet());
        allSecIds.addAll(map2.keySet());

        List<BondsResponse> result = new ArrayList<>();

        for (String secId : allSecIds) {
            BondsResponse bond1 = map1.get(secId);
            BondsResponse bond2 = map2.get(secId);

            if (bond1 == null) {
                result.add(copyBond(bond2));
            } else if (bond2 == null) {
                result.add(copyBond(bond1));
            } else {
                // Оба есть — объединяем
                result.add(mergeTwoBonds(bond1, bond2));
            }
        }

        return result;
    }

    /**
     * Объединяет два объекта BondsResponse по полям
     */
    private static BondsResponse mergeTwoBonds(BondsResponse b1, BondsResponse b2) {
        BondsResponse result = new BondsResponse();

        // Получаем все поля класса
        Field[] fields = BondsResponse.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object v1 = field.get(b1);
                Object v2 = field.get(b2);

                Object mergedValue = mergeValue(v1, v2);
                field.set(result, mergedValue);

            } catch (IllegalAccessException e) {
                // Игнорируем
            }
        }

        return result;
    }

    /**
     * Объединяет два значения по правилам:
     * - если одинаковые — берем любое
     * - если null и не null — берем не null
     * - если 0 и не 0 — берем не 0
     * - если разные — объединяем через "; "
     */
    @SuppressWarnings("unchecked")
    private static Object mergeValue(Object v1, Object v2) {
        // Оба null
        if (v1 == null && v2 == null) {
            return null;
        }

        // Один null
        if (v1 == null) {
            return v2;
        }
        if (v2 == null) {
            return v1;
        }

        // Оба не null
        // Проверяем на равенство
        if (v1.equals(v2)) {
            return v1;
        }

        // Специальная обработка для чисел: 0 vs не 0
        if (v1 instanceof Number && v2 instanceof Number) {
            double d1 = ((Number) v1).doubleValue();
            double d2 = ((Number) v2).doubleValue();

            // Если одно 0, а другое нет — берем не 0
            if (d1 == 0 && d2 != 0) {
                return v2;
            }
            if (d2 == 0 && d1 != 0) {
                return v1;
            }

            // Если разные и оба не 0 — объединяем
            return v1 + "; " + v2;
        }

        // Для строк и других объектов — объединяем через "; "
        if (v1 instanceof String && v2 instanceof String) {
            String s1 = (String) v1;
            String s2 = (String) v2;
            if (s1.isEmpty() && !s2.isEmpty()) {
                return s2;
            }
            if (s2.isEmpty() && !s1.isEmpty()) {
                return s1;
            }
        }

        // Все остальные случаи — объединяем через "; "
        return v1 + "; " + v2;
    }

    /**
     * Создает копию BondsResponse
     */
    private static BondsResponse copyBond(BondsResponse source) {
        if (source == null) {
            return null;
        }
        BondsResponse copy = new BondsResponse();
        Field[] fields = BondsResponse.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                field.set(copy, field.get(source));
            } catch (IllegalAccessException e) {
                // Игнорируем
            }
        }
        return copy;
    }
}