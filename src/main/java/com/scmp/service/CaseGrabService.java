package com.scmp.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.scmp.model.GrabCaseRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Random;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


/**
 *
 * 抢单服务类
 */
public class CaseGrabService {

    private static final Logger log = LoggerFactory.getLogger(CaseGrabService.class);

    private final ObjectMapper objectMapper;
    private RestTemplate restTemplate = new RestTemplate();


    public CaseGrabService() {
        this.objectMapper = new ObjectMapper();
    }

    public String grabCase(Long syskey, String contractNo,String userId)  {

        return getResult();

//        // 1. 构建请求URL
//        String url = "https://csmp.df-finance.com.cn/gateway/orderservice/dfcw/grab/grabCase" +
//                "?tenancyId=DCBDCABAE64F0671E0530100007FE7DD" +
//                "&menuId=016393dc5399435b991371f1de836ec5" +
//                "&menuName=%E5%A7%94%E5%A4%96%E7%94%B5%E5%82%AC%E5%BE%85%E6%8A%A2%E5%8D%95" +
//                "&orgTemplateId=DCBDCABAE6510671E0530100007FE7DD" +
//                "&ClientServer=https:%2F%2Fcsmp.df-finance.com.cn";
//
//        // 2. 构建请求头
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Collections.singletonList(org.springframework.http.MediaType.APPLICATION_JSON));
//        headers.set("Authorization", userId);
//        headers.setCacheControl("no-cache, no-store, max-age=0,must-revalidate");
//        headers.setConnection("keep-alive");
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Cookie", "user-code=kOW32z9z8984xqUa0JUHTNqzM8c/wCLxNnku6/RPm4O7qvcM+2jeWvqPP20jZuhA");
//        headers.setExpires(0);
//        headers.setOrigin("https://csmp.df-finance.com.cn");
//        headers.setPragma("no-cache");
//        headers.set("Sec-Fetch-Dest", "empty");
//        headers.set("Sec-Fetch-Mode", "cors");
//        headers.set("Sec-Fetch-Site", "same-origin");
//        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36");
//        headers.set("clientId", "mplanyou");
//        headers.set("gversion", "");
//        headers.set("lang", "ZH_CN");
//        headers.set("noncestr", "6dbefba2-bb8c-abe7-959d-c8a083b9651d");
//        headers.set("range", "1");
//        headers.set("sec-ch-ua", "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"");
//        headers.set("sec-ch-ua-mobile", "?0");
//        headers.set("sec-ch-ua-platform", "\"Windows\"");
//        headers.set("sign", "3b6b0e3a0c0d314a4b079afdc27d2c62b063296a0d6404b10507cb7dc48b935ea33dcf8f93396caf9994e9ca47973ba53b30365dfcad35763913832a940b3520");
//        headers.set("timestamp",String.valueOf( System.currentTimeMillis()));
//
//        // 3. 构建请求体
//        GrabCaseRequest requestBody = new GrabCaseRequest();
//        requestBody.setSyskey(syskey);
//        requestBody.setContractNo(contractNo);
//
//        // 4. 创建请求实体
//        HttpEntity<GrabCaseRequest> requestEntity = new HttpEntity<>(requestBody, headers);
//
////         5. 发送请求并获取响应
//        try {
//            // 使用 execute 手动读取响应体，避免 Invalid MIME 异常
//            String execute = restTemplate.execute(
//                    url,
//                    HttpMethod.POST,
//                    request -> {
//                        request.getHeaders().putAll(requestEntity.getHeaders());
//                        new ObjectMapper().writeValue(request.getBody(), requestBody);
//                    },
//                    response -> {
//                        InputStream body = response.getBody();
//                        if (body == null) return "";
//                        return new String(body.readAllBytes(), StandardCharsets.UTF_8);
//                    }
//            );
//            log.info("合同编号:【{}】,抢单人:{},执行结果:{}",contractNo,userId,execute);
//            return execute;
//        } catch (RestClientException e) {
//            // 仅记录日志，返回异常信息作为结果，不触发重试
//            log.warn("grabCase exception (ignored MIME) for contract {}: {}", contractNo, e.getMessage());
//            return e.getMessage();
//        }
    }
    String[] messages = {
            "案件信息已经不存在",
            "抢单失败，请稍后重试！",
            "您的订单已经受理，请稍后查看！"
    };
    private String getResult(){


        Random random = new Random();
        int index = random.nextInt(messages.length);
        String selectedMessage = messages[index];
        log.info("模拟返回消息: {}", selectedMessage);
        // 模拟返回结果
        return selectedMessage;
    }

}
