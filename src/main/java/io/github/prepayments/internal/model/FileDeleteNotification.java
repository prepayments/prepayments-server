package io.github.prepayments.internal.model;

import io.github.prepayments.domain.enumeration.PrepsFileDeleteProcessType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDeleteNotification implements TokenizableMessage<String>  {

    private String fileId;

    private long timestamp;

    private String filename;

    private String messageToken;

    private String description;

    private PrepsFileDeleteProcessType prepsfileDeleteProcessType;
}
