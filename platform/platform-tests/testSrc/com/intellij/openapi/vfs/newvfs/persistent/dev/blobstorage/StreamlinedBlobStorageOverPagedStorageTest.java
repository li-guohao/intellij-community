// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.openapi.vfs.newvfs.persistent.dev.blobstorage;

import com.intellij.util.io.PagedFileStorage;
import com.intellij.util.io.blobstorage.SpaceAllocationStrategy;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;


/**
 *
 */
@RunWith(Theories.class)
public class StreamlinedBlobStorageOverPagedStorageTest extends StreamlinedBlobStorageTestBase<StreamlinedBlobStorageOverPagedStorage> {


  public StreamlinedBlobStorageOverPagedStorageTest(final @NotNull Integer pageSize,
                                                    final @NotNull SpaceAllocationStrategy strategy) {
    super(pageSize, strategy);
  }

  @Override
  protected StreamlinedBlobStorageOverPagedStorage openStorage(final Path pathToStorage) throws IOException {
    final PagedFileStorage pagedStorage = new PagedFileStorage(
      pathToStorage,
      LOCK_CONTEXT,
      pageSize,
      true,
      true
    );
    try {
      return new StreamlinedBlobStorageOverPagedStorage(
        pagedStorage,
        allocationStrategy
      );
    }
    catch (Throwable t) {
      storage.close();
      throw t;
    }
  }


  @Override
  public void tearDown() throws Exception {
    if (storage != null) {
      System.out.printf("Storage after test: %d records allocated, %d deleted, %d relocated, live records %.1f%% of total \n",
                        storage.recordsAllocated(),
                        storage.recordsDeleted(),
                        storage.recordsRelocated(),
                        storage.liveRecordsCount() * 100.0 / storage.recordsAllocated()
      );
      System.out.printf("                    %d bytes live payload, %d live capacity, live payload %.1f%% of total storage size \n",
                        storage.totalLiveRecordsPayloadBytes(),
                        storage.totalLiveRecordsCapacityBytes(),
                        storage.totalLiveRecordsPayloadBytes() * 100.0 / storage.sizeInBytes() //including _storage_ header
      );
    }
    super.tearDown();
  }

  @Test
  public void newStorage_HasVersion_OfCurrentStorageFormat() throws Exception {
    assertEquals(
      "New storage version == STORAGE_VERSION_CURRENT",
      storage.getStorageVersion(),
      StreamlinedBlobStorageOverPagedStorage.STORAGE_VERSION_CURRENT
    );
  }
}