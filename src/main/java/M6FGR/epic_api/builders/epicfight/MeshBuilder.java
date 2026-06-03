package M6FGR.epic_api.builders.epicfight;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.Internal;
import yesman.epicfight.api.asset.JsonAssetLoader;
import yesman.epicfight.api.client.model.ClassicMesh;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.Meshes.MeshContructor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.SkinnedMesh.SkinnedMeshPart;
import yesman.epicfight.api.client.model.VertexBuilder;
import yesman.epicfight.client.mesh.HumanoidMesh;

import java.util.function.Function;

public class MeshBuilder {
    private MeshBuilder() {}

    // Standard Overloads for ResourceLocation
    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(ResourceLocation location, MeshType type) {
        return toAccessor(location.getNamespace(), location.getPath(), type);
    }

    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(String parsedLocation, MeshType type) {
        ResourceLocation location = ResourceLocation.parse(parsedLocation);
        return toAccessor(location.getNamespace(), location.getPath(), type);
    }

    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(String modid, String path, MeshType type) {
        return toAccessor(modid, path, type);
    }

    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(String modid, String path) {
        return toAccessor(modid, path, MeshType.SKINNED_MESH);
    }

    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(ResourceLocation location) {
        return toAccessor(location.getNamespace(), location.getPath(), MeshType.SKINNED_MESH);
    }

    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(String parsedLocation) {
        ResourceLocation parsed = ResourceLocation.parse(parsedLocation);
        return toAccessor(parsed.getNamespace(), parsed.getPath(), MeshType.SKINNED_MESH);
    }

    @Internal
    private static <ME extends Mesh> Meshes.MeshAccessor<ME> toAccessor(String modid, String path, MeshType type) {
        return Meshes.MeshAccessor.create(modid, path, type::load);
    }

    public enum MeshType {
        // used for entities since it can contain weight data and more
        SKINNED_MESH(loader -> loader.loadSkinnedMesh(SkinnedMesh::new)),
        HUMANOID_MESH(loader -> loader.loadSkinnedMesh(HumanoidMesh::new)),
        // used for cloth objects mostly?
        COMPOSITE_MESH(JsonAssetLoader::loadCompositeMesh),
        // mostly used for projectiles, such as the laser beam
        CLASSIC_MESH(loader -> loader.loadClassicMesh(ClassicMesh::new)),
        // used to read the mesh loaded inside the .json itself (it needs to contain "mesh_loader" provider!)
        JSON_LOADER(JsonAssetLoader::loadMesh);

        private Function<JsonAssetLoader, Mesh> factory;

        MeshType(Function<JsonAssetLoader, Mesh> factory) {
            this.factory = factory;
        }

        public static MeshType of(MeshContructor<SkinnedMeshPart, VertexBuilder, SkinnedMesh> meshContructor) {
            SKINNED_MESH.factory = loader -> loader.loadSkinnedMesh(meshContructor);
            return SKINNED_MESH;
        }


        @SuppressWarnings("unchecked")
        public <ME extends Mesh> ME load(JsonAssetLoader loader) {
            return (ME) factory.apply(loader);
        }

    }
}