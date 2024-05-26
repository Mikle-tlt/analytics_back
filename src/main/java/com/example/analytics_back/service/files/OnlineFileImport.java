package com.example.analytics_back.service.files;

import com.example.analytics_back.DTO.DetailsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.*;
import com.example.analytics_back.repo.*;
import com.example.analytics_back.service.ClientsService;
import com.example.analytics_back.service.DetailsService;
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
public class OnlineFileImport {
    private final ProductsRepository productsRepository;
    private final PointsRepository pointsRepository;
    private final RegionsRepository regionsRepository;
    private final ClientsService clientsService;
    private final BuysRepository buysRepository;
    private final ClientsRepository clientsRepository;
    private final CategoriesRepository categoriesRepository;
    private final DetailsService detailsService;
    private final UsersService usersService;

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
                    Categories category = getCategory(row.getCell(0).getStringCellValue(), user);
                    Products product = getProduct(row.getCell(1).getStringCellValue(), category, user);

                    Regions region  = getRegion(row.getCell(3).getStringCellValue(), user);
                    Points points = getPoint(row.getCell(2).getStringCellValue(), user, region);
                    String contactData = getCellValueAsString(row.getCell(4), 4);
                    String buyDate = dateFormat.format(getCellValueAsDate(row.getCell(5), 5));
                    int quantity = getCellValueAsInteger(row.getCell(6), 6);
                    double cost = getCellValueAsDouble(row.getCell(7), 7);
                    String clientName = getCellValueAsString(row.getCell(8), 8);

                    Clients client = getClient(clientName, contactData, user);
                    Buys buy = getBuy(buyDate, points, client);

                    DetailsDTO detailsDTO = new DetailsDTO(product.getId(), quantity, cost);
                    detailsService.buyDetailAdd(buy.getId(), detailsDTO);
                } catch (CustomException | IllegalArgumentException e) {
                    throw new CustomException("Ошибка в строке " + row.getRowNum() + ": " + e.getMessage());
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
    public Products getProduct(String productName, Categories category,  Users user) throws CustomException {
        if (!productsRepository.existsByNameAndCategoryAndOwner(productName, category, user)) {
            throw new CustomException("Товар " + "\"" + productName + "\"" +
                    "в категории " + "\"" + category.getName() + "\"" + " еще не содержится в базе данных! " +
                    "Добавьте его и только потом импортируйте историю продаж, которая включает его");
        }
        return productsRepository.findByNameAndCategoryAndOwner(productName, category, user);
    }

    public Regions getRegion(String region, Users user) throws CustomException {
        if (!regionsRepository.existsByNameAndOwner(region, user)) {
            throw new CustomException("Регион " + "\"" + region + "\"" +
                    " еще не содержится в базе данных! Добавьте его и только потом импортируйте " +
                    "историю продаж, которая связана с регионом");
        }
        return regionsRepository.findRegionsByNameAndOwner(region, user);
    }
    public Points getPoint(String pointAddress, Users user, Regions region) throws CustomException {
        if (!pointsRepository.existsByAddressAndRegionAndOwner(pointAddress, region, user)) {
            throw new CustomException("Пункт выдачи с адресом " + "\"" + pointAddress + "\"" +
                    " еще не содержится в базе данных! Добавьте его и только потом импортируйте " +
                    "историю продаж, которая связана с данным пунктом выдачи");
        }
        return pointsRepository.findPointsByAddressAndRegionAndOwner(pointAddress, region, user);
    }
    public Clients getClient(String clientName, String contactData, Users manager) throws CustomException {
        Clients customer = null;
        if (!clientsRepository.existsByNameAndContactAndOwner(clientName, contactData, manager)) {
            Clients addedClient = new Clients(clientName, contactData);
            customer = clientsService.clientAdd(addedClient);
        } else {
            customer = clientsRepository.findByNameAndContactAndOwner(clientName, contactData, manager);
        }
        return customer;
    }
    public Buys getBuy(String date, Points point, Clients client) throws ParseException, CustomException {
        if (compareDate(getToday(), date)) {
            throw new CustomException("Запрещено создавать покупку на будущую дату!");
        }
        Buys buy = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (!buysRepository.existsByDateAndPointsAndClient(dateFormat.parse(date), point, client)){
            Buys addedBuy = new Buys(date, point, client);
            if (client.getBuys() == null) {
                client.setDate(dateFormat.parse(date));
                clientsRepository.save(client);
            }
            buy = buysRepository.save(addedBuy);
        } else {
            buy = buysRepository.findByDateAndPointsAndClient(dateFormat.parse(date), point, client);
        }
        return buy;
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
