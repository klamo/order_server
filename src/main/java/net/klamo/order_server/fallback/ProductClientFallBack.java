package net.klamo.order_server.fallback;

import net.klamo.order_server.server.ProductClient;
import org.springframework.stereotype.Component;


/**
 * 针对商品服务做熔断处理
 */
@Component
public class ProductClientFallBack implements ProductClient {
    @Override
    public String findById(int id) {
        System.out.println("商品服务出错，熔断处理");
        return null;
    }
}
