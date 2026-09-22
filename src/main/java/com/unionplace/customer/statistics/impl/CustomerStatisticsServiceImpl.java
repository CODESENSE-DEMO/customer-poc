package com.unionplace.customer.statistics.impl;

import com.unionplace.customer.management.spec.Customer;
import com.unionplace.customer.management.spec.CustomerRepository;
import com.unionplace.customer.management.spec.CustomerStatus;
import com.unionplace.customer.statistics.spec.CustomerStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerStatisticsServiceImpl implements CustomerStatisticsService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(CustomerStatus status) {
        return customerRepository.countByStatus(status);
    }

    // [DEFECT-022] 비효율 쿼리: 전체 조회 후 in-memory 평균 계산
    // - 대량 데이터 시 OOM 및 성능 저하
    // - JPQL/네이티브 집계 쿼리(AVG)로 DB에서 계산하도록 변경 필요
    @Override
    @Transactional(readOnly = true)
    public double averageAge() {
        List<Customer> all = customerRepository.findAll();
        if (all.isEmpty()) {
            return 0.0;
        }
        LocalDate today = LocalDate.now();
        int sum = 0;
        for (Customer c : all) {
            sum += Period.between(c.getBirthDate(), today).getYears();
        }
        return (double) sum / all.size();
    }

    // [DEFECT-023] 동시성 이슈: 인스턴스 필드를 mutable HashMap으로 관리
    // - 멀티스레드 환경에서 race condition / 무한 루프(HashMap resize) 발생 가능
    // - ConcurrentHashMap + AtomicInteger, 또는 동기화 처리 필요
    private final Map<String, Integer> channelCountCache = new HashMap<>();

    @Override
    public void incrementChannelCount(String channelCode) {
        Integer current = channelCountCache.get(channelCode);
        if (current == null) {
            channelCountCache.put(channelCode, 1);
        } else {
            channelCountCache.put(channelCode, current + 1);
        }
    }
}
