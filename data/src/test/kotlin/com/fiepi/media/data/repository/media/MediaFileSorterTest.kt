/*
 * IsekaiPlayer - Sovereign above myriad realms; shatter every mortal cipher.
 * Copyright (C) 2026 onlymash
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.fiepi.media.data.repository.media

import com.fiepi.media.domain.model.media.MediaFile
import com.fiepi.media.domain.model.preferences.MediaOptions
import com.fiepi.media.domain.model.preferences.SortOrder
import com.fiepi.media.domain.model.preferences.SortType
import com.fiepi.media.domain.model.source.SourceType
import org.junit.Assert.assertEquals
import org.junit.Test

class MediaFileSorterTest {

    private val folderA = MediaFile.Folder(
        path = "/FolderA",
        name = "FolderA",
        sourceType = SourceType.Local,
        lastModified = 1000L,
    )

    private val folderB = MediaFile.Folder(
        path = "/FolderB",
        name = "FolderB",
        sourceType = SourceType.Local,
        lastModified = 3000L,
    )

    private val videoSmall = MediaFile.Video(
        id = "1",
        path = "/VideoSmall.mp4",
        name = "VideoSmall.mp4",
        sourceType = SourceType.Local,
        size = 100L,
        duration = 10L,
        lastModified = 2000L,
    )

    private val videoLarge = MediaFile.Video(
        id = "2",
        path = "/VideoLarge.mp4",
        name = "VideoLarge.mp4",
        sourceType = SourceType.Local,
        size = 1000L,
        duration = 100L,
        lastModified = 4000L,
    )

    @Test
    fun testFoldersFirst_SortBySize_Ascending() {
        val items = listOf(videoLarge, folderB, videoSmall, folderA)
        val options = MediaOptions(
            foldersFirst = true,
            sortType = SortType.Size,
            sortOrder = SortOrder.Ascending
        )

        val sorted = MediaFileSorter.sort(items, options)
        // Folders come first sorted by name (FolderA, FolderB), then videos sorted by size (videoSmall, videoLarge)
        assertEquals(listOf(folderA, folderB, videoSmall, videoLarge), sorted)
    }

    @Test
    fun testFoldersFirst_SortBySize_Descending() {
        val items = listOf(videoSmall, folderA, videoLarge, folderB)
        val options = MediaOptions(
            foldersFirst = true,
            sortType = SortType.Size,
            sortOrder = SortOrder.Descending
        )

        val sorted = MediaFileSorter.sort(items, options)
        // Folders stay first (sorted Z-A -> FolderB, FolderA), then videos (sorted large to small -> videoLarge, videoSmall)
        assertEquals(listOf(folderB, folderA, videoLarge, videoSmall), sorted)
    }

    @Test
    fun testFoldersDisabled_SortBySize_Ascending() {
        val items = listOf(videoLarge, folderB, videoSmall, folderA)
        val options = MediaOptions(
            foldersFirst = false,
            sortType = SortType.Size,
            sortOrder = SortOrder.Ascending
        )

        val sorted = MediaFileSorter.sort(items, options)
        // Folders treated as size 0L -> folderA, folderB (sorted by name), then videoSmall (100L), videoLarge (1000L)
        assertEquals(listOf(folderA, folderB, videoSmall, videoLarge), sorted)
    }

    @Test
    fun testFoldersDisabled_SortBySize_Descending() {
        val items = listOf(videoSmall, folderA, videoLarge, folderB)
        val options = MediaOptions(
            foldersFirst = false,
            sortType = SortType.Size,
            sortOrder = SortOrder.Descending
        )

        val sorted = MediaFileSorter.sort(items, options)
        // Video size descending: videoLarge (1000L), videoSmall (100L), then folders with size 0L (folderB, folderA sorted Z-A)
        assertEquals(listOf(videoLarge, videoSmall, folderB, folderA), sorted)
    }

    @Test
    fun testFoldersFirst_SortByDate_Ascending() {
        val items = listOf(videoLarge, folderB, videoSmall, folderA)
        val options = MediaOptions(
            foldersFirst = true,
            sortType = SortType.Date,
            sortOrder = SortOrder.Ascending
        )

        val sorted = MediaFileSorter.sort(items, options)
        // Folders first (1000L vs 3000L -> folderA, folderB), then videos (2000L vs 4000L -> videoSmall, videoLarge)
        assertEquals(listOf(folderA, folderB, videoSmall, videoLarge), sorted)
    }

    @Test
    fun testFoldersDisabled_SortByDate_Ascending() {
        val items = listOf(videoLarge, folderB, videoSmall, folderA)
        val options = MediaOptions(
            foldersFirst = false,
            sortType = SortType.Date,
            sortOrder = SortOrder.Ascending
        )

        val sorted = MediaFileSorter.sort(items, options)
        // Sorted strictly by date: folderA (1000L), videoSmall (2000L), folderB (3000L), videoLarge (4000L)
        assertEquals(listOf(folderA, videoSmall, folderB, videoLarge), sorted)
    }
}
