package com.orderManagement.orderProcessing.Helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderManagement.orderProcessing.DTOs.Items;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class ItemsConverter implements AttributeConverter<List<Items>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Items> items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert items to JSON", e);
        }
    }

    @Override
    public List<Items> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Items>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to items", e);
        }
    }
}


