package M6FGR.epic_api.network.packets.server;

import M6FGR.epic_api.builders.minecraft.GameRulesBuilder.EnumValue;
import M6FGR.epic_api.main.EpicAPI;
import M6FGR.epic_api.network.packets.ICustomPacket;
import M6FGR.epic_api.utils.EnvironmentHelper;
import M6FGR.epic_api.utils.EnvironmentHelper.Environments;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.IntegerValue;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SPGameRulesSync(String ruleName, int value) implements ICustomPacket<SPGameRulesSync> {
    public static final Type<SPGameRulesSync> TYPE = new Type<>(EpicAPI.identifier("gamerule_sync"));

    public static final StreamCodec<FriendlyByteBuf, SPGameRulesSync> CODEC = CustomPacketPayload.codec(
            (payload, buffer) -> {
                buffer.writeUtf(payload.ruleName);
                buffer.writeInt(payload.value);
            },
            buffer -> new SPGameRulesSync(buffer.readUtf(), buffer.readInt())
    );

    @Override
    public Type<SPGameRulesSync> getType() {
        return TYPE;
    }

    @Override
    public StreamCodec<FriendlyByteBuf, SPGameRulesSync> getCodec() {
        return CODEC;
    }

    @Override
    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (EnvironmentHelper.getCurrentEnvironment().is(Environments.CLIENT))
                handleGameRulesSync(this);
        });
    }

    private static void handleGameRulesSync(SPGameRulesSync packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
                @Override
                public <T extends GameRules.Value<T>> void visit(@NotNull GameRules.Key<T> key, @NotNull GameRules.Type<T> type) {
                    if (key.getId().equals(packet.ruleName())) {
                        T rule = mc.level.getGameRules().getRule(key);
                        switch (rule) {
                            case BooleanValue boolRule -> boolRule.set(packet.value() != 0, null);
                            case IntegerValue intRule -> intRule.set(packet.value(), null);
                            case EnumValue<?> enumRule -> enumRule.setOrdinal(packet.value());
                            default -> {
                            }
                        }
                    }
                }
            });
        }
    }
}