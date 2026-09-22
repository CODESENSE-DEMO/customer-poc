package com.unionplace;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Union Framework 연동 스모크 테스트: 로그인 → 표준 응답 포맷 → 에러 코드 → 권한.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CustomerPocApplicationTests {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    private String adminToken;
    private String operatorToken;

    @BeforeEach
    void login() throws Exception {
        adminToken = tokenOf("admin", "admin1234");
        operatorToken = tokenOf("operator", "operator1234");
    }

    private String tokenOf(String username, String password) throws Exception {
        MvcResult result = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andReturn();
        return om.readTree(result.getResponse().getContentAsString()).path("data").path("accessToken").asText();
    }

    @Test
    void 인증없이_보호된_API_접근시_401_표준응답() throws Exception {
        mvc.perform(get("/api/customers"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("COMMON_401"));
    }

    @Test
    void 잘못된_비밀번호는_도메인_에러코드로_응답한다() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_001"));
    }

    @Test
    void 고객_생성시_ApiResponse로_래핑되고_Auditing_작성자가_기록된다() throws Exception {
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken)
                        .content("{\"customerNo\":\"C-1001\",\"name\":\"홍길동\",\"residentRegistrationNumber\":\"900101-1234567\","
                                + "\"email\":\"hong@example.com\",\"phoneNumber\":\"010-1234-5678\",\"birthDate\":\"1990-01-01\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-Trace-Id"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.customerNo").value("C-1001"))
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    void 검증_실패는_필드오류_목록을_포함한다() throws Exception {
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken)
                        .content("{\"customerNo\":\"\",\"name\":\"\",\"residentRegistrationNumber\":\"bad\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON_400"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void 없는_고객은_도메인_에러코드_404() throws Exception {
        mvc.perform(get("/api/customers/999999").header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("CUSTOMER_001"));
    }

    @Test
    void 일반_운영자는_탈회처리_권한이_없다() throws Exception {
        mvc.perform(delete("/api/customers/1").header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("COMMON_403"));
    }
}
