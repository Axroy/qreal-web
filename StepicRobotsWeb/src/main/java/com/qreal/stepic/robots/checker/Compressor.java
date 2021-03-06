package com.qreal.stepic.robots.checker;

import com.qreal.stepic.robots.constants.PathConstants;

import java.io.*;

/**
 * Created by vladimir-zakharov on 31.08.15.
 */
public class Compressor {

    public void compress(String taskId, String pathToFolder) throws IOException, InterruptedException {
        File folder = new File(pathToFolder);
        ProcessBuilder compressorProcBuilder = new ProcessBuilder("sudo", "compressor", taskId);
        compressorProcBuilder.directory(folder);
        compressorProcBuilder.start().waitFor();
    }

    public void decompress(String taskId) throws IOException, InterruptedException {
        String pathToFile = PathConstants.TASKS_PATH + "/" + taskId;
        File folder = new File(pathToFile);
        File diagramDirectory = new File(pathToFile + "/" + taskId);

        if (!diagramDirectory.exists()) {
            ProcessBuilder processBuilder = new ProcessBuilder(PathConstants.COMPRESSOR_PATH, taskId + ".qrs");
            processBuilder.directory(folder);

            final Process process = processBuilder.start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
        }
    }

}
