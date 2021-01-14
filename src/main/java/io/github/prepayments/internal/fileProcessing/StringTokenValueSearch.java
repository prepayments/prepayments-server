package io.github.prepayments.internal.fileProcessing;

import io.github.prepayments.service.PrepsMessageTokenQueryService;
import io.github.prepayments.service.dto.PrepsMessageTokenCriteria;
import io.github.prepayments.service.dto.PrepsMessageTokenDTO;
import io.github.jhipster.service.filter.StringFilter;
import org.springframework.stereotype.Service;

/**
 * Implementation of token-search where the token value itself is of the value string
 */
@Service("stringTokenValueSearch")
public class StringTokenValueSearch implements TokenValueSearch<String> {

    private final PrepsMessageTokenQueryService messageTokenQueryService;

    public StringTokenValueSearch(final PrepsMessageTokenQueryService messageTokenQueryService) {
        this.messageTokenQueryService = messageTokenQueryService;
    }

    public PrepsMessageTokenDTO getMessageToken(final String tokenValue) {
        StringFilter tokenFilter = new StringFilter();
        tokenFilter.setEquals(tokenValue);
        PrepsMessageTokenCriteria tokenValueCriteria = new PrepsMessageTokenCriteria();
        tokenValueCriteria.setTokenValue(tokenFilter);
        return messageTokenQueryService.findByCriteria(tokenValueCriteria).get(0);
    }
}
