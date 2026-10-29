package ru.cohenrol.ap1jvt03.datasource.mapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.cohenrol.ap1jvt03.domain.enums.CellStatus;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Converter
public class CellArrayConverter implements AttributeConverter<CellStatus[][], String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(CellStatus[][] attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Ошибка конвертации массива ячеек в JSON", e);
        }
    }

    @Override
    public CellStatus[][] convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, CellStatus[][].class);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Ошибка чтения JSON массива ячеек из БД", e);
        }
    }
}