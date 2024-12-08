package BessieInterview;

import com.sun.tools.javac.Main;

import java.io.*;
import java.util.*;

public class TestRunner {
    public static void main(String[] args) throws IOException {
        File dataDir = new File("data");
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            System.err.println("Data directory not found.");
            return;
        }

        // Filter to get all .in files
        File[] inputFiles = dataDir.listFiles((dir, name) -> name.endsWith(".in"));
        if (inputFiles == null) {
            System.err.println("No input files found.");
            return;
        }

        Arrays.sort(inputFiles, Comparator.comparing(File::getName)); // Sort to run in order

        int passed = 0;
        int total = 0;

        for (File inFile : inputFiles) {
            total++;
            String testName = inFile.getName().substring(0, inFile.getName().length()-3);
            // Remove ".in" to get test prefix
            File outFile = new File(dataDir, testName + ".out");

            if (!outFile.exists()) {
                System.out.println("No corresponding output file for test: " + testName);
                continue;
            }

            // Capture original System.in and System.out
            InputStream originalIn = System.in;
            PrintStream originalOut = System.out;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);

            try (FileInputStream fis = new FileInputStream(inFile)) {
                System.setIn(fis);
                System.setOut(ps);

                // Run the main solution
                Main.main(new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Restore System.in and System.out
                System.setIn(originalIn);
                System.setOut(originalOut);
            }

            // Now compare output
            String programOutput = baos.toString().trim();
            String expectedOutput = new String(java.nio.file.Files.readAllBytes(outFile.toPath())).trim();

            if (programOutput.equals(expectedOutput)) {
                System.out.println("Test " + testName + ": PASS");
                passed++;
            } else {
                System.out.println("Test " + testName + ": FAIL");
                System.out.println("Expected:");
                System.out.println(expectedOutput);
                System.out.println("Got:");
                System.out.println(programOutput);
            }
        }

        System.out.println("Passed " + passed + " out of " + total + " tests.");
    }
}

