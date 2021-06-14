package ru.geekbrains.erpsystem.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.geekbrains.erpsystem.entities.OperationEntry;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OperationEntryData {

    private Long id;
    private String workcell;
    private String opName;
    private Long technologyId;
    private List<OperationParameterData> params;
    private Integer turn;
    private float duration;
    private Integer qty;
    private LocalDateTime startDateTime;
    private LocalDateTime finishDateTime;

    private static ObjectMapper mapper = new ObjectMapper();

    public OperationEntryData(OperationEntry oe) {
        id = oe.getId();
        workcell = oe.getWorkcell().getName();
        opName = oe.getOperation().getName();
        technologyId = oe.getTechnology().getId();

        try {
            params = Arrays.asList(mapper.readValue(oe.getParams(), OperationParameterData[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            params = Collections.emptyList();
        }

        turn = oe.getTurn();
        duration = oe.getDuration();
        qty = oe.getQty();
        startDateTime = oe.getStartDateTime();
        finishDateTime = oe.getFinishDateTime();
    }

}
