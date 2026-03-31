package M6FGR.epic_api.api.math;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class MathUtil {

    public static float fromFramesToSeconds(float frameNumber, float fps) {
        return frameNumber / fps;
    }
    public static void calculateAttackTiming(float startFrame, float endFrame, float fps) {
        float begin = fromFramesToSeconds(startFrame, fps);
        float end = fromFramesToSeconds(endFrame, fps);
        if (begin > end) {
            throw new IllegalStateException("Beginning time cannot be bigger than end time!");
        }
        System.out.println("Attack timing begins at: " + begin + "F" + " and ends at " + end + "F");
    }

    public static void calculateTimes(float fps, float... frames) {
        if (fps < 0) {
            throw new IllegalArgumentException("FPS cannot be a negative number!");
        }
        for (float frame : frames) {
            float result = frame / fps;
            System.out.println("Frame times are:" + result + "F");
        }
    }

    public static Vec3 blocksToDelta(double x, double y, double z, float friction) {
        double dx = x * friction / 2.12453;
        double dz = z * friction / 2.12453;
        double dy = 0;
        if (y > 0) {
            dy = Math.sqrt(y * 0.16) + 0.02;
        } else if (y < 0) {
            dy = y * 0.1;
        }
        return new Vec3(dx, dy, dz);
    }

    public static Vec3 cubicVec3ToDelta(Vec3 delta) {
        double dx = delta.x * 2.12453;
        double dz = delta.z * 2.12453;
        double dy = 0;
        if (delta.y > 0) {
            dy = Math.sqrt(delta.y * 0.16) + 0.02;
        } else if (delta.y < 0) {
            dy = delta.y * 0.1;
        }
        return new Vec3(dx, dy, dz);
    }

    public static Vec3 blocksToLookAngleDelta(LivingEntity looker, double x, double y, double z, float friction) {
        Vec3 lookVec = looker.getLookAngle();
        Vec3 sideVec = lookVec.cross(new Vec3(0, 1, 0)).normalize();

        double worldX = (lookVec.x * x) + (sideVec.x * z);
        double worldY = (lookVec.y * x) + y;
        double worldZ = (lookVec.z * x) + (sideVec.z * z);

        return blocksToDelta(worldX, worldY, worldZ, friction);
    }


    public static int toHex(float r, float g, float b) {
        int red = Math.round(r * 255.0F) << 16;
        int green = Math.round(g * 255.0F) << 8;
        int blue = Math.round(b * 255.0F);

        return red | green | blue;
    }

}

