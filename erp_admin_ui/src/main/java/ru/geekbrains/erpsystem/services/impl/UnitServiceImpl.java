package ru.geekbrains.erpsystem.services.impl;

import org.springframework.stereotype.Service;
import ru.geekbrains.erpsystem.data.UnitData;
import ru.geekbrains.erpsystem.entities.Drawing;
import ru.geekbrains.erpsystem.entities.Material;
import ru.geekbrains.erpsystem.entities.Unit;
import ru.geekbrains.erpsystem.entities.User;
import ru.geekbrains.erpsystem.repositories.DrawingRepository;
import ru.geekbrains.erpsystem.repositories.MaterialRepository;
import ru.geekbrains.erpsystem.repositories.UnitRepository;
import ru.geekbrains.erpsystem.repositories.UserRepository;
import ru.geekbrains.erpsystem.services.UnitService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UnitServiceImpl implements UnitService {
    private final UnitRepository unitRepository;
    private final DrawingRepository drawingRepository;
    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;

    public UnitServiceImpl(UnitRepository unitRepository, DrawingRepository drawingRepository, UserRepository userRepository, MaterialRepository materialRepository) {
        this.unitRepository = unitRepository;
        this.drawingRepository = drawingRepository;
        this.userRepository = userRepository;
        this.materialRepository = materialRepository;
    }


    @Override
    public UnitData insert(UnitData unitData) {
        if (unitData.getId() != null){
            throw new RuntimeException("Unit with id - "+unitData.getId()+" is not empty");
        }

        Unit unit = dataToEntity(unitData);

        return new UnitData( unitRepository.save(unit) );
    }

    @Override
    public UnitData update(UnitData unitData) {

        unitRepository.findById(unitData.getId())
                .orElseThrow(()->new RuntimeException("unit with id - " +unitData.getId()+" is empty"));

        Unit unit = dataToEntity(unitData);

        return new UnitData( unitRepository.save(unit) );
    }

    @Override
    public void delete(Long id) {
        unitRepository.delete(unitRepository.findById(id)
                .orElseThrow(()->new RuntimeException("no Unit with id - "+ id)));
    }

    @Override
    public Optional<UnitData> getById(Long id) {
        return unitRepository.findById(id).map(UnitData::new);
    }

    @Override
    public List<UnitData> getAll() {
        return unitRepository.findAll().stream()
                .map(UnitData::new)
                .collect(Collectors.toUnmodifiableList());
    }

    private Unit dataToEntity(UnitData unitData){
        Drawing drawing = drawingRepository.findById(unitData.getDrawingData().getId())
                .orElseThrow(()->new RuntimeException("drawing id is not found"));

        User user = userRepository.findById(unitData.getConstrUserData().getId())
                .orElseThrow(()->new RuntimeException("user id is not found"));

        Material material = materialRepository.findById(unitData.getMaterialData().getId())
                .orElseThrow(()->new RuntimeException("material id is not found"));

        Unit unit = new Unit();
        unit.setId(unitData.getId());
        unit.setArt(unitData.getArt());
        unit.setName(unitData.getName());
        unit.setConstructor(user);
        unit.setAsm(unitData.isAsm());
        unit.setMaterial(material);
        unit.setMaterialAmount(unitData.getMaterialAmount());
        unit.setDrawing(drawing);

        return unit;
    }
}
