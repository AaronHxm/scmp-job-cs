package com.scmp.service;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.scmp.model.HistoryInfo;
import com.scmp.model.QueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
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
                + "?total=0&pageSize=50&pageIndex=1"
                + "&contractNo=RCQ05393922277864"
                + "&tenancyId=DCBDCABAE64F0671E0530100007FE7DD"
                + "&menuId=91ee2e4630764ff8a76c2a2e43f1891d"
                + "&menuName=催收操作主页"
                + "&orgTemplateId=DCBDCABAE6510671E0530100007FE7DD"
                + "&ClientServer=https%3A%2F%2Fcsmp.df-finance.com.cn";

        try {


            // 3. 构建请求头
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.ALL));
            headers.set("Authorization",token);
            headers.set("Expires", "0");
            headers.set("Pragma", "no-cache");
            headers.set("clientId", "mplanyou");
            headers.set("gversion", "");
            headers.set("lang", "ZH_CN");
            headers.set("noncestr", "16636a46-792d-8513-83bb-db0b51f4db54");
            headers.set("range", "1");
            headers.set("sign", "fbc0842bab517a84c3b36a6dc5139fa5a4a0a3105bedf169b5da8514789383c35f706085f4374f5e61c6788b6b52df598be17143c889d4a2b80e435e38c78d65");
            headers.set("timestamp",String.valueOf( System.currentTimeMillis()));
            headers.set("Cookie", "user-code=3qZmZLDReedsYtGBGpdipM8JQCBHhzETyN0wGJl5mfK7qvcM+2jeWvqPP20jZuhA");
            headers.set("User-Agent", "Apifox/1.0.0 (https://apifox.com)");
            headers.set("Host", "csmp.df-finance.com.cn");
            headers.set("Connection", "keep-alive");


            // 4. 发送请求并获取响应
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            // 发送请求并返回响应
            // 5. 发送请求并获取泛型响应
            ResponseEntity<QueryResponse<HistoryInfo>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<QueryResponse<HistoryInfo>>() {}
            );

            QueryResponse<HistoryInfo> body = response.getBody();

            if (body == null) {
                return List.of();
            }
            // 5. 处理响应
           return body.getRows();
        } catch (Exception e) {
            // 仅记录日志，返回异常信息作为结果，不触发重试
            log.warn("grabCase exception for contract {}: {}", contractNo, e.getMessage());
            return List.of();
        }
    }


}
