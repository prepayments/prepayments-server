package io.github.prepayments.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.prepayments.web.rest.TestUtil;

public class PrepsFileUploadDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrepsFileUploadDTO.class);
        PrepsFileUploadDTO prepsFileUploadDTO1 = new PrepsFileUploadDTO();
        prepsFileUploadDTO1.setId(1L);
        PrepsFileUploadDTO prepsFileUploadDTO2 = new PrepsFileUploadDTO();
        assertThat(prepsFileUploadDTO1).isNotEqualTo(prepsFileUploadDTO2);
        prepsFileUploadDTO2.setId(prepsFileUploadDTO1.getId());
        assertThat(prepsFileUploadDTO1).isEqualTo(prepsFileUploadDTO2);
        prepsFileUploadDTO2.setId(2L);
        assertThat(prepsFileUploadDTO1).isNotEqualTo(prepsFileUploadDTO2);
        prepsFileUploadDTO1.setId(null);
        assertThat(prepsFileUploadDTO1).isNotEqualTo(prepsFileUploadDTO2);
    }
}
