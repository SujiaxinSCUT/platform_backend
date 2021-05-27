package com.trace.platform.resource;

import com.trace.platform.entity.*;
import com.trace.platform.repository.AccountRepository;
import com.trace.platform.repository.OrderRepository;
import com.trace.platform.repository.ProductRepository;
import com.trace.platform.repository.StockRepository;
import com.trace.platform.resource.dto.AvgPriceResponse;
import com.trace.platform.resource.dto.TraceRequest;
import com.trace.platform.resource.pojo.PageableResponse;
import com.trace.platform.service.IOrderService;
import com.trace.platform.service.dto.TraceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;


@RestController
@RequestMapping("/trace/admin/order")
public class OrderAdminResource {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private IOrderService iOrderService;

    @GetMapping("/username/{username}/pageable/{page}/{size}")
    public PageableResponse<Order> getOrdersByUsernamePageable(@PathVariable("username")String username,
                                                               @PathVariable("page")int page, @PathVariable("size")int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orderPage = orderRepository.findByUsername(username, pageable);

        PageableResponse<Order> response = new PageableResponse<>();
        response.setContents(orderPage.getContent());
        response.setTotalPages(orderPage.getTotalPages());
        response.setTotalElements(orderPage.getTotalElements());
        response.setSize(orderPage.getSize());
        response.setPage(orderPage.getNumber());

        return response;
    }

    @PostMapping("/trace")
    public ResponseEntity getTraceResult(@RequestBody TraceRequest request) {
        String batchId = request.getBatchId();
        Stock stock = stockRepository.findByBatchId(batchId);
        if (stock == null) {
            return new ResponseEntity("不存在该批次号", HttpStatus.NOT_FOUND);
        }
        Product product = productRepository.findById(stock.getProductId()).get();
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByName(currentUsername);
        TraceResult result = null;
        try {
            if (request.isBack()) {
                result = iOrderService.traceProductBack(stock.getAccountId(),
                        String.valueOf(stock.getProductId()), batchId, currentUsername,
                        request.getClientKey(), request.getClientCrt(), new String(Files.readAllBytes(Paths.get(account.getCertificate()))));
            } else {
                result = iOrderService.traceProduct(stock.getAccountId(),
                        String.valueOf(stock.getProductId()), batchId, currentUsername,
                        request.getClientKey(), request.getClientCrt(), new String(Files.readAllBytes(Paths.get(account.getCertificate()))));
            }
        }  catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("查询出错", HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/avg/product_id/{product_id}/pageable/{page}/{size}")
    public ResponseEntity getAvgPrice(@PathVariable("product_id") int productId,
                                        @PathVariable("page")int page, @PathVariable("size")int size) {
        Double avgPrice = 0.0;
        PageableResponse<OrderWithProduct> orderPage = null;
        try {
            orderPage = iOrderService.getOrderByProductId(productId, PageRequest.of(page, size));
        } catch (ParseException e) {
            e.printStackTrace();
            return new ResponseEntity("查询失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        AvgPriceResponse response = new AvgPriceResponse();

        if (orderPage.getTotalElements() > 0) {
            avgPrice = orderRepository.findAvgPriceByProId(productId);
        }
        response.setAvgPrice(avgPrice);
        response.setPageableResponse(orderPage);

        return new ResponseEntity(response, HttpStatus.OK);
    }
}
