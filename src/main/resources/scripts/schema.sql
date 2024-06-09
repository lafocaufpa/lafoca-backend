create table IF NOT EXISTS access_group
(
    group_id bigint       not null auto_increment,
    name     varchar(255) not null,
    primary key (group_id)
) engine = InnoDB;

create table IF NOT EXISTS article
(
    article_id bigint       not null auto_increment,
    journal    varchar(225) not null,
    title      varchar(225) not null,
    slug       varchar(500) not null,
    url        VARCHAR(700) not null,
    primary key (article_id)
) engine = InnoDB;

create table IF NOT EXISTS article_line_of_research
(
    article_id          bigint       not null,
    line_of_research_id varchar(255) not null
) engine = InnoDB;

create table IF NOT EXISTS function_member
(
    function_member_id bigint       not null auto_increment,
    description        varchar(225) not null,
    name               varchar(225) not null,
    primary key (function_member_id)
) engine = InnoDB;

create table IF NOT EXISTS lafoca
(
    number_of_defendedtccs       integer not null,
    number_of_members            integer not null,
    number_of_projects           integer not null,
    number_of_published_articles integer not null,
    date_time                    datetime(6),
    lafoca_id                    bigint  not null auto_increment,
    primary key (lafoca_id)
) engine = InnoDB;

create table IF NOT EXISTS line_of_research
(
    name                varchar(50)  not null,
    description         varchar(225) not null,
    line_of_research_id varchar(255) not null,
    primary key (line_of_research_id)
) engine = InnoDB;

create table IF NOT EXISTS member
(
    date_register      datetime     not null,
    function_member_id bigint,
    tcc_id             bigint,
    description        varchar(225) not null,
    email              varchar(225) not null,
    first_name         varchar(225) not null,
    last_name          varchar(225) not null,
    link_linkedin      varchar(225) not null,
    link_portifolio    varchar(225),
    biography          varchar(500) not null,
    slug               varchar(500) not null,
    member_id          varchar(255) not null,
    photo_id           varchar(255),
    primary key (member_id)
) engine = InnoDB;

create table IF NOT EXISTS member_article
(
    article_id bigint       not null,
    member_id  varchar(255) not null,
    primary key (article_id, member_id)
) engine = InnoDB;

create table IF NOT EXISTS member_project
(
    member_id  varchar(255) not null,
    project_id varchar(255) not null,
    primary key (member_id, project_id)
) engine = InnoDB;

create table IF NOT EXISTS member_skill
(
    skill_id  integer      not null,
    member_id varchar(255) not null,
    primary key (skill_id, member_id)
) engine = InnoDB;

create table IF NOT EXISTS member_photo
(
    size         bigint       not null,
    content_type varchar(15)  not null,
    file_name    varchar(225) not null,
    url          varchar(700),
    photo_id     varchar(255) not null,
    primary key (photo_id)
) engine = InnoDB;

create table IF NOT EXISTS news
(
    news_date   datetime     not null,
    description varchar(225) not null,
    title       varchar(225) not null,
    slug        varchar(500) not null,
    content     TEXT         not null,
    news_id     varchar(255) not null,
    photo_id    varchar(255),
    primary key (news_id)
) engine = InnoDB;

create table IF NOT EXISTS news_line_of_research
(
    line_of_research_id varchar(255) not null,
    news_id             varchar(255) not null
) engine = InnoDB;

create table IF NOT EXISTS news_photo
(
    size         bigint       not null,
    content_type varchar(15)  not null,
    file_name    varchar(225) not null,
    url          varchar(700),
    photo_id     varchar(255) not null,
    primary key (photo_id)
) engine = InnoDB;

create table IF NOT EXISTS permission
(
    permission_id bigint       not null auto_increment,
    name          varchar(50)  not null,
    description   varchar(225) not null,
    primary key (permission_id)
) engine = InnoDB;

create table IF NOT EXISTS permission_group
(
    group_id      bigint not null,
    permission_id bigint not null,
    primary key (group_id, permission_id)
) engine = InnoDB;

create table IF NOT EXISTS project
(
    completed   bit           not null,
    year        varchar(4)    not null,
    slug        varchar(500)  not null,
    description varchar(1000) not null,
    photo_id    varchar(255),
    project_id  varchar(255)  not null,
    title       varchar(255)  not null,
    primary key (project_id)
) engine = InnoDB;

create table IF NOT EXISTS project_line_of_research
(
    line_of_research_id varchar(255) not null,
    project_id          varchar(255) not null
) engine = InnoDB;

create table IF NOT EXISTS project_photo
(
    size         bigint       not null,
    content_type varchar(15)  not null,
    file_name    varchar(225) not null,
    url          varchar(700),
    photo_id     varchar(255) not null,
    primary key (photo_id)
) engine = InnoDB;

