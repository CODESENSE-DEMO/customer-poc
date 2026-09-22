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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 고객 상담 메모 API 테스트 — 등록/조회/수정/삭제(소프트 삭제) 및 권한·소유 검증.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CustomerMemoApiTests {

    private static final String CUSTOMER_NO = "C-9001";
    private static final String OTHER_CUSTOMER_NO = "C-9002";

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    private String adminToken;
    private String operatorToken;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = tokenOf("admin", "admin1234");
        operatorToken = tokenOf("operator", "operator1234");
        createCustomerIfAbsent(CUSTOMER_NO, "김상담", "memo1@example.com", "010-1111-2222");
        createCustomerIfAbsent(OTHER_CUSTOMER_NO, "이상담", "memo2@example.com", "010-3333-4444");
    }

    @Test
    void 메모를_등록하면_201과_표준응답으로_래핑된다() throws Exception {
        mvc.perform(post("/api/customers/{customerNo}/memos", CUSTOMER_NO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + operatorToken)
                        .content(memoJson("INBOUND_CALL", "보험료 문의", "갱신 보험료 안내 완료")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.customerNo").value(CUSTOMER_NO))
                .andExpect(jsonPath("$.data.category").value("INBOUND_CALL"))
                .andExpect(jsonPath("$.data.createdBy").isNotEmpty());
    }

    @Test
    void 메모_목록은_PageResponse_형태로_응답한다() throws Exception {
        createMemo(CUSTOMER_NO, "COMPLAINT", "처리 지연 불만", "재발 방지 안내");

        mvc.perform(get("/api/customers/{customerNo}/memos", CUSTOMER_NO)
                        .param("category", "COMPLAINT")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.content[0].category").value("COMPLAINT"));
    }

    @Test
    void 메모를_수정하면_내용이_반영된다() throws Exception {
        long memoId = createMemo(CUSTOMER_NO, "ETC", "1차 안내", "최초 안내");

        mvc.perform(put("/api/customers/{customerNo}/memos/{memoId}", CUSTOMER_NO, memoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + operatorToken)
                        .content(memoJson("CLAIM_GUIDE", "2차 안내", "보험금 청구 절차 재안내")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("2차 안내"))
                .andExpect(jsonPath("$.data.category").value("CLAIM_GUIDE"));
    }

    @Test
    void 다른_고객의_메모에는_접근할_수_없다() throws Exception {
        long memoId = createMemo(CUSTOMER_NO, "ETC", "소유 검증", "다른 고객 접근 차단");

        mvc.perform(get("/api/customers/{customerNo}/memos/{memoId}", OTHER_CUSTOMER_NO, memoId)
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("MEMO_003"));
    }

    @Test
    void 없는_메모는_도메인_에러코드_404() throws Exception {
        mvc.perform(get("/api/customers/{customerNo}/memos/{memoId}", CUSTOMER_NO, 999999)
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MEMO_001"));
    }

    @Test
    void 삭제는_ADMIN만_가능하며_소프트_삭제로_목록에서_제외된다() throws Exception {
        long memoId = createMemo(CUSTOMER_NO, "OUTBOUND_CALL", "삭제 대상", "삭제 후 조회되지 않아야 함");

        mvc.perform(delete("/api/customers/{customerNo}/memos/{memoId}", CUSTOMER_NO, memoId)
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("COMMON_403"));

        mvc.perform(delete("/api/customers/{customerNo}/memos/{memoId}", CUSTOMER_NO, memoId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/customers/{customerNo}/memos/{memoId}", CUSTOMER_NO, memoId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MEMO_001"));
    }

    @Test
    void 내용이_비어있으면_검증_실패한다() throws Exception {
        mvc.perform(post("/api/customers/{customerNo}/memos", CUSTOMER_NO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + operatorToken)
                        .content(memoJson("ETC", "제목만 있음", "")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON_400"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    private long createMemo(String customerNo, String category, String title, String content) throws Exception {
        MvcResult result = mvc.perform(post("/api/customers/{customerNo}/memos", customerNo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + operatorToken)
                        .content(memoJson(category, title, content)))
                .andExpect(status().isCreated())
                .andReturn();
        return om.readTree(result.getResponse().getContentAsString()).path("data").path("id").asLong();
    }

    private String memoJson(String category, String title, String content) {
        return "{\"category\":\"" + category + "\",\"title\":\"" + title + "\",\"content\":\"" + content + "\"}";
    }

    private void createCustomerIfAbsent(String customerNo, String name, String email, String phone) throws Exception {
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content("{\"customerNo\":\"" + customerNo + "\",\"name\":\"" + name + "\","
                        + "\"residentRegistrationNumber\":\"900101-1234567\","
                        + "\"email\":\"" + email + "\",\"phoneNumber\":\"" + phone + "\","
                        + "\"birthDate\":\"1990-01-01\"}"));
    }

    private String tokenOf(String username, String password) throws Exception {
        MvcResult result = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return om.readTree(result.getResponse().getContentAsString()).path("data").path("accessToken").asText();
    }
}
