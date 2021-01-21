package io.github.prepayments.internal.service;

import io.github.prepayments.service.dto.PrepaymentEntryDTO;
import org.springframework.stereotype.Service;

@Service("prepaymentEntryCompilationDeserializer")
public class PrepaymentEntryCompilationDeserializer implements PrepaymentDataCompilationDeserializer<PrepaymentEntryDTO> {
}
