package dev.golgolex.golgocloud.rank.util;



/*
 * Copyright 2023-2024 golgocloud-module contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeConverter {

    public String convertTime(long millis) {
        if (millis <= -1) {
            return "forever";
        }

        final long MILLIS_PER_SECOND = 1000;
        final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
        final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
        final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;
        final long MILLIS_PER_MONTH = 30 * MILLIS_PER_DAY;
        final long MILLIS_PER_YEAR = 12 * MILLIS_PER_MONTH;

        long years = millis / MILLIS_PER_YEAR;
        millis %= MILLIS_PER_YEAR;

        long months = millis / MILLIS_PER_MONTH;
        millis %= MILLIS_PER_MONTH;

        long days = millis / MILLIS_PER_DAY;
        millis %= MILLIS_PER_DAY;

        long hours = millis / MILLIS_PER_HOUR;
        millis %= MILLIS_PER_HOUR;

        long minutes = millis / MILLIS_PER_MINUTE;
        millis %= MILLIS_PER_MINUTE;

        long seconds = millis / MILLIS_PER_SECOND;

        var result = new StringBuilder();

        if (years > 0) result.append(years).append(" Jahre ");
        if (months > 0) result.append(months).append(" Monate ");
        if (days > 0) result.append(days).append(" Tage ");
        if (hours > 0) result.append(hours).append(" Stunden ");
        if (minutes > 0) result.append(minutes).append(" Minuten ");
        if (seconds > 0 || result.isEmpty()) result.append(seconds).append(" Sekunden");

        return result.toString().trim();
    }

}
