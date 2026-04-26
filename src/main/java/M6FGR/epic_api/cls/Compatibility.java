package M6FGR.epic_api.cls;

import net.minecraftforge.fml.ModList;

import java.lang.annotation.*;

/**
 * This annotation acts as a <b>Conditional Gatekeeper</b> for {@link ILoadableClass} implementations.
 * <p>
 * It prevents the API from attempting to initialize a class if the required mod dependencies 
 * are missing or if the class is being loaded on the wrong physical side (Client vs. Server).
 * </p>
 * Example:
 *
 * <pre>
 * {@code
 * @Compatibility(modid = "examplemod", clientSide = true, printWarns = false)
 * public class ExampleModCompatibility implements ILoadableClass {
 *      ...
 * }
 * }
 * </pre>
 *
 * @see ILoadableClass
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Compatibility {

    /**
     * The unique Mod ID required for this class to load.
     * If {@link ModList#isLoaded(String)} returns {@code false},
     * this class will be ignored.
     */
    String modid();

    /**
     * If {@code true}, the loading process will bail out if the current 
     * environment is a Dedicated Server. 
     * <p>
     * Set this to {@code true} for classes that reference rendering, 
     * models, or UI code to prevent server crashes.
     */
    boolean clientSide();

    /**
     * Toggles verbose logging for compatibility check failures.
     * <p>
     * When {@code true}, explicitly logs why a class was skipped (e.g., missing mod
     * or physical side mismatch). When {@code false}, skips the class silently.
     * </p>
     * Useful for debugging registration or preventing server crashes by identifying
     * misplaced client-only code (models, UI, etc.).
     */
    boolean printWarns() default false;

}