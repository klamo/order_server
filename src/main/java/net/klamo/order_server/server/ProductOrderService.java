package net.klamo.order_server.server;


import net.klamo.order_server.domain.ProductOrder;

public interface ProductOrderService {

    public ProductOrder save(int userId,int productId);
}
