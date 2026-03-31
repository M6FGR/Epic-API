package M6FGR.epic_api.api.registry;

import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.asset.JsonAssetLoader;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;

import java.util.function.Function;

public class MeshRegistry {
    public static <M extends SkinnedMesh> Meshes.MeshAccessor<M> newMesh(ResourceLocation location, Function<JsonAssetLoader, M> meshFunc) {
        return Meshes.MeshAccessor.create(location.getNamespace(), location.getPath(), meshFunc);
    }
    public static <M extends SkinnedMesh> Meshes.MeshAccessor<M> newMesh(String parsedLocation, Function<JsonAssetLoader, M> meshFunc) {
        ResourceLocation location = ResourceLocation.parse(parsedLocation);
        return Meshes.MeshAccessor.create(location.getNamespace(), location.getPath(), meshFunc);
    }
    public static <M extends SkinnedMesh> Meshes.MeshAccessor<M> newMesh(String modid, String path, Function<JsonAssetLoader, M> meshFunc) {
        return Meshes.MeshAccessor.create(modid, path, meshFunc);
    }
}
