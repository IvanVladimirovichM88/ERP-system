package ru.geekbrains.erpsystem.services.impl;

import org.springframework.stereotype.Service;
import ru.geekbrains.erpsystem.data.OperationData;
import ru.geekbrains.erpsystem.entities.Operation;
import ru.geekbrains.erpsystem.repositories.OperationRepository;
import ru.geekbrains.erpsystem.services.OperationService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;

    public OperationServiceImpl(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Override
    public OperationData insert(OperationData operationData) {

        if (operationData.getId() != null){
            throw new RuntimeException("Operation with id -" + operationData.getId() + "is not empty");
        }

        Operation operation = dataToEntity(operationData);

        return new OperationData(operationRepository.save(operation));
    }

    @Override
    public OperationData update(OperationData operationData) {

        if(operationRepository.findById(operationData.getId()).isEmpty()){
            throw new RuntimeException("Operation with id - "+operationData.getId()+" is empty");
        }

        Operation operation = dataToEntity(operationData);

        return new OperationData(operationRepository.save(operation));
    }

    @Override
    public void delete(Long id) {
        operationRepository.delete(operationRepository.findById(id)
        .orElseThrow(()->new RuntimeException("no User with id - "+id)));
    }

    @Override
    public Optional<OperationData> getById(Long id) {
        return operationRepository.findById(id).map(OperationData::new);
    }

    @Override
    public List<OperationData> getAll() {
        return operationRepository.findAll().stream()
                .map(OperationData::new)
                .collect(Collectors.toUnmodifiableList());
    }

    private Operation dataToEntity(OperationData operationData){
        Operation operation = new Operation();
        operation.setId(operationData.getId());
        operation.setName(operationData.getName());
        operation.setDescription(operationData.getDescription());

        return operation;
    }

    @Override
    public Optional<Operation> getByName(String opName) {
        return operationRepository.findByName(opName);
    }

}
