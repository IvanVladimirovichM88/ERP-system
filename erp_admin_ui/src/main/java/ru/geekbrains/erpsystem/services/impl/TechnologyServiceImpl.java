package ru.geekbrains.erpsystem.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ru.geekbrains.erpsystem.services.OperationService;
import ru.geekbrains.erpsystem.services.TechnologyService;
import ru.geekbrains.erpsystem.services.WorkcellService;

import javax.transaction.Transactional;
import java.util.*;
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
    private WorkcellService workcellService;
    @Autowired
    private OperationService operationService;
    @Autowired
    OperationEntryRepository operationEntryRepository;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional
    public TechnologyData insert(TechnologyData data) {
        Technology t = new Technology();
        t.setId(data.getId());
        t.setTechnologist(userRepository.findById(data.getTechnologistId()).orElseThrow(
                () -> new NotFoundException("Не найден технолог с id = " + data.getTechnologistId())
        ));

        Technology persistedTechnology = technologyRepository.save(t);
        TechnologyData persistedTechnologyData = new TechnologyData(persistedTechnology);

        List<OperationEntry> opEnts = data.getOpEntries().stream()
                .map(dto -> createOpEntryFromOpEntryData(dto, persistedTechnology))
                .collect(Collectors.toList());

        /*
            Удалить OperationEntities, присутствующие в базе, но удаленные пользователем в ходе последнего редактирования технологии.
            todo: сделать удаление sql-запросом
         */
        List<Long> ids = opEnts.stream().map(OperationEntry::getId).collect(Collectors.toList());
        List<OperationEntry> neglectedOpEnts = operationEntryRepository
                .findAllByTechnologyId(persistedTechnology.getId())
                .stream()
                .filter(e -> !ids.contains(e.getId()))
                .collect(Collectors.toList());
        operationEntryRepository.deleteAll(neglectedOpEnts);

        List<OperationEntryData> persistedOpEntDatas = operationEntryRepository.saveAll(opEnts)
                .stream().map(OperationEntryData::new).collect(Collectors.toUnmodifiableList());

        persistedTechnologyData.setOpEntries(persistedOpEntDatas);

        return persistedTechnologyData;
    }

    @Override
    public TechnologyData update(TechnologyData data) {
        throw new RuntimeException("Not implemented -- TechnologyServiceImpl::update");
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

    private OperationEntry createOpEntryFromOpEntryData(OperationEntryData data, Technology t) {
        OperationEntry oe = new OperationEntry();
        oe.setId(data.getId());
        oe.setWorkcell(workcellService.getByName(data.getWorkcell()).orElseThrow(
                () -> new NotFoundException(String.format("Не найден участок '%s'", data.getWorkcell()))
        ));
        oe.setOperation(operationService.getByName(data.getOpName()).orElseThrow(
                () -> new NotFoundException(String.format("Не найдена операция '%s'", data.getOpName()))
        ));
        oe.setTechnology(t);
        try {
            oe.setParams(mapper.writeValueAsString(data.getParams()));
        } catch (JsonProcessingException e) {
            logger.error("Не удалось разобрать параметры операции: " + data.getParams());
            e.printStackTrace();
            oe.setParams("");
        }
        oe.setQty(data.getQty());
        oe.setTurn(data.getTurn());
        oe.setDuration(data.getDuration());
        oe.setStartDateTime(data.getStartDateTime());

        return oe;
    }

}
