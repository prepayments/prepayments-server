package io.github.prepayments.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class AmortizationEntryMapperTest {

    private AmortizationEntryMapper amortizationEntryMapper;

    @BeforeEach
    public void setUp() {
        amortizationEntryMapper = new AmortizationEntryMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(amortizationEntryMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(amortizationEntryMapper.fromId(null)).isNull();
    }
}
