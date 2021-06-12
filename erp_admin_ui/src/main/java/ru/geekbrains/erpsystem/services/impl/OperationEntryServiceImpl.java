package ru.geekbrains.erpsystem.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.erpsystem.data.OperationEntryData;
import ru.geekbrains.erpsystem.entities.OperationEntry;
import ru.geekbrains.erpsystem.exeptions.NotFoundException;
import ru.geekbrains.erpsystem.repositories.OperationEntryRepository;
import ru.geekbrains.erpsystem.repositories.TechnologyRepository;
import ru.geekbrains.erpsystem.services.OperationEntryService;
import ru.geekbrains.erpsystem.services.OperationService;
import ru.geekbrains.erpsystem.services.TechnologyService;
import ru.geekbrains.erpsystem.services.WorkcellService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OperationEntryServiceImpl implements OperationEntryService {

    @Autowired
    private WorkcellService workcellService;
    @Autowired
    private OperationEntryRepository operationEntryRepository;
    @Autowired
    private OperationService operationService;
    @Autowired
    private TechnologyRepository technologyRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public OperationEntryData insert(OperationEntryData data) throws NotFoundException {
        OperationEntry oe = new OperationEntry();
        oe.setId(null);
        oe.setStartDateTime(data.getStartDateTime());
        oe.setDuration(data.getDuration());
        oe.setQty(data.getQty());
        oe.setTurn(data.getTurn());

        oe.setWorkcell(workcellService.getByName(data.getWorkcell()).orElseThrow(
                () -> new NotFoundException(String.format("Не найден участок '%s'", data.getWorkcell()))
        ));
        oe.setOperation(operationService.getByName(data.getOpName()).orElseThrow(
                () -> new NotFoundException(String.format("Не найдена операция '%s'", data.getOpName())))
        );
        oe.setTechnology(technologyRepository.findById(data.getTechnologyId()).orElseThrow(
                () -> new NotFoundException(String.format("Не найдена технология с id = %s", data.getTechnologyId()))
        ));

        try {
            oe.setParams(mapper.writeValueAsString(data.getParams()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            oe.setParams("");
        }

        return new OperationEntryData(operationEntryRepository.save(oe));
    }

    @Override
    public OperationEntryData update(OperationEntryData data) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void delete(Long id) {
        operationEntryRepository.deleteById(id);
    }

    @Override
    public Optional<OperationEntryData> getById(Long id) {
        return operationEntryRepository.findById(id).map(OperationEntryData::new);
    }

    @Override
    public List<OperationEntryData> getAll() {
        return operationEntryRepository.findAll().stream()
                .map(OperationEntryData::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<OperationEntryData> findAllById(List<Long> ids) {
        return operationEntryRepository.findAllById(ids).stream()
                .map(OperationEntryData::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<OperationEntryData> findAllByTechnologyId(Long id) {
        return operationEntryRepository.findAllByTechnologyId(id).stream()
                .map(OperationEntryData::new)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByTechnologyId(Long id) {
        operationEntryRepository.deleteByTechnologyId(id);
    }

}
