package io.airbyte.workers.helper;

import io.airbyte.config.Geography;

/**
 * Maps a {@link Geography} to a Temporal Task Queue that should be used to run syncs for the given Geography.
 */
public interface GeographyMapper {

  String getTaskQueue(Geography geography);

}
