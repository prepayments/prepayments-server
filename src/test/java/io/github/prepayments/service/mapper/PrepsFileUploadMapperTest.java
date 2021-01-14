package io.github.prepayments.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PrepsFileUploadMapperTest {

    private PrepsFileUploadMapper prepsFileUploadMapper;

    @BeforeEach
    public void setUp() {
        prepsFileUploadMapper = new PrepsFileUploadMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(prepsFileUploadMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(prepsFileUploadMapper.fromId(null)).isNull();
    }
}
