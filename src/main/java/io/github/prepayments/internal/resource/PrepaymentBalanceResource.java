package io.github.prepayments.internal.resource;

import io.github.prepayments.internal.model.PrepaymentBalanceDTO;
import io.github.prepayments.internal.service.OutstandingBalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static io.github.prepayments.internal.AppConstants.DATETIME_FORMATTER;

@RestController
@RequestMapping("/api/app")
public class PrepaymentBalanceResource {

    private final OutstandingBalanceService<PrepaymentBalanceDTO> prepaymentOutstandingBalanceService;

    public PrepaymentBalanceResource(final OutstandingBalanceService<PrepaymentBalanceDTO> prepaymentOutstandingBalanceService) {
        this.prepaymentOutstandingBalanceService = prepaymentOutstandingBalanceService;
    }

    /**
     * {@code GET  /prepayment-balance} : Return prepayment as at a given point in time.
     *
     * @param balanceDate the date of the balance
     * @return the {@link ResponseEntity} with a list of prepayment-balance DTO's
     */
    @GetMapping("/prepayment-balance")
    public ResponseEntity<List<PrepaymentBalanceDTO>> getPrepaymentBalance(String balanceDate) {
        return ResponseEntity.ok()
                             // TODO .headers(headers)
                             .body(prepaymentOutstandingBalanceService.outstandingBalance(LocalDate.parse(balanceDate, DATETIME_FORMATTER)));
    }
}
