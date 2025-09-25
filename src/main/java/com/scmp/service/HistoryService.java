package com.scmp.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.scmp.model.ContractInfo;
import com.scmp.model.HistoryInfo;
import com.scmp.model.QueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j

public class HistoryService {

    private final ObjectMapper objectMapper;

    private RestTemplate restTemplate = new RestTemplate();

    public HistoryService() {
        this.objectMapper = new ObjectMapper();
    }

    public List<HistoryInfo> getHistoryInfoByContractNo(String contractNo, String token) {
        // 1. 构建请求URL
        // 请求 URL（curl 里的完整地址）
        String url = "https://csmp.df-finance.com.cn/gateway/collectionservice/dfcw/csMain/getWorkOrderInfo"

                + "?contractNo=" +contractNo;


        try {


            // 3. 构建请求头
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.ALL));
            headers.set("Authorization",token);



            // 4. 发送请求并获取响应
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity,  String.class);
            String json = response.getBody();
            List<HistoryInfo> histories = List.of();
            if (json != null && !json.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(json);
                if (root != null) {
                    JsonNode rowNode = root.path("row");
                    if (!rowNode.isMissingNode() && !rowNode.isNull()) {
                        JsonNode recordsNode = rowNode.path("records");
                        if (!recordsNode.isMissingNode() && recordsNode.isArray()) {
                            histories = mapper.readValue(
                                    recordsNode.toString(),
                                    new TypeReference<List<HistoryInfo>>() {}
                            );
                        }
                    }
                }
            }
           return histories;
        } catch (Exception e) {
            // 仅记录日志，返回异常信息作为结果，不触发重试
            log.info("grabCase exception for contract {}: {}", contractNo, e.getMessage());
            return List.of();
        }
    }



    public static void main(String[] args) {
        HistoryService historyService = new HistoryService();
        List<HistoryInfo> historyInfoByContractNo = historyService.getHistoryInfoByContractNo("RCQ05393922277864", "ecffefda3ce147018ee19a222bee3af4");

        System.out.println(historyInfoByContractNo);

        ContractService contractService = new ContractService();
        QueryResponse<ContractInfo> queryResponse = contractService.fetchPreGrabCases(1, "ecffefda3ce147018ee19a222bee3af4");

        List<ContractInfo> rows = queryResponse.getRows();
        for (ContractInfo row : rows) {
            System.out.println(row.getContractNo());
        }
    }
}
