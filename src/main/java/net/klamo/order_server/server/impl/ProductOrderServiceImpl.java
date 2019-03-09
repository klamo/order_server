package net.klamo.order_server.server.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import net.klamo.order_server.domain.ProductOrder;
import net.klamo.order_server.server.ProductClient;
import net.klamo.order_server.server.ProductOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


@Service
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancer;

    @Autowired
    private ProductClient productClient;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public ProductOrder save(int userId, int productId) {
        logger.info("order server log日志...");
        //方式一：使用注入的restTemplate调用
        //Map forObject = restTemplate.getForObject("http://product-service/api/v1/product/find?id=" + productId, Map.class);
        //方式二：使用LoadBalancerClient调用
        ServiceInstance instance = loadBalancer.choose("product-service");
        URI storesUri = URI.create(String.format("http://%s:%s/api/v1/product/find?id="+productId, instance.getHost(), instance.getPort()));
        RestTemplate restTemplate2 = new RestTemplate();
        Map forObject = restTemplate2.getForObject(storesUri, Map.class);

        //打印日志
        System.out.println(forObject);
        logger.info("order server log日志...");
        ProductOrder productOrder = new ProductOrder();
        productOrder.setCreateTime(new Date());
        productOrder.setUserId(userId);
        productOrder.setTradeNo(UUID.randomUUID().toString());
        productOrder.setProductName(forObject.get("name").toString());
        productOrder.setPrice(Integer.parseInt(forObject.get("price").toString()));

        return productOrder;
    }

    @Override
    public ProductOrder findById(int id) {
        logger.info("order server log日志...");
        String response = productClient.findById(id);
        JSONObject forObject = JSON.parseObject(response);
        logger.info("order server log日志...");
        //打印日志
        System.out.println(forObject);

        ProductOrder productOrder = new ProductOrder();
        productOrder.setCreateTime(new Date());
        productOrder.setProductName(forObject.get("name").toString());
        productOrder.setPrice(Integer.parseInt(forObject.get("price").toString()));
        return productOrder;
    }


}
