package io.github.prepayments.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.prepayments.web.rest.TestUtil;

public class PrepsMessageTokenTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrepsMessageToken.class);
        PrepsMessageToken prepsMessageToken1 = new PrepsMessageToken();
        prepsMessageToken1.setId(1L);
        PrepsMessageToken prepsMessageToken2 = new PrepsMessageToken();
        prepsMessageToken2.setId(prepsMessageToken1.getId());
        assertThat(prepsMessageToken1).isEqualTo(prepsMessageToken2);
        prepsMessageToken2.setId(2L);
        assertThat(prepsMessageToken1).isNotEqualTo(prepsMessageToken2);
        prepsMessageToken1.setId(null);
        assertThat(prepsMessageToken1).isNotEqualTo(prepsMessageToken2);
    }
}
