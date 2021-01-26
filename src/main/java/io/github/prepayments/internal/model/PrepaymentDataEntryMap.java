package io.github.prepayments.internal.model;

import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.dto.PrepaymentEntryDTO;
import org.springframework.stereotype.Component;

@Component
public class PrepaymentDataEntryMap implements PrepaymentDataEntryDTOMapping  {


    @Override
    public PrepaymentDataDTO toValue1(PrepaymentEntryDTO arg0) {
        if ( arg0 == null ) {
            return null;
        }

        PrepaymentDataDTO prepaymentDataDTO = new PrepaymentDataDTO();

        prepaymentDataDTO.setId( arg0.getPrepaymentDataId() );
        prepaymentDataDTO.setAccountName( arg0.getAccountName() );
        prepaymentDataDTO.setDescription( arg0.getDescription() );
        prepaymentDataDTO.setAccountNumber( arg0.getAccountNumber() );
        prepaymentDataDTO.setPrepaymentNumber( arg0.getPrepaymentNumber() );
        prepaymentDataDTO.setPrepaymentDate( arg0.getPrepaymentDate() );
        prepaymentDataDTO.setUploadToken( arg0.getUploadToken() );
        prepaymentDataDTO.setPrepaymentAmount(arg0.getTransactionAmount());

        return prepaymentDataDTO;
    }

    @Override
    public PrepaymentEntryDTO toValue2(PrepaymentDataDTO arg0) {
        if ( arg0 == null ) {
            return null;
        }

        PrepaymentEntryDTO prepaymentEntryDTO = new PrepaymentEntryDTO();

        prepaymentEntryDTO.setId( arg0.getId() );
        prepaymentEntryDTO.setAccountName( arg0.getAccountName() );
        prepaymentEntryDTO.setDescription( arg0.getDescription() );
        prepaymentEntryDTO.setAccountNumber( arg0.getAccountNumber() );
        prepaymentEntryDTO.setPrepaymentNumber( arg0.getPrepaymentNumber() );
        prepaymentEntryDTO.setPrepaymentDate( arg0.getPrepaymentDate() );
        prepaymentEntryDTO.setUploadToken( arg0.getUploadToken() );
        prepaymentEntryDTO.setTransactionAmount(arg0.getPrepaymentAmount());
        prepaymentEntryDTO.setPrepaymentDataId(arg0.getId());

        return prepaymentEntryDTO;
    }
}
