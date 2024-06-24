package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Tcc;
import com.ufpa.lafocabackend.repository.TccRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TccService {

    private final TccRepository tccRepository;


    public TccService(TccRepository tccRepository) {
        this.tccRepository = tccRepository;
    }

    public Tcc save (Tcc tcc) {
        return tccRepository.save(tcc);
    }

    public Page<Tcc> list (Pageable pageable){

        return tccRepository.findAll(pageable);
    }

    public Page<Tcc> list(String name, Pageable pageable) {
        if (name != null && !name.isEmpty()) {
            return tccRepository.findByNameContaining(name, pageable);
        } else {
            return tccRepository.findAll(pageable);
        }
    }

    public Tcc read (Long tccId) {
        return getOrFail(tccId);
    }

    public Tcc update (Tcc tcc) {

        return save(tcc);
    }

    public void delete (Long tccId) {

        try {
            tccRepository.deleteById(tccId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(Tcc.class.getSimpleName(), tccId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(Tcc.class.getSimpleName(), tccId);
        }

    }
    private Tcc getOrFail(Long tccId) {
        return tccRepository.findById(tccId)
                .orElseThrow( () -> new EntityNotFoundException(Tcc.class.getSimpleName(), tccId));
    }
}
