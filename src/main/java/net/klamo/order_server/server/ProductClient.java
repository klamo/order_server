package net.klamo.order_server.server;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 商品服务feign客户端
 */
@FeignClient(name = "product-service")
public interface ProductClient {

    @PostMapping("/api/v1/product/find")
    String findById(@RequestParam(value = "id") int id);



}
