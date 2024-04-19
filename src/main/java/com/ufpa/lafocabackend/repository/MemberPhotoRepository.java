package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.MemberPhoto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberPhotoRepository extends JpaRepository<MemberPhoto, String> {

    @Transactional
    @Modifying
    @Query("DELETE FROM MemberPhoto up WHERE up.photoId = :photoId")
    void deleteMemberPhotoByMemberId(String photoId);

    /**
     * Remove a referência da chave estrangeira (foreign key) de um registro de um Member
     * para permitir que o registro relacionado na outra tabela (MemberPhoto) seja excluído.
     *
     * @param memberId O ID do Member cuja referência da foto (photo) deve ser removida.
     *           Isso permitirá que o registro da foto (photo) associado seja excluído sem afetar o usuário.
     */
    @Transactional
    @Modifying
    @Query("UPDATE Member m SET m.memberPhoto = null WHERE m.memberId = :memberId")
    void removeMemberPhotoReference(String memberId);
    

    /**
     * Retorna o filename da foto baseado no id. Necessário para ser deletado da nuvem
     *
     * @param photoId O id da foto de um Member.
     * @return O filename da foto.
     */
    @Transactional
    @Query("SELECT mp.fileName FROM MemberPhoto mp WHERE mp.photoId = :photoId")
    String findMemberPhotoFileNameByPhotoId(String photoId);
}