create table IF NOT EXISTS skill
(
    skill_id integer     not null auto_increment,
    name     varchar(50) not null,
    primary key (skill_id)
) engine = InnoDB;

create table IF NOT EXISTS tcc
(
    date   date         not null,
    tcc_id bigint       not null auto_increment,
    slug   varchar(500) not null,
    name   varchar(255) not null,
    url    VARCHAR(700) not null,
    primary key (tcc_id)
) engine = InnoDB;

create table IF NOT EXISTS user
(
    date_register datetime     not null,
    slug          varchar(500) not null,
    email         varchar(255) not null,
    name          varchar(255) not null,
    password      varchar(255) not null,
    url_photo     VARCHAR(700),
    user_id       varchar(255) not null,
    primary key (user_id)
) engine = InnoDB;

create table IF NOT EXISTS user_group
(
    group_id bigint       not null,
    user_id  varchar(255) not null,
    primary key (group_id, user_id)
) engine = InnoDB;

alter table access_group
    add constraint UK_name unique (name);
alter table article
    add constraint UK_slug unique (slug);
alter table article
    add constraint UK_url unique (url);
alter table line_of_research
    add constraint UK_name unique (name);
alter table member
    add constraint UK_tcc_id unique (tcc_id);
alter table member
    add constraint UK_email unique (email);
alter table member
    add constraint UK_linkedin unique (link_linkedin);
alter table member
    add constraint UK_portifolio unique (link_portifolio);
alter table member
    add constraint UK_slug unique (slug);
alter table member
    add constraint UK_photo_id unique (photo_id);
alter table news
    add constraint UK_slug unique (slug);
alter table news
    add constraint UK_photo_id unique (photo_id);
alter table project
    add constraint UK_slug unique (slug);
alter table project
    add constraint UK_photo_id unique (photo_id);
alter table tcc
    add constraint UK_slug unique (slug);
alter table tcc
    add constraint UK_url unique (url);
alter table user
    add constraint UK_slug unique (slug);
alter table user
    add constraint UK_email unique (email);

alter table article_line_of_research
    add constraint fk_research_article_id
        foreign key (line_of_research_id)
            references line_of_research (line_of_research_id);

alter table article_line_of_research
    add constraint fk_article_research_id
        foreign key (article_id)
            references article (article_id);

alter table member
    add constraint fk_member_function_member_id
        foreign key (function_member_id)
            references function_member (function_member_id);

alter table member
    add constraint fk_member_photo_id
        foreign key (photo_id)
            references member_photo (photo_id)
            on delete set null;

alter table member
    add constraint fk_member_tcc_id
        foreign key (tcc_id)
            references tcc (tcc_id);

alter table member_article
    add constraint fk_member_article_article_id
        foreign key (article_id)
            references article (article_id);

alter table member_article
    add constraint fk_member_article_member_id
        foreign key (member_id)
            references member (member_id);

alter table member_project
    add constraint fk_member_project_project_id
        foreign key (project_id)
            references project (project_id);

alter table member_project
    add constraint fk_member_project_member_id
        foreign key (member_id)
            references member (member_id);

alter table member_skill
    add constraint fk_member_skill_skill_id
        foreign key (skill_id)
            references skill (skill_id);

alter table member_skill
    add constraint fk_member_skill_member_id
        foreign key (member_id)
            references member (member_id);

alter table news
    add constraint fk_photo_id
        foreign key (photo_id)
            references news_photo (photo_id)
            on delete set null;

alter table news_line_of_research
    add constraint fk_research_news_id
        foreign key (line_of_research_id)
            references line_of_research (line_of_research_id);

alter table news_line_of_research
    add constraint fk_news_research_id
        foreign key (news_id)
            references news (news_id);

alter table permission_group
    add constraint fk_permission_group_permission_id
        foreign key (permission_id)
            references permission (permission_id);

alter table permission_group
    add constraint fk_permission_group_group_id
        foreign key (group_id)
            references access_group (group_id);

alter table project
    add constraint fk_project_photo_id
        foreign key (photo_id)
            references project_photo (photo_id)
            on delete set null;

alter table project_line_of_research
    add constraint fk_research_project_id
        foreign key (line_of_research_id)
            references line_of_research (line_of_research_id);

alter table project_line_of_research
    add constraint fk_project_research_id
        foreign key (project_id)
            references project (project_id);

alter table user_group
    add constraint fk_user_group_group_id
        foreign key (group_id)
            references access_group (group_id);

alter table user_group
    add constraint fk_user_group_user_id
        foreign key (user_id)
            references user (user_id);
