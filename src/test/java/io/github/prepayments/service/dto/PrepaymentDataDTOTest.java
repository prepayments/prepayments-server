package io.github.prepayments.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.prepayments.web.rest.TestUtil;

public class PrepaymentDataDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrepaymentDataDTO.class);
        PrepaymentDataDTO prepaymentDataDTO1 = new PrepaymentDataDTO();
        prepaymentDataDTO1.setId(1L);
        PrepaymentDataDTO prepaymentDataDTO2 = new PrepaymentDataDTO();
        assertThat(prepaymentDataDTO1).isNotEqualTo(prepaymentDataDTO2);
        prepaymentDataDTO2.setId(prepaymentDataDTO1.getId());
        assertThat(prepaymentDataDTO1).isEqualTo(prepaymentDataDTO2);
        prepaymentDataDTO2.setId(2L);
        assertThat(prepaymentDataDTO1).isNotEqualTo(prepaymentDataDTO2);
        prepaymentDataDTO1.setId(null);
        assertThat(prepaymentDataDTO1).isNotEqualTo(prepaymentDataDTO2);
    }
}
