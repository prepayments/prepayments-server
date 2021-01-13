package io.github.prepayments.repository;

import io.github.prepayments.domain.PrepaymentData;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the PrepaymentData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PrepaymentDataRepository extends JpaRepository<PrepaymentData, Long>, JpaSpecificationExecutor<PrepaymentData> {
}
