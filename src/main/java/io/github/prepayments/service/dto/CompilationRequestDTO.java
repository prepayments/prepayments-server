package io.github.prepayments.service.dto;

import java.io.Serializable;
import io.github.prepayments.domain.enumeration.CompilationStatus;
import io.github.prepayments.domain.enumeration.CompilationType;

/**
 * A DTO for the {@link io.github.prepayments.domain.CompilationRequest} entity.
 */
public class CompilationRequestDTO implements Serializable {
    
    private Long id;

    private String description;

    private Long fileUploadId;

    private CompilationStatus compilationStatus;

    private CompilationType compilationType;

    private String compilationToken;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getFileUploadId() {
        return fileUploadId;
    }

    public void setFileUploadId(Long fileUploadId) {
        this.fileUploadId = fileUploadId;
    }

    public CompilationStatus getCompilationStatus() {
        return compilationStatus;
    }

    public void setCompilationStatus(CompilationStatus compilationStatus) {
        this.compilationStatus = compilationStatus;
    }

    public CompilationType getCompilationType() {
        return compilationType;
    }

    public void setCompilationType(CompilationType compilationType) {
        this.compilationType = compilationType;
    }

    public String getCompilationToken() {
        return compilationToken;
    }

    public void setCompilationToken(String compilationToken) {
        this.compilationToken = compilationToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompilationRequestDTO)) {
            return false;
        }

        return id != null && id.equals(((CompilationRequestDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompilationRequestDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", fileUploadId=" + getFileUploadId() +
            ", compilationStatus='" + getCompilationStatus() + "'" +
            ", compilationType='" + getCompilationType() + "'" +
            ", compilationToken='" + getCompilationToken() + "'" +
            "}";
    }
}
