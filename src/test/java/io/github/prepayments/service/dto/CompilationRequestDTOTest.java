package io.github.prepayments.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.prepayments.web.rest.TestUtil;

public class CompilationRequestDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompilationRequestDTO.class);
        CompilationRequestDTO compilationRequestDTO1 = new CompilationRequestDTO();
        compilationRequestDTO1.setId(1L);
        CompilationRequestDTO compilationRequestDTO2 = new CompilationRequestDTO();
        assertThat(compilationRequestDTO1).isNotEqualTo(compilationRequestDTO2);
        compilationRequestDTO2.setId(compilationRequestDTO1.getId());
        assertThat(compilationRequestDTO1).isEqualTo(compilationRequestDTO2);
        compilationRequestDTO2.setId(2L);
        assertThat(compilationRequestDTO1).isNotEqualTo(compilationRequestDTO2);
        compilationRequestDTO1.setId(null);
        assertThat(compilationRequestDTO1).isNotEqualTo(compilationRequestDTO2);
    }
}
