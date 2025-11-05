package com.smartlogi.smartlogidms.common.service.implementation;

import com.smartlogi.smartlogidms.common.domain.entity.BaseEntity;
import com.smartlogi.smartlogidms.common.domain.repository.GenericRepository;
import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.common.service.BaseCrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class BaseCrudServiceImpl<T extends BaseEntity<ID>, RequestDTO, ResponseDTO, ID> implements BaseCrudService<T,RequestDTO, ResponseDTO, ID> {

    protected final GenericRepository<T, ID> repository;
    protected final BaseMapper<T, RequestDTO, ResponseDTO> mapper;

    protected BaseCrudServiceImpl(GenericRepository<T, ID> repository, BaseMapper<T, RequestDTO, ResponseDTO> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ResponseDTO save(RequestDTO requestDto) {
        T entity = mapper.toEntity(requestDto);
        T savedEntity = repository.save(entity);
        return mapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    public ResponseDTO update(ID id, RequestDTO requestDTO) {

        T existingEntity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));

        mapper.updateEntityFromDto(requestDTO, existingEntity);

        T savedEntity = repository.save(existingEntity);
        return mapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDTO findById(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
        return mapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseDTO> findAll() {
        List<T> entities = repository.findAll();
        return mapper.entitiesToResponseDtos(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResponseDTO> findAll(Pageable pageable) {
        Page<T> entityPage = repository.findAll(pageable);
        return entityPage.map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResponseDTO> findAll(Pageable pageable, Specification<T> spec) {
        Page<T> entityPage =repository.findAll(spec, pageable);
        return entityPage.map(mapper::toDto);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

}