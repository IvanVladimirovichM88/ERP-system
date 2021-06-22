package ru.geekbrains.erpsystem.data;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.geekbrains.erpsystem.entities.Drawing;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DrawingData implements Serializable {
    private Long id;
    private String path;

    public DrawingData(Drawing drawing){
        this.id=drawing.getId();
        this.path= drawing.getPath();
    }
}
