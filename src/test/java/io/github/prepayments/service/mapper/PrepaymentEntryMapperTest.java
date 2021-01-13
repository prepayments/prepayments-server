package io.github.prepayments.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PrepaymentEntryMapperTest {

    private PrepaymentEntryMapper prepaymentEntryMapper;

    @BeforeEach
    public void setUp() {
        prepaymentEntryMapper = new PrepaymentEntryMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(prepaymentEntryMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(prepaymentEntryMapper.fromId(null)).isNull();
    }
}
