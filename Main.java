import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Method;

/**
 * Cross-platform Java-based runner that compiles the packages and runs the Triage System.
 * Replaces platform-specific batch (.bat) and shell (.sh) scripts.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Cleaning old class files...");
        cleanOldClassFiles(new File("."));

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("\n[ERROR] JDK is required to compile the source code.");
            System.err.println("Please make sure you are running with a JDK, not a JRE.");
            return;
        }

        System.out.println("Compiling the emergency room package...");
        // 1. Compile the emergency room package files
        int result = compiler.run(null, null, null,
            "-d", ".",
            "emergencyroom/Doctor.java",
            "emergencyroom/DoctorQueue.java",
            "emergencyroom/Patient.java",
            "emergencyroom/PatientHistoryBST.java",
            "emergencyroom/PatientLookup.java",
            "emergencyroom/SeverityLevel.java"
        );

        if (result != 0) {
            System.err.println("\n[ERROR] Package compilation failed.");
            return;
        }

        System.out.println("Compiling the triage system...");
        // 2. Compile TriageSystem using the current directory as the classpath
        result = compiler.run(null, null, null,
            "-cp", ".",
            "TriageSystem.java"
        );

        if (result != 0) {
            System.err.println("\n[ERROR] TriageSystem compilation failed.");
            return;
        }

        System.out.println("Compilation successful! Launching the Triage System...\n");

        // 3. Dynamically load and run TriageSystem to avoid compile-time dependencies in Main.java
        try {
            Class<?> triageClass = Class.forName("TriageSystem");
            Object systemInstance = triageClass.getDeclaredConstructor().newInstance();
            Method startMethod = triageClass.getMethod("start");
            startMethod.invoke(systemInstance);
        } catch (Exception e) {
            System.err.println("\n[ERROR] Failed to launch the Triage System.");
            e.printStackTrace();
        }
    }

    private static void cleanOldClassFiles(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".class")) {
                    file.delete();
                } else if (file.isDirectory()) {
                    cleanOldClassFiles(file); // Recursively clean class files inside subdirectories
                }
            }
        }
    }

    private static void deleteDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }
}
