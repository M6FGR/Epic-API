package M6FGR.epic_api.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class EnvironmentHelper {

    private static final Dist dist = FMLLoader.getDist();
    private static final boolean IS_DEVELOPER = !FMLLoader.isProduction();

    private EnvironmentHelper() {}

    public static @Nullable MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static @Nullable Minecraft getClient() {
        return Minecraft.getInstance();
    }

    private static boolean isOfficialMC() {
        if (!dist.isClient()) return false;
        if (getClient() == null) return false;
        User user = getClient().getUser();
        return user != null && !user.getAccessToken().equals("0");
    }

    private static boolean isDevAndOfficialMC() {
        return IS_DEVELOPER && isOfficialMC();
    }

    private static boolean isServerAuthenticated() {
        if (dist.isClient()) return false;
        MinecraftServer currentServer = getServer();
        return currentServer != null && currentServer.usesAuthentication();
    }

    public static Environments getCurrentEnvironment() {
        MinecraftServer server = getServer();
        if (server != null) {
            System.out.println("Server is ALIVE!");
            if (server instanceof GameTestServer) {
                return Environments.GAME_TEST_SERVER;
            } else if (server.isDedicatedServer()) {
                return Environments.DEDICATED_SERVER;
            }
        }


        if (dist.isClient()) {
            if (server instanceof IntegratedServer) {
                return Environments.LAN_SERVER;
            }
            return IS_DEVELOPER ? Environments.IDE : Environments.CLIENT;
        }

        return Environments.COMMON;
    }

    public enum Environments {
        CLIENT(IS_DEVELOPER),
        IDE(true, CLIENT),
        DEDICATED_SERVER(false),
        COMMON(false),
        LAN_SERVER(IS_DEVELOPER),
        GAME_TEST_SERVER(true);

        private final boolean isDeveloper;
        private final @Nullable Environments parent;

        Environments(boolean developer, @Nullable Environments parent) {
            this.isDeveloper = developer;
            this.parent = parent;
        }

        Environments(boolean developer) {
            this(developer, null);
        }

        public boolean isDevEnv() {
            return this.isDeveloper;
        }

        @Nullable
        public Environments getParent() {
            return this.parent;
        }

        public boolean isAuthenticated() {
            if (this == CLIENT || this == LAN_SERVER) return EnvironmentHelper.isOfficialMC();
            if (this == IDE) return EnvironmentHelper.isDevAndOfficialMC();
            if (this == DEDICATED_SERVER) return EnvironmentHelper.isServerAuthenticated();
            return false;
        }

        public boolean is(Environments matching) {
            if (this == matching) {
                return true;
            }
            if (this.parent != null) {
                return this.parent.is(matching);
            }
            return false;
        }

        public boolean isSameAndAuthenticated(Environments matching) {
            if (this == matching) {
                return this.isAuthenticated();
            }
            if (this.parent != null) {
                return this.parent.isSameAndAuthenticated(matching);
            }
            return false;
        }
    }
}