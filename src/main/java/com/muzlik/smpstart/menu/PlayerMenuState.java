package com.muzlik.smpstart.menu;

import java.util.UUID;

/**
 * Tracks player menu state for interactive input handling.
 */
public class PlayerMenuState {
    public enum InputMode {
        NONE,
        COUNTDOWN_DURATION,
        COOLDOWN_DURATION,
        PRE_BORDER_SIZE,
        FINAL_BORDER_SIZE,
        PVP_DURATION,
        MIN_PLAYERS,
        REMINDER_INTERVAL,
        BORDER_CENTER_X,
        BORDER_CENTER_Z,
        BORDER_TRANSITION
    }

    private final UUID playerUUID;
    private InputMode inputMode = InputMode.NONE;

    public PlayerMenuState(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public InputMode getInputMode() {
        return inputMode;
    }

    public void setInputMode(InputMode mode) {
        this.inputMode = mode;
    }

    public void clearInputMode() {
        this.inputMode = InputMode.NONE;
    }

    public boolean isAwaitingInput() {
        return inputMode != InputMode.NONE;
    }
}
