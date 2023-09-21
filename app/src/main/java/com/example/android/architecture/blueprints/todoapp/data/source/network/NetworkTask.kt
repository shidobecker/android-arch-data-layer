/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.android.architecture.blueprints.todoapp.data.source.network

import com.example.android.architecture.blueprints.todoapp.data.source.local.LocalTask

/**
 * The task description is named shortDescription instead of description.
 * The isCompleted field is represented as a status enum, which has two possible values: ACTIVE and COMPLETE.
 * It contains an extra priority field, which is an integer.
 */
data class NetworkTask(
    val id: String,
    val title: String,
    val shortDescription: String,
    val priority: Int? = null,
    val status: TaskStatus = TaskStatus.ACTIVE
) {
    enum class TaskStatus {
        ACTIVE,
        COMPLETE
    }
}

fun NetworkTask.toLocal() = LocalTask(
    id = id,
    title = title,
    description = shortDescription,
    isCompleted = (status == NetworkTask.TaskStatus.COMPLETE),
)

fun List<NetworkTask>.toLocal() = map(NetworkTask::toLocal)

fun LocalTask.toNetwork() = NetworkTask(
    id = id,
    title = title,
    shortDescription = description,
    status = if (isCompleted) { NetworkTask.TaskStatus.COMPLETE } else { NetworkTask.TaskStatus.ACTIVE }
)

fun List<LocalTask>.toNetwork() = map(LocalTask::toNetwork)