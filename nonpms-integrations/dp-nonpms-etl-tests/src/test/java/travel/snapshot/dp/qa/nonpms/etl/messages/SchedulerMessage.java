package travel.snapshot.dp.qa.nonpms.etl.messages;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class SchedulerMessage {

    @NonNull
    Instant fireTime;

    String propertyId;

    LocalDate integrationDate;

    Boolean isCurrentDay;

    Boolean overrideData;

}
