package com.scmp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scmp.model.ContractInfo;
import com.scmp.model.GrabCaseRequest;
import com.scmp.model.QueryResponse;
import com.scmp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

public class ApiService {
    
    private OkHttpClient client;
    private static final Logger logger = Logger.getLogger(ApiService.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final CaseGrabService caseGrabService;
    
    public ApiService() {
        // 初始化OkHttpClient
        this.client = new OkHttpClient();
        // 初始化抢单服务
        this.caseGrabService = new CaseGrabService();
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
    public List<ContractInfo> queryContracts(User user, Integer maxOverdueDays, String letter) {
        // 首先查询所有合同
        List<ContractInfo> allContracts = queryAllContracts(user);
        
        // 如果没有任何过滤条件，直接返回所有合同
        if ((maxOverdueDays == null || maxOverdueDays < 0) && (letter == null || letter.trim().isEmpty())) {
            logger.info("没有设置过滤条件，返回所有合同");
            return allContracts;
        }
        
        // 创建过滤后的合同列表
        List<ContractInfo> filteredContracts = new ArrayList<>();
        
        for (ContractInfo contract : allContracts) {
            boolean passFilter = true;
            
            // 检查逾期天数过滤条件
            if (maxOverdueDays != null && maxOverdueDays >= 0 && contract.getTotalODDays() != null) {
                passFilter = contract.getTotalODDays() < maxOverdueDays;
            }
            
            // 如果通过了逾期天数过滤，再检查字母过滤条件
            if (passFilter && letter != null && !letter.trim().isEmpty() && contract.getContractNo() != null) {
                String contractNo = contract.getContractNo();
                // 检查合同编号是否至少有3个字符
                if (contractNo.length() >= 3) {
                    // 获取第三个字符（索引为2）并检查是否匹配
                    char thirdChar = contractNo.charAt(2);
                    passFilter = String.valueOf(thirdChar).equalsIgnoreCase(letter.trim());
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
    
    // 兼容旧版本的方法
    public List<ContractInfo> queryContracts(Integer maxOverdueDays, String letter) {
        // 伪代码实现
        System.out.println("调用查询接口: 逾期天数<" + maxOverdueDays + ", 字母=" + letter);
        
        // 模拟返回数据
        ContractInfo contract1 = new ContractInfo();
        contract1.setCustomerName("张三");

        ContractInfo contract2 = new ContractInfo();
        contract2.setCustomerName("李四");
        
        return Arrays.asList(contract1, contract2);
    }
    
    // 新的查询合同接口 - 不考虑筛选条件，使用token认证
    public List<ContractInfo> queryAllContracts(User user) {
        // 检查用户是否已登录并有token
        if (user == null || !user.isLoggedIn() || user.getToken() == null || user.getToken().trim().isEmpty()) {
            logger.warning("用户未登录或token为空");
            return new ArrayList<>();
        }
        
        logger.info("调用新的查询接口，使用token认证");
        
        // 构建请求URL
        String url = CollectionServiceConstants.BASE_URL + 
                (CollectionServiceConstants.BASE_URL.endsWith("/") ? "" : "/") +
                "gateway/collectionservice/dfcw/outSrc/preGrabCaseList" +
                "?tenancyId=" + CollectionServiceConstants.TENANCY_ID +
                "&menuId=" + CollectionServiceConstants.MENU_ID +
                "&menuName=" + CollectionServiceConstants.MENU_NAME +
                "&orgTemplateId=" + CollectionServiceConstants.ORG_TEMPLATE_ID +
                "&ClientServer=https:%2F%2Fcsmp.df-finance.com.cn";
        
        // 构建请求头
        Headers headers = new Headers.Builder()
                .add("Content-Type", "application/json")
                .add("Accept", "*")
                .add("Authorization", user.getToken())
                .add("Expires", "0")
                .add("Pragma", "no-cache")
                .add("clientId", "mplanyou")
                .add("gversion", "")
                .add("lang", "ZH_CN")
                .add("noncestr", "16636a46-792d-8513-83bb-db0b51f4db54")
                .add("range", "1")
                .add("sign", "fbc0842bab517a84c3b36a6dc5139fa5a4a0a3105bedf169b5da8514789383c35f706085f4374f5e61c6788b6b52df598be17143c889d4a2b80e435e38c78d65")
                .add("timestamp", String.valueOf(System.currentTimeMillis()))
                .add("Cookie", "user-code=" + CollectionServiceConstants.USER_CODE_COOKIE)
                .add("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .add("Host", "csmp.df-finance.com.cn")
                .add("Connection", "keep-alive")
                .build();
        
        // 构建请求体（不考虑筛选条件）
        String requestBody = String.format(
                "{\"pageIndex\":%d,\"pageSize\":%d}",
                1, // 只获取第一页数据
                CollectionServiceConstants.PAGE_SIZE
        );
        
        // 创建请求
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();
        
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                logger.info("查询合同成功，响应体大小: " + responseBody.length() + " 字节");
                
                // 解析响应体，先解析到包装类
                // 使用 TypeReference 指定泛型类型
                QueryResponse<ContractInfo> responseWrapper = objectMapper.readValue(
                        responseBody,
                        new TypeReference<QueryResponse<ContractInfo>>() {}
                );
                if (responseWrapper != null) {
                    QueryResponse<ContractInfo> queryResponse = responseWrapper;
                    if (queryResponse.getRows() != null) {
                        return queryResponse.getRows();
                    } else {
                        logger.warning("查询结果中的数据列表为空");
                        return new ArrayList<>();
                    }
                } else {

                    return new ArrayList<>();
                }
            } else {
                logger.warning("查询合同失败，HTTP状态码: " + response.code());
                return new ArrayList<>();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "查询合同发生异常", e);
            return new ArrayList<>();
        }
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