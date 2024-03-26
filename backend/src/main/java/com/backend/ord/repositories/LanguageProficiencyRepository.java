package com.backend.ord.repositories;

import com.backend.ord.domain.entities.LanguageProficiency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageProficiencyRepository extends JpaRepository<LanguageProficiency, Long> {
}
