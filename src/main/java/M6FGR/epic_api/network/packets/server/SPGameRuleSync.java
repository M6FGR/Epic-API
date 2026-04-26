package M6FGR.epic_api.network.packets.server;

import M6FGR.epic_api.builders.minecraft.GameRulesBuilder.EnumValue;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.network.NetworkEvent.Context;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SPGameRuleSync {
    private final String ruleName;
    private final int value;

    public SPGameRuleSync(String ruleName, int value) {
        this.ruleName = ruleName;
        this.value = value;
    }

    // Encoder: Writing data to the buffer
    public static void encode(SPGameRuleSync msg, FriendlyByteBuf buffer) {
        buffer.writeUtf(msg.ruleName);
        buffer.writeInt(msg.value);
    }

    // Decoder: Reading data from the buffer
    public static SPGameRuleSync decode(FriendlyByteBuf buffer) {
        return new SPGameRuleSync(buffer.readUtf(), buffer.readInt());
    }

    // Handler: The logic that runs on the Client
    public static void handle(SPGameRuleSync msg, Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                // GameRules visitor logic remains largely the same
                GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
                    @Override
                    public <T extends GameRules.Value<T>> void visit(GameRules.@NotNull Key<T> key, GameRules.@NotNull Type<T> type) {
                        if (key.getId().equals(msg.ruleName)) {
                            T rule = mc.level.getGameRules().getRule(key);
                            // Note: Java 17 pattern matching (switch) works, but verify
                            // 1.20.1 Forge is running on a high enough JDK (usually it is).
                            if (rule instanceof GameRules.BooleanValue bool) {
                                bool.set(msg.value != 0, null);
                            } else if (rule instanceof GameRules.IntegerValue intRule) {
                                intRule.set(msg.value, null);
                            } else if (rule instanceof EnumValue<?> enumValue) {
                                enumValue.setOrdinal(msg.value);
                            }
                        }
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}