package com.smartlogi.smartlogidms.common.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public abstract class BaseCrudServiceImpl<T, ID> implements BaseCrudService<T, ID> {

    private final JpaRepository<T, ID> repository;


    protected BaseCrudServiceImpl(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        if(!repository.existsById(id)){
           throw new ResourceNotFoundException( "Resource not found with id: " + id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(ID id){
        return repository.existsById(id);
    }

}