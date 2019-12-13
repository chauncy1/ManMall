package com.man.mapper;

import com.man.dto.EsProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface EsProductRepository extends ElasticsearchRepository<EsProductDTO, Long> {

    Page<EsProductDTO> findByKeywords(String keywords, Pageable pageable);
}
