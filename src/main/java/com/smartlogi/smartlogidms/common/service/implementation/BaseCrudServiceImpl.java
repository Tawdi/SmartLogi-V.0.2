package com.smartlogi.smartlogidms.common.service.implementation;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.common.service.BaseCrudService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public abstract class BaseCrudServiceImpl<T, RequestDTO, ResponseDTO, ID> implements BaseCrudService<RequestDTO, ResponseDTO, ID> {

    protected final JpaRepository<T, ID> repository;
    protected final BaseMapper<T, RequestDTO, ResponseDTO> mapper;

    protected BaseCrudServiceImpl(JpaRepository<T, ID> repository, BaseMapper<T, RequestDTO, ResponseDTO> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ResponseDTO save(RequestDTO requestDto) {
        T entity = mapper.requestDtoToEntity(requestDto);
        T savedEntity = repository.save(entity);
        return mapper.entityToResponseDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResponseDTO> findById(ID id) {

        return repository.findById(id).map(mapper::entityToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseDTO> findAll() {
        List<T> entities =  repository.findAll();
        return mapper.entitiesToResponseDtos(entities);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found with id: " + id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

}