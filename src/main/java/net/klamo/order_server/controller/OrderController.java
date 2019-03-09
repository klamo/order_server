package net.klamo.order_server.controller;

import com.google.common.collect.Maps;
import com.netflix.discovery.converters.Auto;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import net.klamo.order_server.server.ProductOrderService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

  @Autowired private ProductOrderService productOrderService;

  @Autowired private StringRedisTemplate redisTemplate;

  @RequestMapping("save")
  public Object save(
      @RequestParam("user_id") int userId, @RequestParam("product_id") int productId) {
    return productOrderService.save(userId, productId);
  }

  @RequestMapping("findById")
  @HystrixCommand(fallbackMethod = "findByIdDefaultStores")
  public Object findById(@RequestParam("id") int id, HttpServletRequest httpServletRequest) {
    String token = httpServletRequest.getHeader("token");
    String cookie = httpServletRequest.getHeader("cookie");
    String aaa = httpServletRequest.getHeader("aaa");
    Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
    System.out.println("token=" + token + ",cookie=" + cookie+ ",aaa=" + aaa);
    HashMap<String, Object> data = Maps.newHashMap();
    data.put("code", -1);
    data.put("data", productOrderService.findById(id));
    return data;
  }

  private Object findByIdDefaultStores(int id, HttpServletRequest httpServletRequest) {
    String findById = redisTemplate.opsForValue().get("findById");
    String remoteAddr = httpServletRequest.getRemoteAddr();
    new Thread(
            () -> {
              if (StringUtils.isBlank(findById)) {
                System.out.println("服务器:" + remoteAddr + ",出现故障，发送短信！");
                redisTemplate.opsForValue().set("findById", "findById", 10, TimeUnit.SECONDS);
              } else {
                System.out.println("已发送短信，不再重复发送！");
              }
            })
        .start();

    HashMap<String, Object> msg = Maps.newHashMap();
    msg.put("code", 0);
    msg.put("data", "调用人数太多，请稍后再试");
    return msg;
  }
}
