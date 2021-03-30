package com.trace.platform.resource;

import com.trace.platform.entity.Stock;
import com.trace.platform.repository.AccountRepository;
import com.trace.platform.repository.ProductRepository;
import com.trace.platform.repository.StockRepository;
import com.trace.platform.resource.dto.StockCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/trace/business")
public class StockResource {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/stock")
    public ResponseEntity addProduct(StockCreateRequest stockCreateRequest) {
        if (accountRepository.findById(stockCreateRequest.getAccountId()) == null) {
            return new ResponseEntity("不存在该用户", HttpStatus.NOT_FOUND);
        }
        if (productRepository.findById(stockCreateRequest.getProductId()) == null) {
            return new ResponseEntity("不存在该产品", HttpStatus.NOT_FOUND);
        }

        String batchId = UUID.randomUUID().toString().replace("-","").toLowerCase();
        Stock stock = new Stock();
        stock.setAccountId(stockCreateRequest.getAccountId());
        stock.setProductId(stockCreateRequest.getProductId());
        stock.setBatchId(batchId);
        stock.setDate(new Date());
        stock.setPrice(stockCreateRequest.getPrice());
        stock.setQuantity(stockCreateRequest.getQuantity());
        stock.setStatus(Stock.FREE);

        stockRepository.save(stock);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
