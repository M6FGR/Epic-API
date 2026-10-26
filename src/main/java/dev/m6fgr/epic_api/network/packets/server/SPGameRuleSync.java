package dev.m6fgr.epic_api.network.packets.server;

import dev.m6fgr.epic_api.cls.Comment;
import dev.m6fgr.epic_api.main.EpicAPI;
import dev.m6fgr.epic_api.network.interfaces.PacketBufHandler;
import dev.m6fgr.epic_api.network.packets.ICustomPacket;
import dev.m6fgr.epic_api.utils.EnvironmentHelper;
import dev.m6fgr.epic_api.utils.EnvironmentHelper.Environments;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SPGameRuleSync(String ruleName, int value) implements ICustomPacket<SPGameRuleSync> {

    private SPGameRuleSync() {
        this("", 0);
    }

    @Override
    public @NotNull Type<SPGameRuleSync> type() {
        return new Type<>(EpicAPI.getInstance().identifier("gamerule_sync"));
    }

    @Comment("Invoked by PacketBufHandler#codecOf")
    public SPGameRuleSync(FriendlyByteBuf byteBuf) {
        this(byteBuf.readUtf(), byteBuf.readInt());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.ruleName);
        buf.writeInt(this.value);
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, SPGameRuleSync> codec() {
        return PacketBufHandler.codecOf(SPGameRuleSync.class, SPGameRuleSync::encode, SPGameRuleSync::decode);
    }

    @Override
    public SPGameRuleSync decode(FriendlyByteBuf buf) {
        return new SPGameRuleSync(buf);
    }

    @Override
    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (EnvironmentHelper.getEnvironment().is(Environments.CLIENT)) {
                SPGameRuleSync.handleGameRulesSync(this);
            }
        });
    }

    private static void handleGameRulesSync(SPGameRuleSync packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            GameRules gameRules = mc.level.getGameRules();
            GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
                @Override
                public <T extends GameRules.Value<T>> void visit(@NotNull GameRules.Key<T> key, @NotNull GameRules.Type<T> type) {
                    if (key.getId().equals(packet.ruleName())) {
                        T rule = gameRules.getRule(key);
                        switch (rule) {
                            case GameRules.BooleanValue boolRule -> boolRule.set(packet.value() != 0, null);
                            case GameRules.IntegerValue intRule -> intRule.set(packet.value(), null);
                            default -> throw new IllegalStateException("Unexpected game rule type: " + rule);
                        }
                        EpicAPI.debugIfDevSide("Synced rule {}", key.getId());
                    }
                }
            });
        }
    }
}