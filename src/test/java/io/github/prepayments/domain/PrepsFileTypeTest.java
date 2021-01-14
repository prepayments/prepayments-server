package io.github.prepayments.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.prepayments.web.rest.TestUtil;

public class PrepsFileTypeTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrepsFileType.class);
        PrepsFileType prepsFileType1 = new PrepsFileType();
        prepsFileType1.setId(1L);
        PrepsFileType prepsFileType2 = new PrepsFileType();
        prepsFileType2.setId(prepsFileType1.getId());
        assertThat(prepsFileType1).isEqualTo(prepsFileType2);
        prepsFileType2.setId(2L);
        assertThat(prepsFileType1).isNotEqualTo(prepsFileType2);
        prepsFileType1.setId(null);
        assertThat(prepsFileType1).isNotEqualTo(prepsFileType2);
    }
}
