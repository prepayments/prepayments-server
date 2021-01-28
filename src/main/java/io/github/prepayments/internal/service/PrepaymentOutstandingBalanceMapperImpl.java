package io.github.prepayments.internal.service;

import io.github.prepayments.internal.model.PrepaymentBalanceDTO;
import io.github.prepayments.service.dto.PrepaymentEntryDTO;
import org.springframework.stereotype.Component;

@Component
public class PrepaymentOutstandingBalanceMapperImpl implements PrepaymentOutstandingBalanceMapper {

    @Override
    public PrepaymentBalanceDTO map(final PrepaymentEntryDTO prepaymentEntry) {

        return PrepaymentBalanceDTO.builder()
                                   .// TODO Implement this
                                   .build();
    }
}
