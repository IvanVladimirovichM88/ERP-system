package ru.geekbrains.erpsystem.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.erpsystem.data.OperationEntryData;
import ru.geekbrains.erpsystem.data.TechnologyData;
import ru.geekbrains.erpsystem.entities.OperationEntry;
import ru.geekbrains.erpsystem.entities.Technology;
import ru.geekbrains.erpsystem.exeptions.NotFoundException;
import ru.geekbrains.erpsystem.repositories.OperationEntryRepository;
import ru.geekbrains.erpsystem.repositories.TechnologyRepository;
import ru.geekbrains.erpsystem.repositories.UserRepository;
import ru.geekbrains.erpsystem.services.OperationEntryService;
import ru.geekbrains.erpsystem.services.TechnologyService;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TechnologyServiceImpl implements TechnologyService {

    @Autowired
    private TechnologyRepository technologyRepository;
    @Autowired
    private OperationEntryService operationEntryService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    OperationEntryRepository operationEntryRepository;

    @Override
    @Transactional
    public TechnologyData insert(TechnologyData data) {
        if (data.getId() != null) {
            data.setId(null);
        }
        Technology persistedTechnology = persistTechnologyData(data, new Technology());
        data.setId(persistedTechnology.getId());
        data.getOpEntries().forEach(oe -> {
            oe.setTechnologyId(persistedTechnology.getId());
            oe.setId(null);
        });
        operationEntryService.insertOrUpdateAll(data.getOpEntries());

        return data;
    }

    private Technology persistTechnologyData(TechnologyData data, Technology t) {
        t.setId(data.getId());
        t.setTechnologist(userRepository.findById(data.getTechnologistId()).orElseThrow(
                () -> new NotFoundException("Не найден технолог с id = " + data.getTechnologistId())
        ));
        return technologyRepository.save(t);
    }

    @Override
    public TechnologyData update(TechnologyData data) {
        Long technologyId = data.getId();
        if (technologyId == null) {
            throw new IllegalArgumentException("TechnologyService::update -- попытка обновить технологию без id");
        }

        Technology t = technologyRepository.findById(technologyId).orElseThrow(
                () -> new NotFoundException(
                        String.format("TechnologyService::update -- технология с id = %d отсутствует в БД", technologyId)
                )
        );

        if (!t.getTechnologist().getId().equals(data.getTechnologistId())) { //если технолог не изменился, обновлять нечего
            persistTechnologyData(data, t);
        }

        List<OperationEntry> persistedOpEntries = operationEntryRepository.findAllByTechnologyId(technologyId);

        //Удалить OperationEntities, присутствующие в базе, но удаленные пользователем в ходе последнего редактирования технологии.
        List<Long> ids = data.getOpEntries().stream().map(OperationEntryData::getId).collect(Collectors.toList());
        List<OperationEntry> neglectedOpEnts = persistedOpEntries
                .stream()
                .filter(e -> !ids.contains(e.getId()))
                .collect(Collectors.toList());
        operationEntryRepository.deleteAll(neglectedOpEnts);

        /* Если технолог редактирует и обновляет технологию, у плановика не должны обнуляться нормочасы и даты.
        * todo: сделать 2 отдельных метода обновления: для технолога и для плановика.
        *  Плановик перезаписывает даты,технолог - нет.
        *  */
        persistedOpEntries.sort(Comparator.comparingLong(OperationEntry::getId));
        List<Long> persistedIds = persistedOpEntries.stream().map(OperationEntry::getId).collect(Collectors.toUnmodifiableList());
        for (OperationEntryData oed : data.getOpEntries()) {
            if (oed.getStartDateTime() != null || oed.getId() == null) {
                continue;
            }
            int i = Collections.binarySearch(persistedIds, oed.getId());
            if (i >= 0) {
                oed.setDuration(persistedOpEntries.get(i).getDuration());
                oed.setStartDateTime(persistedOpEntries.get(i).getStartDateTime());
                oed.setFinishDateTime(persistedOpEntries.get(i).getFinishDateTime());
            }
        }

        operationEntryService.insertOrUpdateAll(data.getOpEntries());

        return data;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        operationEntryService.deleteByTechnologyId(id);
        technologyRepository.deleteById(id);
    }

    @Override
    public Optional<TechnologyData> getById(Long id) {
        return technologyRepository.findById(id).map(TechnologyData::new);
    }

    @Override
    public List<TechnologyData> getAll() {
        return technologyRepository.findAll().stream()
                .map(TechnologyData::new)
                .collect(Collectors.toUnmodifiableList());
    }

}
