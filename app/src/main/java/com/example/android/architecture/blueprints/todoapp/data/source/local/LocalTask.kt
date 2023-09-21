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

package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.architecture.blueprints.todoapp.data.Task


/**
 * The LocalTask class represents data stored in a table named task in the Room database.
 * It is strongly coupled to Room and shouldn't be used for other data sources such as DataStore.
 *
 * The Local prefix in the class name is used to indicate that this data is stored locally.
 * It is also used to distinguish this class from the Task data model, which is exposed to the other layers in the app.
 * Put another way, LocalTask is internal to the data layer, and Task is external to the data layer.
 *
 *
 */
@Entity(tableName = "task")
data class LocalTask(
    @PrimaryKey val id: String,
    var title: String,
    var description: String,
    var isCompleted: Boolean
)

/**
 * Key point: Mapping functions should live on the boundaries of where they are used. In this case,
 * LocalTask.kt is a good place for mapping functions to and from that type.
 */
fun LocalTask.toExternal() = Task(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)

fun List<LocalTask>.toExternal() = map(LocalTask::toExternal)


/**
 * You're just copying identical fields from one data type to another; why couldn't you just use LocalTask everywhere?
 *
 * The reasons are:
 *
 * Separation of concerns. LocalTask is specifically concerned with how the task is stored in the database and includes extra information (for example, the @Entity Room annotation), which isn't relevant to the other architectural layers.
 * Flexibility. By separating the internal and external models you gain flexibility. You can change the internal storage structure without affecting the other layers. For example, if you want to switch from using Room to DataStore for local storage you could do without breaking the data layer API.
 */