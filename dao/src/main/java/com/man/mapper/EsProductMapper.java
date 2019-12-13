package com.man.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.man.dto.EsProductDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EsProductMapper extends BaseMapper<EsProductDTO> {
    List<EsProductDTO> selectAllEsProductInfo();
}
