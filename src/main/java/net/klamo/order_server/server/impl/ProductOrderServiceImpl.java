package net.klamo.order_server.server.impl;

import net.klamo.order_server.domain.ProductOrder;
import net.klamo.order_server.server.ProductOrderService;
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


    @Override
    public ProductOrder save(int userId, int productId) {
        //方式一：使用注入的restTemplate调用
        //Map forObject = restTemplate.getForObject("http://product-service/api/v1/product/find?id=" + productId, Map.class);
        //方式二：使用LoadBalancerClient调用
        ServiceInstance instance = loadBalancer.choose("product-service");
        URI storesUri = URI.create(String.format("http://%s:%s/api/v1/product/find?id="+productId, instance.getHost(), instance.getPort()));
        RestTemplate restTemplate2 = new RestTemplate();
        Map forObject = restTemplate2.getForObject(storesUri, Map.class);

        //打印日志
        System.out.println(forObject);

        ProductOrder productOrder = new ProductOrder();
        productOrder.setCreateTime(new Date());
        productOrder.setUserId(userId);
        productOrder.setTradeNo(UUID.randomUUID().toString());
        productOrder.setProductName(forObject.get("name").toString());
        productOrder.setPrice(Integer.parseInt(forObject.get("price").toString()));

        return productOrder;
    }
}
