package com.ufpa.lafocabackend.core.storage;

import com.amazonaws.regions.Regions;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Getter
@Setter
@Component
@ConfigurationProperties("lafoca.storage")
public class StorageProperties {

    private Local local = new Local();
    private S3 s3 = new S3();
    private TipoStorage tipoStorage = TipoStorage.LOCAL;
    public enum TipoStorage {
        LOCAL, S3
    }
    @Getter
    @Setter
    public class Local {
        private Path diretorioFotos;
    }

    @Setter
    @Getter
    public class S3 {
        private String idChaveAcesso;
        private String chaveAcessoSecreta;
        private String bucket;
        private Regions regiao;
        private String diretorio;
        private String diretorio_news;
        private String diretorio_users;
        private String diretorio_projects;
        private String diretorio_members;
        private String diretorio_skills;
    }

}
