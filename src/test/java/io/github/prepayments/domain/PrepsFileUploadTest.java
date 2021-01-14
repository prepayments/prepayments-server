package io.github.prepayments.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.prepayments.web.rest.TestUtil;

public class PrepsFileUploadTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrepsFileUpload.class);
        PrepsFileUpload prepsFileUpload1 = new PrepsFileUpload();
        prepsFileUpload1.setId(1L);
        PrepsFileUpload prepsFileUpload2 = new PrepsFileUpload();
        prepsFileUpload2.setId(prepsFileUpload1.getId());
        assertThat(prepsFileUpload1).isEqualTo(prepsFileUpload2);
        prepsFileUpload2.setId(2L);
        assertThat(prepsFileUpload1).isNotEqualTo(prepsFileUpload2);
        prepsFileUpload1.setId(null);
        assertThat(prepsFileUpload1).isNotEqualTo(prepsFileUpload2);
    }
}
