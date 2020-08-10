package com.example.springboot;

import com.example.springboot.filehandlers.FileToBase64StringConversion;
import com.example.springboot.services.RestService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;


@SpringBootApplication
public class Application implements CommandLineRunner {

	private RestService restService;
	private static final int THRESHOLD = 40;
	public Application(RestService restService) {
		this.restService = restService;
	}

	public static void main(String[] args) {

		ApplicationContext ctx = SpringApplication.run(Application.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		try {


			String referenceImagePath = args[0];
			//String probeImagePath = "/home/jakub/Downloads/4318_6669_bundle_archive/train/mindy_kaling/httpssmediacacheakpinimgcomxeedccadejpg.jpg";

			String referenceImage = FileToBase64StringConversion.encoder(referenceImagePath);
			//String probeImage = FileToBase64StringConversion.encoder(probeImagePath);
			String probeDirectoryPath = args[1];
			String referenceImageTemplate = null;

			try {
				referenceImageTemplate = restService.findTemplate(referenceImage, "/api/v6/face/detect");
			} catch (HttpClientErrorException e) {
				System.out.println("Problem occurred during loading of the reference image. " +
						"Please check the provided path.");
			}


			//System.out.println(restService.findTemplate(referenceImage, "/api/v6/face/detect"));

			//System.out.println(probeImage);

			File directory = new File(probeDirectoryPath);
			String files[] = directory.list();
			System.out.println();

			String fileName = null;
			try {
				for (String file : files) {
					fileName = file;
					try {
						if (restService.verifyRequest("/api/v6/face/verify",
								referenceImageTemplate,
								restService.findTemplate(FileToBase64StringConversion.encoder(probeDirectoryPath
										+ "/" + file), "/api/v6/face/detect")) > THRESHOLD) {
							System.out.println(file + " : ZHODA");
						} else System.out.println(probeDirectoryPath + "/" + file + " : " + "NEZHODA");

					} catch (StringIndexOutOfBoundsException e) {
						System.out.println("Check the the photo at " + probeDirectoryPath + "/" + fileName + "it could not be processed properly! Face probably could not be recognized");
					}
				}
				//System.out.println(probeDirectoryPath+"/"+file);
			}/*catch (StringIndexOutOfBoundsException e) {
			System.out.println("Check the the photo at " + probeDirectoryPath + "/" + fileName + "it could not be processed properly! Face probably could not be recognized");
		}*/ catch (NullPointerException ex) {
				System.out.println("Problem with with probe images directory path. Check it out and try again.");
				if (!directory.exists() || !directory.isDirectory()) {
					System.out.println(probeDirectoryPath + "is does not exists or it is not a directory");
				}
			}
		}catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Reference image path and probe direcotry path cannot be null!");
		}
	}
}
