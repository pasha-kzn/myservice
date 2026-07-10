package ru.paul.myservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.paul.myservice.dto.BondsResponse;
import ru.paul.myservice.service.BondExportService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bonds")
@RequiredArgsConstructor
@Tag(name = "Облигации", description = "API для работы с облигациями Московской биржи")
public class BondController {

    private final BondExportService bondExportService;

    @Operation(
            summary = "Получить все облигации",
            description = "Загружает все облигации с Московской биржи и возвращает в формате JSON"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список облигаций успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BondsResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка при загрузке данных")
    })
    @GetMapping("/all")
    public ResponseEntity<List<BondsResponse>> getAllBonds() {
        log.info("📥 Запрос на получение всех облигаций");
        try {
            List<BondsResponse> bonds = bondExportService.loadAllBonds();
            return ResponseEntity.ok(bonds);
        } catch (Exception e) {
            log.error("❌ Ошибка при загрузке облигаций: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Экспортировать облигации в Excel",
            description = "Загружает все облигации с Московской биржи и сохраняет в Excel файл в папку data"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Excel файл успешно создан"),
            @ApiResponse(responseCode = "500", description = "Ошибка при создании Excel файла")
    })
    @PostMapping("/export/excel")
    public ResponseEntity<String> exportToExcel() {
        log.info("📊 Запрос на экспорт облигаций в Excel");
        try {
            String filePath = bondExportService.exportBondsToExcel();
            return ResponseEntity.ok("✅ Excel файл создан: " + filePath);
        } catch (Exception e) {
            log.error("❌ Ошибка при экспорте: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("❌ Ошибка: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Скачать Excel файл",
            description = "Скачивает последний созданный Excel файл с облигациями"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл успешно скачан"),
            @ApiResponse(responseCode = "404", description = "Файл не найден")
    })
    @GetMapping("/download/excel")
    public ResponseEntity<byte[]> downloadExcel() throws IOException {
        log.info("📥 Запрос на скачивание Excel файла");
        String latestFile = bondExportService.findLatestExcelFile();

        if (latestFile == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(latestFile);
        byte[] fileBytes = Files.readAllBytes(path);

        String fileName = path.getFileName().toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(fileBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileBytes);
    }

    @Operation(
            summary = "Получить облигации с фильтром",
            description = "Загружает все облигации и фильтрует их по заданным параметрам"
    )
    @GetMapping("/filter")
    public ResponseEntity<List<BondsResponse>> getFilteredBonds(
            @Parameter(description = "Только для неквалифицированных инвесторов (true/false)")
            @RequestParam(required = false) Boolean forNonQualified,

            @Parameter(description = "Минимальная доходность в %")
            @RequestParam(required = false) Double minYield,

            @Parameter(description = "Максимальная доходность в %")
            @RequestParam(required = false) Double maxYield,

            @Parameter(description = "Тип облигации")
            @RequestParam(required = false) String bondType) {

        log.info("🔍 Запрос с фильтрами: forNonQualified={}, minYield={}, maxYield={}, bondType={}",
                forNonQualified, minYield, maxYield, bondType);

        try {
            List<BondsResponse> bonds = bondExportService.loadAllBonds();

            // Фильтрация
            if (forNonQualified != null && forNonQualified) {
                bonds = bonds.stream()
                        .filter(b -> b.getIsQualifiedInvestors() != null && b.getIsQualifiedInvestors() == 0)
                        .collect(java.util.stream.Collectors.toList());
            }

            if (minYield != null) {
                bonds = bonds.stream()
                        .filter(b -> b.getYieldAtWap() != null && b.getYieldAtWap() >= minYield)
                        .collect(java.util.stream.Collectors.toList());
            }

            if (maxYield != null) {
                bonds = bonds.stream()
                        .filter(b -> b.getYieldAtWap() != null && b.getYieldAtWap() <= maxYield)
                        .collect(java.util.stream.Collectors.toList());
            }

            if (bondType != null && !bondType.isEmpty()) {
                bonds = bonds.stream()
                        .filter(b -> b.getBondType() != null && b.getBondType().contains(bondType))
                        .collect(java.util.stream.Collectors.toList());
            }

            log.info("✅ Найдено {} облигаций по фильтрам", bonds.size());
            return ResponseEntity.ok(bonds);

        } catch (Exception e) {
            log.error("❌ Ошибка при фильтрации: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
