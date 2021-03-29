package com.trace.platform.resource;

import com.trace.platform.entity.Product;
import com.trace.platform.repository.ProductRepository;
import com.trace.platform.resource.dto.ProductManageRequest;
import com.trace.platform.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity createProduct(ProductManageRequest productManageRequest) {
        if (productRepository.findByName(productManageRequest.getName()) != null) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        String path = ImgDirUrl + File.separator + productManageRequest.getName();
        if (!FileUtil.existsOrCreateDir(path)) {
            return new ResponseEntity("创建产品图片文件夹失败",HttpStatus.INTERNAL_SERVER_ERROR);
        }

        MultipartFile[] images = productManageRequest.getImages();
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
        product.setName(productManageRequest.getName());
        product.setDescription(productManageRequest.getDescription());
        product.setDate(new Date());
        product.setImgDirUrl(path);
        product.setSubmitterId(productManageRequest.getSubmitterId());
        productRepository.save(product);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
