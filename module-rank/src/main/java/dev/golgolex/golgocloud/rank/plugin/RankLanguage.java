package dev.golgolex.golgocloud.rank.plugin;

import dev.golgolex.quala.translation.basic.Input;
import dev.golgolex.quala.translation.basic.listener.Bounding;
import dev.golgolex.quala.translation.basic.listener.TranslationClass;

import java.util.Arrays;
import java.util.List;

public class RankLanguage implements TranslationClass {

    @Bounding(system = "cloud-module-rank", folder = "ingame-command-rank-usage", type = Bounding.Type.FOLDER)
    public List<Input> onIngameCommands() {
        return Arrays.asList(
                new Input("command-usage-title", "<dark_gray>»</dark_gray> <gray>Manage</gray><dark_gray>:</dark_gray> <yellow>rank</yellow>"),
                new Input("command-usage-line-1", "<dark_gray>»</dark_gray> <dark_gray>/</dark_gray><gray>rank</gray> <yellow>manipulate</yellow> <dark_gray>'</dark_gray><yellow>player</yellow><dark_gray>' -</dark_gray> <gray>Manipulate a player Rank</gray>")
        );
    }

    @Bounding(system = "cloud-module-rank", folder = "ingame-command-rank-manipulate", type = Bounding.Type.FOLDER)
    public List<Input> onManipulateIngameCommands() {
        return Arrays.asList(
                new Input("player-not-found", "<yellow>{0}</yellow> <gray>could <red><u>not</u></red> be found</gray><dark_gray>.</dark_gray>")
        );
    }

    @Bounding(system = "cloud-module-rank", folder = "ingame-inventory-category-overview", type = Bounding.Type.FOLDER)
    public List<Input> onInventoryCategoryOverviewIngame() {
        return Arrays.asList(
                new Input("lore-highest-permissible-group", "<gray>Highest permissible group</gray> <dark_gray>»</dark_gray> <color:{0}>{1}</color>")
        );
    }

    @Bounding(system = "cloud-module-rank", folder = "global-time-units", type = Bounding.Type.FOLDER)
    public List<Input> onGlobalTimeUnits() {
        return Arrays.asList(
                new Input("second", "Second"),
                new Input("seconds", "Seconds"),
                new Input("minute", "Minute"),
                new Input("minutes", "Minutes"),
                new Input("hour", "Hour"),
                new Input("hours", "Hours"),
                new Input("day", "Day"),
                new Input("days", "Days"),
                new Input("week", "Week"),
                new Input("weeks", "Weeks"),
                new Input("month", "Month"),
                new Input("months", "Months"),
                new Input("year", "Year"),
                new Input("years", "Years"),
                new Input("millisecond", "Millisecond"),
                new Input("milliseconds", "Milliseconds"),
                new Input("microsecond", "Microsecond"),
                new Input("microseconds", "Microseconds"),
                new Input("nanosecond", "Nanosecond"),
                new Input("nanoseconds", "Nanoseconds"),
                new Input("picosecond", "Picosecond"),
                new Input("picoseconds", "Picoseconds"),
                new Input("femtosecond", "Femtosecond"),
                new Input("femtoseconds", "Femtoseconds"),
                new Input("attosecond", "Attosecond"),
                new Input("attoseconds", "Attoseconds"),
                new Input("decade", "Decade"),
                new Input("decades", "Decades"),
                new Input("century", "Century"),
                new Input("centuries", "Centuries"),
                new Input("millennium", "Millennium"),
                new Input("millennia", "Millennia"),
                new Input("forever", "Forever")
        );
    }

}
