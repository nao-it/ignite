/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite;

import org.apache.ignite.cache.*;
import org.apache.ignite.cache.affinity.*;
import org.apache.ignite.cluster.*;
import org.apache.ignite.configuration.*;
import org.apache.ignite.fs.IgniteFsConfiguration;
import org.apache.ignite.plugin.*;
import org.apache.ignite.internal.product.*;
import org.apache.ignite.hadoop.*;
import org.apache.ignite.plugin.security.*;
import org.apache.ignite.internal.util.typedef.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Main entry-point for all GridGain APIs.
 * You can obtain an instance of {@code Grid} through {@link Ignition#ignite()},
 * or for named grids you can use {@link Ignition#ignite(String)}. Note that you
 * can have multiple instances of {@code Grid} running in the same VM by giving
 * each instance a different name.
 * <p>
 * Note that {@code Grid} extends {@link ClusterGroup} which means that it provides grid projection
 * functionality over the whole grid (instead os a subgroup of nodes).
 * <p>
 * In addition to {@link ClusterGroup} functionality, from here you can get the following:
 * <ul>
 * <li>{@link org.apache.ignite.cache.GridCache} - functionality for in-memory distributed cache.</li>
 * <li>{@link IgniteDataLoader} - functionality for loading data large amounts of data into cache.</li>
 * <li>{@link IgniteFs} - functionality for distributed Hadoop-compliant in-memory file system and map-reduce.</li>
 * <li>{@link IgniteStreamer} - functionality for streaming events workflow with queries and indexes into rolling windows.</li>
 * <li>{@link IgniteScheduler} - functionality for scheduling jobs using UNIX Cron syntax.</li>
 * <li>{@link IgniteProduct} - functionality for licence management and update and product related information.</li>
 * <li>{@link IgniteCompute} - functionality for executing tasks and closures on all grid nodes (inherited form {@link ClusterGroup}).</li>
 * <li>{@link IgniteMessaging} - functionality for topic-based message exchange on all grid nodes (inherited form {@link ClusterGroup}).</li>
 * <li>{@link IgniteEvents} - functionality for querying and listening to events on all grid nodes  (inherited form {@link ClusterGroup}).</li>
 * </ul>
 */
public interface Ignite extends AutoCloseable {
    /**
     * Gets the name of the grid this grid instance (and correspondingly its local node) belongs to.
     * Note that single Java VM can have multiple grid instances all belonging to different grids. Grid
     * name allows to indicate to what grid this particular grid instance (i.e. grid runtime and its
     * local node) belongs to.
     * <p>
     * If default grid instance is used, then
     * {@code null} is returned. Refer to {@link Ignition} documentation
     * for information on how to start named grids.
     *
     * @return Name of the grid, or {@code null} for default grid.
     */
    public String name();

    /**
     * Gets grid's logger.
     *
     * @return Grid's logger.
     */
    public IgniteLogger log();

    /**
     * Gets the configuration of this grid instance.
     * <p>
     * <b>NOTE:</b>
     * <br>
     * SPIs obtains through this method should never be used directly. SPIs provide
     * internal view on the subsystem and is used internally by GridGain kernal. In rare use cases when
     * access to a specific implementation of this SPI is required - an instance of this SPI can be obtained
     * via this method to check its configuration properties or call other non-SPI
     * methods.
     *
     * @return Grid configuration instance.
     */
    public IgniteConfiguration configuration();

    /**
     * Gets an instance of {@link IgniteCluster} interface.
     *
     * @return Instance of {@link IgniteCluster} interface.
     */
    public IgniteCluster cluster();

    /**
     * Gets {@code compute} functionality over this grid projection. All operations
     * on the returned {@link IgniteCompute} instance will only include nodes from
     * this projection.
     *
     * @return Compute instance over this grid projection.
     */
    public IgniteCompute compute();

    /**
     * @param prj Projection.
     * @return Compute instance over given projection.
     */
    public IgniteCompute compute(ClusterGroup prj);

    /**
     * Gets {@code messaging} functionality over this grid projection. All operations
     * on the returned {@link IgniteMessaging} instance will only include nodes from
     * this projection.
     *
     * @return Messaging instance over this grid projection.
     */
    public IgniteMessaging message();

    /**
     * @param prj Projection.
     * @return Messaging instance over given projection.
     */
    public IgniteMessaging message(ClusterGroup prj);

    /**
     * Gets {@code events} functionality over this grid projection. All operations
     * on the returned {@link IgniteEvents} instance will only include nodes from
     * this projection.
     *
     * @return Events instance over this grid projection.
     */
    public IgniteEvents events();

    /**
     * @param prj Projection.
     * @return Events instance over given projection.
     */
    public IgniteEvents events(ClusterGroup prj);

    /**
     * Gets {@code services} functionality over this grid projection. All operations
     * on the returned {@link IgniteMessaging} instance will only include nodes from
     * this projection.
     *
     * @return Services instance over this grid projection.
     */
    public IgniteManaged managed();

    /**
     * @param prj Projection.
     * @return {@code Services} functionality over given projection.
     */
    public IgniteManaged managed(ClusterGroup prj);

    /**
     * Creates new {@link ExecutorService} which will execute all submitted
     * {@link java.util.concurrent.Callable} and {@link Runnable} jobs on nodes in this grid projection.
     * This essentially
     * creates a <b><i>Distributed Thread Pool</i</b> that can be used as a
     * replacement for local thread pools.
     *
     * @return Grid-enabled {@code ExecutorService}.
     */
    public ExecutorService executorService();

    /**
     * @param prj Projection.
     * @return {@link ExecutorService} which will execute jobs on nodes in given projection.
     */
    public ExecutorService executorService(ClusterGroup prj);

    /**
     * Gets information about product as well as license management capabilities.
     *
     * @return Instance of product.
     */
    public IgniteProduct product();

    /**
     * Gets an instance of cron-based scheduler.
     *
     * @return Instance of scheduler.
     */
    public IgniteScheduler scheduler();

    /**
     * Gets an instance of {@code GridSecurity} interface. Available in enterprise edition only.
     *
     * @return Instance of {@code GridSecurity} interface.
     */
    public GridSecurity security();

    /**
     * Gets an instance of {@code GridPortables} interface. Available in enterprise edition only.
     *
     * @return Instance of {@code GridPortables} interface.
     */
    public IgnitePortables portables();

    /**
     * Gets the cache instance for the given name, if one does not
     * exist {@link IllegalArgumentException} will be thrown.
     * Note that in case named cache instance is used as GGFS data or meta cache, {@link IllegalStateException}
     * will be thrown.
     *
     * @param <K> Key type.
     * @param <V> Value type.
     * @param name Cache name.
     * @return Cache instance for given name.
     * @see IgniteFsConfiguration
     * @see IgniteFsConfiguration#getDataCacheName()
     * @see IgniteFsConfiguration#getMetaCacheName()
     */
    public <K, V> GridCache<K, V> cache(@Nullable String name);

    /**
     * Gets all configured caches.
     * Caches that are used as GGFS meta and data caches will not be returned in resulting collection.
     *
     * @see IgniteFsConfiguration
     * @see IgniteFsConfiguration#getDataCacheName()
     * @see IgniteFsConfiguration#getMetaCacheName()
     * @return All configured caches.
     */
    public Collection<GridCache<?, ?>> caches();

    /**
     * Gets an instance of {@link IgniteCache} API. {@code IgniteCache} is a fully-compatible
     * implementation of {@code JCache (JSR 107)} specification.
     *
     * @param name Cache name.
     * @return Instance of the cache for the specified name.
     */
    public <K, V> IgniteCache<K, V> jcache(@Nullable String name);

    /**
     * Gets grid transactions facade.
     *
     * @return Grid transactions facade.
     */
    public IgniteTransactions transactions();

    /**
     * Gets a new instance of data loader associated with given cache name. Data loader
     * is responsible for loading external data into in-memory data grid. For more information
     * refer to {@link IgniteDataLoader} documentation.
     *
     * @param cacheName Cache name ({@code null} for default cache).
     * @return Data loader.
     */
    public <K, V> IgniteDataLoader<K, V> dataLoader(@Nullable String cacheName);

    /**
     * Gets an instance of GGFS - GridGain In-Memory File System, if one is not
     * configured then {@link IllegalArgumentException} will be thrown.
     * <p>
     * GGFS is fully compliant with Hadoop {@code FileSystem} APIs and can
     * be plugged into Hadoop installations. For more information refer to
     * documentation on Hadoop integration shipped with GridGain.
     *
     * @param name GGFS name.
     * @return GGFS instance.
     */
    public IgniteFs fileSystem(String name);

    /**
     * Gets all instances of the grid file systems.
     *
     * @return Collection of grid file systems instances.
     */
    public Collection<IgniteFs> fileSystems();

    /**
     * Gets an instance of Hadoop.
     *
     * @return Hadoop instance.
     */
    public GridHadoop hadoop();

    /**
     * Gets an instance of streamer by name, if one does not exist then
     * {@link IllegalArgumentException} will be thrown.
     *
     * @param name Streamer name.
     * @return Streamer for given name.
     */
    public IgniteStreamer streamer(@Nullable String name);

    /**
     * Gets all instances of streamers.
     *
     * @return Collection of all streamer instances.
     */
    public Collection<IgniteStreamer> streamers();

    /**
     * Will get an atomic sequence from cache and create one if it has not been created yet and {@code create} flag
     * is {@code true}.
     *
     * @param name Sequence name.
     * @param initVal Initial value for sequence. If sequence already cached, {@code initVal} will be ignored.
     * @param create Boolean flag indicating whether data structure should be created if does not exist.
     * @return Sequence for the given name.
     * @throws IgniteCheckedException If sequence could not be fetched or created.
     */
    @Nullable public IgniteAtomicSequence atomicSequence(String name, long initVal, boolean create)
        throws IgniteCheckedException;

    /**
     * Will get a atomic long from cache and create one if it has not been created yet and {@code create} flag
     * is {@code true}.
     *
     * @param name Name of atomic long.
     * @param initVal Initial value for atomic long. If atomic long already cached, {@code initVal}
     *        will be ignored.
     * @param create Boolean flag indicating whether data structure should be created if does not exist.
     * @return Atomic long.
     * @throws IgniteCheckedException If atomic long could not be fetched or created.
     */
    @Nullable public IgniteAtomicLong atomicLong(String name, long initVal, boolean create)
        throws IgniteCheckedException;

    /**
     * Will get a atomic reference from cache and create one if it has not been created yet and {@code create} flag
     * is {@code true}.
     *
     * @param name Atomic reference name.
     * @param initVal Initial value for atomic reference. If atomic reference already cached,
     *      {@code initVal} will be ignored.
     * @param create Boolean flag indicating whether data structure should be created if does not exist.
     * @return Atomic reference for the given name.
     * @throws IgniteCheckedException If atomic reference could not be fetched or created.
     */
    @Nullable public <T> IgniteAtomicReference<T> atomicReference(String name, @Nullable T initVal, boolean create)
        throws IgniteCheckedException;

    /**
     * Will get a atomic stamped from cache and create one if it has not been created yet and {@code create} flag
     * is {@code true}.
     *
     * @param name Atomic stamped name.
     * @param initVal Initial value for atomic stamped. If atomic stamped already cached,
     *      {@code initVal} will be ignored.
     * @param initStamp Initial stamp for atomic stamped. If atomic stamped already cached,
     *      {@code initStamp} will be ignored.
     * @param create Boolean flag indicating whether data structure should be created if does not exist.
     * @return Atomic stamped for the given name.
     * @throws IgniteCheckedException If atomic stamped could not be fetched or created.
     */
    @Nullable public <T, S> IgniteAtomicStamped<T, S> atomicStamped(String name, @Nullable T initVal,
        @Nullable S initStamp, boolean create) throws IgniteCheckedException;

    /**
     * Gets or creates count down latch. If count down latch is not found in cache and {@code create} flag
     * is {@code true}, it is created using provided name and count parameter.
     *
     * @param name Name of the latch.
     * @param cnt Count for new latch creation.
     * @param autoDel {@code True} to automatically delete latch from cache
     *      when its count reaches zero.
     * @param create Boolean flag indicating whether data structure should be created if does not exist.
     * @return Count down latch for the given name.
     * @throws IgniteCheckedException If operation failed.
     */
    @Nullable public IgniteCountDownLatch countDownLatch(String name, int cnt, boolean autoDel, boolean create)
        throws IgniteCheckedException;

    /**
     * Will get a named queue from cache and create one if it has not been created yet and {@code create} flag
     * is {@code true}.
     * If queue is present in cache already, queue properties will not be changed. Use
     * collocation for {@link CacheMode#PARTITIONED} caches if you have lots of relatively
     * small queues as it will make fetching, querying, and iteration a lot faster. If you have
     * few very large queues, then you should consider turning off collocation as they simply
     * may not fit in a single node's memory. However note that in this case
     * to get a single element off the queue all nodes may have to be queried.
     *
     * @param name Name of queue.
     * @param cfg Queue configuration.
     * @param cap Capacity of queue, {@code 0} for unbounded queue.
     * @param create Boolean flag indicating whether data structure should be created if does not exist.
     * @return Queue with given properties.
     * @throws IgniteCheckedException If remove failed.
     */
    @Nullable public <T> IgniteQueue<T> queue(String name,
        IgniteCollectionConfiguration cfg,
        int cap,
        boolean create)
        throws IgniteCheckedException;

    /**
     * Will get a named set from cache and create one if it has not been created yet and {@code create} flag
     * is {@code true}.
     *
     * @param name Set name.
     * @param cfg Set configuration.
     * @param create Flag indicating whether set should be created if does not exist.
     * @return Set with given properties.
     * @throws IgniteCheckedException If failed.
     */
    @Nullable public <T> IgniteSet<T> set(String name,
        IgniteCollectionConfiguration cfg,
        boolean create)
        throws IgniteCheckedException;

    /**
     * Gets an instance of deployed Ignite plugin.
     *
     * @param name Plugin name.
     * @param <T> Plugin type.
     * @return Plugin instance.
     * @throws PluginNotFoundException If plugin for the given name was not found.
     */
    public <T extends IgnitePlugin> T plugin(String name) throws PluginNotFoundException;

    /**
     * Closes {@code this} instance of grid. This method is identical to calling
     * {@link G#stop(String, boolean) G.stop(gridName, true)}.
     * <p>
     * The method is invoked automatically on objects managed by the
     * {@code try-with-resources} statement.
     *
     * @throws IgniteCheckedException If failed to stop grid.
     */
    @Override public void close() throws IgniteCheckedException;

    /**
     * Gets affinity service to provide information about data partitioning
     * and distribution.
     * @param cacheName Cache name.
     * @param <K> Cache key type.
     * @return Affinity.
     */
    public <K> CacheAffinity<K> affinity(String cacheName);
}
