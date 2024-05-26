package com.example.analytics_back.service.files;

import com.example.analytics_back.DTO.OfflineDetailsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.*;
import com.example.analytics_back.repo.*;
import com.example.analytics_back.service.OfflineDetailsService;
import com.example.analytics_back.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfflineFileImport {
    private final UsersService usersService;
    private final CategoriesRepository categoriesRepository;
    private final ProductsRepository productsRepository;
    private final OfflinePointsRepository offlinePointsRepository;
    private final RegionsRepository regionsRepository;
    private final OfflineBuysRepository offlineBuysRepository;
    private final OfflineDetailsService offlineDetailsService;
    private final OfflinePointProductsRepository offlinePointProductsRepository;
    public void handleImportExcelFile(MultipartFile file) throws IOException, CustomException, ParseException {
        Users user = usersService.getUserInfo();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        InputStream inputStream = file.getInputStream();
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (isRowEmpty(row)) {
                    break;
                }
                try {
                    String pointAddress = getCellValueAsString(row.getCell(4), 4);

                    String pointRegion = getCellValueAsString(row.getCell(5), 5);

                    OfflinePoints offlinePoint = getOfflinePoint(pointAddress, pointRegion, user);

                    Categories category = getCategory(row.getCell(0).getStringCellValue(), user);

                    OfflinePointProducts offlinePointProducts =
                            getProduct(row.getCell(1).getStringCellValue(), category, user, offlinePoint);

                    int quantity = getCellValueAsInteger(row.getCell(2), 2);
                    double cost = getCellValueAsDouble(row.getCell(3), 3);
                    String buyDate = dateFormat.format(getCellValueAsDate(row.getCell(6), 6));

                    OfflineBuys offlineBuy = getOfflineBuy(buyDate, offlinePoint);

                    OfflineDetailsDTO offlineDetailsDTO = new OfflineDetailsDTO(offlinePointProducts.getId(), quantity, cost);
                    offlineDetailsService.offlineDetailAdd(offlineBuy.getId(), offlineDetailsDTO);
                } catch (CustomException | IllegalArgumentException e) {
                    throw new CustomException("Ошибка в строке " + row.getRowNum() + 1 + ": " + e.getMessage());
                }
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public Categories getCategory(String categoryName, Users user) throws CustomException {
        if (!categoriesRepository.existsByNameAndOwner(categoryName, user)) {
            throw new CustomException("Категория " + "\"" + categoryName + "\"" + " еще не содержится в базе данных! " +
                    "Добавьте её и только потом импортируйте историю продаж, которая включает его");
        }
        return categoriesRepository.findByNameAndOwner(categoryName, user);
    }
    public OfflinePointProducts getProduct(String productName, Categories category,  Users user, OfflinePoints offlinePoint)
            throws CustomException {
        if (!productsRepository.existsByNameAndCategoryAndOwner(productName, category, user)) {
            throw new CustomException("Товар " + "\"" + productName + "\"" +
                    "в категории " + "\"" + category.getName() + "\"" + " еще не содержится в базе данных! " +
                    "Добавьте его и только потом импортируйте историю продаж, которая включает его");
        }
        Products product = productsRepository.findByNameAndCategoryAndOwner(productName, category, user);
        if (!offlinePointProductsRepository.existsByOfflinePointsAndProductId(offlinePoint, product.getId())) {
            throw new CustomException("Товара " + "\"" + productName + "\"" +
                    "в категории " + "\"" + category.getName() + "\"" + " нет в наличии! " +
                    "Добавьте его и только потом импортируйте историю продаж, которая включает его");
        }
        return offlinePointProductsRepository.findByOfflinePointsAndProductId(offlinePoint, product.getId());
    }
    public Regions getRegion(String regionName, Users user) throws CustomException {
        if (!regionsRepository.existsByNameAndOwner(regionName, user)) {
            throw new CustomException("Регион " + "\"" + regionName + "\"" +
                    " еще не содержится в базе данных! Добавьте его и " +
                    "только потом импортируйте историю продаж, которая связана с данным пунктом выдачи");
        }
        return regionsRepository.findRegionsByNameAndOwner(regionName, user);
    }
    public OfflinePoints getOfflinePoint(String pointAddress, String pointRegion,  Users user)
            throws CustomException {
        if (!offlinePointsRepository.existsByAddressAndRegionAndOwner(pointAddress, getRegion(pointRegion, user), user)) {
            throw new CustomException("Оффлайн точка по адресу " + "\"" + pointAddress + "\"" + " в регионе " +
                    "\"" + pointRegion + "\"" + " еще не содержится в базе данных! Добавьте его и " +
                    "только потом импортируйте историю продаж, которая связана сс данным пунктом выдачи");
        }
        return offlinePointsRepository.findByAddressAndRegionAndOwner(pointAddress, getRegion(pointRegion, user), user);
    }

    public OfflineBuys getOfflineBuy(String date, OfflinePoints offlinePoint) throws ParseException, CustomException {
        if (compareDate(getToday(), date)) {
            throw new CustomException("Запрещено создавать продажу на будущую дату!");
        }
        OfflineBuys offlineBuys = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (!offlineBuysRepository.existsByDateAndOfflinePoints(dateFormat.parse(date), offlinePoint)){
            OfflineBuys addedOfflineBuy = new OfflineBuys(date, offlinePoint);
            offlineBuys = offlineBuysRepository.save(addedOfflineBuy);
        } else {
            offlineBuys = offlineBuysRepository.findByDateAndOfflinePoints(dateFormat.parse(date), offlinePoint);
        }
        return offlineBuys;
    }
    public String getToday() {return new SimpleDateFormat("yyyy-MM-dd").format(new Date());}
    public boolean compareDate(String today, String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = sdf.parse(today);
        Date date2 = sdf.parse(date);
        return date2.after(date1);
    }

    private String getCellValueAsString(Cell cell, int cellIndex) throws CustomException {
        int cellNumber = cellIndex + 1;
        if (cell == null || cell.getCellType() != CellType.STRING) {
            throw new CustomException("и столбце " + cellNumber + ": Значение должно быть строкой");
        }
        return cell.getStringCellValue();
    }

    private int getCellValueAsInteger(Cell cell, int cellIndex) throws CustomException {
        int cellNumber = cellIndex + 1;
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            throw new CustomException("и столбце " + cellNumber + ": Значение должно быть числом");
        }
        return (int) cell.getNumericCellValue();
    }

    private double getCellValueAsDouble(Cell cell, int cellIndex) throws CustomException {
        int cellNumber = cellIndex + 1;
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            throw new CustomException("и столбце " + cellNumber + ": Значение должно быть числом");
        }
        return cell.getNumericCellValue();
    }

    private Date getCellValueAsDate(Cell cell, int cellIndex) throws CustomException {
        int cellNumber = cellIndex + 1;
        if (cell == null || cell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(cell)) {
            throw new CustomException("и столбце " + cellNumber + ": Значение должно быть датой");
        }
        return cell.getDateCellValue();
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }
}
