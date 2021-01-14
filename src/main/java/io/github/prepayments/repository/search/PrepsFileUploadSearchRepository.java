package io.github.prepayments.repository.search;

import io.github.prepayments.domain.PrepsFileUpload;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link PrepsFileUpload} entity.
 */
public interface PrepsFileUploadSearchRepository extends ElasticsearchRepository<PrepsFileUpload, Long> {
}
