package ru.geekbrains.erpsystem.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.erpsystem.data.WorkcellData;
import ru.geekbrains.erpsystem.entities.Workcell;
import ru.geekbrains.erpsystem.repositories.WorkcellRepository;
import ru.geekbrains.erpsystem.services.WorkcellService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkcellServiceImpl implements WorkcellService {

    @Autowired
    private WorkcellRepository workcellRepository;

    @Override
    public WorkcellData insert(WorkcellData var1) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public WorkcellData update(WorkcellData var1) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void delete(Long var1) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Optional<WorkcellData> getById(Long var1) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<WorkcellData> getAll() {
        return workcellRepository.findAll().stream()
                .map(WorkcellData::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<Workcell> getByName(String name) {
        return workcellRepository.findByName(name);
    }


}
