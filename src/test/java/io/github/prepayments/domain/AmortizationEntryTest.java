package io.github.prepayments.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.prepayments.web.rest.TestUtil;

public class AmortizationEntryTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AmortizationEntry.class);
        AmortizationEntry amortizationEntry1 = new AmortizationEntry();
        amortizationEntry1.setId(1L);
        AmortizationEntry amortizationEntry2 = new AmortizationEntry();
        amortizationEntry2.setId(amortizationEntry1.getId());
        assertThat(amortizationEntry1).isEqualTo(amortizationEntry2);
        amortizationEntry2.setId(2L);
        assertThat(amortizationEntry1).isNotEqualTo(amortizationEntry2);
        amortizationEntry1.setId(null);
        assertThat(amortizationEntry1).isNotEqualTo(amortizationEntry2);
    }
}
