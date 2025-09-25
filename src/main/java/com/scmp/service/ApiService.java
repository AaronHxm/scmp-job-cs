package com.scmp.service;

import com.scmp.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

@Slf4j
public class ApiService {
    
    private OkHttpClient client;
    private static final Logger logger = Logger.getLogger(ApiService.class.getName());
    private final CaseGrabService caseGrabService;
    private final ContractService contractService;
    
    public ApiService() {
        // 初始化OkHttpClient
        this.client = new OkHttpClient();
        // 初始化抢单服务
        this.caseGrabService = new CaseGrabService();

        this.contractService = new ContractService();
    }
    
    // 登录接口 - 账号密码登录
    public boolean loginWithCredentials(User user) {
        // 伪代码实现
        System.out.println("调用登录接口: " + user.getUsername());
        
        // 实际HTTP请求代码示例（注释掉）
        /*
        RequestBody body = new FormBody.Builder()
                .add("username", user.getUsername())
                .add("password", user.getPassword())
                .build();
        
        Request request = new Request.Builder()
                .url("http://api.example.com/login")
                .post(body)
                .build();
        
        try {
            Response response = client.newCall(request).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        */
        
        // 模拟登录成功
        return true;
    }
    
    // 登录接口 - Token登录
    public boolean loginWithToken(String token) {
        // 伪代码实现
        System.out.println("调用Token登录接口: " + token);
        
        // 实际HTTP请求代码示例（注释掉）
        /*
        Request request = new Request.Builder()
                .url("http://api.example.com/login/token")
                .header("Authorization", "Bearer " + token)
                .build();
        
        try {
            Response response = client.newCall(request).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        */
        
        // 模拟登录成功
        return true;
    }
    
    // 查询合同接口 - 带筛选条件
    public List<ContractInfo> queryContracts(User user, Integer maxOverdueDays, List<String> letters) {
        // 首先查询所有合同
        QueryResponse<ContractInfo> contractInfoQueryResponse = contractService.fetchAllPreGrabCases(user);
        if (Objects.isNull(contractInfoQueryResponse)) {
            return List.of();
        }

        List<ContractInfo> allContracts = contractInfoQueryResponse.getRows();
        
        // 如果没有任何过滤条件，直接返回所有合同
        if ((maxOverdueDays == null || maxOverdueDays < 0) ) {
            logger.info("没有设置过滤条件，返回所有合同");
            return allContracts;
        }
        
        // 创建过滤后的合同列表
        List<ContractInfo> filteredContracts = new ArrayList<>();
        
        for (ContractInfo contract : allContracts) {

            contract.setUserId(user.getToken());
            boolean passFilter = true;
            
            // 检查逾期天数过滤条件
            if (maxOverdueDays != null && maxOverdueDays >= 0 && contract.getTotalODDays() != null) {
                passFilter = contract.getTotalODDays() < maxOverdueDays;
            }
            
            // 如果通过了逾期天数过滤，再检查字母过滤条件
            if (passFilter && letters != null && !letters.isEmpty() && contract.getContractNo() != null) {
                String contractNo = contract.getContractNo();
                // 检查合同编号是否至少有3个字符
                if (contractNo.length() >= 3) {
                    // 获取第三个字符（索引为2）
                    char thirdChar = contractNo.charAt(2);
                    String thirdCharStr = String.valueOf(thirdChar).toUpperCase();
                    
                    // 检查第三个字符是否在允许的字母列表中
                    boolean letterMatch = false;
                    for (String letter : letters) {
                        if (thirdCharStr.equalsIgnoreCase(letter.trim())) {
                            letterMatch = true;
                            break;
                        }
                    }
                    passFilter = letterMatch;
                } else {
                    // 合同编号不足3个字符，不满足条件
                    passFilter = false;
                }
            }
            
            // 如果通过所有过滤条件，添加到结果列表
            if (passFilter) {
                filteredContracts.add(contract);
            }
        }
        
        logger.info("过滤后返回合同数量: " + filteredContracts.size());
        return filteredContracts;
    }



    
    // 抢单接口 - 原始方法，保持向后兼容
    public boolean grabContract(String contractNumber) {
        // 由于缺少必要参数（syskey和userId），此处保留模拟实现
        System.out.println("调用抢单接口: " + contractNumber + "（缺少必要参数，使用模拟实现）");
        
        // 模拟抢单成功
        return Math.random() > 0.3; // 70%成功率
    }
    
    // 抢单接口 - 新方法，使用实际的CaseGrabService
    public boolean grabContract(User user, ContractInfo contract) {
        logger.info("使用实际抢单服务调用抢单接口: " + contract.getContractNo() + "，用户: " + user.getUsername());
        
        // 检查必要参数
        if (user == null || contract == null || contract.getSyskey() == null) {
            logger.warning("抢单参数不完整");
            return false;
        }
        
        try {
            // 使用CaseGrabService进行实际抢单，使用token作为userId
            String result = caseGrabService.grabCase(contract.getSyskey(), contract.getContractNo(), user.getToken());
            
            // 记录抢单结果
            logger.info("抢单结果: " + result);
            
            // 根据返回结果判断是否成功
            // 实际项目中可能需要根据接口返回的具体格式来判断成功与否
            return result != null && !result.contains("error") && !result.contains("失败");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "抢单过程中发生异常", e);
            return false;
        }
    }
}