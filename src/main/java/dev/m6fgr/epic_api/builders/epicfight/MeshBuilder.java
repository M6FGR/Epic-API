package dev.m6fgr.epic_api.builders.epicfight;

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
    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(ResourceLocation location, MeshTypes type) {
        return toAccessor(location.getNamespace(), location.getPath(), type);
    }

    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(String parsedLocation, MeshTypes type) {
        ResourceLocation location = ResourceLocation.parse(parsedLocation);
        return toAccessor(location.getNamespace(), location.getPath(), type);
    }

    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(String modid, String path, MeshTypes type) {
        return toAccessor(modid, path, type);
    }

    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(String modid, String path) {
        return toAccessor(modid, path, MeshTypes.SKINNED);
    }

    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(ResourceLocation location) {
        return toAccessor(location.getNamespace(), location.getPath(), MeshTypes.SKINNED);
    }

    public static <ME extends SkinnedMesh> Meshes.MeshAccessor<ME> newMesh(String parsedLocation) {
        ResourceLocation parsed = ResourceLocation.parse(parsedLocation);
        return toAccessor(parsed.getNamespace(), parsed.getPath(), MeshTypes.SKINNED);
    }

    @Internal
    private static <ME extends Mesh> Meshes.MeshAccessor<ME> toAccessor(String modid, String path, MeshTypes type) {
        return Meshes.MeshAccessor.create(modid, path, type::load);
    }

    public enum MeshTypes {
        // used for entities since it can contain weight data and more
        SKINNED(loader -> loader.loadSkinnedMesh(SkinnedMesh::new)),
        HUMANOID(loader -> loader.loadSkinnedMesh(HumanoidMesh::new)),
        // used for cloth objects mostly?
        COMPOSITE(JsonAssetLoader::loadCompositeMesh),
        // mostly used for projectiles, such as the laser beam
        CLASSIC(loader -> loader.loadClassicMesh(ClassicMesh::new)),
        // used to read the mesh loaded inside the .json itself (it needs to contain "mesh_loader" provider!)
        JSON_LOADER(JsonAssetLoader::loadMesh);

        private Function<JsonAssetLoader, Mesh> factory;

        MeshTypes(Function<JsonAssetLoader, Mesh> factory) {
            this.factory = factory;
        }

        public static MeshTypes of(MeshContructor<SkinnedMeshPart, VertexBuilder, SkinnedMesh> meshContructor) {
            SKINNED.factory = loader -> loader.loadSkinnedMesh(meshContructor);
            return SKINNED;
        }


        @SuppressWarnings("unchecked")
        public <ME extends Mesh> ME load(JsonAssetLoader loader) {
            return (ME) factory.apply(loader);
        }

    }
}