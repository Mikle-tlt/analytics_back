package com.example.analytics_back.service;

import com.example.analytics_back.DTO.DetailsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.*;
import com.example.analytics_back.repo.*;
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

import static org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadFilesService {

    private final ProductsRepository productsRepository;
    private final UsersRepository usersRepository;
    private final PointsRepository pointsRepository;
    private final ClientsService clientsService;
    private final BuysRepository buysRepository;
    private final ClientsRepository clientsRepository;
    private final BuysService buysService;
    private final CategoriesRepository categoriesRepository;
    private final DetailsRepository detailsRepository;
    private final DetailsService detailsService;

    public void handleExcelFile(MultipartFile file, Long userId) throws IOException, CustomException, ParseException {
        Users user = usersRepository.getReferenceById(userId);
        InputStream inputStream = file.getInputStream();
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Предполагается, что данные находятся на первом листе
            Iterator<Row> rowIterator = sheet.iterator();

            int numberOfRow = 0;
            boolean hasEmptyCell = false;

            while (rowIterator.hasNext()) {
                numberOfRow++;
                Row row = rowIterator.next();
                if (isRowEmpty(row) || hasEmptyCell) {
                    break;
                }
                Iterator<Cell> cellIterator = row.cellIterator();

                String clientName = null;
                String contactData = null;
                String pointAddress = null;
                String categoryName = null;
                Categories category = null;
                Points point = null;
                Date dateBuy = null;
                String formattedDate = null;
                String productName = null;
                Products product = null;
                Buys buy = null;
                int quantity = 0;
                double cost = 0;

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (isCellEmpty(cell)) {
                        hasEmptyCell = true;
                        System.out.println("колонки");
                        break;
                    }
                    int columnIndex = cell.getColumnIndex();

                    switch (columnIndex) {
                        case 0:
                            if (cell.getCellType() == CellType.STRING) {
                                categoryName = cell.getStringCellValue();
                                if (!categoriesRepository.existsByNameAndOwner(categoryName, user)) {
                                    throw new CustomException("Категория " + "\"" + categoryName + "\"" + " еще не содержится в базе данных! " +
                                            "Добавьте его и только потом импортируйте историю продаж, которая включает его");
                                }
                                category = categoriesRepository.findByNameAndOwner(categoryName, user);
                            } else {
                                throw new CustomException("Первая колонка содержит неверное значение в следующей строке: "
                                        + numberOfRow + ". Дальнейший импорт после этой строки был прекращен");
                            }
                            break;
                        case 1:
                            if (cell.getCellType() == CellType.STRING) {
                                productName = cell.getStringCellValue();
                                if (!productsRepository.existsByNameAndCategoryAndOwner(productName, category, user)) {
                                    throw new CustomException("Товар " + "\"" + productName + "\"" +
                                            "в категории еще не содержится в базе данных! " +
                                            "Добавьте его и только потом импортируйте историю продаж, которая включает его");
                                }
                                product = productsRepository.findByNameAndCategoryAndOwner(productName, category, user);
                            } else {
                                throw new CustomException("Первая колонка содержит неверное значение в следующей строке: "
                                        + numberOfRow + ". Дальнейший импорт после этой строки был прекращен");
                            }
                            break;
                        case 2:

                            if (cell.toString().trim().isEmpty()) {
                                System.out.println(cell.toString().trim().isEmpty());
                                throw new CustomException("Вторая колонка пуста в следующей строке: " + numberOfRow + ". Дальнейший импорт после этой строки был прекращен");
                            }
                            System.out.println(cell.toString().trim().isEmpty());
                            if (cell.getCellType() == CellType.STRING) {
                                pointAddress = cell.getStringCellValue();
                                System.out.println(pointAddress);
                                if (!pointsRepository.existsByAddressAndOwner(pointAddress, user)) {
                                    throw new CustomException("Пункт выдачи с адресом " + "\"" + pointAddress + "\"" +
                                            " еще не содержится в базе данных! Добавьте его и только потом импортируйте " +
                                            "историю продаж, которая связана с данным пунктом выдачи");
                                } else {
                                    point = pointsRepository.findByAddressAndOwner(pointAddress, user);
                                }
                            } else {
                                throw new CustomException("Вторя колонка содержит неверное значение в следующей строке: "
                                        + numberOfRow + ". Дальнейший импорт после этой строки был прекращен");
                            }
                            break;
                        case 3:
                            if (cell.getCellType() == CellType.STRING) {
                                contactData = cell.getStringCellValue();
                            } else {
                                throw new CustomException("Третья колонка содержит неверное значение в следующей строке: "
                                        + numberOfRow + ". Дальнейший импорт после этой строки был прекращен");
                            }
                            break;
                        case 4:
                            if (isCellDateFormatted(cell)) {
                                dateBuy = cell.getDateCellValue();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                formattedDate = dateFormat.format(dateBuy);
                            } else {
                                throw new CustomException("Четвертая колонка содержит неверное значение в следующей строке: "
                                        + numberOfRow + ". Дальнейший импорт после этой строки был прекращен");
                            }
                            break;
                        case 5:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                double numericValue = cell.getNumericCellValue();
                                quantity = (int) numericValue;
                            } else {
                                throw new CustomException("Пятая колонка содержит неверное значение в следующей строке: "
                                        + numberOfRow + ". Дальнейший импорт после этой строки был прекращен");
                            }
                            break;
                        case 6:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                cost = cell.getNumericCellValue();
                            } else {
                                throw new CustomException("Шестая колонка содержит неверное значение в следующей строке: "
                                        + numberOfRow + ". Дальнейший импорт после этой строки был прекращен");
                            }
                            break;
                        case 7:
                            if (cell.getCellType() == CellType.STRING) {
                                clientName = cell.getStringCellValue();
                            } else {
                                throw new CustomException("Седьмая колонка содержит неверное значение в следующей строке: "
                                        + numberOfRow + ". Дальнейший импорт после этой строки был прекращен");
                            }
                    }
                }

                //проверка, есть ли клиент с данными контактными данными, именем есть в базе менеджера
                //если нет, то добавляем его в базу
                Clients clients = null;
                if (!clientsRepository.existsByNameAndContactAndOwner(clientName, contactData, user)) {
                    clients = clientsService.clientAdd(clientName, contactData, userId);
                } else {
                    clients = clientsRepository.findByNameAndContactAndOwner(clientName, contactData, user);
                }

                //проверка, есть ли покупки с заданной датой и данного клиента
                //если нет, то добавляем ее в базу
                if (!buysRepository.existsByDateAndPointsAndClient(
                        new SimpleDateFormat("yyyy-MM-dd").parse(formattedDate),
                        point, clients)){
                    Buys buys = new Buys(formattedDate, point, clients);
                    if (compareDate(formattedDate)) {
                        throw new CustomException("Запрещено создавать покупку на будущую дату! Проверьте даты в документе!");
                    }
                    buy = buysRepository.save(buys);
                } else {
                    buy = buysRepository.findByDateAndPointsAndClient(new SimpleDateFormat("yyyy-MM-dd")
                            .parse(formattedDate), point, clients);
                }

                assert product != null;
                DetailsDTO detailsDTO = new DetailsDTO(product.getId(), quantity, cost);
                detailsService.detailAdd(buy.getId(), detailsDTO);
            }
        } catch (CustomException e) {
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private boolean isCellEmpty(Cell cell) {
        return cell == null || cell.getCellType() == CellType.BLANK;
    }
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (!isCellEmpty(cell)) {
                return false;
            }
        }
        return true;
    }
    public String getToday() {return new SimpleDateFormat("yyyy-MM-dd").format(new Date());}
    public boolean compareDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = sdf.parse(getToday());
        Date date2 = sdf.parse(date);
        return date2.after(date1);
    }
}
