package io.github.prepayments.repository.search;

import io.github.prepayments.domain.PrepsFileType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link PrepsFileType} entity.
 */
public interface PrepsFileTypeSearchRepository extends ElasticsearchRepository<PrepsFileType, Long> {
}
