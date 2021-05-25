package com.trace.platform.resource;

import com.trace.platform.entity.Account;
import com.trace.platform.entity.ProductMaterialRel;
import com.trace.platform.entity.Stock;
import com.trace.platform.repository.*;
import com.trace.platform.resource.dto.*;
import com.trace.platform.resource.pojo.PageableResponse;
import com.trace.platform.service.IStockService;
import com.trace.platform.service.dto.StockCreateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
    private OrderRepository orderRepository;

    @Autowired
    private ProductMaterialRelRepository productMaterialRelRepository;

    @Autowired
    private IStockService iStockService;

    /*
    *   添加已有商品进入库存
    * */
    @PostMapping
    public ResponseEntity createStock(StockCreateRequest stockCreateRequest) {
        Account account = accountRepository.findByName(stockCreateRequest.getAccountName());
        if (account == null) {
            return new ResponseEntity("不存在该用户", HttpStatus.NOT_FOUND);
        }
        if (!productRepository.findById(stockCreateRequest.getProductId()).isPresent()) {
            return new ResponseEntity("不存在该产品", HttpStatus.NOT_FOUND);
        }

        String batchId = UUID.randomUUID().toString().replace("-","").toLowerCase();
        Date date = new Date();
        Stock stock = new Stock();
        stock.setAccountId(stockCreateRequest.getAccountName());
        stock.setProductId(stockCreateRequest.getProductId());
        stock.setBatchId(batchId);
        stock.setDate(date);
        stock.setPrice(stockCreateRequest.getPrice());
        stock.setQuantity(stockCreateRequest.getQuantity());
        stock.setRestQuantity(stockCreateRequest.getQuantity());
        stock.setStatus(Stock.ON_SAVING);

        Stock savedStock = stockRepository.save(stock);
        return new ResponseEntity(savedStock, HttpStatus.OK);
    }

    /*
    *  保存商品批次到区块链中
    * */
    @PostMapping("/save")
    @Transactional
    public ResponseEntity saveStock(@RequestBody StockSaveRequest stockSaveRequest) {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByName(currentUsername);
        Stock stock = stockSaveRequest.getStock();

        List<FormedBatch> batchList = stockSaveRequest.getBatchList();
        Map<String, List<String>> form = new HashMap<>();
        for (FormedBatch batch : batchList) {
            Set<String> keySet = batch.getBatchesNumMap().keySet();
            List<String> batches = new ArrayList<>(keySet);
            form.put(batch.getProductName(), batches);
        }

        com.trace.platform.service.dto.StockCreateRequest request = new com.trace.platform.service.dto.StockCreateRequest();
        request.setAccountName(currentUsername);
        request.setBatchId(stock.getBatchId());
        request.setDate(stock.getDate());
        request.setForm(form);
        request.setProductId(stock.getProductId());
        request.setProductName(stockSaveRequest.getProductName());
        request.setUnit(stockSaveRequest.getUnit());
        request.setQuantity(stock.getQuantity());
        request.setClientCrt(stockSaveRequest.getClientCrt());
        request.setClientKey(stockSaveRequest.getClientKey());

        StockCreateResponse response = null;
        try {
            request.setServerCrt(new String(Files.readAllBytes(Paths.get(account.getCertificate()))));
            response = iStockService.addStock(request);
        } catch (Exception e) {
            e.printStackTrace();
            stock.setStatus(Stock.FAILED_SAVING);
            stockRepository.save(stock);
            return new ResponseEntity(HttpStatus.EXPECTATION_FAILED);
        }

        if (response != null && response.isSuccess()) {
            stock.setStatus(Stock.FREE);
            stockRepository.save(stock);

            for (FormedBatch batch : batchList) {
                Set<String> keySet = batch.getBatchesNumMap().keySet();
                for (String key : keySet) {
                    ProductMaterialRel productMaterialRel = new ProductMaterialRel();
                    productMaterialRel.setProductName(stockSaveRequest.getProductName());
                    productMaterialRel.setProductBatchId(stock.getBatchId());
                    productMaterialRel.setMaterialName(batch.getProductName());
                    productMaterialRel.setMaterialBatchId(key);
                    productMaterialRel.setProductQuantity(stock.getQuantity());
                    productMaterialRel.setMaterialQuantity(batch.getBatchesNumMap().get(key));
                    productMaterialRel.setDate(stock.getDate());
                    productMaterialRel.setAccountName(stock.getAccountId());
                    productMaterialRelRepository.save(productMaterialRel);

                    Stock materialStock = stockRepository.findByBatchId(key);
                    materialStock.setQuantity(materialStock.getQuantity() - batch.getBatchesNumMap().get(key));
                    stockRepository.save(materialStock);
                }
            }

            return new ResponseEntity(response.getMessage(), HttpStatus.OK);
        } else {
            stock.setStatus(Stock.FAILED_SAVING);
            stockRepository.save(stock);
        }

        return new ResponseEntity(HttpStatus.EXPECTATION_FAILED);
    }


    @GetMapping("/pageable/{page}/{size}")
    public PageableResponse<ProductDetailsResponse> getProductInStockPageable(
            @PathVariable("page")int page, @PathVariable("size") int size) {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(page, size);
        PageableResponse<ProductDetailsResponse> response = iStockService.getProductInStockPageable(currentUsername, pageable);
        return response;
    }

    @GetMapping("/product_id/{product_id}")
    public ResponseEntity getStockByProductIdAndAccountId(@PathVariable("product_id") int product_id) {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        List<Stock> stockList = stockRepository.findAllByAccountIdAndProductId(currentUsername, product_id);
        return new ResponseEntity(stockList, HttpStatus.OK);
    }

    @GetMapping("/product_id/{product_id}/pageable/{page}/{size}")
    public PageableResponse<Stock> getStockByProductIdAndAccountIdPageable(@PathVariable("page")int page,
                                                                  @PathVariable("size") int size,
                                                                  @PathVariable("product_id") int product_id) {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<Stock> stockPage = stockRepository.findByAccountIdAndProductIdPageable(currentUsername, product_id, pageable);

        PageableResponse<Stock> response = new PageableResponse<>();
        response.setPage(stockPage.getNumber());
        response.setSize(stockPage.getSize());
        response.setTotalElements(stockPage.getTotalElements());
        response.setTotalPages(stockPage.getTotalPages());
        response.setContents(stockPage.getContent());

        return response;
    }

    @GetMapping("/account_name/{account_name}")
    public ResponseEntity getAllProductInStock(@PathVariable("account_name") String accountName) {
        List<ProductDetailsResponse> productDetailsResponseList = iStockService.getAllProductsInStock(accountName);
        return new ResponseEntity(productDetailsResponseList, HttpStatus.OK);
    }


    @GetMapping("/material/product_name/{product_name}")
    public List<ProductMaterialRel> getProductMaterialAll(@PathVariable("product_name")String productName) {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        return productMaterialRelRepository.findByProductIdAll(productName, currentUsername);
    }

    @GetMapping("/material/product_name/{product_name}/type/{type}/pageable/{page}/{size}")
    public PageableResponse<ProductMaterialRel> getProductMaterialPageable(
            @PathVariable("page")int page, @PathVariable("size") int size, 
            @PathVariable("product_name")String productName, @PathVariable("type") int type) {
        Pageable pageable = PageRequest.of(page, size);
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        Page<ProductMaterialRel> pages = type == 1
                ? productMaterialRelRepository.findByProductIdPageable(productName, currentUsername, pageable)
                : productMaterialRelRepository.findByMaterialIdPageable(productName, currentUsername, pageable);

        PageableResponse<ProductMaterialRel> response = new PageableResponse<>();
        response.setPage(pages.getNumber());
        response.setSize(pages.getSize());
        response.setTotalElements(pages.getTotalElements());
        response.setTotalPages(pages.getTotalPages());
        response.setContents(pages.getContent());

        return response;
    }

    @GetMapping("/personal_stock")
    public PersonalStockResponse getPersonalStock() {
        String currentUsername = (String) SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(0, 5);
//        获取库存商品列表
        PageableResponse<ProductDetailsResponse> response = iStockService.getProductInStockPageable(currentUsername, pageable);

//        获取最近5次入库记录
        Page<Stock> page = stockRepository.findByAccountIdPageable(currentUsername, PageRequest.of(0, 5));

//        获取生产批次数量
        int produceBatches = productMaterialRelRepository.findCountOfProductMaterialBatches(currentUsername);
//        获取交易批次数量
        int txBatches = orderRepository.findCountOfTxBatches(currentUsername);

        PersonalStockResponse personalStockResponse = new PersonalStockResponse();
        personalStockResponse.setProduceBatch(produceBatches);
        personalStockResponse.setTotalBatchNum((int) page.getTotalElements());
        personalStockResponse.setTransactionBatch(txBatches);
        personalStockResponse.setProductsResponse(response);
        personalStockResponse.setRecentStock(page.getContent());

        return personalStockResponse;
    }
}
