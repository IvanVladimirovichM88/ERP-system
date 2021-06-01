package ru.geekbrains.erpsystem.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.geekbrains.erpsystem.entities.Operation;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class OperationData implements Serializable {
    private Long id;
    private String name;
    private String description;

    public OperationData(Operation operation){
        this.id = operation.getId();
        this.name = operation.getName();
        this.description = operation.getDescription();
    }
}
