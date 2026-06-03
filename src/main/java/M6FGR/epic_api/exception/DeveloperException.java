package M6FGR.epic_api.exception;

import M6FGR.epic_api.main.EpicAPI;

public class DeveloperException extends RuntimeException {

    public DeveloperException(String message) {
        super(message);
    }

    /**
     * Throws a DeveloperException only if running in a development environment.
     */
    public static void throwEx(String message) {
        if (EpicAPI.getEnvHelper().isDevEnv()) {
            throw new DeveloperException(message);
        }
    }


    public static void throwOrLog(String message, Object... args) {
        if (EpicAPI.getEnvHelper().isDevEnv()) {
            throw new DeveloperException(message);
        } else {
            EpicAPI.err(message, args);
        }
    }

    /**
     * Throws both RuntimeException if the environment is production (not-dev), and DeveloperException if it was an IDE environment
     */
    public static void throwExBoth(String message) {
        if (EpicAPI.getEnvHelper().isDevEnv()) {
            throw new DeveloperException(message);
        } else {
            throw new RuntimeException(message);
        }
    }
}