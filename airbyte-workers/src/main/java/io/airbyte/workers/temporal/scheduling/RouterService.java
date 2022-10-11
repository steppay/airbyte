/*
 * Copyright (c) 2022 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.workers.temporal.scheduling;

import com.google.common.annotations.VisibleForTesting;
import io.airbyte.commons.temporal.TemporalJobType;
import io.airbyte.config.Geography;
import io.airbyte.config.persistence.ConfigRepository;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * Decides which Task Queue should be used for a given connection's sync operations, based on the
 * configured {@link Geography}
 */
@Singleton
@Slf4j
public class RouterService {

  static final String SYNC_AWS_PARIS_TASK_QUEUE = "SYNC_AWS_PARIS";

  private final ConfigRepository configRepository;
  private final Map<Geography, String> geographyToTaskQueueName;

  public RouterService(final ConfigRepository configRepository) {
    this(configRepository, initializeGeographyTaskQueueMap());
  }

  /**
   * Constructor for testing only, so that test geography mappings can be used instead of the mappings
   * from {@link RouterService#initializeGeographyTaskQueueMap()}
   */
  @VisibleForTesting
  RouterService(final ConfigRepository configRepository, final Map<Geography, String> geographyToTaskQueueName) {
    this.configRepository = configRepository;
    this.geographyToTaskQueueName = geographyToTaskQueueName;
  }

  /**
   * This method is the source of truth for mappings between configurable Geographies and which Task
   * Queue will be routed to. Every time a new Geography is added, this method should be updated to
   * indicate which Task Queue should be used for it.
   */
  @VisibleForTesting
  static Map<Geography, String> initializeGeographyTaskQueueMap() {
    final Map<Geography, String> geographyToTaskQueueName = new HashMap<>();

    geographyToTaskQueueName.put(Geography.EU, SYNC_AWS_PARIS_TASK_QUEUE);
    geographyToTaskQueueName.put(Geography.US, TemporalJobType.SYNC.name());
    geographyToTaskQueueName.put(Geography.AUTO, TemporalJobType.SYNC.name());

    return geographyToTaskQueueName;
  }

  /**
   * Given a connectionId, look up the connection's configured {@link Geography} in the config DB and
   * use it to determine which Task Queue should be used for this connection's sync.
   */
  public String getTaskQueue(final UUID connectionId) throws IOException {
    final Geography geography = configRepository.getGeographyForConnection(connectionId);

    if (geographyToTaskQueueName.containsKey(geography)) {
      return geographyToTaskQueueName.get(geography);
    } else {
      throw new IllegalArgumentException(String.format("Unexpected geography %s for connectionId %s", geography, connectionId));
    }
  }

}
