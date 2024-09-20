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

import dev.golgolex.golgocloud.cloudapi.CloudAPI;
import dev.golgolex.quala.translation.basic.Language;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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

    public String convertMillisToDateTime(long millis) {
        if (millis <= -1) {
            return "forever";
        }
        var date = new Date(millis);
        var dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(date);
    }

    public String convertTime(@NotNull Language language, long millis) {
        var translation = CloudAPI.instance().translationAPI().repositoryOf(language, "cloud-module-rank", "global-time-units");

        if (millis <= -1) {
            return translation.message("forever");
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
        if (years == 1) result.append(years).append(" ").append(translation.message("year")).append(" ");
        else if (years > 1) result.append(years).append(" ").append(translation.message("years")).append(" ");

        if (months == 1) result.append(months).append(" ").append(translation.message("month")).append(" ");
        else if (months > 1) result.append(months).append(" ").append(translation.message("months")).append(" ");

        if (days == 1) result.append(days).append(" ").append(translation.message("day")).append(" ");
        else if (days > 1) result.append(days).append(" ").append(translation.message("days")).append(" ");

        if (hours == 1) result.append(hours).append(" ").append(translation.message("hour")).append(" ");
        else if (hours > 1) result.append(hours).append(" ").append(translation.message("hours")).append(" ");

        if (minutes == 1) result.append(minutes).append(" ").append(translation.message("minute")).append(" ");
        else if (minutes > 1) result.append(minutes).append(" ").append(translation.message("minutes")).append(" ");

        if (seconds == 1) result.append(seconds).append(" ").append(translation.message("second"));
        else if (seconds > 1 || result.isEmpty())
            result.append(seconds).append(" ").append(translation.message("seconds"));

        return result.toString().trim();
    }

}
