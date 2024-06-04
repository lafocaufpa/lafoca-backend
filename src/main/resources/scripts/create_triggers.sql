DELIMITER //

-- Trigger para a tabela article
CREATE TRIGGER after_article_insert
AFTER INSERT ON article
FOR EACH ROW
BEGIN
    INSERT INTO lafoca (date_time, number_of_defendedtccs, number_of_members, number_of_projects, number_of_published_articles)
    SELECT NOW(6), 
           (SELECT COUNT(*) FROM tcc), 
           (SELECT COUNT(*) FROM member), 
           (SELECT COUNT(*) FROM project), 
           (SELECT COUNT(*) FROM article);
END//

CREATE TRIGGER after_article_delete
AFTER DELETE ON article
FOR EACH ROW
BEGIN
    INSERT INTO lafoca (date_time, number_of_defendedtccs, number_of_members, number_of_projects, number_of_published_articles)
    SELECT NOW(6), 
           (SELECT COUNT(*) FROM tcc), 
           (SELECT COUNT(*) FROM member), 
           (SELECT COUNT(*) FROM project), 
           (SELECT COUNT(*) FROM article);
END//

-- Trigger para a tabela tcc
CREATE TRIGGER after_tcc_insert
AFTER INSERT ON tcc
FOR EACH ROW
BEGIN
    INSERT INTO lafoca (date_time, number_of_defendedtccs, number_of_members, number_of_projects, number_of_published_articles)
    SELECT NOW(6), 
           (SELECT COUNT(*) FROM tcc), 
           (SELECT COUNT(*) FROM member), 
           (SELECT COUNT(*) FROM project), 
           (SELECT COUNT(*) FROM article);
END//

CREATE TRIGGER after_tcc_delete
AFTER DELETE ON tcc
FOR EACH ROW
BEGIN
    INSERT INTO lafoca (date_time, number_of_defendedtccs, number_of_members, number_of_projects, number_of_published_articles)
    SELECT NOW(6), 
           (SELECT COUNT(*) FROM tcc), 
           (SELECT COUNT(*) FROM member), 
           (SELECT COUNT(*) FROM project), 
           (SELECT COUNT(*) FROM article);
END//

-- Trigger para a tabela project
CREATE TRIGGER after_project_insert
AFTER INSERT ON project
FOR EACH ROW
BEGIN
    INSERT INTO lafoca (date_time, number_of_defendedtccs, number_of_members, number_of_projects, number_of_published_articles)
    SELECT NOW(6), 
           (SELECT COUNT(*) FROM tcc), 
           (SELECT COUNT(*) FROM member), 
           (SELECT COUNT(*) FROM project), 
           (SELECT COUNT(*) FROM article);
END//

CREATE TRIGGER after_project_delete
AFTER DELETE ON project
FOR EACH ROW
BEGIN
    INSERT INTO lafoca (date_time, number_of_defendedtccs, number_of_members, number_of_projects, number_of_published_articles)
    SELECT NOW(6), 
           (SELECT COUNT(*) FROM tcc), 
           (SELECT COUNT(*) FROM member), 
           (SELECT COUNT(*) FROM project), 
           (SELECT COUNT(*) FROM article);
END//

-- Trigger para a tabela member
CREATE TRIGGER after_member_insert
AFTER INSERT ON member
FOR EACH ROW
BEGIN
    INSERT INTO lafoca (date_time, number_of_defendedtccs, number_of_members, number_of_projects, number_of_published_articles)
    SELECT NOW(6), 
           (SELECT COUNT(*) FROM tcc), 
           (SELECT COUNT(*) FROM member), 
           (SELECT COUNT(*) FROM project), 
           (SELECT COUNT(*) FROM article);
END//

CREATE TRIGGER after_member_delete
AFTER DELETE ON member
FOR EACH ROW
BEGIN
    INSERT INTO lafoca (date_time, number_of_defendedtccs, number_of_members, number_of_projects, number_of_published_articles)
    SELECT NOW(6), 
           (SELECT COUNT(*) FROM tcc), 
           (SELECT COUNT(*) FROM member), 
           (SELECT COUNT(*) FROM project), 
           (SELECT COUNT(*) FROM article);
END//

DELIMITER ;
