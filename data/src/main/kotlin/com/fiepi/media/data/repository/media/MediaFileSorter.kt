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

internal object MediaFileSorter {

    fun sort(items: List<MediaFile>, options: MediaOptions): List<MediaFile> {
        val comparator = compareBy<MediaFile> {
            if (options.foldersFirst) {
                if (it is MediaFile.Folder) 0 else 1
            } else {
                0
            }
        }.then { item1, item2 ->
            val result = when (options.sortType) {
                SortType.Name -> compareByName(item1, item2)

                SortType.Date -> {
                    val d1 = item1.lastModified ?: 0L
                    val d2 = item2.lastModified ?: 0L
                    val cmp = d1.compareTo(d2)
                    if (cmp != 0) cmp else compareByName(item1, item2)
                }

                SortType.Size -> {
                    val s1 = if (item1 is MediaFile.Folder) 0L else (item1.size ?: 0L)
                    val s2 = if (item2 is MediaFile.Folder) 0L else (item2.size ?: 0L)
                    val cmp = s1.compareTo(s2)
                    if (cmp != 0) cmp else compareByName(item1, item2)
                }

                SortType.Duration -> {
                    val dur1 = (item1 as? MediaFile.Video)?.duration ?: 0L
                    val dur2 = (item2 as? MediaFile.Video)?.duration ?: 0L
                    val cmp = dur1.compareTo(dur2)
                    if (cmp != 0) cmp else compareByName(item1, item2)
                }
            }

            if (options.sortOrder == SortOrder.Ascending) result else -result
        }

        return items.sortedWith(comparator)
    }

    private fun compareByName(item1: MediaFile, item2: MediaFile): Int {
        val cmp = item1.name.compareTo(item2.name, ignoreCase = true)
        return if (cmp != 0) cmp else item1.name.compareTo(item2.name)
    }
}
