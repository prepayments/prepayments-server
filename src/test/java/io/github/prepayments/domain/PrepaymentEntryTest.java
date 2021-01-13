package io.github.prepayments.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.prepayments.web.rest.TestUtil;

public class PrepaymentEntryTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrepaymentEntry.class);
        PrepaymentEntry prepaymentEntry1 = new PrepaymentEntry();
        prepaymentEntry1.setId(1L);
        PrepaymentEntry prepaymentEntry2 = new PrepaymentEntry();
        prepaymentEntry2.setId(prepaymentEntry1.getId());
        assertThat(prepaymentEntry1).isEqualTo(prepaymentEntry2);
        prepaymentEntry2.setId(2L);
        assertThat(prepaymentEntry1).isNotEqualTo(prepaymentEntry2);
        prepaymentEntry1.setId(null);
        assertThat(prepaymentEntry1).isNotEqualTo(prepaymentEntry2);
    }
}
