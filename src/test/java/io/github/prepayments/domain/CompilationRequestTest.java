package io.github.prepayments.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.prepayments.web.rest.TestUtil;

public class CompilationRequestTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompilationRequest.class);
        CompilationRequest compilationRequest1 = new CompilationRequest();
        compilationRequest1.setId(1L);
        CompilationRequest compilationRequest2 = new CompilationRequest();
        compilationRequest2.setId(compilationRequest1.getId());
        assertThat(compilationRequest1).isEqualTo(compilationRequest2);
        compilationRequest2.setId(2L);
        assertThat(compilationRequest1).isNotEqualTo(compilationRequest2);
        compilationRequest1.setId(null);
        assertThat(compilationRequest1).isNotEqualTo(compilationRequest2);
    }
}
