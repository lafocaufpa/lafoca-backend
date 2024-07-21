package com.ufpa.lafocabackend.core.database;

import com.smattme.MysqlExportService;
import com.smattme.MysqlImportService;
import lombok.Getter;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Comparator;
import java.util.Properties;

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

    @Value("${lafoca.database.dirtriggers}")
    private String triggers;

    @Value("${lafoca.database.backuppass}")
    private String backuppass;

    public File backupDatabase() {
        Path tempDirPath = null;
        try {
            tempDirPath = Files.createTempDirectory("db-backup-");
            MysqlExportService mysqlExportService = getMysqlExportService(tempDirPath);

            File generatedZipFile = mysqlExportService.getGeneratedZipFile();

            if (generatedZipFile != null && generatedZipFile.exists()) {
                return createPasswordProtectedZip(generatedZipFile, backuppass);
            } else {
                System.out.println("Falha ao gerar o arquivo de backup.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private MysqlExportService getMysqlExportService(Path tempDirPath) throws IOException, SQLException, ClassNotFoundException {
        String tempDir = tempDirPath.toString();

        Properties properties = new Properties();
        properties.setProperty(MysqlExportService.DB_NAME, "lafoca");
        properties.setProperty(MysqlExportService.DB_USERNAME, dbUsername);
        properties.setProperty(MysqlExportService.JDBC_CONNECTION_STRING, dbUrl);
        properties.setProperty(MysqlExportService.DB_PASSWORD, dbPassword);
        properties.setProperty(MysqlExportService.PRESERVE_GENERATED_ZIP, "true");
        properties.setProperty(MysqlExportService.TEMP_DIR, tempDir);

        MysqlExportService mysqlExportService = new MysqlExportService(properties);

        mysqlExportService.export();
        return mysqlExportService;
    }

    private File createPasswordProtectedZip(File fileToZip, String password) throws IOException {
        String zipFilePath = fileToZip.getAbsolutePath();
        String zipFileName = zipFilePath.substring(0, zipFilePath.lastIndexOf('.')) + "_protected.zip";

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setCompressionLevel(CompressionLevel.HIGHER);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);

        ZipFile zipFile = new ZipFile(zipFileName, password.toCharArray());
        zipFile.addFile(fileToZip, zipParameters);

        return new File(zipFileName);
    }

    public boolean importDatabase(File backupFile) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            connection.setAutoCommit(false);

            if (!deleteAllTables(connection)) {
                connection.rollback();
                return false;
            }

            String sql = new String(Files.readAllBytes(backupFile.toPath()));

            boolean result = MysqlImportService.builder()
                    .setDatabase("lafoca")
                    .setSqlString(sql)
                    .setUsername(dbUsername)
                    .setPassword(dbPassword)
                    .importDatabase();

            if (!result) {
                connection.rollback();
                return false;
            }

            executeTriggersScript(connection);

            connection.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        }
    }

    private boolean deleteAllTables(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName);
            }

            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAllRecords() {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            connection.setAutoCommit(false);

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate("DELETE FROM " + tableName);
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        }
    }

    private void executeTriggersScript(Connection connection) throws SQLException {
        String filePath = triggers;

        try (
                Statement stmt = connection.createStatement();
                InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);
                BufferedReader br = new BufferedReader(new InputStreamReader(is))
        ) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + filePath);
            }

            StringBuilder sql = new StringBuilder();
            String line;
            String delimiter = ";";

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("DELIMITER")) {
                    delimiter = line.split(" ")[1];
                    continue;
                }

                if (!line.isEmpty() && !line.startsWith("--") && !line.startsWith("#")) {
                    sql.append(line).append("\n");

                    if (line.endsWith(delimiter)) {
                        String command = sql.toString().replace(delimiter, ";");
                        try {
                            stmt.execute(command);
                        } catch (SQLException e) {
                            System.err.println("Error executing SQL command: " + command);
                            throw e;
                        }
                        sql.setLength(0);
                    }
                }
            }

            connection.commit();
            System.out.println("SQL file executed successfully!");

        } catch (SQLException e) {
            connection.rollback();
            System.err.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void fileDeletion(Path filePath) {
        if (filePath != null && Files.exists(filePath)) {
            try {
                Path parentDir = filePath.getParent();

                if (parentDir != null && Files.exists(parentDir)) {
                    Files.walk(parentDir)
                            .sorted(Comparator.reverseOrder())
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                    System.out.println("Deletado: " + path);
                                } catch (IOException e) {
                                    System.err.println("Erro ao deletar: " + path);
                                    e.printStackTrace();
                                }
                            });

                    if (Files.exists(parentDir)) {
                        Files.delete(parentDir);
                        System.out.println("Diretório temporário deletado: " + parentDir);
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao deletar o diretório: " + filePath.getParent());
                e.printStackTrace();
            }
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
}
