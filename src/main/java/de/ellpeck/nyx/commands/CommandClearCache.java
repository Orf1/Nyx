package de.ellpeck.nyx.commands;

import de.ellpeck.nyx.capabilities.NyxWorld;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandClearCache extends CommandBase {
    @Override
    public String getName() {
        return "nyxclearcache";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.nyx.clearcache.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World w = sender.getEntityWorld();
        if (w.isRemote) return;
        
        NyxWorld world = NyxWorld.get(w);
        if (world == null)
            return;
        int size = world.cachedMeteorPositions.size();
        world.cachedMeteorPositions.clear();
        world.sendToClients();
        notifyCommandListener(sender, this, "command.nyx.clearcache.success", size);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }
}
