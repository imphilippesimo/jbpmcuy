package com.aft.jbpmcuy.service.util;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.task.UserGroupCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aft.jbpmcuy.domain.Office;
import com.aft.jbpmcuy.domain.Person;
import com.aft.jbpmcuy.repository.OfficeRepository;
import com.aft.jbpmcuy.repository.PersonRepository;
import com.aft.jbpmcuy.repository.UserRepository;

public class DBUserGroupCallBack implements UserGroupCallback {

	/**
	 * 
	 * Implementation of UserGroupCallBack that helps us to use our own set of
	 * user and groups (from the database)
	 * 
	 */

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private OfficeRepository officeRepository;

	private UserRepository userRepository;

	private PersonRepository personRepository;

	public DBUserGroupCallBack(OfficeRepository officeRepository, UserRepository userRepository,
			PersonRepository personRepository) {
		this.userRepository = userRepository;
		this.officeRepository = officeRepository;
		this.personRepository = personRepository;
	}

	@Override
	public boolean existsUser(String login) {

		if (login.equals("Administrator"))
			return true;

		if (userRepository.findOneByLogin(login).isPresent())
			return true;
		return false;

	}

	@Override
	public boolean existsGroup(String officeName) {

		logger.info("checking existence for office " + officeName);

		if (officeName.equals("Administrators"))
			return true;

		if (officeRepository.findOneByName(officeName).isPresent())
			return true;
		return false;
	}

	public List<String> getGroupsForUser(String login, List<String> officesNames) {
		return getGroupsForUser(login);
	}

	@Override
	public List<String> getGroupsForUser(String login, List<String> officesNames,
			List<String> allExistingofficesNames) {
		return getGroupsForUser(login);
	}

	public List<String> getGroupsForUser(String login) {

		logger.info("Getting groups for user " + login);

		Person person = personRepository.findOneByUserLogin(login);
		logger.info(person.getId().toString());

		List<String> groupList = new ArrayList<String>();
		if (login.equals("Administrator")) {
			groupList.add("Administrators");
		} else {
			for (Office office : person.getOffices()) {
				logger.debug("Found " + login + " inside " + office.getName());
				groupList.add(office.getName());
			}

		}
		return groupList;
	}

}
