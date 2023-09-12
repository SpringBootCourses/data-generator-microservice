package com.example.datageneratormicroservice.web.mapper;

import com.example.datageneratormicroservice.model.test.DataTestOptions;
import com.example.datageneratormicroservice.web.dto.DataTestOptionsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DataTestOptionsMapper
        extends Mappable<DataTestOptions, DataTestOptionsDto> {
}
