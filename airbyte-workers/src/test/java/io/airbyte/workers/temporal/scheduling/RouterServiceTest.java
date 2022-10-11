/*
 * Copyright (c) 2022 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.workers.temporal.scheduling;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.airbyte.config.Geography;
import io.airbyte.config.persistence.ConfigRepository;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test suite for the {@link RouterService} class.
 */
@ExtendWith(MockitoExtension.class)
class RouterServiceTest {

  private static final UUID CONNECTION_ID = UUID.randomUUID();
  private static final String US_TASK_QUEUE = "US_TASK_QUEUE";
  private static final String EU_TASK_QUEUE = "EU_TASK_QUEUE";

  private static final Map<Geography, String> GEOGRAPHY_MAP = Map.of(
      Geography.AUTO, US_TASK_QUEUE,
      Geography.US, US_TASK_QUEUE,
      Geography.EU, EU_TASK_QUEUE);

  @Mock
  private ConfigRepository mConfigRepository;

  private RouterService routerService;

  @BeforeEach
  void init() {
    routerService = new RouterService(mConfigRepository, GEOGRAPHY_MAP);
  }

  @Test
  void testGetTaskQueue() throws IOException {
    Mockito.when(mConfigRepository.getGeographyForConnection(CONNECTION_ID)).thenReturn(Geography.AUTO);
    assertEquals(US_TASK_QUEUE, routerService.getTaskQueue(CONNECTION_ID));

    Mockito.when(mConfigRepository.getGeographyForConnection(CONNECTION_ID)).thenReturn(Geography.US);
    assertEquals(US_TASK_QUEUE, routerService.getTaskQueue(CONNECTION_ID));

    Mockito.when(mConfigRepository.getGeographyForConnection(CONNECTION_ID)).thenReturn(Geography.EU);
    assertEquals(EU_TASK_QUEUE, routerService.getTaskQueue(CONNECTION_ID));
  }

  /**
   * If this test fails, it likely means that a new value was added to the {@link Geography} enum. A
   * new entry must be added to {@link RouterService#initializeGeographyTaskQueueMap()} to get this
   * test to pass.
   */
  @Test
  void testAllGeographiesHaveAMapping() {
    final Set<Geography> allGeographies = Arrays.stream(Geography.values()).collect(Collectors.toSet());
    final Set<Geography> mappedGeographies = RouterService.initializeGeographyTaskQueueMap().keySet();

    assertEquals(allGeographies, mappedGeographies);
  }

}
