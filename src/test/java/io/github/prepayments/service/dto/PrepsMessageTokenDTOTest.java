package io.github.prepayments.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.prepayments.web.rest.TestUtil;

public class PrepsMessageTokenDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrepsMessageTokenDTO.class);
        PrepsMessageTokenDTO prepsMessageTokenDTO1 = new PrepsMessageTokenDTO();
        prepsMessageTokenDTO1.setId(1L);
        PrepsMessageTokenDTO prepsMessageTokenDTO2 = new PrepsMessageTokenDTO();
        assertThat(prepsMessageTokenDTO1).isNotEqualTo(prepsMessageTokenDTO2);
        prepsMessageTokenDTO2.setId(prepsMessageTokenDTO1.getId());
        assertThat(prepsMessageTokenDTO1).isEqualTo(prepsMessageTokenDTO2);
        prepsMessageTokenDTO2.setId(2L);
        assertThat(prepsMessageTokenDTO1).isNotEqualTo(prepsMessageTokenDTO2);
        prepsMessageTokenDTO1.setId(null);
        assertThat(prepsMessageTokenDTO1).isNotEqualTo(prepsMessageTokenDTO2);
    }
}
