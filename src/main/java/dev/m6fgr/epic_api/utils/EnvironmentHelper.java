package dev.m6fgr.epic_api.utils;

import dev.m6fgr.epic_api.main.EpicAPIMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class EnvironmentHelper {

    public static final Dist dist = FMLLoader.getDist();
    private static final boolean IS_DEVELOPER = !FMLLoader.isProduction();

    private static MinecraftServer currentServer;
    private static ServerTypes serverType;

    private EnvironmentHelper() {}

    public static @Nullable MinecraftServer getCurrentServer() {
        return currentServer;
    }

    public static ServerTypes getCurrentServerType() {
        return serverType;
    }

    public static Minecraft getClient() {
        return Minecraft.getInstance();
    }

    private static boolean isOfficialMC() {
        if (!EnvironmentHelper.dist.isClient()) return false;
        User user = EnvironmentHelper.getClient().getUser();
        return !user.getAccessToken().equals("0") && !user.getAccessToken().equals("NotValid");
    }

    @Internal
    public static void setServerAndType(MinecraftServer server, ServerTypes type) {
        EnvironmentHelper.currentServer = server;
        EnvironmentHelper.serverType = type;
        EpicAPIMod.debugIfDevSide("Set the server and it's type: {}, {}", type.is(ServerTypes.SINGLE_PLAYER) ? "SinglePlayerServer" : server.getClass().getSimpleName(), type.getDisplayName());
    }

    private static boolean isDevAndOfficialMC() {
        return IS_DEVELOPER && isOfficialMC();
    }

    public static boolean isDevelopmentEnvironment() {
        return IS_DEVELOPER;
    }

    private static boolean isServerAuthenticated() {
        if (EnvironmentHelper.dist.isClient()) return false;
        MinecraftServer currentServer = getCurrentServer();
        return currentServer != null && currentServer.usesAuthentication();
    }

    public static boolean isLogicalServer() {
        if (dist.isClient()) return false;
        return currentServer != null && currentServer.isSameThread()
                || EnvironmentHelper.getEnvironment().is(Environments.DEDICATED_SERVER, Environments.GAME_TEST_SERVER, Environments.LAN_SERVER);
    }

    public static boolean isLogicalClient() {
        if (!dist.isClient()) return false;
        return currentServer == null
                || !currentServer.isSameThread()
                || EnvironmentHelper.getEnvironment().is(Environments.CLIENT);
    }

    public static Environments getEnvironment() {
        MinecraftServer server = EnvironmentHelper.getCurrentServer();
        ServerTypes type = EnvironmentHelper.getCurrentServerType();
        if (EnvironmentHelper.dist.isDedicatedServer()) {
            if (server != null && type != null) {
                if (server instanceof GameTestServer || type.is(ServerTypes.GAME_TEST)) {
                    return Environments.GAME_TEST_SERVER;
                } else if (server.isDedicatedServer() || type.is(ServerTypes.DEDICATED)) {
                    return Environments.DEDICATED_SERVER;
                }
            }
            return Environments.DEDICATED_SERVER;
        }

        if (EnvironmentHelper.dist.isClient()) {
            if (type != null && server != null) {
                if (server instanceof IntegratedServer || type.is(ServerTypes.INTEGRATED)) {
                    return Environments.LAN_SERVER;
                }
            }
            return IS_DEVELOPER ? Environments.IDE : Environments.CLIENT;
        }

        return Environments.COMMON;
    }

    public enum ServerTypes {
        DEDICATED("Dedicated"),
        INTEGRATED("Integrated"),
        GAME_TEST("GameTest"),
        SINGLE_PLAYER("SinglePlayer");

        private final String displayName;

        ServerTypes(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public boolean is(ServerTypes match) {
            return this == match;
        }
    }

    public enum Environments {
        CLIENT(EnvironmentHelper::isDevelopmentEnvironment),
        IDE(EnvironmentHelper::isDevelopmentEnvironment, CLIENT),
        DEDICATED_SERVER(EnvironmentHelper::isDevelopmentEnvironment),
        COMMON(EnvironmentHelper::isDevelopmentEnvironment),
        LAN_SERVER(EnvironmentHelper::isDevelopmentEnvironment, CLIENT),
        GAME_TEST_SERVER(EnvironmentHelper::isDevelopmentEnvironment);

        private final boolean isDeveloper;
        private final @Nullable Environments parent;

        Environments(BooleanSupplier developer, @Nullable Environments parent) {
            this.isDeveloper = developer.getAsBoolean();
            this.parent = parent;
        }

        Environments(BooleanSupplier developer) {
            this(developer, null);
        }

        Environments(boolean developer) {
            this(() -> developer, null);
        }

        Environments(boolean developer, @Nullable Environments parent) {
            this(() -> developer, parent);
        }

        public boolean isDevEnv() {
            return this.isDeveloper;
        }

        public boolean isClient() {
            return this.is(Environments.CLIENT);
        }

        public boolean isServer() {
            return this.is(Environments.DEDICATED_SERVER, Environments.GAME_TEST_SERVER);
        }

        @Nullable
        public Environments getParent() {
            return this.parent;
        }

        public String getAdvancedName() {
            String[] words = name().split("_");
            StringBuilder result = new StringBuilder();

            for (String word : words) {
                if (!word.isEmpty()) {
                    result.append(Character.toUpperCase(word.charAt(0)))
                            .append(word.substring(1).toLowerCase())
                            .append(" ");
                }
            }

            return result.toString().trim();
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

        public boolean is(Environments... matchings) {
            for (Environments env : matchings) {
                return this.is(env);
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