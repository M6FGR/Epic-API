package M6FGR.epic_api.mixins.minecraft;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

@Mixin(value = CrashReport.class, remap = false)
public abstract class CrashReportMixin {

    // Shadow the description title field so we can overwrite it dynamically
    @Shadow @Final @Mutable private String title;

    // Grab the internal details list from CrashReport
    @Accessor("details")
    protected abstract List<CrashReportCategory> epicAPI$getDetails();

    // Access the private title field inside CrashReportCategory
    @Mixin(value = CrashReportCategory.class, remap = false)
    private interface CrashReportCategoryAccessor {
        @Accessor("title")
        String epicAPI$getTitle();
    }

    @Unique
    private static final String[] RANDOM_MESSAGES = {
            "Epic-API is not epic after all huh?",
            "This crash is definitely from Epic API. Go yell at M6FGR here: https://github.com/M6FGR/Epic-API/issues",
            "An animation frame tried to divide by zero. Please hand this log over to the authorities: https://github.com/M6FGR/Epic-API/issues",
            "The combos were too epic for the engine to handle. Report this absolute disaster!: https://github.com/M6FGR/Epic-API/issues",
            "Don't panic, but a compiler gremlin just broke the combat pipeline. Open an issue at https://github.com/M6FGR/Epic-API/issues!",
            "Local developer forgets how index boundaries work. Go roast them on GitHub: https://github.com/M6FGR/Epic-API/issues"
    };

    @Inject(method = "saveToFile(Ljava/nio/file/Path;Lnet/minecraft/ReportType;)Z", at = @At("HEAD"))
    private void epicAPI$injectBlameHeader(Path path, ReportType type, CallbackInfoReturnable<Boolean> cir) {
        try {
            CrashReport report = (CrashReport) (Object) this;
            boolean isEpicAPIFault = false;

            // 1. Check mid-game exception stack traces
            if (report.getException() != null) {
                Throwable currentThrowable = report.getException();
                while (currentThrowable != null) {
                    for (StackTraceElement element : currentThrowable.getStackTrace()) {
                        if (element.getClassName().contains("M6FGR.epic_api")) {
                            isEpicAPIFault = true;
                            break;
                        }
                    }
                    if (isEpicAPIFault) break;
                    currentThrowable = currentThrowable.getCause();
                }
            }

            // 2. Check mod loading category descriptions (For early bootstrap failures)
            if (!isEpicAPIFault) {
                List<CrashReportCategory> categories = this.epicAPI$getDetails();
                if (categories != null) {
                    for (CrashReportCategory category : categories) {
                        String catTitle = ((CrashReportCategoryAccessor) category).epicAPI$getTitle();
                        if (catTitle != null && catTitle.contains("epic_api")) {
                            isEpicAPIFault = true;
                            break;
                        }
                    }
                }
            }

            // 3. Overwrite the main description title to include our message banner at the top
            if (isEpicAPIFault && this.title != null && !this.title.contains("Epic API Blame Room")) {
                Random rand = new Random();
                String chosenMessage = RANDOM_MESSAGES[rand.nextInt(RANDOM_MESSAGES.length)];

                // Format the top banner block neatly right into the title field string stream

                this.title = this.title + "\n\n" +
                        "Epic-API Faced an Error: " + chosenMessage + "\n";
            }
        } catch (Throwable ignored) {
            // Fail-safe wrapper
        }
    }
}