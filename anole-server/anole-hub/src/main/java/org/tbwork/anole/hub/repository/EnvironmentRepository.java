package org.tbwork.anole.hub.repository;

import java.util.List;

import org.anole.infrastructure.model.AnoleEnvironment;

public interface EnvironmentRepository {

	List<AnoleEnvironment> getEnviroments();
}
