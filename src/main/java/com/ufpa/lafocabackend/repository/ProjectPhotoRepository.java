package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.ProjectPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProjectPhotoRepository extends JpaRepository<ProjectPhoto, String> {

    @Transactional
    @Modifying
    @Query("DELETE FROM ProjectPhoto pp WHERE pp.photoId = :photoId")
    void deleteProjectPhotoByProjectId(String photoId);

    /**
     * Remove a referência da chave estrangeira (foreign key) de um registro de um Project
     * para permitir que o registro relacionado na outra tabela (ProjectPhoto) seja excluído.
     *
     * @param projectId O ID do Project cuja referência da foto (photo) deve ser removida.
     *           Isso permitirá que o registro da foto (photo) associado seja excluído sem afetar o usuário.
     */
    @Transactional
    @Modifying
    @Query("UPDATE Project p SET p.projectPhoto = null WHERE p.projectId = :projectId")
    void removeProjectPhotoReference(String projectId);

    /**
     * Retorna o filename da foto baseado no id. Necessário para ser deletado da nuvem
     *
     * @param photoId O id da foto de um Project.
     * @return O filename da foto.
     */
    @Transactional
    @Query("SELECT pp.fileName FROM ProjectPhoto pp WHERE pp.photoId = :photoId")
    String findProjectPhotoFileNameByPhotoId(String photoId);
}
