package ru.paul.myservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.paul.myservice.client.MoexSecuritiesClient;
import ru.paul.myservice.dto.BondsResponse;

import java.util.List;

@Service
@Slf4j
public class BondService {

    private final MoexSecuritiesClient moexClient;

    public BondService(MoexSecuritiesClient moexClient) {
        this.moexClient = moexClient;
    }

    public void printAllBonds() {
        List<BondsResponse> bonds = moexClient.getAllBonds();

        // Фильтрация активных облигаций (статус "A")
        List<BondsResponse> activeBonds = bonds.stream()
                .filter(b -> "A".equals(b.getStatus()))
                .toList();

//        log.info("Всего облигаций: {}, активных: {}", bonds.size(), activeBonds.size());
//
//        // Вывод первых 5 активных облигаций
//        activeBonds.stream()
//                .limit(5)
//                .forEach(bond -> {
//                    log.info("{} ({}) - ISIN: {}, НКД: {}, Доходность: {}%",
//                            bond.getShortName(),
//                            bond.getSecId(),
//                            bond.getIsin(),
//                            bond.getAccruedInt(),
//                            bond.getYieldAtPrevWapPrice());
//                });
    }
}
