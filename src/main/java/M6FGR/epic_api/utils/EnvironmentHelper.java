package M6FGR.epic_api.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class EnvironmentHelper {

    public static Minecraft getClient() {
        return Minecraft.getInstance();
    }

    public static MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }


    private static boolean isDeveloper() {
        return !FMLEnvironment.production;
    }

    private static boolean isClientAuthenticated() {
        if (!FMLLoader.getDist().isClient()) {
            return false;
        }
       return !getClient().getUser().getAccessToken().equals("0");
    }

    private static boolean isServerAuthenticated() {
        return getServer() != null ? getServer().usesAuthentication() : false;
    }


    private static boolean isLoggedInDev() {
        return isDeveloper() && isClientAuthenticated();
    }


    public static Environments getCurrentEnvironment() {
        MinecraftServer server = getServer();
        if (server != null) {
            if (server instanceof GameTestServer) {
                return Environments.GAME_TEST_SERVER;
            } else if (server.isDedicatedServer()) {
                return Environments.DEDICATED_SERVER;
            }
        }

        if (FMLLoader.getDist().isClient()) {
            if (server instanceof IntegratedServer) {
                return Environments.LAN_SERVER;
            }
            return isDeveloper() ? Environments.IDE : Environments.CLIENT;
        }

        return Environments.COMMON;
    }

    public enum Environments {
        CLIENT(EnvironmentHelper.isDeveloper(), EnvironmentHelper.isClientAuthenticated()),
        DEDICATED_SERVER(false, EnvironmentHelper.isServerAuthenticated()),
        LAN_SERVER(EnvironmentHelper.isDeveloper(), EnvironmentHelper.isClientAuthenticated()),
        GAME_TEST_SERVER(true, false),
        IDE(true, EnvironmentHelper.isLoggedInDev(), CLIENT),
        COMMON(false, false);


        private boolean isDeveloper;
        private boolean isAuthenticated;
        private @Nullable Environments parent;
        Environments(boolean isDev, boolean authenticated, @Nullable Environments parent) {
            this.isDeveloper = isDev;
            this.isAuthenticated = authenticated;
            this.parent = parent;
        }

        Environments(boolean isDev, boolean authenticated) {
            this(isDev, authenticated, null);
        }

        public boolean isAuthenticated() {
            return this.isAuthenticated;
        }

        public boolean isDeveloper() {
            return this.isDeveloper;
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
