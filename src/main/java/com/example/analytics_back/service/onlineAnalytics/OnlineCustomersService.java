package com.example.analytics_back.service.onlineAnalytics;

import com.example.analytics_back.model.Clients;
import com.example.analytics_back.service.ClientsService;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OnlineCustomersService {
    private final ClientsService clientsService;

    public Map<String, Object> getCustomersAnalytics(int year) {
        List<Clients> clients = clientsService.getClients();
        int[][] customers = new int[12][4];

        List<Clients> clientsWithBuys = clients.stream()
                .filter(client -> client.getDate() != null)
                .toList();

        int total = clientsWithBuys.stream().reduce(0, (c, client) -> {
            String date = client.getDate().toString();
            int i = Integer.parseInt(date.substring(0, 4));
            if (i < year) return c + 1;
            return c;
        }, Integer::sum);

        for (int i = 0; i < customers.length; i++) {
            String date;
            if (i < 9) date = year + "-0" + (i + 1);
            else date = year + "-" + (i + 1);

            customers[i][0] = i + 1;

            total += clientsWithBuys.stream().reduce(0, (c, client) -> {
                if (client.getDate().toString().startsWith(date)) return c + 1;
                return c;
            }, Integer::sum);

            customers[i][1] = total;

            customers[i][2] = clientsWithBuys.stream().reduce(0, (c, client) -> {
                int buys = client.getBuys().stream().reduce(0, (b, buy) -> {
                    if (buy.getDate().startsWith(date)) return b + 1;
                    return b;
                }, Integer::sum);
                return buys > 0 ? c + 1 : c;
            }, Integer::sum);

            if (customers[i][2] == 0) customers[i][3] = 0;
            else customers[i][3] = (customers[i][2] * 100) / customers[i][1];
        }

        String[] arrayOfMonths = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль",
                "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

        boolean isEmpty = true;
        for (int i = 0; i < customers.length; i++) {
            if (customers[i][3] > 0) {
                isEmpty = false;
                break;
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("month", arrayOfMonths);
        resultMap.put("customers", customers);
        resultMap.put("labelText", generateDescription(Arrays.copyOf(customers, customers.length), isEmpty, year));
        return resultMap;
    }

    private String generateDescription(int[][] array, boolean mark, int year){
        if (mark) return "В выбранном году ("+ year +") клиенты не были зарегистрированы";
        Arrays.sort(array, (a, b) -> Integer.compare(b[3], a[3]));
        int[][] resultArray = Arrays.copyOfRange(array, 0, 4);
        StringBuilder bestMonth = new StringBuilder();
        for (int i = 0; i < 4; i ++) {
            bestMonth.append(getMonthsByInt(resultArray[i][0])).append("; ");
        }
        return "Результаты анализа показывают, что в следующих месяцах эффективность проработки " +
                "клиентской базы была самая эффективная: " + bestMonth;
    }

    private String getMonthsByInt(int key){
        switch (key){
            case 1:
                return "Январь";
            case 2:
                return "Февраль";
            case 3:
                return "Март";
            case 4:
                return "Апрель";
            case 5:
                return "Май";
            case 6:
                return "Июнь";
            case 7:
                return "Июль";
            case 8:
                return "Август";
            case 9:
                return "Сентябрь";
            case 10:
                return "Октябрь";
            case 11:
                return "Ноябрь";
            case 12:
                return "Декабрь";
        }
        return "";
    }
}