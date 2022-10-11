/*
 * Copyright (c) 2022 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.workers.temporal.scheduling.activities;

import io.airbyte.workers.temporal.exception.RetryableException;
import io.airbyte.workers.temporal.scheduling.RouterService;
import jakarta.inject.Singleton;
import java.io.IOException;

@Singleton
public class RouteToSyncTaskQueueActivityImpl implements RouteToSyncTaskQueueActivity {

  private final RouterService routerService;

  public RouteToSyncTaskQueueActivityImpl(final RouterService routerService) {
    this.routerService = routerService;
  }

  @Override
  public RouteToSyncTaskQueueOutput route(final RouteToSyncTaskQueueInput input) {
    try {
      final String taskQueueForConnectionId = routerService.getTaskQueue(input.getConnectionId());

      return new RouteToSyncTaskQueueOutput(taskQueueForConnectionId);
    } catch (final IOException e) {
      throw new RetryableException(e);
    }
  }

}
