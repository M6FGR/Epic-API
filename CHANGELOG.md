_previous versions __[2.1.13 and below]__ didn't have update logs sadly, so only 21.1.4 and above will have update logs!_

### [21.2.1]

### Fixes:
- Fixed issue [HeavyAttack](src/main/java/M6FGR/epic_api/skills/common/HeavyAttack.java) multiplies damage instead of impact if it was more than 1. [[Commit]](https://github.com/M6FGR/Epic-API/commit/72151d011df77d1e290d28fa831005e2898f2127)
- Fixed issue [ExCapCapabilityRegistryEventHook](src/main/java/M6FGR/epic_api/events/item/ExCapCapabilityRegistryEventHook.java) won't fire for other addons. [[Commit]](https://github.com/M6FGR/Epic-API/commit/fb9ef769f2fb2d5ae9c8a7f54977a2483d925900)
- Fixed issue [ArmatureRegistrar](src/main/java/M6FGR/epic_api/builders/epicfight/ArmatureRegistrar.java) won't register armatures. [[Commit]](https://github.com/M6FGR/Epic-API/commit/a10f359c09961e11d2538a06dddb39395fffa952)
- Fixed issue [EntityPatchRegistrar](src/main/java/M6FGR/epic_api/builders/epicfight/EntityPatchRegistrar.java) won't put the armature and not registering any renderers/patches. [[Commit]](https://github.com/M6FGR/Epic-API/commit/96d492b4b8879ea2fa0a680e8cf3c0b146977e2b)
- Fixed issue [EpicAPIKeyMappings](src/main/java/M6FGR/epic_api/gameassets/EpicAPIKeyMappings.java) causes a crash in EpicFight 21.16.3. [[Commit]](https://github.com/M6FGR/Epic-API/commit/6bf2def2c829519029125ea9558c098b55f30789)
- Fixed issue [SimpleAttackAnimation](src/main/java/M6FGR/epic_api/animation/types/SimpleAttackAnimation.java) constructor doesn't apply basis speed of the animation. [[Commit]](https://github.com/M6FGR/Epic-API/commit/d07fdf7347767058846693401871bf000304cc2b)
### Added:
- New Interface: `IEventHook`, used for events that extend [Event](https://github.com/Antikythera-Studios/epicfight/blob/1.21.1/neoforge/src/main/java/yesman/epicfight/api/event/Event.java), **more info is written in the class.**
- New Annotation: `@Compatibility`, used to load a class if a specific mod-id was present, __more info is written in the class.__
- New classes for [Minecraft Builders](src/main/java/M6FGR/epic_api/builders/minecraft) to ease the vanilla registries:
[[CommandsBuilder](src/main/java/M6FGR/epic_api/builders/minecraft/CommandsBuilder.java)]
[[GameRulesBuilder](src/main/java/M6FGR/epic_api/builders/minecraft/GameRulesBuilder.java)]
[[ItemsBuilder](src/main/java/M6FGR/epic_api/builders/minecraft/ItemsBuilder.java)]
- New Event [EntityPatchEventHook](src/main/java/M6FGR/epic_api/events/entity/EntityPatchEventHook.java) for ease of registering entity patches.

### [21.2.2]

### Fixes:
- Fixed issue [ILoadableClass](src/main/java/M6FGR/epic_api/cls/ILoadableClass.java) crashes on a dedicated server if the class should be client-sided. [[Commit]](https://github.com/M6FGR/Epic-API/commit/865b6b4e9089c9e46f1b96bac3bf3092044337b8)
- Fixed issue [MoveSetCapabilityRegistryEventHook](src/main/java/M6FGR/epic_api/events/item/MoveSetCapabilityRegistryEventHook.java) only registers one capability.

### Added:
- [KeyMappingsBuilder](src/main/java/M6FGR/epic_api/builders/minecraft/KeyMappingsBuilder.java), ease of code readability and registry for keymappings.
- [KeyCodes](src/main/java/M6FGR/epic_api/input/KeyCodes.java), now you can know which keycode is the one! (not to note InputConstants, has the same thing)
