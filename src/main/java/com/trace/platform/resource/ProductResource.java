package com.trace.platform.resource;

import com.trace.platform.entity.Product;
import com.trace.platform.repository.ProductRepository;
import com.trace.platform.resource.dto.ProductCreateRequest;
import com.trace.platform.resource.pojo.PageableRequest;
import com.trace.platform.resource.pojo.PageableResponse;
import com.trace.platform.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;

@RestController
@RequestMapping("/trace/product")
public class ProductResource {

    @Autowired
    private ProductRepository productRepository;

    @Value("${platform.product.img-dir}")
    private String ImgDirUrl;

    @PostMapping
    public ResponseEntity createProduct(ProductCreateRequest productCreateRequest) {
        if (productRepository.findByName(productCreateRequest.getName()) != null) {
            return new ResponseEntity("已存在该产品", HttpStatus.CONFLICT);
        }

        String path = ImgDirUrl + File.separator + productCreateRequest.getName();
        if (!FileUtil.existsOrCreateDir(path)) {
            return new ResponseEntity("创建产品图片文件夹失败",HttpStatus.INTERNAL_SERVER_ERROR);
        }

        MultipartFile[] images = productCreateRequest.getImages();
        for (MultipartFile image : images) {
            try {
                String originImgName = image.getOriginalFilename();
                File file = new File(new File(path).getAbsolutePath() + File.separator + originImgName);
                image.transferTo(file);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity("保存产品图片失败",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        Product product = new Product();
        product.setName(productCreateRequest.getName());
        product.setDescription(productCreateRequest.getDescription());
        product.setDate(new Date());
        product.setImgDirUrl(path);
        product.setSubmitterId(productCreateRequest.getSubmitterId());
        product.setUnit(productCreateRequest.getUnit());
        productRepository.save(product);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping
    public PageableResponse<Product> getProductsPageable(PageableRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Product> productPage = productRepository.findAll(pageable);

        PageableResponse<Product> response = new PageableResponse<>();
        response.setContents(productPage.getContent());
        response.setPage(productPage.getNumber());
        response.setSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());

        return response;
    }
}
