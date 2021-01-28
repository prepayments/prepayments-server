package io.github.prepayments.internal.service;

import io.github.prepayments.internal.model.PrepaymentBalanceDTO;
import io.github.prepayments.service.dto.PrepaymentEntryDTO;

public interface PrepaymentOutstandingBalanceMapper {

    PrepaymentBalanceDTO map(PrepaymentEntryDTO prepaymentEntry);
}
