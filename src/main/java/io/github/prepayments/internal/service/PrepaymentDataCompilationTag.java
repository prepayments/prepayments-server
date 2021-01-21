package io.github.prepayments.internal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.prepayments.domain.enumeration.CompilationStatus;
import io.github.prepayments.internal.util.TokenGenerator;
import io.github.prepayments.service.CompilationRequestService;
import org.springframework.stereotype.Service;

@Service("prepaymentDataCompilationTag")
public class PrepaymentDataCompilationTag implements CompilationJobTag {

    private final CompilationRequestService compilationRequestService;
    private final TokenGenerator tokenGenerator;

    public PrepaymentDataCompilationTag(final CompilationRequestService compilationRequestService, final TokenGenerator tokenGenerator) {
        this.compilationRequestService = compilationRequestService;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public void tag(final long compilationRequestId) {

        compilationRequestService.findOne(compilationRequestId).ifPresent(found -> {

            // set compilation-status to complete
            found.setCompilationStatus(CompilationStatus.COMPLETE);

            try {
                found.setCompilationToken(tokenGenerator.md5Digest(found));
            } catch (JsonProcessingException e) {
                // TODO Define job action for this
            }

            compilationRequestService.save(found);
        });
    }
}
