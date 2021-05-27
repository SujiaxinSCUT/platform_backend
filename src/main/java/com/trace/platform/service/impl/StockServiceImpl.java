package com.trace.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.trace.platform.repository.StockRepository;
import com.trace.platform.resource.dto.ProductDetailsResponse;
import com.trace.platform.resource.pojo.PageableResponse;
import com.trace.platform.service.IStockService;
import com.trace.platform.service.dto.StockCreateRequest;
import com.trace.platform.service.dto.StockCreateResponse;
import com.trace.platform.utils.DateUtil;
import org.hyperledger.fabric.sdk.FabricClient;
import org.hyperledger.fabric.sdk.util.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StockServiceImpl implements IStockService {

    @Autowired
    private StockRepository stockRepository;

    @Override
    public PageableResponse<ProductDetailsResponse> getProductInStockPageable(String account_name, Pageable pageable) {
        Page<Map<String, Object>> page = stockRepository.findProductInStockPageable(account_name, pageable);
        List<Map<String, Object>> content = page.getContent();
        List<ProductDetailsResponse> list = new ArrayList<>();

        PageableResponse<ProductDetailsResponse> response = new PageableResponse<>();
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setContents(list);

        try {
            for (Map<String, Object> map: content) {
                ProductDetailsResponse productDetailsResponse = new ProductDetailsResponse();
                productDetailsResponse.setId(Integer.valueOf(map.get("id").toString()));
                productDetailsResponse.setDescription((String) map.get("description"));
                productDetailsResponse.setName((String) map.get("name"));
                productDetailsResponse.setUnit((String) map.get("unit"));
                productDetailsResponse.setSum(Double.valueOf(map.get("sum").toString()));
                list.add(productDetailsResponse);
            }
        } catch (Exception e) {
            return response;
        }


        return response;
    }

    @Override
    public List<ProductDetailsResponse> getAllProductsInStock(String account_id) {
        List<Map<String, Object>> mapList = stockRepository.findAllProductsInStock(account_id);
        List<ProductDetailsResponse> list = new ArrayList<>();

        try {
            for (Map<String, Object> map: mapList) {
                ProductDetailsResponse productDetailsResponse = new ProductDetailsResponse();
                productDetailsResponse.setId(Integer.valueOf(map.get("id").toString()));
                productDetailsResponse.setDescription((String) map.get("description"));
                productDetailsResponse.setName((String) map.get("name"));
                productDetailsResponse.setUnit((String) map.get("unit"));
                productDetailsResponse.setSum(Double.valueOf(map.get("sum").toString()));
                list.add(productDetailsResponse);
            }
        } catch (Exception e) {
//            e.printStackTrace();
            return list;
        }


        return list;
    }

    @Override
    public StockCreateResponse addStock(StockCreateRequest request) throws Exception {
        FabricClient client = new FabricClient(request.getAccountName(),
                request.getClientKey(), request.getClientCrt(), request.getServerCrt());
        client.init();
        List<String> formList = new ArrayList<String>();
        Map<Integer, List<String>> maps = request.getForm();
        if (maps != null) {
            Set<Integer> keySet = maps.keySet();
            for (int key : keySet) {
                List<String> list = maps.get(key);
                for (String batchId : list) {
                    formList.add(key + batchId);
                }
            }
        }
        String formStr = JSON.toJSONString(formList);
        Responses responses = client.addProducts(String.valueOf(request.getProductId()), request.getProductName(),
                request.getUnit(), request.getBatchId(), formStr, DateUtil.toNormalizeString(request.getDate()));
        StockCreateResponse response = new StockCreateResponse();
        System.out.println("The response is: " + responses.getMessages());
        response.setSuccess(responses.getCode() == 0);
        response.setMessage(responses.getMessages());
        return response;
    }
}
