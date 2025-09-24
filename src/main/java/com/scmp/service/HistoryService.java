package com.scmp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scmp.model.ContractInfo;
import com.scmp.model.GrabCaseRequest;
import com.scmp.model.HistoryInfo;
import com.scmp.model.QueryResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;


public class HistoryService {
    private static final Logger log = LoggerFactory.getLogger(CaseGrabService.class);
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public HistoryService() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<HistoryInfo> getHistoryInfoByContractNo(String contractNo, String userId) {
        // 1. 构建请求URL
        String url = CollectionServiceConstants.BASE_URL +
                (CollectionServiceConstants.BASE_URL.endsWith("/") ? "" : "/") +
                "gateway/collectionservice/dfcw/csMain/getWorkOrderInfo" +
                "?total=" + 0 +
                "&pageSize=50" +
                "&pageIndex=1" +
                "&contractNo=" + contractNo +
                "&tenancyId=DCBDCABAE64F0671E0530100007FE7DD" +
                "&menuId=91ee2e4630764ff8a76c2a2e43f1891d" +
                "&menuName=%E5%82%AC%E6%94%B6%E6%93%8D%E4%BD%9C%E4%B8%BB%E9%A1%B5" +
                "&orgTemplateId=DCBDCABAE6510671E0530100007FE7DD" +
                "&ClientServer=https:%2F%2Fcsmp.df-finance.com.cn";

        try {


            // 3. 构建请求头
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", userId)
                    .addHeader("Cache-Control", "no-cache, no-store, max-age=0,must-revalidate")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Cookie", "user-code=kOW32z9z8984xqUa0JUHTNqzM8c/wCLxNnku6/RPm4O7qvcM+2jeWvqPP20jZuhA")
                    .addHeader("Expires", "0")
                    .addHeader("Origin", "https://csmp.df-finance.com.cn")
                    .addHeader("Pragma", "no-cache")
                    .addHeader("Sec-Fetch-Dest", "empty")
                    .addHeader("Sec-Fetch-Mode", "cors")
                    .addHeader("Sec-Fetch-Site", "same-origin")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36")
                    .addHeader("clientId", "mplanyou")
                    .addHeader("gversion", "")
                    .addHeader("lang", "ZH_CN")
                    .addHeader("noncestr", "6dbefba2-bb8c-abe7-959d-c8a083b9651d")
                    .addHeader("range", "1")
                    .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"")
                    .addHeader("sec-ch-ua-mobile", "?0")
                    .addHeader("sec-ch-ua-platform", "\"Windows\"")
                    .addHeader("sign", "3b6b0e3a0c0d314a4b079afdc27d2c62b063296a0d6404b10507cb7dc48b935ea33dcf8f93396caf9994e9ca47973ba53b30365dfcad35763913832a940b3520")
                    .addHeader("timestamp", String.valueOf(System.currentTimeMillis()));

            // 4. 发送请求并获取响应
            Request request = requestBuilder.build();
            Response response = client.newCall(request).execute();

            // 5. 处理响应
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();

                QueryResponse<HistoryInfo> responseWrapper = objectMapper.readValue(
                        responseBody,
                        new TypeReference<QueryResponse<HistoryInfo>>() {
                        }
                );
                return responseWrapper.getRows();
            } else {
                String errorMessage = "抢单请求失败，HTTP状态码: " + response.code();
                log.warn(errorMessage + "，合同编号:【{}】", contractNo);
                return List.of();
            }
        } catch (IOException e) {
            // 仅记录日志，返回异常信息作为结果，不触发重试
            log.warn("grabCase exception for contract {}: {}", contractNo, e.getMessage());
            return List.of();
        }
    }


}
