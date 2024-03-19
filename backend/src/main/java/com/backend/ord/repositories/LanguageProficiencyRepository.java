package com.backend.ord.repositories;

import com.backend.ord.domain.entities.LanguageProficiency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageProficiencyRepository extends CrudRepository<LanguageProficiency, Long> {
}
