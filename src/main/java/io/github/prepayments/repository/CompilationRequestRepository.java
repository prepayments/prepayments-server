package io.github.prepayments.repository;

import io.github.prepayments.domain.CompilationRequest;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the CompilationRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompilationRequestRepository extends JpaRepository<CompilationRequest, Long>, JpaSpecificationExecutor<CompilationRequest> {
}
