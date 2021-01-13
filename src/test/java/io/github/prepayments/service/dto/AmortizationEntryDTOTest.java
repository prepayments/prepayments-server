package io.github.prepayments.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.prepayments.web.rest.TestUtil;

public class AmortizationEntryDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AmortizationEntryDTO.class);
        AmortizationEntryDTO amortizationEntryDTO1 = new AmortizationEntryDTO();
        amortizationEntryDTO1.setId(1L);
        AmortizationEntryDTO amortizationEntryDTO2 = new AmortizationEntryDTO();
        assertThat(amortizationEntryDTO1).isNotEqualTo(amortizationEntryDTO2);
        amortizationEntryDTO2.setId(amortizationEntryDTO1.getId());
        assertThat(amortizationEntryDTO1).isEqualTo(amortizationEntryDTO2);
        amortizationEntryDTO2.setId(2L);
        assertThat(amortizationEntryDTO1).isNotEqualTo(amortizationEntryDTO2);
        amortizationEntryDTO1.setId(null);
        assertThat(amortizationEntryDTO1).isNotEqualTo(amortizationEntryDTO2);
    }
}
