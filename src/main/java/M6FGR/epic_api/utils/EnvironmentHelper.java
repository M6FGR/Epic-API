package M6FGR.epic_api.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class EnvironmentHelper {

    private static final EnvironmentHelper instance = new EnvironmentHelper();
    private static final Dist dist = FMLLoader.getDist();
    private static final boolean IS_DEVELOPER = !FMLEnvironment.production;

    private EnvironmentHelper() {}

    public static EnvironmentHelper getInstance() {
        return instance;
    }

    public @Nullable Minecraft getClient() {
        try {
            return Minecraft.getInstance();
        } catch (Throwable t) {
            return null; // Guard against client-side calls on dedicated server classloading
        }
    }

    public @Nullable MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    private static boolean isOfficialMC() {
        if (!dist.isClient()) return false;
        Minecraft client = instance.getClient();
        if (client == null) return false;
        try {
            User user = client.getUser();
            return user != null && !user.getAccessToken().equals("0");
        } catch (Throwable t) {
            return false;
        }
    }

    private static boolean isDeveloper() {
        return IS_DEVELOPER;
    }

    private static boolean isDevAndOfficialMC() {
        return isDeveloper() && isOfficialMC();
    }

    private static boolean isServerAuthenticated() {
        if (dist.isClient()) return false;
        MinecraftServer currentServer = instance.getServer();
        return currentServer != null && currentServer.usesAuthentication();
    }

    public Environments getCurrentEnvironment() {
        if (dist.isDedicatedServer()) {
            return Environments.DEDICATED_SERVER;
        }

        MinecraftServer server = getServer();
        if (server != null) {
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
        CLIENT(EnvironmentHelper::isDeveloper),
        IDE(() -> true, CLIENT),
        DEDICATED_SERVER(() -> false),
        COMMON(() -> false),
        LAN_SERVER(EnvironmentHelper::isDeveloper),
        GAME_TEST_SERVER(() -> true);

        private final BooleanSupplier isDeveloperSupplier;
        private final @Nullable Environments parent;

        Environments(BooleanSupplier developer, @Nullable Environments parent) {
            this.isDeveloperSupplier = developer;
            this.parent = parent;
        }

        Environments(BooleanSupplier developer) {
            this(developer, null);
        }

        public boolean isDevEnv() {
            return this.isDeveloperSupplier.getAsBoolean();
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