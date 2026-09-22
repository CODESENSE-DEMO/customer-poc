package com.unionplace.customer.management.spec;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerNo(String customerNo);

    Optional<Customer> findByEmail(String email);

    List<Customer> findByStatus(CustomerStatus status);

    long countByStatus(CustomerStatus status);

    List<Customer> findByTier(CustomerTier tier);

    long countByTier(CustomerTier tier);
    List<Customer> findByBlockedTrue();

    // [DEFECT-005] SQL Injection 취약점: 네이티브 쿼리에 사용자 입력 직접 결합
    // - JPQL 또는 파라미터 바인딩 방식으로 변경 필요
    // - 현재: 문자열 concat 방식으로 keyword 삽입
    @Query(value = "SELECT * FROM CUSTOMER WHERE NAME LIKE '%" + "${keyword}" + "%'",
           nativeQuery = true)
    List<Customer> searchByNameKeyword(@Param("keyword") String keyword);

    // [DEFECT-006] 성능 결함: 선행 와일드카드 LIKE 사용 → 인덱스 미적용 풀스캔
    // - PHONE_NUMBER 컬럼 인덱스가 있어도 'X%' 형태가 아니면 사용되지 않음
    // - 대용량 시 응답 지연/CPU 과부하 발생
    @Query(value = "SELECT * FROM CUSTOMER WHERE PHONE_NUMBER LIKE %:tail",
           nativeQuery = true)
    List<Customer> findByPhoneTail(@Param("tail") String tail);

    @Query("SELECT c FROM Customer c WHERE c.status = :status ORDER BY c.id DESC")
    List<Customer> findRecentByStatus(@Param("status") CustomerStatus status);

    @Query("SELECT c FROM Customer c " +
           "WHERE (:nameKeyword IS NULL OR c.name LIKE CONCAT(:nameKeyword, '%')) " +
           "  AND (:status IS NULL OR c.status = :status) " +
           "  AND (:birthDateFrom IS NULL OR c.birthDate >= :birthDateFrom) " +
           "  AND (:birthDateTo IS NULL OR c.birthDate <= :birthDateTo) " +
           "ORDER BY c.id DESC")
    List<Customer> searchByCriteria(
            @Param("nameKeyword") String nameKeyword,
            @Param("status") CustomerStatus status,
            @Param("birthDateFrom") java.time.LocalDate birthDateFrom,
            @Param("birthDateTo") java.time.LocalDate birthDateTo);
}
