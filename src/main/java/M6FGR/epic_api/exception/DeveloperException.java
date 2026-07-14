package M6FGR.epic_api.exception;

import M6FGR.epic_api.main.EpicAPI;
import M6FGR.epic_api.utils.EnvironmentHelper;

public class DeveloperException extends RuntimeException {

    public DeveloperException(String message) {
        super(message);
    }

    /**
     * Throws a DeveloperException only if running in a development environment.
     */
    public static void throwEx(String message) {
        if (EnvironmentHelper.getCurrentEnvironment().isDevEnv()) {
            throw new DeveloperException(message);
        }
    }

    /**
     * Throws both RuntimeException if the environment is production (not-dev), and DeveloperException if it was an IDE environment, accepts args for logging
     */
    public static void throwExBoth(String message, Object... args) {
        String loggedMessage = "";
        for (Object argument : args) {
            loggedMessage = message.replace("{}", argument.toString());
        }
        if (EnvironmentHelper.getCurrentEnvironment().isDevEnv()) {
            throw new DeveloperException(loggedMessage);
        } else {
            throw new RuntimeException(loggedMessage);
        }
    }


    public static void throwOrLog(String message, Object... args) {
        String loggedMessage = "";
        for (Object argument : args) {
            loggedMessage = message.replace("{}", argument.toString());
        }
        if (EnvironmentHelper.getCurrentEnvironment().isDevEnv()) {
            throw new DeveloperException(loggedMessage);
        } else {
            EpicAPI.err(loggedMessage);
        }
    }

    /**
     * Throws both RuntimeException if the environment is production (not-dev), and DeveloperException if it was an IDE environment
     */
    public static void throwExBoth(String message) {
        if (EnvironmentHelper.getCurrentEnvironment().isDevEnv()) {
            throw new DeveloperException(message);
        } else {
            throw new RuntimeException(message);
        }
    }
}