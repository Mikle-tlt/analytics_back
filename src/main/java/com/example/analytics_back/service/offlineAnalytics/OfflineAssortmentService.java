package com.example.analytics_back.service.offlineAnalytics;

import com.example.analytics_back.model.OfflinePoints;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfflineAssortmentService {
    private final UsersService usersService;

    public Map<String, Object> getAssortment() {
        Users user = usersService.getUserInfo();
        List<OfflinePoints> offlines = user.getOfflinePoints();

        String[] points = new String[offlines.size()];
        int[] numberOfProducts = new int[offlines.size()];

        for (int i = 0; i < offlines.size(); i++) {
            points[i] = offlines.get(i).getName() + " (" + offlines.get(i).getAddress() + ", "
                    + offlines.get(i).getRegion().getName() + ")" ;
            numberOfProducts[i] = offlines.get(i).getOfflinePointProducts().stream()
                    .reduce(0, (op, offlineProduct) -> op + offlineProduct.getQuantityReal(), Integer::sum);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("points", points);
        resultMap.put("numbers", numberOfProducts);
        return resultMap;
    }
}
