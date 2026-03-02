package org.example.mapper;

import org.example.dto.HistoryDto;
import org.example.entity.History;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HistoryMapper {

    @Mapping(source = "document.id", target = "documentId")
    HistoryDto toDto(History history);

    List<HistoryDto> toListDto(List<History> histories);
}
