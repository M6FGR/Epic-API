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

    // Avoid usage, Use MeshAccessor#create instead
    @Internal
    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(String modid, String path, MeshContructor<SkinnedMeshPart, VertexBuilder, ME> constructor) {
        return Meshes.MeshAccessor.create(modid, path, loader -> loader.loadSkinnedMesh(constructor));
    }

    @Internal
    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(ResourceLocation location, MeshContructor<SkinnedMeshPart, VertexBuilder, ME> constructor) {
        return Meshes.MeshAccessor.create(location.getNamespace(), location.getPath(), loader -> loader.loadSkinnedMesh(constructor));
    }

    @Internal
    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(String parsedLocation, MeshContructor<SkinnedMeshPart, VertexBuilder, ME> constructor) {
        ResourceLocation parsed = ResourceLocation.parse(parsedLocation);
        return Meshes.MeshAccessor.create(parsed.getNamespace(), parsed.getPath(), loader -> loader.loadSkinnedMesh(constructor));
    }

    private static <ME extends Mesh> Meshes.MeshAccessor<ME> toAccessor(String modid, String path, MeshType type) {
        return Meshes.MeshAccessor.create(modid, path, type::load);
    }

    public enum MeshType {
        SKINNED_MESH(loader -> loader.loadSkinnedMesh(SkinnedMesh::new)),
        HUMANOID_MESH(loader -> loader.loadSkinnedMesh(HumanoidMesh::new)),
        COMPOSITE_MESH(JsonAssetLoader::loadCompositeMesh),
        MESH_LOADER(JsonAssetLoader::loadMesh),
        CLASSIC_MESH(loader -> loader.loadClassicMesh(ClassicMesh::new));

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