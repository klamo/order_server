package net.klamo.order_server.controller;


import com.google.common.collect.Maps;
import com.netflix.discovery.converters.Auto;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import net.klamo.order_server.server.ProductOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    private ProductOrderService productOrderService;

    @RequestMapping("save")
    public Object save(@RequestParam("user_id")int userId,@RequestParam("product_id") int productId){
        return  productOrderService.save(userId,productId);
    }

    @RequestMapping("findById")
    @HystrixCommand(fallbackMethod = "findByIdDefaultStores")
    public Object findById(@RequestParam("id") int id){
        HashMap<String, Object> data = Maps.newHashMap();
        data.put("code",-1);
        data.put("data",productOrderService.findById(id));
        return data;
    }

    private Object findByIdDefaultStores(int id){
        HashMap<String, Object> msg = Maps.newHashMap();
        msg.put("code",0);
        msg.put("data","调用人数太多，请稍后再试");
        return msg;
    }
}
