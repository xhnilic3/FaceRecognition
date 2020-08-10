package com.example.springboot.filehandlers;

import ch.qos.logback.core.util.FileUtil;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.*;
import java.util.Base64;

public class FileToBase64StringConversion {

    public static String encoder(String imagePath) {
        String base64Image = "";
        File file = new File(imagePath);
        try(FileInputStream imageInFile = new FileInputStream(file)) {
            // Reading an Image file from file system
            byte imageData[] = new byte[(int) file.length()];
            imageInFile.read(imageData);
            base64Image = Base64.getEncoder().encodeToString(imageData);
        } catch (FileNotFoundException e) {
            System.out.println("Image not found! " + e);
        } catch (IOException e) {
            System.out.println("Exception while reading the Image " + e);
        }
        return base64Image;
    }

    public static void decoder(String inputPath, String outputPath) {
        try(FileOutputStream imageOutFile = new FileOutputStream(outputPath)) {
            byte[] imageByteArray = Base64.getDecoder().decode(inputPath);
            imageOutFile.write(imageByteArray);
        } catch (FileNotFoundException e) {
            System.out.println("Image not found! " + e);
        } catch (IOException e) {
            System.out.println("Exception while reading the Image " + e);
        }
    }

}
