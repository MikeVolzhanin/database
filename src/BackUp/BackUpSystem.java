package BackUp;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackUpSystem {
    private static String backUpName;

    public BackUpSystem(String BackUpName) {
        backUpName = BackUpName;
    }
    public BackUpSystem(){

    }

    public void createBackUp() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
        String formattedDateTime = currentDateTime.format(formatter);
        // Путь к директории, которую вы хотите архивировать
        String sourceDir = "server";
        // Путь и имя ZIP-файла, который будет создан
        String zipFilePath = "BackUp_" + formattedDateTime + ".zip";

        try {
            FileOutputStream fos = new FileOutputStream(zipFilePath);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File sourceDirectory = new File(sourceDir);

            // Рекурсивно архивируем содержимое директории
            zipDirectory(sourceDirectory, sourceDirectory.getName(), zipOut);

            zipOut.close();
            fos.close();

            System.out.println("Директория успешно архивирована в " + zipFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void zipDirectory(File directory, String parent, ZipOutputStream zipOut) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    zipDirectory(file, parent + "/" + file.getName(), zipOut);
                } else {
                    FileInputStream fis = new FileInputStream(file);
                    ZipEntry zipEntry = new ZipEntry(parent + "/" + file.getName());
                    zipOut.putNextEntry(zipEntry);
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                    fis.close();
                }
            }
        }
    }

    public void loadBackUp() {
        // Путь к ZIP-файлу, который нужно разархивировать
        String destDir = System.getProperty("user.dir");

        try {
            File destDirectory = new File(destDir);
            if (!destDirectory.exists()) {
                destDirectory.mkdirs();
            }

            FileInputStream fis = new FileInputStream(backUpName);
            ZipInputStream zipIn = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;

            while ((entry = zipIn.getNextEntry()) != null) {
                String entryName = entry.getName();
                File entryFile = new File(destDir, entryName);

                if (entry.isDirectory()) {
                    entryFile.mkdirs();
                } else {
                    entryFile.getParentFile().mkdirs(); // Создаем директории для файла, если их нет
                    FileOutputStream fos = new FileOutputStream(entryFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = zipIn.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    fos.close();
                }

                zipIn.closeEntry();
            }

            zipIn.close();
            fis.close();

            System.out.println("BackUp успешно распакован в текущей директории: " + destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

