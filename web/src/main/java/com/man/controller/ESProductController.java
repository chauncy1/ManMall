package com.man.controller;

import com.man.common.result.CommonResult;
import com.man.dto.EsProductDTO;
import com.man.service.ESProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/es")
public class ESProductController {

    @Autowired
    ESProductService esProductService;

    @GetMapping("/saveAll")
    public CommonResult<Integer> saveAll() {
        int count = esProductService.saveAll();
        return CommonResult.success(count);
    }

    @GetMapping("/getAll")
    public CommonResult<Page<EsProductDTO>> getAll(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "15") int pageSize) {
        Page<EsProductDTO> result = esProductService.getAll(pageNum, pageSize);
        return CommonResult.success(result);
    }

    @GetMapping("/search/keywords")
    public CommonResult<Page<EsProductDTO>> findByKeywords(
            @RequestParam String keywords,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "15") int pageSize) {
        Page<EsProductDTO> prodPage = esProductService.searchByKeyword(keywords, pageNum, pageSize);
        return CommonResult.success(prodPage);
    }
}
