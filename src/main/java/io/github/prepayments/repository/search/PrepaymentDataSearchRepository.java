package io.github.prepayments.repository.search;

import io.github.prepayments.domain.PrepaymentData;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link PrepaymentData} entity.
 */
public interface PrepaymentDataSearchRepository extends ElasticsearchRepository<PrepaymentData, Long> {
}
