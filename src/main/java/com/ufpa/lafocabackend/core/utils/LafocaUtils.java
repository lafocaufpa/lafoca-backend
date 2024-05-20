package com.ufpa.lafocabackend.core.utils;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
    public static String createSlug(String name, String id) {

        String slug = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "")
                .replaceAll("-{2,}", "-");
        if (id != null) {

            String suffix = id.substring(0, id.indexOf("-"));
            return slug + "-" + suffix;
        } else {

            LocalDateTime currentDateTime = LocalDateTime.now();
            String ddMMyyyyHHmmss = currentDateTime.format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"));
            return slug + "-" + ddMMyyyyHHmmss;
        }

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
}
