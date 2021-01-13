package io.github.prepayments.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.prepayments.web.rest.TestUtil;

public class PrepaymentDataTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrepaymentData.class);
        PrepaymentData prepaymentData1 = new PrepaymentData();
        prepaymentData1.setId(1L);
        PrepaymentData prepaymentData2 = new PrepaymentData();
        prepaymentData2.setId(prepaymentData1.getId());
        assertThat(prepaymentData1).isEqualTo(prepaymentData2);
        prepaymentData2.setId(2L);
        assertThat(prepaymentData1).isNotEqualTo(prepaymentData2);
        prepaymentData1.setId(null);
        assertThat(prepaymentData1).isNotEqualTo(prepaymentData2);
    }
}
