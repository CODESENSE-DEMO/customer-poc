package com.unionplace.customer.management.impl;

import com.unionplace.customer.management.spec.Customer;
import com.unionplace.customer.management.spec.CustomerCreateRequest;
import com.unionplace.customer.management.spec.CustomerNotFoundException;
import com.unionplace.customer.management.spec.CustomerRepository;
import com.unionplace.customer.management.spec.CustomerResponse;
import com.unionplace.customer.management.spec.CustomerSearchRequest;
import com.unionplace.customer.management.spec.CustomerService;
import com.unionplace.customer.management.spec.CustomerStatus;
import com.unionplace.customer.management.spec.CustomerUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    // NM-05: 매직 스트링 상수
    private static final String BIRTH_DATE_PATTERN = "yyyy-MM-dd";
    private static final String CSV_DELIMITER = ",";
    private static final String BIRTH_DATE_PARSE_ERROR_MSG = "생년월일 파싱 실패: ";

    // [DEFECT-015] 동시성 결함: SimpleDateFormat 인스턴스를 static 필드로 공유
    // - SimpleDateFormat은 thread-safe 하지 않음 (parse/format 시 내부 상태 변경)
    // - DateTimeFormatter 또는 ThreadLocal<SimpleDateFormat> 사용 필요
    private static final SimpleDateFormat BIRTH_DATE_FORMAT = new SimpleDateFormat(BIRTH_DATE_PATTERN);

    // [DEFECT-011] 트랜잭션 누락: 데이터 변경 메서드에 @Transactional 누락
    // - 쓰기 작업이므로 @Transactional 필요
    // - 예외 발생 시 롤백 보장 안 됨
    @Override
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        Customer customer = new Customer(
                request.getCustomerNo(),
                request.getName(),
                request.getResidentRegistrationNumber(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getBirthDate()
        );
        Customer saved = customerRepository.save(customer);
        return CustomerResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("고객을 찾을 수 없습니다. id=" + id));
        return CustomerResponse.from(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerResponse updateContact(Long id, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("고객을 찾을 수 없습니다. id=" + id));
        customer.updateContact(request.getEmail(), request.getPhoneNumber());
        return CustomerResponse.from(customer);
    }

    @Override
    @Transactional
    public void withdrawCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("고객을 찾을 수 없습니다. id=" + id));
        customer.withdraw();
    }

    // [DEFECT-012] NullPointerException 가능성: Optional 처리 없이 .get() 호출
    // - findByEmail은 Optional<Customer> 반환
    // - 존재하지 않으면 NoSuchElementException 발생
    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email).get();
        return CustomerResponse.from(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(CustomerStatus status) {
        return customerRepository.countByStatus(status);
    }

    @Override
    @Transactional
    public int importCustomersFromCsv(String filePath) throws IOException {
        List<String[]> lines = readCsvLines(filePath);
        int count = 0;
        for (String[] tokens : lines) {
            if (tokens.length >= 6) {
                LocalDate birthDate = parseBirthDate(tokens[5]);
                Customer customer = new Customer(
                        tokens[0], tokens[1], tokens[2], tokens[3], tokens[4], birthDate);
                customerRepository.save(customer);
                count++;
            }
        }
        return count;
    }

    private List<String[]> readCsvLines(String filePath) throws IOException {
        List<String[]> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // 헤더 스킵
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.split(CSV_DELIMITER));
            }
        }
        return lines;
    }

    private LocalDate parseBirthDate(String raw) {
        try {
            return BIRTH_DATE_FORMAT.parse(raw).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        } catch (Exception e) {
            throw new IllegalArgumentException(BIRTH_DATE_PARSE_ERROR_MSG + raw, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomers(CustomerSearchRequest request) {
        return customerRepository.searchByCriteria(
                        request.hasNameKeyword() ? request.getNameKeyword() : null,
                        request.hasStatus() ? request.getStatus() : null,
                        request.hasBirthDateRange() ? request.getBirthDateFrom() : null,
                        request.hasBirthDateRange() ? request.getBirthDateTo() : null)
                .stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }

    // [DEFECT-014] 비즈니스 로직 결함: 만 나이 계산 시 윤년/생일 미경과 케이스 누락
    // - 연도 차이만 사용 → 같은 해 생일 미경과인 경우 1세 과대 계산
    // - java.time.Period.between(birthDate, today).getYears() 사용 필요
    public int calculateKoreanAge(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        return today.getYear() - birthDate.getYear();
    }
}
