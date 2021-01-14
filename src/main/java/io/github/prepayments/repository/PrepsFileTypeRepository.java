package io.github.prepayments.repository;

import io.github.prepayments.domain.PrepsFileType;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the PrepsFileType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PrepsFileTypeRepository extends JpaRepository<PrepsFileType, Long>, JpaSpecificationExecutor<PrepsFileType> {
}
