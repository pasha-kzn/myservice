package ru.paul.myservice.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import ru.paul.myservice.client.MoexSecuritiesClient;
import ru.paul.myservice.dto.BondsResponse;
import ru.paul.myservice.service.ExcelExportService;

import java.util.List;

@Slf4j
//@Component
public class DataLoaderRunner implements CommandLineRunner {

    private final MoexSecuritiesClient moexClient;
    private final ExcelExportService excelExportService;

    public DataLoaderRunner(MoexSecuritiesClient moexClient, ExcelExportService excelExportService) {
        this.moexClient = moexClient;
        this.excelExportService = excelExportService;
    }

    @Override
    public void run(String... args) {
        log.info("🚀 Запуск загрузки данных с Московской биржи...");

        try {
            // 1. Получаем все облигации
            List<BondsResponse> bonds = moexClient.getAllBonds();

            if (bonds.isEmpty()) {
                log.warn("⚠️ Не удалось загрузить облигации. Проверьте соединение с MOEX.");
                return;
            }

            // 2. Сохраняем в Excel
            String filePath = excelExportService.exportBondsToExcel(bonds);

            log.info("✅ Готово! Файл сохранен: {}", filePath);

        } catch (Exception e) {
            log.error("❌ Ошибка при выполнении: {}", e.getMessage(), e);
        }
    }
}
