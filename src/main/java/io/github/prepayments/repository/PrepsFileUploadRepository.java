package io.github.prepayments.repository;

import io.github.prepayments.domain.PrepsFileUpload;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the PrepsFileUpload entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PrepsFileUploadRepository extends JpaRepository<PrepsFileUpload, Long>, JpaSpecificationExecutor<PrepsFileUpload> {
}
