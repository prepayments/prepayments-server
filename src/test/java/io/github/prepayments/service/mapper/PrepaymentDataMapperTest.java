package io.github.prepayments.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PrepaymentDataMapperTest {

    private PrepaymentDataMapper prepaymentDataMapper;

    @BeforeEach
    public void setUp() {
        prepaymentDataMapper = new PrepaymentDataMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(prepaymentDataMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(prepaymentDataMapper.fromId(null)).isNull();
    }
}
