package com.aft.jbpmcuy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.aft.jbpmcuy.domain.Document;

/**
 * Spring Data JPA repository for the Document entity.
 */

public interface DocumentRepository extends JpaRepository<Document,Long> {

    @Query("select document from Document document where document.user.login = ?#{principal.username}")
    List<Document> findByUserIsCurrentUser();

}
