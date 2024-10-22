package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Article;
import com.ufpa.lafocabackend.domain.model.LineOfResearch;
import com.ufpa.lafocabackend.domain.model.Member;
import com.ufpa.lafocabackend.domain.model.MemberInfo;
import com.ufpa.lafocabackend.domain.model.dto.input.ArticleInputDto;
import com.ufpa.lafocabackend.repository.ArticleRepository;
import com.ufpa.lafocabackend.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;
    private final LineOfResearchService lineOfResearchService;
    private final MemberRepository memberRepository;

    public ArticleService(ArticleRepository articleRepository, ModelMapper modelMapper, LineOfResearchService lineOfResearchService, MemberRepository memberRepository) {
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
        this.lineOfResearchService = lineOfResearchService;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Article save (ArticleInputDto articleDto) {

        Article article = modelMapper.map(articleDto, Article.class);

        if(articleDto.getLineOfResearchIds() != null){
            for (String lineOfResearchId : articleDto.getLineOfResearchIds()) {
                LineOfResearch lineOfResearch = lineOfResearchService.read(lineOfResearchId);
                article.addLineOfResearch(lineOfResearch);
            }
        }

        Article articleSaved = articleRepository.save(article);

        setMembersInArticle(articleSaved, articleDto.getMembers());


        return articleSaved;
    }

    public Page<Article> list(String title, String lineOfResearchId, Integer year, Pageable pageable) {
        if (title != null && !title.isEmpty() && lineOfResearchId != null && !lineOfResearchId.isEmpty() && year != null) {
            return articleRepository.findByTitleContainingAndLineOfResearchIdAndDate(title, lineOfResearchId, String.valueOf(year), pageable);
        } else if (title != null && !title.isEmpty() && lineOfResearchId != null && !lineOfResearchId.isEmpty()) {
            return articleRepository.findByTitleContainingAndLineOfResearchId(title, lineOfResearchId, pageable);
        } else if (title != null && !title.isEmpty() && year != null) {
            return articleRepository.findByTitleContainingAndDate(title, String.valueOf(year), pageable);
        } else if (lineOfResearchId != null && !lineOfResearchId.isEmpty() && year != null) {
            return articleRepository.findByLineOfResearchIdAndDate(lineOfResearchId, String.valueOf(year), pageable);
        } else if (lineOfResearchId != null && !lineOfResearchId.isEmpty()) {
            return articleRepository.findByLineOfResearchId(lineOfResearchId, pageable);
        } else if (title != null && !title.isEmpty()) {
            return articleRepository.findByTitleContaining(title, pageable);
        } else if (year != null) {
            return articleRepository.findByDate(String.valueOf(year), pageable);
        } else {
            return articleRepository.findAll(pageable);
        }
    }

    public Article read (Long articleId) {
        return getOrFail(articleId);
    }

    public Article readBySlug(String slug) {
        return articleRepository.findBySlug(slug).
                orElseThrow(() -> new EntityNotFoundException(Article.class.getSimpleName(), slug));
    }

    public Article update(Long articleId, ArticleInputDto newArticle) {

        final Article currentArticle = read(articleId);
        Set<MemberInfo> currentMembers = currentArticle.getMembers();

        modelMapper.map(newArticle, currentArticle);
        currentArticle.setArticleId(articleId);

        // Atualizar linhas de pesquisa
        List<LineOfResearch> linesOfResearches = new ArrayList<>();
        if (newArticle.getLineOfResearchIds() != null) {
            for (String lineOfResearchId : newArticle.getLineOfResearchIds()) {
                LineOfResearch lineOfResearch = lineOfResearchService.read(lineOfResearchId);
                linesOfResearches.add(lineOfResearch);
            }
        }
        currentArticle.setLinesOfResearch(linesOfResearches);

        // Gerenciar membros (adicionar/associar ou remover)
        Set<MemberInfo> newMembers = newArticle.getMembers();

        // Remover membros que não estão mais associados
        Set<MemberInfo> membersToRemove = new HashSet<>(currentMembers);
        membersToRemove.removeAll(newMembers);  // Membros que devem ser removidos

        for (MemberInfo memberInfoToRemove : membersToRemove) {
            if (memberInfoToRemove.getSlug() != null) {
                Optional<Member> member = memberRepository.findBySlug(memberInfoToRemove.getSlug());
                if (member.isPresent()) {
                    Member foundMember = member.get();
                    foundMember.removeArticle(currentArticle);  // Remover a associação do membro ao artigo
                    memberRepository.save(foundMember);  // Persistir a alteração no banco
                }
            }
        }

        // Associar novos membros
        setMembersInArticle(currentArticle, newMembers);

        return articleRepository.save(currentArticle);  // Salvar o artigo atualizado
    }

    private void setMembersInArticle(Article currentArticle, Set<MemberInfo> members) {
        if (members != null && !members.isEmpty()) {
            for (MemberInfo memberInfo : members) {

                if (memberInfo.getSlug() != null) {
                    Optional<Member> member = memberRepository.findBySlug(memberInfo.getSlug());

                    if (member.isPresent()) {
                        Member foundMember = member.get();
                        foundMember.addArticles(currentArticle);  // Associar o artigo ao membro
                        memberRepository.save(foundMember);  // Persistir a alteração no banco
                    }
                } else if (memberInfo.getSlug() == null && memberInfo.getName() != null) {
                    // Tratar membros externos (sem slug)
                    currentArticle.addMember(memberInfo);  // Adiciona o MemberInfo diretamente ao artigo
                }
            }
        }
    }

    public void delete (Long articleId) {

        try {
            articleRepository.deleteById(articleId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(Article.class.getSimpleName(), articleId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(Article.class.getSimpleName(), articleId);
        }

    }

    private Article getOrFail(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow( () -> new EntityNotFoundException(Article.class.getSimpleName(), articleId));
    }

    public void addLinesOfResearch(Long articleId, List<String> linesOfResearch) {

    }
}
