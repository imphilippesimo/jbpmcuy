package com.aft.jbpmcuy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.aft.jbpmcuy.domain.Office;

import com.aft.jbpmcuy.repository.OfficeRepository;
import com.aft.jbpmcuy.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Office.
 */
@RestController
@RequestMapping("/api")
public class OfficeResource {

    private final Logger log = LoggerFactory.getLogger(OfficeResource.class);

    private static final String ENTITY_NAME = "office";
        
    private final OfficeRepository officeRepository;

    public OfficeResource(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    /**
     * POST  /offices : Create a new office.
     *
     * @param office the office to create
     * @return the ResponseEntity with status 201 (Created) and with body the new office, or with status 400 (Bad Request) if the office has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/offices")
    @Timed
    public ResponseEntity<Office> createOffice(@Valid @RequestBody Office office) throws URISyntaxException {
        log.debug("REST request to save Office : {}", office);
        if (office.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new office cannot already have an ID")).body(null);
        }
        Office result = officeRepository.save(office);
        return ResponseEntity.created(new URI("/api/offices/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /offices : Updates an existing office.
     *
     * @param office the office to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated office,
     * or with status 400 (Bad Request) if the office is not valid,
     * or with status 500 (Internal Server Error) if the office couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/offices")
    @Timed
    public ResponseEntity<Office> updateOffice(@Valid @RequestBody Office office) throws URISyntaxException {
        log.debug("REST request to update Office : {}", office);
        if (office.getId() == null) {
            return createOffice(office);
        }
        Office result = officeRepository.save(office);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, office.getId().toString()))
            .body(result);
    }

    /**
     * GET  /offices : get all the offices.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of offices in body
     */
    @GetMapping("/offices")
    @Timed
    public List<Office> getAllOffices() {
        log.debug("REST request to get all Offices");
        List<Office> offices = officeRepository.findAllWithEagerRelationships();
        return offices;
    }

    /**
     * GET  /offices/:id : get the "id" office.
     *
     * @param id the id of the office to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the office, or with status 404 (Not Found)
     */
    @GetMapping("/offices/{id}")
    @Timed
    public ResponseEntity<Office> getOffice(@PathVariable Long id) {
        log.debug("REST request to get Office : {}", id);
        Office office = officeRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(office));
    }

    /**
     * DELETE  /offices/:id : delete the "id" office.
     *
     * @param id the id of the office to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/offices/{id}")
    @Timed
    public ResponseEntity<Void> deleteOffice(@PathVariable Long id) {
        log.debug("REST request to delete Office : {}", id);
        officeRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
