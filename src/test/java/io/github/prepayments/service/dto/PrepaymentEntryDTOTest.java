package io.github.prepayments.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.prepayments.web.rest.TestUtil;

public class PrepaymentEntryDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrepaymentEntryDTO.class);
        PrepaymentEntryDTO prepaymentEntryDTO1 = new PrepaymentEntryDTO();
        prepaymentEntryDTO1.setId(1L);
        PrepaymentEntryDTO prepaymentEntryDTO2 = new PrepaymentEntryDTO();
        assertThat(prepaymentEntryDTO1).isNotEqualTo(prepaymentEntryDTO2);
        prepaymentEntryDTO2.setId(prepaymentEntryDTO1.getId());
        assertThat(prepaymentEntryDTO1).isEqualTo(prepaymentEntryDTO2);
        prepaymentEntryDTO2.setId(2L);
        assertThat(prepaymentEntryDTO1).isNotEqualTo(prepaymentEntryDTO2);
        prepaymentEntryDTO1.setId(null);
        assertThat(prepaymentEntryDTO1).isNotEqualTo(prepaymentEntryDTO2);
    }
}
