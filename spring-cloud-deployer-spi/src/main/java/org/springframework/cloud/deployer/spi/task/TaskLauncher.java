/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.deployer.spi.task;

/**
 * SPI defining a runtime environment capable of launching and managing the
 * lifecycle of tasks. The term 'task' in the context of a {@code TaskLauncher}
 * is merely a runtime representation of a container or application wherein the
 * task should be executed.
 *
 * The SPI itself doesn't expect the launcher to keep state of launched tasks,
 * meaning it doesn't need to reconstruct all existing {@link TaskStatus}es or
 * the {@link TaskLaunchId}s needed to resolve a {@link TaskStatus}. The
 * responsibility for keeping track of the state of all existing tasks lies to
 * whomever is using this SPI. It is unrealistic to expect the launcher to be
 * able to store enough information using the underlying infrastructure to
 * reconstruct the full history of task executions.
 *
 * @author Mark Fisher
 * @author Janne Valkealahti
 */
public interface TaskLauncher {

	/**
	 * Launch a task using a {@link TaskLaunchRequest}. The returned
	 * {@link TaskLaunchId} may later be used with {@link #cancel(TaskLaunchId)}
	 * or {@link #status(TaskLaunchId)} to cancel a task or get its status, respectively.
	 *
	 * Implementations may perform this operation asynchronously; therefore a successful
	 * launch may not be assumed upon return. To determine the status of a launch,
	 * invoke {@link #status(TaskLaunchId)}.
	 *
	 * @param request the task launch request
	 * @return the id for the launched task
	 */
	TaskLaunchId launch(TaskLaunchRequest request);

	/**
	 * Cancel the task corresponding to the provided {@link TaskLaunchId}.
	 * Implementations may perform this operation asynchronously; therefore a
	 * successful cancellation may not be assumed upon return. To determine the
	 * status of a cancellation, invoke {@link #status(TaskLaunchId)}.
	 *
	 * @param id
	 *            the task launch id
	 */
	void cancel(TaskLaunchId id);

	/**
	 * Returns the {@link TaskStatus} for a task represented by a {@link TaskLaunchId}.
	 *
	 * @param id the task launch id
	 * @return the task status
	 */
	TaskStatus status(TaskLaunchId id);
}
