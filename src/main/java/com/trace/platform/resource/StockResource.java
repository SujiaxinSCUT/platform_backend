package com.trace.platform.resource;

import com.trace.platform.entity.Account;
import com.trace.platform.entity.Stock;
import com.trace.platform.repository.AccountRepository;
import com.trace.platform.repository.ProductRepository;
import com.trace.platform.repository.StockRepository;
import com.trace.platform.resource.dto.ProductDetailsResponse;
import com.trace.platform.resource.dto.StockCreateRequest;
import com.trace.platform.resource.pojo.PageableResponse;
import com.trace.platform.service.IStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trace/business/stock")
public class StockResource {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private IStockService iStockService;

    @PostMapping
    public ResponseEntity addProduct(StockCreateRequest stockCreateRequest) {
        if (!accountRepository.findById(stockCreateRequest.getAccountId()).isPresent()) {
            return new ResponseEntity("不存在该用户", HttpStatus.NOT_FOUND);
        }
        if (!productRepository.findById(stockCreateRequest.getProductId()).isPresent()) {
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

    @GetMapping("/pageable/{page}/{size}")
    public PageableResponse<ProductDetailsResponse> getProductInStockPageable(
            @PathVariable("page")int page, @PathVariable("size") int size) {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        Account currentUser = accountRepository.findByName(currentUsername);
        Pageable pageable = PageRequest.of(page, size);
        PageableResponse<ProductDetailsResponse> response = iStockService.getProductInStockPageable(currentUser.getId(), pageable);
        return response;
    }

    @GetMapping("/product_id/{product_id}")
    public ResponseEntity getStockByProductIdAndAccountId(@PathVariable("product_id") int product_id) {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        Account currentUser = accountRepository.findByName(currentUsername);
        List<Stock> stockList = stockRepository.findAllByAccountIdAndProductId(currentUser.getId(), product_id);
        return new ResponseEntity(stockList, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getAllProductInStock() {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        Account currentUser = accountRepository.findByName(currentUsername);
        List<ProductDetailsResponse> productDetailsResponseList = iStockService.getAllProductsInStock(currentUser.getId());
        return new ResponseEntity(productDetailsResponseList, HttpStatus.OK);
    }
}
