package dev.golgolex.golgocloud.rank.module;

import dev.golgolex.golgocloud.base.CloudBase;
import dev.golgolex.golgocloud.rank.module.commands.PermissionsCommand;
import dev.golgolex.quala.module.Module;
import dev.golgolex.quala.module.ModuleLogger;
import dev.golgolex.quala.module.ModuleProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class RankModule extends Module {

    public RankModule(@NotNull ModuleProperties moduleProperties,
                      @NotNull File modulesDirectory,
                      @Nullable ModuleLogger moduleLogger) {
        super(moduleProperties, modulesDirectory, moduleLogger);
    }

    @Override
    public void initialize(@NotNull Object[] objects) {
    }

    @Override
    public void activate(@NotNull Object[] objects) {
        CloudBase.instance().cloudTerminal().terminalCommandService().registerCommand(new PermissionsCommand());
    }

    @Override
    public void deactivate() {
        CloudBase.instance().cloudTerminal().terminalCommandService().commands().removeIf(object -> object instanceof PermissionsCommand);
    }

    @Override
    public void refresh() {
        CloudBase.instance().cloudTerminal().terminalCommandService().commands().removeIf(object -> object instanceof PermissionsCommand);
        CloudBase.instance().cloudTerminal().terminalCommandService().registerCommand(new PermissionsCommand());
    }
}
