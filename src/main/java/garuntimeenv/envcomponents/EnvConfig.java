package garuntimeenv.envcomponents;

import garuntimeenv.gacomponents.GAManager;

/**
 * Class representing the configuration of the runtime environment
 */
public class EnvConfig {

    private static EnvConfig INSTANCE = new EnvConfig();
    private GAManager gaManager = null;


    private boolean writeData = true;       // If true data gets saved persistently
    private boolean memSafeMode = true;     // If true only a fixed amount of populations get saved
    private boolean savePictures = true;    // If true. writeData is true and visual is enabled save pictures of the gui
    private boolean visualEnabled = true;   // Enables the gui

    // The custom log levels to only allow logs from a specific class or component. * --> all is allowed
    private String[] logLevels = new String[]{
            "*"
    };

    private boolean autoJump = true;        // If true allways jump to the latest pane if a new one gets created
    public static int windowSize = -1;      // The amount of data shown on the frontend
    public static int meanWindowSize = 40;  // the widow size for the average indicator

    public double roundTo = 1000;   // The precission of the double values

    // Manage the start and stop logic
    private long startPause = 0;
    private long endPause = 0;
    private long pausedTime = 0;
    private boolean paused = true;

    /**
     * Constructor for the environment configuration object
     */
    private EnvConfig() {
        if (paused) this.startPause = System.currentTimeMillis();
    }

    /**
     * Singelton pattern instance getter
     *
     * @return The singleton envconfig object
     */
    public static EnvConfig getInstance() {
        return INSTANCE;
    }

    /**
     * Set the current genetic algorithm m@anager
     *
     * @param gaManager The new ga manager instance
     */
    public void setGaManager(GAManager gaManager) {
        this.gaManager = gaManager;
    }

    /**
     * Check if the runtime environment is paused
     *
     * @return true if the ga manager is paused otherwise false
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Pause the ga manager and resume if {@code paused} is false
     *
     * @param paused True starts a pause and false ends a pause and notifiys the ga manager
     */
    public void setPaused(boolean paused) {
        if (!paused && this.gaManager != null) {
            synchronized (this.gaManager) {
                this.gaManager.notify();
            }
        }
        this.paused = paused;

        if (!paused) {
            this.endPause = System.currentTimeMillis();
            this.pausedTime += endPause - startPause;
        } else {
            this.startPause = System.currentTimeMillis();
        }
    }

    /**
     * Getter for the auto jump field
     *
     * @return the auto jump field
     */
    public boolean isAutoJump() {
        return autoJump;
    }

    /**
     * Setter for the auto jump field
     *
     * @param autoJump true if auto jump should be enabled
     */
    public void setAutoJump(boolean autoJump) {
        this.autoJump = autoJump;
    }

    /**
     * Getter for the time the ga manager was paused
     *
     * @return the time the ga manager was paused
     */
    public long getPausedTime() {
        return pausedTime;
    }

    /**
     * Reset the pause timer for a new run
     */
    public void resetPauseTime() {
        this.pausedTime = 0;
    }

    /**
     * Getter for the mem mode
     *
     * @return true if memory mode is enabled
     */
    public boolean isMemSafeMode() {
        return memSafeMode;
    }

    /**
     * Getter for the save picture field
     *
     * @return The save picture flag
     */
    public boolean savePictures() {
        return this.savePictures;
    }

    /**
     * Getter for the visual flag
     *
     * @return The visual flag
     */
    public boolean isVisualEnabled() {
        return visualEnabled;
    }

    /**
     * Setter for the visual flag
     *
     * @param visualEnabled if the visual should be enabled
     */
    public void setVisualEnabled(boolean visualEnabled) {
        this.visualEnabled = visualEnabled;
    }

    /**
     * Getter for the write data flag
     *
     * @return If data should be written
     */
    public boolean isWriteData() {
        return writeData;
    }

    /**
     * Setter for the write data flag
     *
     * @param writeData Write data flag
     */
    public void setWriteData(boolean writeData) {
        this.writeData = writeData;
    }

    /**
     * Getter for the set log level
     *
     * @return The log levels
     */
    public String[] getLogLevel() {
        return this.logLevels;
    }

    /**
     * Setter for the save pictures flag
     *
     * @param savePictures true if pictures should be set
     */
    public void setSavePictures(boolean savePictures) {
        this.savePictures = savePictures;
    }
}
