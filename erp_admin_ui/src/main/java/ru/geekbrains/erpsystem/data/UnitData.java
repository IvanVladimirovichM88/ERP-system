package ru.geekbrains.erpsystem.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.geekbrains.erpsystem.entities.Unit;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UnitData implements Serializable {
    private Long id;
    private String art;
    private String name;
    private UserData constrUserData;
    private boolean isAsm;
    private MaterialData materialData;
    private Float materialAmount;
    private DrawingData drawingData;
    private Set<Long> includedUnitId = new HashSet<>();

    public UnitData(Unit unit){
        this.id = unit.getId();
        this.art = unit.getArt();
        this.name = unit.getName();
        this.constrUserData = new UserData(unit.getConstructor());
        this.isAsm = unit.isAsm();
        this.materialData = new MaterialData(unit.getMaterial());
        this.materialAmount = unit.getMaterialAmount();
        this.drawingData = new DrawingData(unit.getDrawing());
    }
}
