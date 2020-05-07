/*-
 * ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 * The Apache Software License, Version 2.0
 * ——————————————————————————————————————————————————————————————————————————————
 * Copyright (C) 2013 - 2020 Autonomic, LLC - All rights reserved
 * ——————————————————————————————————————————————————————————————————————————————
 * Proprietary and confidential.
 * 
 * NOTICE:  All information contained herein is, and remains the property of
 * Autonomic, LLC and its suppliers, if any.  The intellectual and technical
 * concepts contained herein are proprietary to Autonomic, LLC and its suppliers
 * and may be covered by U.S. and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from Autonomic, LLC.
 * 
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * ______________________________________________________________________________
 */
package shop;

import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

import org.h2.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

import com.github.davidmoten.fsm.persistence.Persistence;

@Service
public class PersistenceService {

	private static final Logger log = LoggerFactory.getLogger(PersistenceService.class);

	private final Persistence persistence;

	public PersistenceService() {
		DataSource dataSource = createDataSource();
		this.persistence = StateMachine.createPersistence( //
				() -> dataSource.getConnection());
		StateMachine.setup(persistence);
	}

	public Persistence get() {
		return persistence;
	}

	private static DataSource createDataSource() {
		log.info("creating data source");
		try {
			// create a new file based db in target on every startup
			File file = File.createTempFile("test", "db", new File("target"));
			return DataSourceBuilder //
					.create() //
					.url("jdbc:h2:file:" + file.getAbsolutePath()) //
					.driverClassName(Driver.class.getName()) //
					.build();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
