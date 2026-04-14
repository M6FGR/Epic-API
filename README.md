# Epic-API
A simple library for easing the registry of things specifically in [EpicFight](https://github.com/Antikythera-Studios/epicfight) modding.

_previous versions __[2.1.13 and below]__ didn't have update logs sadly, so only 21.1.4 and above will have update logs!_

### [21.2.1]

### Fixes:
- Fixed issue [HeavyAttack](src/main/java/M6FGR/epic_api/skills/common/HeavyAttack.java) multiplies damage instead of impact if it was more than 1. [[Commit]](https://github.com/M6FGR/Epic-API/commit/72151d011df77d1e290d28fa831005e2898f2127)
- Fixed issue event [ExCapCapabilityRegistryEventHook](src/main/java/M6FGR/epic_api/events/item/ExCapCapabilityRegistryEventHook.java) won't fire for other addons. [[Commit]](https://github.com/M6FGR/Epic-API/commit/fb9ef769f2fb2d5ae9c8a7f54977a2483d925900)
### Added:
- New Interface: `IEventHook`, used for events that extend [Event](https://github.com/Antikythera-Studios/epicfight/blob/1.21.1/neoforge/src/main/java/yesman/epicfight/api/event/Event.java), **more info is written in the class.**
- New Annotation: `@Compatibility`, used to load a class if a specific mod-id was present, __more info is written in the class.__
- New classes for [Minecraft Builders](src/main/java/M6FGR/epic_api/builders/minecraft) to ease the vanilla registries:
- [[CommandsBuilder](src/main/java/M6FGR/epic_api/builders/minecraft/CommandsBuilder.java)]
- [[GameRulesBuilder](src/main/java/M6FGR/epic_api/builders/minecraft/GameRulesBuilder.java)]
- [[ItemsBuilder](src/main/java/M6FGR/epic_api/builders/minecraft/ItemsBuilder.java)]