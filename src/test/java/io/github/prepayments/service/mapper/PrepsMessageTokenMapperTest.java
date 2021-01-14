package io.github.prepayments.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PrepsMessageTokenMapperTest {

    private PrepsMessageTokenMapper prepsMessageTokenMapper;

    @BeforeEach
    public void setUp() {
        prepsMessageTokenMapper = new PrepsMessageTokenMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(prepsMessageTokenMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(prepsMessageTokenMapper.fromId(null)).isNull();
    }
}
