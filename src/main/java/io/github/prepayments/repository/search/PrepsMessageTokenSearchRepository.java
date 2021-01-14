package io.github.prepayments.repository.search;

import io.github.prepayments.domain.PrepsMessageToken;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link PrepsMessageToken} entity.
 */
public interface PrepsMessageTokenSearchRepository extends ElasticsearchRepository<PrepsMessageToken, Long> {
}
