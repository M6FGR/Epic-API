package dev.m6fgr.epic_api.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

public class EpicAPIConfigs {
    public static class Common {
        private static final ModConfigSpec.Builder BUILDER = new Builder();
        private static final ModConfigSpec.BooleanValue ENABLE_CAMERA_ANIMATIONS = BUILDER.comment("Whether to enable Camera Animations").define("camAniToggle", true);
        private static final ModConfigSpec.BooleanValue CAMERA_HEAD_CLIPPING_TOGGLE = BUILDER.comment("Whether to enable Head to be hidden if camera goes inside player head while camera animation").define("camHeadClipToggle", true);

        public static final ModConfigSpec SPEC = BUILDER.build();

        public static boolean isCamAnimsEnabled() {
            return ENABLE_CAMERA_ANIMATIONS.getAsBoolean();
        }

        public static boolean shouldClipToHead() {
            return CAMERA_HEAD_CLIPPING_TOGGLE.getAsBoolean();
        }
    }

    public static class Client {}

    public static class Server {}
}
