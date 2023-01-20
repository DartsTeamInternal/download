package com.file.download;

import com.amazonaws.SdkClientException;
import com.opencsv.CSVWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class DownloadApplication {
    private static final Logger logger = LogManager.getLogger(DownloadApplication.class);

    static String destination = "/home/dartsapp/temp/";
    static String path = "C:\\darts-projects-setups\\download\\src\\main\\resources\\dove-issues.csv";


    public static void main(String[] args) {
        SpringApplication.run(DownloadApplication.class, args);
        Map<String, List<String>> stringStringMap = readCSV(path);
        System.out.println(stringStringMap);
    }

    public static Map<String,List<String>> readCSV(String path) {
        Map<String,List<String>> dove = new LinkedHashMap<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(path));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord csvRecord : csvParser) {
                String name = csvRecord.get("name");
                String archive_location = csvRecord.get("archive_location");
                logger.info("Name: " + name);
                logger.info("Archive_location: " + archive_location);
                dove.put(name,checkPath(archive_location));
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
        return dove;
    }

    public static List<String> checkPath(String cs) {
        List<String> list = new ArrayList<>();
        File file = new File(cs);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles !=null) {
                    for (File f:listFiles) {
                        if (f.isDirectory()) {
                            list.add(f.getName());
                        }
                    }
                }
            }
        }
        return list;
    }

    private static void appendOutputFile(String name) {
        logger.info("----appendOutputFile start---");
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter("output.csv", true))) {
            csvWriter.writeNext(new String[]{name});
            logger.info("----csvWriter end---");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

