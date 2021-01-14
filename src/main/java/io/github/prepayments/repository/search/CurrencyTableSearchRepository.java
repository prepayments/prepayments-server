package io.github.prepayments.repository.search;

import io.github.prepayments.domain.CurrencyTable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link CurrencyTable} entity.
 */
public interface CurrencyTableSearchRepository extends ElasticsearchRepository<CurrencyTable, Long> {
}
