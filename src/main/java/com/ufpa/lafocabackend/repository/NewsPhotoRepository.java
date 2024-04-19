package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.NewsPhoto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsPhotoRepository extends JpaRepository<NewsPhoto, String> {

    @Transactional
    @Modifying
    @Query("DELETE FROM NewsPhoto np WHERE np.photoId = :photoId")
    void deleteNewsPhotoByNewsId(String photoId);

    /**
     * Remove a referência da chave estrangeira (foreign key) de um registro de um News
     * para permitir que o registro relacionado na outra tabela (NewsPhoto) seja excluído.
     *
     * @param newsId O ID do News cuja referência da foto (photo) deve ser removida.
     *           Isso permitirá que o registro da foto (photo) associado seja excluído sem afetar o usuário.
     */
    @Transactional
    @Modifying
    @Query("UPDATE News n SET n.newsPhoto = null WHERE n.newsId = :newsId")
    void removeNewsPhotoReference(String newsId);


    /**
     * Retorna o filename da foto baseado no id. Necessário para ser deletado da nuvem.
     * O id de uma Photo é o mesmo que o Id da entidade vinculada
     *
     * @param photoId O id da foto de um News.
     * @return O filename da foto.
     */
    @Transactional
    @Query("SELECT np.fileName FROM NewsPhoto np WHERE np.photoId = :photoId")
    String findNewsPhotoFileNameByPhotoId(String photoId);

}
