package ru.paul.myservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.paul.myservice.client.MoexBondGridClient;
import ru.paul.myservice.client.MoexSecuritiesClient;
import ru.paul.myservice.dto.BondsResponse;

import java.io.File;
import java.util.List;

import static ru.paul.myservice.client.BondMerger.mergeBonds;

@Slf4j
@Service
@RequiredArgsConstructor
public class BondExportService {

    private final RestClient restClient;
    private final ExcelExportService excelExportService;
    private final MoexSecuritiesClient moexClient;

    /**
     * Загружает все облигации с Московской биржи
     */
    public List<BondsResponse> loadAllBonds() throws InterruptedException {
        MoexBondGridClient client = new MoexBondGridClient(restClient);
        return client.getAllBondsWithRate();
    }

    /**
     * Экспортирует облигации в Excel
     */
    public String exportBondsToExcel() throws InterruptedException {
        List<BondsResponse> bondsWithRate = loadAllBonds();
        List<BondsResponse> bondsFullInfo = moexClient.getAllBonds();
        bondsWithRate.addAll(bondsFullInfo);
        List<BondsResponse> bonds = mergeBonds(bondsWithRate, bondsFullInfo);
        return excelExportService.exportBondsToExcel(bonds);
    }

    /**
     * Находит последний созданный Excel файл
     */
    public String findLatestExcelFile() {
        String projectRoot = System.getProperty("user.dir");
        File dataDir = new File(projectRoot, "data");

        if (!dataDir.exists()) {
            return null;
        }

        File[] files = dataDir.listFiles((dir, name) ->
                name.startsWith("Облигации_") && name.endsWith(".xlsx"));

        if (files == null || files.length == 0) {
            return null;
        }

        File latest = files[0];
        for (File file : files) {
            if (file.lastModified() > latest.lastModified()) {
                latest = file;
            }
        }

        return latest.getAbsolutePath();
    }
}
