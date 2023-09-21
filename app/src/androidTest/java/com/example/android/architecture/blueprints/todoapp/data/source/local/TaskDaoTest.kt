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

package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TaskDaoTest {
    private lateinit var database: ToDoDatabase

    @Before
    fun initDb() {
        /*
        This will create an in-memory database before each test. An in-memory database is much faster than a disk-based database.
        This makes it a good choice for automated tests in which the data does not need to persist for longer than the tests.
         */
        database = Room.inMemoryDatabaseBuilder(getApplicationContext(), ToDoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }


    /**
     * Given
     *
     * An empty database
     *
     * When
     *
     * A task is inserted and you start observing the the tasks stream
     *
     * Then
     *
     * The first item in the tasks stream matches the task that was inserted
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertTaskAndGetTasks() = runTest {
        val task = LocalTask(
            title = "title",
            description = "description",
            id = "id",
            isCompleted = false,
        )
        database.taskDao().upsert(task)

        val tasks = database.taskDao().observeAll().first()

        assertEquals(1, tasks.size)
        assertEquals(task, tasks[0])
    }

}