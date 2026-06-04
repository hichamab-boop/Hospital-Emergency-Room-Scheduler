import emergencyroom.TriageSystem;

/**
 * Cross-platform Java-based runner that starts the Triage System.
 * Replaces platform-specific batch (.bat) and shell (.sh) scripts.
 */
public class Main {
    public static void main(String[] args) {
        TriageSystem system = new TriageSystem();
        system.start();
    }
}
