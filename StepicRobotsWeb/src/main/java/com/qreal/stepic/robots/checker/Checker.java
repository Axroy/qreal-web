package com.qreal.stepic.robots.checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qreal.stepic.robots.constants.PathConstants;
import com.qreal.stepic.robots.exceptions.NotExistsException;
import com.qreal.stepic.robots.exceptions.SubmitException;
import com.qreal.stepic.robots.model.diagram.Report;
import com.qreal.stepic.robots.model.diagram.ReportMessage;
import com.qreal.stepic.robots.model.diagram.SubmitResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.context.MessageSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by vladimir-zakharov on 24.08.15.
 */
public class Checker {

    public SubmitResponse submit(String taskId, String filename, String uuidStr,
                                        MessageSource messageSource, Locale locale) throws SubmitException {
        String nameWithoutExt = filename.substring(0, filename.length() - 4);
        try {
            File taskFields = new File(PathConstants.TASKS_PATH + "/" + taskId + "/fields");
            File solutionFolder = new File(PathConstants.TASKS_PATH + "/" + taskId + "/solutions/" + uuidStr);

            if (taskFields.exists()) {
                File solutionFields = new File(solutionFolder.getPath() + "/fields/" + nameWithoutExt);
                FileUtils.copyDirectory(taskFields, solutionFields);
            }

            ProcessBuilder interpreterProcBuilder = new ProcessBuilder(PathConstants.CHECKER_PATH, filename);
            Map<String, String> environment = interpreterProcBuilder.environment();

            if (locale.equals(new Locale("en", ""))) {
                environment.put("LANG", "en_US.utf8");
            } else {
                environment.put("LANG", "ru_RU.utf8");
            }
            interpreterProcBuilder.directory(solutionFolder);

            final Process process = interpreterProcBuilder.start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();

            String trajectoryPath;
            Report report;

            File failedField = new File(PathConstants.TASKS_PATH + "/" + taskId +
                    "/solutions/" + uuidStr + "/failed-field");
            String fieldXML = null;
            if (failedField.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(failedField));
                String pathToFailedField = br.readLine();
                fieldXML = new String(Files.readAllBytes(Paths.get(pathToFailedField)), StandardCharsets.UTF_8);
                String[] pathParts = pathToFailedField.split("/");
                String failedFilename = pathParts[pathParts.length - 1];
                String failedName = failedFilename.substring(0, failedFilename.length() - 4);
                trajectoryPath = PathConstants.TASKS_PATH + "/" + taskId +
                        "/solutions/" + uuidStr + "/trajectories/" + nameWithoutExt + "/" + failedName;

                report = parseReportFile(new File(PathConstants.TASKS_PATH + "/" + taskId +
                        "/solutions/" + uuidStr + "/reports/" + nameWithoutExt + "/" + failedName),
                        messageSource, locale);
            } else {
                String pathToMetainfo = PathConstants.TASKS_PATH + "/" + taskId + "/" + taskId + "/metaInfo.xml";

                try {
                    fieldXML = getWorldModelFromMetainfo(pathToMetainfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new SubmitException(messageSource.getMessage("label.twoDModelError", null, locale));
                }
                trajectoryPath = PathConstants.TASKS_PATH + "/" + taskId + "/solutions/" + uuidStr + "/trajectory";

                report = parseReportFile(new File(PathConstants.TASKS_PATH + "/" + taskId +
                        "/solutions/" + uuidStr + "/report"), messageSource, locale);
            }

            String trace = new String(Files.readAllBytes(Paths.get(trajectoryPath)));
            //FileUtils.deleteDirectory(solutionFolder);

            return new SubmitResponse(messageSource.getMessage("label.successUpload", null, locale),
                    report, trace, fieldXML);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SubmitException(messageSource.getMessage("label.checkingError", null, locale));
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new SubmitException(messageSource.getMessage("label.checkingError", null, locale));
        }
    }

    public String getWorldModelFromMetainfo(String pathToMetaInfo) throws NotExistsException,
            IOException, ParserConfigurationException, SAXException {

        File metainfo = new File(pathToMetaInfo);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document metainfoXML = dBuilder.parse(metainfo);

        NodeList infos = metainfoXML.getElementsByTagName("info");
        for (int i = 0; i < infos.getLength(); i++) {
            Element info = (Element) infos.item(i);
            if (info.getAttribute("key").equals("worldModel")) {
                return StringEscapeUtils.unescapeXml(info.getAttribute("value"));
            }
        }
        throw new NotExistsException("There is no attribute key with value worldModel in the metainfo");
    }

    private Report parseReportFile(File file, MessageSource messageSource, Locale locale) throws SubmitException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ReportMessage> messages = mapper.readValue(file, List.class);
            return new Report(messages);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SubmitException(messageSource.getMessage("label.reportError", null, locale));
        }
    }

}
