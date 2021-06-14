package ru.geekbrains.erpsystem.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.geekbrains.erpsystem.entities.Technology;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TechnologyData {

    private Long id;
    private Long technologistId;
    private List<OperationEntryData> opEntries = new ArrayList<>();

    public TechnologyData(Technology t) {
        id = t.getId();
        technologistId = t.getTechnologist().getId();
        t.getOperationEntries().stream()
                .map(OperationEntryData::new)
                .forEach(opEntries::add);
    }
}
