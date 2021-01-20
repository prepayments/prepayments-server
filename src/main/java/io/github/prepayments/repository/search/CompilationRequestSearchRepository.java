package io.github.prepayments.repository.search;

import io.github.prepayments.domain.CompilationRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link CompilationRequest} entity.
 */
public interface CompilationRequestSearchRepository extends ElasticsearchRepository<CompilationRequest, Long> {
}
