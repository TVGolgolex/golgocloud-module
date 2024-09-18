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

}
