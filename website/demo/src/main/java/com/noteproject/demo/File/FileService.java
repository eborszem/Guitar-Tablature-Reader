package com.noteproject.demo.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    public String readFile(String file) {
        try {
            Resource resource = new ClassPathResource(file);
            Path path = resource.getFile().toPath();
    
            if (Files.exists(path) && Files.isReadable(path)) {
                return new String(Files.readAllBytes(path));
            } else {
                throw new RuntimeException("file not found " + file);
            }

        } catch(Exception e) {
            throw new RuntimeException("error reading file " + file, e);
        }
    }

    public void writeToFile(String file) {
        System.out.println("writing to file");
        try {
            Resource resource = new ClassPathResource(file);
            Path path = resource.getFile().toPath();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), true))) {
                writer.newLine();  // creates new line on last line in file
                writer.newLine();  // empty new line indicates new measure
                writer.write("0,0,0,0,0,0");  // empty first chord of new measure
            }
        } catch (IOException e) {
            throw new RuntimeException("error writing to file " + file, e);
        }
    }
}
