package org.example.mapper;

import org.example.dto.DocumentDto;
import org.example.dto.DocumentRequestDto;
import org.example.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = HistoryMapper.class)
public interface DocumentMapper {

    Document toEntity(DocumentDto dto);

    DocumentRequestDto toDto(Document entity);
}
