package com.ufpa.lafocabackend.domain.service;

import com.smattme.MysqlExportService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static com.smattme.MysqlExportService.TEMP_DIR;

@Service
public class DbInfo {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Getter
    @Value("${lafoca.database.dirbackup}")
    private String backupDir;

    public File backupDatabase() {

        Properties properties = new Properties();
        properties.setProperty(MysqlExportService.DB_NAME, "lafoca");
        properties.setProperty(MysqlExportService.DB_USERNAME, dbUsername);
        properties.setProperty(MysqlExportService.JDBC_CONNECTION_STRING, dbUrl);
        properties.setProperty(MysqlExportService.DB_PASSWORD, dbPassword);
        properties.setProperty(MysqlExportService.PRESERVE_GENERATED_ZIP, "true");
        properties.setProperty(MysqlExportService.TEMP_DIR, backupDir);

        MysqlExportService mysqlExportService = new MysqlExportService(properties);

        try {
            mysqlExportService.export();

            File generatedZipFile = mysqlExportService.getGeneratedZipFile();

            if (generatedZipFile != null && generatedZipFile.exists()) {
                return generatedZipFile;
            } else {
                System.out.println("Falha ao gerar o arquivo de backup.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void FileDeletion(File file) {

            try {
                if (file.exists() && file.isFile()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        System.out.println("Arquivo deletado: " + file.getAbsolutePath());
                    } else {
                        System.out.println("Falha ao deletar o arquivo: " + file.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public String getDatabaseName() {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            DatabaseMetaData metaData = connection.getMetaData();
            return metaData.getDatabaseProductName();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    public String getDatabaseVersion() {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            DatabaseMetaData metaData = connection.getMetaData();
            return metaData.getDatabaseProductVersion();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    public String getDatabaseStatus() {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            return "Connected";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Disconnected";
        }
    }

    public String getLastBackup() {
        return "Not implemented";
    }

}
