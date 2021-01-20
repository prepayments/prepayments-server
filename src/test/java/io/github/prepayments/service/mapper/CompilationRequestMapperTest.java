package io.github.prepayments.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class CompilationRequestMapperTest {

    private CompilationRequestMapper compilationRequestMapper;

    @BeforeEach
    public void setUp() {
        compilationRequestMapper = new CompilationRequestMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(compilationRequestMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(compilationRequestMapper.fromId(null)).isNull();
    }
}
