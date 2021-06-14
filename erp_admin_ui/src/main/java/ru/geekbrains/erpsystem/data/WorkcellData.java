package ru.geekbrains.erpsystem.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.geekbrains.erpsystem.entities.Operation;
import ru.geekbrains.erpsystem.entities.Workcell;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class WorkcellData implements Serializable {
    private Long id;
    private String name;
    private String description;

    public WorkcellData(Workcell workcell){
        this.id = workcell.getId();
        this.name = workcell.getName();
        this.description = workcell.getDescription();
    }

    public Workcell getEntity() {
        Workcell workcell = new Workcell();
        workcell.setId(this.id);
        workcell.setName(this.name);
        workcell.setDescription(this.description);
        return workcell;
    }

}
