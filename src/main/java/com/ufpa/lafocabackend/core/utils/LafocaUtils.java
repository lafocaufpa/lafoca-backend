package com.ufpa.lafocabackend.core.utils;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LafocaUtils {

    /**
     * Cria um slug com base no nome e ID fornecidos, ou apenas no nome se o ID for nulo.
     * Se o ID for nulo, o slug será composto pelo nome seguido pela data e hora atuais formatadas.
     *
     * @param name O nome a ser utilizado para gerar o slug.
     * @param id O ID a ser utilizado para gerar o sufixo do slug. Pode ser nulo.
     * @return O slug gerado.
     * <p>
     * Exemplo:
     * Se o nome fornecido for "Meu Projeto" e o ID for nulo, o slug resultante será algo como
     * "meu-projeto-04122020231555", onde "04122020231555" representa a data e hora atuais formatadas.
     */
    // Método principal para criar o slug
    public static String createSlug(String name, String id) {
        String slug = generateSlugFromName(name);

        if (id != null && !id.isEmpty()) {
            String formattedId = formatId(id);
            return slug + "-" + formattedId;
        } else {
            String currentDate = getCurrentDate();
            return slug + "-" + currentDate;
        }
    }

    // Método para gerar o slug a partir do nome
    private static String generateSlugFromName(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "")
                .replaceAll("-{2,}", "-");
    }

    // Método para formatar o ID, verificando se está no formato de data "yyyy-MM-dd"
    private static String formatId(String id) {
        if (isValidDateFormat(id)) {
            return convertDateFormat(id);
        } else {
            return extractPrefix(id);
        }
    }

    // Método para verificar se o ID está no formato "yyyy-MM-dd"
    private static boolean isValidDateFormat(String id) {
        Pattern pattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        Matcher matcher = pattern.matcher(id);
        return matcher.matches();
    }

    // Método para converter a data de "yyyy-MM-dd" para "dd-MM-yyyy"
    private static String convertDateFormat(String id) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(id, inputFormatter);
            return date.format(outputFormatter);
        } catch (DateTimeParseException e) {
            // Se ocorrer um erro, retorna o ID original
            return id;
        }
    }

    // Método para extrair o prefixo do ID até o primeiro "-"
    private static String extractPrefix(String id) {
        return id.contains("-") ? id.substring(0, id.indexOf("-")) : id;
    }

    // Método para obter a data e hora atual no formato "dd-MM-yyyy"
    private static String getCurrentDate() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return currentDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }


    /**
     * Esse método cria o nome da foto a ser armazenada na nuvem, concatenando o id e o formato de arquivo
     * @param id             id da entidade (project, user, member). ex: john-doe-yue83j
     * @param originalFilename nome original da foto ou qualquer de outro arquivo enviado pelo endpoint ou uri que foi
     *                        enviado para ser vinculado a entidade. É necessário que esse nome original da foto contenha
     *                        o formato de arquivo seguido do ponto (.). ex
     *                         foto_download_member-2024.jpg ou foto_download_member-2024.png
     * @return retorna o nome do arquivo junto ao seu formato de arquivo. ex: doe-uie83j.jpg ou doe-uie83j.png
     */
    public static String createPhotoFilename(String id, String originalFilename) {
        return id
                + Objects.requireNonNull
                        (originalFilename)
                .substring(originalFilename.lastIndexOf("."));
    }

    public static String generateRandomPassword() {
        int length = 10;
        Random random = new Random();

        List<Character> characters = new ArrayList<>();

        for (char c = 'A'; c <= 'Z'; c++) {
            characters.add(c);
        }

        for (char c = 'a'; c <= 'z'; c++) {
            characters.add(c);
        }

        for (char c = '0'; c <= '9'; c++) {
            characters.add(c);
        }

        List<Character> result = new ArrayList<>();

        for (int i = 0; i < length - 1; i++) {
            result.add(characters.get(random.nextInt(characters.size())));
        }

        result.add('@');
        Collections.shuffle(result);

        StringBuilder stringBuilder = new StringBuilder();
        for (Character ch : result) {
            stringBuilder.append(ch);
        }

        return stringBuilder.toString();
    }

    public static String formatOffsetDateTime(OffsetDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS'Z'");
        return dateTime.format(formatter);
    }

}
