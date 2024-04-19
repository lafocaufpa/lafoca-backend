package com.ufpa.lafocabackend.core.utils;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class LafocaUtils {
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
            LocalDate currentDate = LocalDate.now();
            String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return slug + "-" + formattedDate;
        }

    }

    /**
     * Esse método cria o nome da foto a ser armazenada na nuvem, concatenando o slug e o formato de arquivo
     * @param slug             slug da entidade (project, user, member). ex: john-doe-yue83j
     * @param originalFilename nome original da foto ou qualquer de outro arquivo enviado pelo endpoint ou uri que foi
     *                        enviado para ser vinculado a entidade. É necessário que esse nome original da foto contenha
     *                        o formato de arquivo seguido do ponto (.). ex
     *                         foto_download_member-2024.jpg ou foto_download_member-2024.png
     * @return retorna o nome do arquivo junto ao seu formato de arquivo. ex: doe-uie83j.jpg ou doe-uie83j.png
     */
    public static String createPhotoFilename(String slug, String originalFilename) {
        return slug
                + Objects.requireNonNull
                        (originalFilename)
                .substring(originalFilename.lastIndexOf("."));
    }

}
