package io.github.prepayments.internal.model;

import io.github.prepayments.internal.Mapping;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.github.prepayments.internal.AppConstants.DATETIME_FORMATTER;

@Mapper(componentModel = "spring", uses = {})
public interface PrepaymentDataEVMMapping extends Mapping<PrepaymentDataEVM, PrepaymentDataDTO> {

    @Mappings(
        value = {
            @org.mapstruct.Mapping(target = "prepaymentAmount", source = "prepaymentAmount"),
        }
    )
    default BigDecimal toBigDecimalMap(Double doublePrecisionAmount) {
        return NumberUtils.toScaledBigDecimal(doublePrecisionAmount);
    }

    @Mappings(
        value = {
            @org.mapstruct.Mapping(target = "prepaymentAmount", source = "prepaymentAmount"),
        }
    )
    default Double toDoubleMap(BigDecimal toDoublePrecisionAmount) {
        return NumberUtils.toDouble(toDoublePrecisionAmount);
    }

    @Mappings(
        value = {
            @org.mapstruct.Mapping(target = "prepaymentDate", source = "prepaymentDate"),
        }
    )
    default LocalDate toLocalDateMap(String stringDate) {
        return LocalDate.parse(stringDate, DATETIME_FORMATTER);
    }

    @Mappings(
        value = {
            @org.mapstruct.Mapping(target = "prepaymentDate", source = "prepaymentDate"),
        }
    )
    default String toStringDateMap(LocalDate toStringDate) {
        return DATETIME_FORMATTER.format(toStringDate);
    }
}
