package ru.paul.myservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;
import ru.paul.myservice.client.MoexSecuritiesClient;
import ru.paul.myservice.dto.BondsResponse;
import ru.paul.myservice.service.ExcelExportService;

import java.util.List;

public class GetBonds {
    private static final Logger log = LoggerFactory.getLogger(GetBonds.class);

    public static RestClient restClient = RestClient.builder()
            .baseUrl("https://iss.moex.com")
            .defaultHeader("Accept", "application/json")
            .defaultHeader("User-Agent", "Mozilla/5.0")
            .build();
    public static MoexSecuritiesClient moexClient = new MoexSecuritiesClient(restClient);
    public static ExcelExportService excelService = new ExcelExportService();

    private static void getAllBonds() {
        log.info("🚀 Ручной запуск загрузки данных с Московской биржи...");

        try {// 3. Получаем данные
            List<BondsResponse> bonds = moexClient.getAllBonds();
            if (bonds.isEmpty()) {
                log.warn("⚠️ Не удалось загрузить облигации. Проверьте соединение с MOEX.");
                return;
            }
            log.info("✅ Получено {} облигаций", bonds.size());

            // 4. Сохраняем в Excel
            String filePath = excelService.exportBondsToExcel(bonds);
            log.info("✅ Готово! Файл сохранен: {}", filePath);

        } catch (Exception e) {
            log.error("❌ Ошибка при выполнении: {}", e.getMessage(), e);
        }
    }

    private static void getBondsWithParams() {
        log.info("🚀 Ручной запуск загрузки данных с Московской биржи...");

        try {// 3. Получаем данные
            List<BondsResponse> bonds = moexClient.getAllBonds();
            if (bonds.isEmpty()) {
                log.warn("⚠️ Не удалось загрузить облигации. Проверьте соединение с MOEX.");
                return;
            }
            log.info("✅ Получено {} облигаций", bonds.size());

            // 4. Сохраняем в Excel
            String filePath = excelService.exportBondsToExcel(bonds);
            log.info("✅ Готово! Файл сохранен: {}", filePath);

        } catch (Exception e) {
            log.error("❌ Ошибка при выполнении: {}", e.getMessage(), e);
        }
    }
}
