package io.github.prepayments.repository;

import io.github.prepayments.domain.PrepsMessageToken;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the PrepsMessageToken entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PrepsMessageTokenRepository extends JpaRepository<PrepsMessageToken, Long>, JpaSpecificationExecutor<PrepsMessageToken> {
}
