/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.data

import com.example.android.architecture.blueprints.todoapp.data.source.local.TaskDao
import com.example.android.architecture.blueprints.todoapp.data.source.local.toExternal
import com.example.android.architecture.blueprints.todoapp.data.source.network.TaskNetworkDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.network.toLocal
import com.example.android.architecture.blueprints.todoapp.data.source.network.toNetwork
import com.example.android.architecture.blueprints.todoapp.di.ApplicationScope
import com.example.android.architecture.blueprints.todoapp.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject


/**
 * But wait! What if the task ID creation is computationally expensive? Perhaps it uses cryptography to create a hash key for the ID,
 * which takes several seconds. This could lead to UI jank if called on the main thread.
 *
 * The data layer has a responsibility to ensure that long-running or complex tasks do not block the main thread.
 *
 *
 * The Hilt qualifier @ApplicationScope (defined in di/CoroutinesModule.kt) is used to inject a scope that follows the lifecycle of the app.
 */
class DefaultTaskRepository @Inject constructor(
    private val localDataSource: TaskDao,
    private val networkDataSource: TaskNetworkDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
) {
    /**
     * Data should be exposed using flows. This allows callers to be notified of changes over time to that data.
     *
     */

    /**
     * Repositories should expose data from a single source of truth. That is, data should come from only one data source.
     * This could be an in-memory cache, a remote server, or, in this case, the local database.
     *
     * The tasks in the local database can be accessed using TaskDao.observeAll, which conveniently returns a flow.
     * But it's a flow of LocalTask models, in which LocalTask is an internal model that should not be exposed to other architectural layers.
     *
     * You need to transform LocalTask into a Task. This is an external model that forms part of the data layer API.
     */
    fun observeAll(): Flow<List<Task>> {
        return localDataSource.observeAll().map { tasks ->
            tasks.map { it.toExternal() }
        }
    }

    /**
     * 🤔 If you know that the task ID creation is complex, why don't you just hardcode it to be executed using Dispatchers.Default?
     *
     * By specifying it as a parameter to the repository, a different dispatcher can be injected in tests, where it is
     * often desirable to have all instructions executed on the same thread for deterministic behavior.
     */

    /**
     * Why don't you use withContext to wrap insertTask or toLocalModel, like you did before?
     *
     * Firstly, insertTask is provided by the Room library, which takes responsibility for ensuring a non-UI thread is used.
     *
     * Secondly, toLocalModel is an in-memory copy of a single, small object. If it were CPU or I/O bound, or an operation on a collection of objects, then withContext should be used.
     */
    suspend fun create(title: String, description: String): String {
        val taskId = withContext(dispatcher) {
            createTaskId()
        }

        val task = Task(
            id = taskId,
            title = title,
            description = description
        )

        localDataSource.upsert(task.toLocal())
        saveTasksToNetwork()
        return taskId
    }

    suspend fun complete(taskId: String) {
        localDataSource.updateCompleted(taskId, true)
        saveTasksToNetwork()
    }

    suspend fun refresh() {
        val networkTasks = networkDataSource.loadTasks()
        localDataSource.deleteAll()
        val localTasks = withContext(dispatcher) {
            networkTasks.toLocal()
        }
        localDataSource.upsertAll(networkTasks.toLocal())
    }

    private suspend fun saveTasksToNetwork() {
        scope.launch {
            val localTasks = localDataSource.observeAll().first()
            val networkTasks = withContext(dispatcher) {
                localTasks.toNetwork()
            }
            networkDataSource.saveTasks(networkTasks)
        }
    }

    private fun createTaskId(): String {
        return UUID.randomUUID().toString()
    }
}