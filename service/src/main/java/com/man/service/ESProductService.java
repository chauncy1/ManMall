package com.man.service;

import com.man.dto.EsProductDTO;
import com.man.mapper.EsProductMapper;
import com.man.mapper.EsProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class ESProductService {

    @Autowired
    EsProductMapper esProductMapper;

    @Autowired
    EsProductRepository esProductRepository;

    public int saveAll() {
        List<EsProductDTO> productList = esProductMapper.selectAllEsProductInfo();
        Iterable<EsProductDTO> esProductIterable = esProductRepository.saveAll(productList);
        Iterator<EsProductDTO> iterator = esProductIterable.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            count++;
            iterator.next();
        }
        return count;
    }

    public Page<EsProductDTO> getAll(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<EsProductDTO> prodPage = esProductRepository.findAll(pageable);
        return prodPage;
    }

    public Page<EsProductDTO> searchByKeyword(String keywords, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return esProductRepository.findByKeywords(keywords, pageable);
    }


}
