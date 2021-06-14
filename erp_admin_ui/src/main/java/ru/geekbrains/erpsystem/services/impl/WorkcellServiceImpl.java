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
    public WorkcellData insert(WorkcellData workcellData) {
        if (workcellData.getId() != null) {
            throw new RuntimeException(
                    String.format(
                            "Ошибка сохранения в БД. Workcell с id = %d существует", workcellData.getId())
            );
        }
        return new WorkcellData(
                workcellRepository.save(workcellData.getEntity())
        );
    }

    @Override
    public WorkcellData update(WorkcellData workcellData) {
        Optional<Workcell> workcell = workcellRepository.findById(workcellData.getId());
        if (workcell.isEmpty()) {
            throw new RuntimeException(
                    String.format(
                            "Ошибка обновления записи в БД. Workcell с id = %d не существует.", workcellData.getId())
            );
        }
        return new WorkcellData(
                workcellRepository.save(workcellData.getEntity())
        );
    }

    @Override
    public void delete(Long id) {
        workcellRepository.deleteById(id);
    }

    @Override
    public Optional<WorkcellData> getById(Long id) {
        return workcellRepository.findById(id).map(WorkcellData::new);
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
