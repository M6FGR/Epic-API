package M6FGR.epic_api.cls;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A custom metadata marker used to embed descriptive notes, documentation,
 * or context directly into the compiled bytecode.
 *
 * <p>Unlike standard Java comments ({@code //} or {@code /*}) which are stripped
 * by the compiler during build time, this annotation defaults to {@link RetentionPolicy#CLASS}.
 * This ensures the documentation survives compilation and remains visible inside
 * the {@code .class} files, making it retrievable by decompilers and bytecode analysis tools.
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>
 *   // Shorthand syntax using the default value property:
 *   &#64;Comment("This method handles the legacy network packet workaround.")
 *   public void processOldData() { ... }
 *
 *   // Explicit property syntax:
 *   &#64;Comment(value = "Critical render pipeline hook.", )
 *   public class RenderMixin { ... }
 * </pre>
 *
 * @see Retention
 * @see Target
 */
@Target({
        ElementType.TYPE,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
        ElementType.CONSTRUCTOR,
        ElementType.LOCAL_VARIABLE,
        ElementType.ANNOTATION_TYPE,
        ElementType.PACKAGE,
        ElementType.TYPE_PARAMETER,
        ElementType.TYPE_USE,
        ElementType.MODULE,
        ElementType.RECORD_COMPONENT
})
@Retention(RetentionPolicy.CLASS)
public @interface Comment {

    /**
     * The documentation, note, or contextual comment associated with the target element.
     *
     * @return a String containing the descriptive comment.
     */
    String value();

    /**
     * The explanation of the documentation, note, or contextual comment associated with the target element.
     *
     * @return a String containing the explanation comment.
     */
    String explanation() default "";
}