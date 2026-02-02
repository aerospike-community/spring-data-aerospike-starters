/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.util;

import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.EventPolicy;
import com.aerospike.client.async.NettyEventLoops;
import com.aerospike.client.async.NioEventLoops;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.aerospike.AerospikeProperties;
import org.springframework.util.StringUtils;

/**
 * Factory for creating EventLoops for reactive Aerospike clients.
 * This class is isolated to avoid loading Netty classes when not needed.
 * Should only be used in reactive modules where Netty is a required dependency.
 *
 * @author Anastasiia Smirnova
 */
@Slf4j
public class EventLoopsFactory {

    /**
     * Creates and configures EventLoops using Netty for reactive operations.
     *
     * @param eventLoopsProperties the configuration properties for EventLoops
     * @return configured EventLoops instance
     */
    public static EventLoops createEventLoops(AerospikeProperties.EventLoopsProperties eventLoopsProperties) {
        EventPolicy eventPolicy = new EventPolicy();

        if (eventLoopsProperties.maxCommandsInProcess > 0) {
            eventPolicy.maxCommandsInProcess = eventLoopsProperties.maxCommandsInProcess;
        }
        if (eventLoopsProperties.maxCommandsInQueue > 0) {
            eventPolicy.maxCommandsInQueue = eventLoopsProperties.maxCommandsInQueue;
        }
        if (eventLoopsProperties.queueInitialCapacity > 0) {
            eventPolicy.queueInitialCapacity = eventLoopsProperties.queueInitialCapacity;
        }
        if (eventLoopsProperties.minTimeout > 0) {
            eventPolicy.minTimeout = eventLoopsProperties.minTimeout;
        }
        if (eventLoopsProperties.ticksPerWheel > 0) {
            eventPolicy.ticksPerWheel = eventLoopsProperties.ticksPerWheel;
        }
        if (eventLoopsProperties.commandsPerEventLoop > 0) {
            eventPolicy.commandsPerEventLoop = eventLoopsProperties.commandsPerEventLoop;
        }

        if (eventLoopsProperties.eventLoopsType.equalsIgnoreCase("netty")) {
            return new NettyEventLoops(eventPolicy, chooseLoopGroup(eventLoopsProperties));
        }
        return getNioEventLoopsOrFail(eventPolicy, eventLoopsProperties);
    }

    private static EventLoops getNioEventLoopsOrFail(EventPolicy eventPolicy,
                                                     AerospikeProperties.EventLoopsProperties eventLoopsProperties) {
        if (!eventLoopsProperties.eventLoopsType.equalsIgnoreCase("nio")) {
            throw new UnsupportedOperationException(
                    String.format("Expecting 'nio' or 'netty' as eventLoopsType, got '%s' instead",
                            eventLoopsProperties.eventLoopsType)
            );
        }
        int threads = Math.max(eventLoopsProperties.getThreads(), 0);
        boolean useDaemonThreads = eventLoopsProperties.nioDaemonThreads;
        String poolName = eventLoopsProperties.nioPoolName;
        return new NioEventLoops(eventPolicy, threads, useDaemonThreads, poolName);
    }

    private static EventLoopGroup chooseLoopGroup(AerospikeProperties.EventLoopsProperties eventLoopsProperties) {
        int threadsNumber = Math.max(eventLoopsProperties.getThreads(), 0);

        if (!StringUtils.hasText(eventLoopsProperties.groupType)) {
            log.info("Proceeding with standard EventLoops group type 'NioEventLoopGroup'");
            return new NioEventLoopGroup(threadsNumber);
        }

        return switch (eventLoopsProperties.groupType.toLowerCase()) {
            case "epolleventloopgroup" -> createEpollEventLoopGroup(threadsNumber);
            case "kqueueeventloopgroup" -> createKQueueEventLoopGroup(threadsNumber);
            case "nioeventloopgroup" -> new NioEventLoopGroup(threadsNumber);
            default -> {
                log.warn("Unexpected EventLoops group type '{}', proceeding with 'NioEventLoopGroup' instead",
                        eventLoopsProperties.groupType);
                yield new NioEventLoopGroup(threadsNumber);
            }
        };
    }

    private static EventLoopGroup createEpollEventLoopGroup(int threadsNumber) {
        if (Epoll.isAvailable()) {
            log.info("Using EpollEventLoopGroup for native Linux transport");
            return new EpollEventLoopGroup(threadsNumber);
        } else {
            log.warn("EpollEventLoopGroup requested but Epoll is not available on this platform. " +
                            "Cause: {}. Falling back to NioEventLoopGroup.",
                    Epoll.unavailabilityCause() != null ? Epoll.unavailabilityCause().getMessage() : "unknown");
            return new NioEventLoopGroup(threadsNumber);
        }
    }

    private static EventLoopGroup createKQueueEventLoopGroup(int threadsNumber) {
        if (KQueue.isAvailable()) {
            log.info("Using KQueueEventLoopGroup for native macOS/BSD transport");
            return new KQueueEventLoopGroup(threadsNumber);
        } else {
            log.warn("KQueueEventLoopGroup requested but KQueue is not available on this platform. " +
                            "Cause: {}. Falling back to NioEventLoopGroup.",
                    KQueue.unavailabilityCause() != null ? KQueue.unavailabilityCause().getMessage() : "unknown");
            return new NioEventLoopGroup(threadsNumber);
        }
    }
}
