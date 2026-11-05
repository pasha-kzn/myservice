package ru.paul.myservice.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.paul.myservice.dto.BondsResponse;
import ru.paul.myservice.enums.PaulBonds;
import ru.paul.myservice.enums.VeraBonds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ExcelExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FILE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    private File dataDir;

    @PostConstruct
    public void init() {
        this.dataDir = new File(dataDirectory);

        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (created) {
                log.info("Папка data создана: {}", dataDir.getAbsolutePath());
            } else {
                log.error("Не удалось создать папку data: {}", dataDir.getAbsolutePath());
            }
        } else {
            log.info("Папка data существует: {}", dataDir.getAbsolutePath());
        }
    }

    /**
     * Экспортирует список облигаций в Excel файл в папку data
     *
     * @param bonds список облигаций
     * @return абсолютный путь к созданному файлу
     */
    public String   exportBondsToExcel(List<BondsResponse> bonds) {
        String fileName = "bonds_" + LocalDateTime.now().format(FILE_DATE_TIME_FORMATTER) + ".xlsx";
        File file = new File(dataDir, fileName);

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            Sheet sheet = workbook.createSheet("Облигации");

            // Создаем стиль для заголовков
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Создаем заголовки
            createHeaderRow(sheet, headerStyle);

            // Заполняем данными
            int rowNum = 1;
            for (BondsResponse bond : bonds) {
                Row row = sheet.createRow(rowNum++);
                fillRowWithData(row, bond);
            }

            // Автоматически подгоняем ширину колонок
            autoSizeColumns(sheet);

            // Сохраняем файл
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }

            log.info("Excel файл успешно создан: {}", file.getAbsolutePath());
            return file.getAbsolutePath();

        } catch (IOException e) {
            log.error("Ошибка при создании Excel файла: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать Excel файл", e);
        }
    }

    /**
     * Создает стиль для заголовков
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // Жирный шрифт
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);

        // Заливка фона
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Границы
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // Выравнивание
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    /**
     * Создает строку с заголовками
     */
    private void createHeaderRow(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Код", "Код режима торгов", "Краткое имя", "Имя", "Полное имя", "ISIN", "регистрационный номер",
                "латинское наименование", "ПРИОСТАНОВЛЕН ЛИ ЛИСТИНГ", "EMITENT_NAME", "ИНН",
                "НОМИНАЛ", "Валюта", "объем выпуска", "размещенный объем", "стоимость лота", "размер лота",
                "КУПОН, %", "РАЗМЕР КУПОНА", "ПЕРИОД КУПОНА В ДНЯХ", "дата следующей выплаты купона", "НКД",
                "ДАТА ПОГАШЕНИЯ", "дата предыдущей сделки", "дата оферты", "дата оферты (альтернативная)",
                "дата расчетов", "дата call-опциона", "дата put-опциона", "DATEYIELDFROMISSUER",
                "средневзвешенная цена", "ДОХОДНОСТЬ, %", "цена последней сделки", "юридическая цена закрытия",
                "ДОХОДНОСТЬ К ПОГАШЕНИЮ", "HIGH_RISK", "Является ли бумага обеспечением", "Цена последней сделки в процентах от номинала",
                "Цена последней сделки в рублях (с учетом НКД)", "Средневзвешенная цена за сессию (в % от номинала)",
                "Статус", "ТИП", "Биржа", "код рынка", "код сектора", "тип бумаги", "подтип облигации",
                "валюта", "УРОВЕНЬ ЛИСТИНГА", "количество знаков после запятой", "минимальный шаг цены",
                "примечания", "идентификатор инструмента", "цена оферты", "FACEVALUEONSETTLEDATE",
                "КВАЛ ОНЛИ", "Паша количество", "Паша сумма", "Вера количество", "Вера сумма"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * Заполняет строку данными облигации
     */
    private void fillRowWithData(Row row, BondsResponse bond) {
        int col = 0;

        // Основные идентификаторы
        row.createCell(col++).setCellValue(nullSafe(bond.getSecId()));
        row.createCell(col++).setCellValue(nullSafe(bond.getBoardId()));
        row.createCell(col++).setCellValue(nullSafe(bond.getShortName()));
        row.createCell(col++).setCellValue(nullSafe(bond.getName()));
        row.createCell(col++).setCellValue(nullSafe(bond.getSecName()));
        row.createCell(col++).setCellValue(nullSafe(bond.getIsin()));
        row.createCell(col++).setCellValue(nullSafe(bond.getRegNumber()));
        row.createCell(col++).setCellValue(nullSafe(bond.getLatName()));
        row.createCell(col++).setCellValue(nullSafe(bond.getSuspensionListing()));
        row.createCell(col++).setCellValue(nullSafe(bond.getEmitentName()));
        row.createCell(col++).setCellValue(nullSafe(bond.getInn()));

        // Номинал и валюта
        row.createCell(col++).setCellValue(nullSafe(bond.getFaceValue()));
        row.createCell(col++).setCellValue(nullSafe(bond.getFaceUnit()));
        row.createCell(col++).setCellValue(nullSafe(bond.getIssueSize()));
        row.createCell(col++).setCellValue(nullSafe(bond.getIssueSizePlaced()));
        row.createCell(col++).setCellValue(nullSafe(bond.getLotValue()));
        row.createCell(col++).setCellValue(nullSafe(String.valueOf(bond.getLotSize())));

        // Купонные параметры
        row.createCell(col++).setCellValue(nullSafe(bond.getCouponPercent()));
        row.createCell(col++).setCellValue(nullSafe(bond.getCouponValue()));
        row.createCell(col++).setCellValue(nullSafe(String.valueOf(bond.getCouponPeriod())));
        row.createCell(col++).setCellValue(nullSafe(bond.getNextCoupon()));
        row.createCell(col++).setCellValue(nullSafe(bond.getAccruedInt()));

        // Даты
        row.createCell(col++).setCellValue(bond.getMatDate());
        row.createCell(col++).setCellValue(bond.getPrevDate());
        row.createCell(col++).setCellValue(bond.getBuybackDate());
        row.createCell(col++).setCellValue(bond.getOfferDate());
        row.createCell(col++).setCellValue(bond.getSettleDate());
        row.createCell(col++).setCellValue(bond.getCallOptionDate());
        row.createCell(col++).setCellValue(bond.getPutOptionDate());
        row.createCell(col++).setCellValue(bond.getDateYieldFromIssuer());

        // Объем и доходность
        row.createCell(col++).setCellValue(nullSafe(bond.getPrevWapPrice()));
        row.createCell(col++).setCellValue(nullSafe(bond.getYieldAtPrevWapPrice()));
        row.createCell(col++).setCellValue(nullSafe(bond.getPrevPrice()));
        row.createCell(col++).setCellValue(nullSafe(bond.getPrevLegalClosePrice()));
        row.createCell(col++).setCellValue(nullSafe(bond.getYieldAtWap()));
        row.createCell(col++).setCellValue(nullSafe(String.valueOf(bond.getHighRisk())));
        row.createCell(col++).setCellValue(nullSafe(String.valueOf(bond.getIsCollateral())));
        row.createCell(col++).setCellValue(nullSafe(bond.getPrice()));
        row.createCell(col++).setCellValue(nullSafe(bond.getPriceRub()));
        row.createCell(col++).setCellValue(nullSafe(bond.getWaprice()));

        // Статусы и классификация
        row.createCell(col++).setCellValue(nullSafe(bond.getStatus()));
        row.createCell(col++).setCellValue(nullSafe(bond.getBondType()));
        row.createCell(col++).setCellValue(nullSafe(bond.getBoardName()));
        row.createCell(col++).setCellValue(nullSafe(bond.getMarketCode()));
        row.createCell(col++).setCellValue(nullSafe(bond.getSectorId()));
        row.createCell(col++).setCellValue(nullSafe(bond.getSecType()));
        row.createCell(col++).setCellValue(nullSafe(bond.getBondSubType()));
        row.createCell(col++).setCellValue(nullSafe(bond.getCurrencyId()));
        row.createCell(col++).setCellValue(nullSafe(String.valueOf(bond.getListLevel())));
        row.createCell(col++).setCellValue(nullSafe(String.valueOf(bond.getDecimals())));
        row.createCell(col++).setCellValue(nullSafe(bond.getMinStep()));
        row.createCell(col++).setCellValue(nullSafe(bond.getRemarks()));
        row.createCell(col++).setCellValue(nullSafe(bond.getInstrId()));
        row.createCell(col++).setCellValue(nullSafe(bond.getBuybackPrice()));
        row.createCell(col++).setCellValue(nullSafe(bond.getFaceValueOnSettleDate()));

        // Доступность для инвесторов
        row.createCell(col++).setCellValue(nullSafe(String.valueOf(bond.getIsQualifiedInvestors())));

        //Паша
        row.createCell(col++).setCellValue(nullSafe(getPaulBondsCount(bond.getIsin())));
        col++;

        //Вера
        row.createCell(col++).setCellValue(nullSafe(getVeraBondsCount(bond.getIsin())));
        col++;
    }

    private String getPaulBondsCount(String isin) {
        Optional<PaulBonds> optionalValue = Arrays.stream(PaulBonds.values()).toList().stream()
                .filter(v -> isin.equals(v.getCode()))
                .findFirst();
        return optionalValue.isPresent() ? optionalValue.get().getCount() : "";
    }

    private String getVeraBondsCount(String isin) {
        Optional<VeraBonds> optionalValue = Arrays.stream(VeraBonds.values()).toList().stream()
                .filter(v -> isin.equals(v.getCode()))
                .findFirst();
        return optionalValue.isPresent() ? optionalValue.get().getCount() : "";
    }

    /**
     * Автоматически подгоняет ширину колонок под содержимое
     */
    private void autoSizeColumns(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow != null) {
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
                // Ограничиваем ширину
                int width = sheet.getColumnWidth(i);
                if (width > 20000) {
                    sheet.setColumnWidth(i, 20000);
                }
                if (width < 4000) {
                    sheet.setColumnWidth(i, 4000);
                }
            }
        }
    }

    // --- Вспомогательные методы для null-безопасности ---

    private String nullSafe(String value) {
        return value != null ? value : "";
    }

    private double nullSafe(Double value) {
        return value != null ? value : 0.0;
    }

    private long nullSafe(Long value) {
        return value != null ? value : 0L;
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }
}