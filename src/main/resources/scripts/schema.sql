
create table access_group (
                              group_id bigint not null auto_increment,
                              name varchar(255) not null,
                              primary key (group_id)
) engine=InnoDB;

create table all_system (
                            backend_jvm_free_memorymb bigint not null,
                            backend_jvm_max_memorymb bigint not null,
                            backend_jvm_total_memorymb bigint not null,
                            id bigint not null,
                            backend_host_address varchar(255),
                            backend_host_name varchar(255),
                            backend_java_version varchar(255),
                            backend_os_codename varchar(255),
                            backend_os_description varchar(255),
                            backend_os_distributor varchar(255),
                            backend_os_release varchar(255),
                            backend_processor_info varchar(255),
                            backend_ram_memory varchar(255),
                            backend_spring_boot_version varchar(255),
                            backend_spring_security_core_version varchar(255),
                            backend_spring_version varchar(255),
                            backend_storage varchar(255),
                            db_last_backup varchar(255),
                            db_name varchar(255),
                            db_status varchar(255),
                            db_version varchar(255),
                            frontend_host_address varchar(255),
                            frontend_host_name varchar(255),
                            frontend_nextauth_version varchar(255),
                            frontend_nextjs_version varchar(255),
                            frontend_node_version varchar(255),
                            frontend_npm_version varchar(255),
                            frontend_os_codename varchar(255),
                            frontend_os_description varchar(255),
                            frontend_os_distributor varchar(255),
                            frontend_os_release varchar(255),
                            frontend_processor_info varchar(255),
                            frontend_ram_memory varchar(255),
                            frontend_react_version varchar(255),
                            frontend_storage varchar(255),
                            primary key (id)
) engine=InnoDB;

create table article (
                         date varchar(4),
                         article_id bigint not null auto_increment,
                         journal varchar(225) not null,
                         title varchar(225) not null,
                         slug varchar(500) not null,
                         abstract_text TEXT,
                         url VARCHAR(700) not null,
                         primary key (article_id)
) engine=InnoDB;

create table article_line_of_research (
                                          article_id bigint not null,
                                          line_of_research_id varchar(255) not null
) engine=InnoDB;

create table article_members (
                                 article_id bigint not null,
                                 name varchar(255),
                                 slug varchar(255)
) engine=InnoDB;

create table function_member (
                                 function_member_id bigint not null auto_increment,
                                 description varchar(225) not null,
                                 name varchar(225) not null,
                                 primary key (function_member_id)
) engine=InnoDB;

create table lafoca (
                        number_of_defendedtccs integer not null,
                        number_of_members integer not null,
                        number_of_projects integer not null,
                        number_of_published_articles integer not null,
                        date_time datetime(6),
                        lafoca_id bigint not null auto_increment,
                        primary key (lafoca_id)
) engine=InnoDB;

create table line_of_research (
                                  name varchar(50) not null,
                                  description varchar(225) not null,
                                  line_of_research_id varchar(255) not null,
                                  primary key (line_of_research_id)
) engine=InnoDB;

create table member (
                        date_register datetime not null,
                        function_member_id bigint,
                        tcc_id bigint,
                        year_class_id bigint,
                        description varchar(225) not null,
                        display_name varchar(225) not null,
                        email varchar(225) not null,
                        full_name varchar(225) not null,
                        link_linkedin varchar(225),
                        link_portifolio varchar(225),
                        biography varchar(1000) not null,
                        slug varchar(500) not null,
                        member_id varchar(255) not null,
                        photo_id varchar(255),
                        primary key (member_id)
) engine=InnoDB;

create table member_article (
                                article_id bigint not null,
                                member_id varchar(255) not null,
                                primary key (article_id, member_id)
) engine=InnoDB;

create table member_project (
                                member_id varchar(255) not null,
                                project_id varchar(255) not null,
                                primary key (member_id, project_id)
) engine=InnoDB;

create table member_skill (
                              skill_id bigint not null,
                              member_id varchar(255) not null,
                              primary key (skill_id, member_id)
) engine=InnoDB;

create table member_photo (
                              size bigint not null,
                              content_type varchar(15) not null,
                              data_update varchar(40),
                              file_name varchar(225) not null,
                              url varchar(700),
                              photo_id varchar(255) not null,
                              primary key (photo_id)
) engine=InnoDB;

create table news (
                      news_date datetime not null,
                      description varchar(225) not null,
                      title varchar(225) not null,
                      slug varchar(500) not null,
                      content TEXT not null,
                      news_id varchar(255) not null,
                      photo_id varchar(255),
                      primary key (news_id)
) engine=InnoDB;

create table news_line_of_research (
                                       line_of_research_id varchar(255) not null,
                                       news_id varchar(255) not null
) engine=InnoDB;

create table news_photo (
                            size bigint not null,
                            content_type varchar(15) not null,
                            data_update varchar(40),
                            file_name varchar(225) not null,
                            url varchar(700),
                            photo_id varchar(255) not null,
                            primary key (photo_id)
) engine=InnoDB;

create table permission (
                            permission_id bigint not null auto_increment,
                            name varchar(50) not null,
                            description varchar(225) not null,
                            primary key (permission_id)
) engine=InnoDB;

create table permission_group (
                                  group_id bigint not null,
                                  permission_id bigint not null,
                                  primary key (group_id, permission_id)
) engine=InnoDB;

create table project (
                         date varchar(10) not null,
                         end_date varchar(10),
                         slug varchar(500) not null,
                         abstract_text TEXT,
                         modality varchar(10),
                         photo_id varchar(255),
                         project_id varchar(255) not null,
                         title varchar(255) not null,
                         primary key (project_id)
) engine=InnoDB;

create table project_line_of_research (
                                          line_of_research_id varchar(255) not null,
                                          project_id varchar(255) not null
) engine=InnoDB;

create table project_members (
                                 name varchar(255),
                                 project_id varchar(255) not null,
                                 slug varchar(255)
) engine=InnoDB;

create table project_photo (
                               size bigint not null,
                               content_type varchar(15) not null,
                               data_update varchar(40),
                               file_name varchar(225) not null,
                               url varchar(700),
                               photo_id varchar(255) not null,
                               primary key (photo_id)
) engine=InnoDB;

create table record_count (
                              articles integer not null,
                              members integer not null,
                              projects integer not null,
                              tccs integer not null,
                              id bigint not null auto_increment,
                              primary key (id)
) engine=InnoDB;

create table skill (
                       skill_id bigint not null auto_increment,
                       skill_picture_id bigint,
                       name varchar(50) not null,
                       primary key (skill_id)
) engine=InnoDB;

create table skill_picture (
                               size bigint not null,
                               skill_picture_id bigint not null,
                               content_type varchar(15) not null,
                               data_update varchar(40),
                               file_name varchar(225) not null,
                               url varchar(700),
                               primary key (skill_picture_id)
) engine=InnoDB;

create table tcc (
                     date date not null,
                     tcc_id bigint not null auto_increment,
                     slug varchar(500) not null,
                     abstract_text TEXT,
                     name_member varchar(255),
                     slug_member varchar(255),
                     title varchar(255) not null,
                     url VARCHAR(700) not null,
                     primary key (tcc_id)
) engine=InnoDB;

create table tcc_line_of_research (
                                      tcc_id bigint not null,
                                      line_of_research_id varchar(255) not null
) engine=InnoDB;

create table user (
                      date_register datetime not null,
                      photo_update varchar(40),
                      slug varchar(500) not null,
                      email varchar(255) not null,
                      name varchar(255) not null,
                      password varchar(255) not null,
                      url_photo VARCHAR(700),
                      user_id varchar(255) not null,
                      primary key (user_id)
) engine=InnoDB;

create table user_group (
                            group_id bigint not null,
                            user_id varchar(255) not null,
                            primary key (group_id, user_id)
) engine=InnoDB;

create table year_class (
                            year integer not null,
                            year_class_id bigint not null,
                            primary key (year_class_id)
) engine=InnoDB;

alter table access_group
    add constraint UK_2rpgfvxump55l2ycp3bjhgi8j unique (name);

alter table article
    add constraint UK_lc76j4bqg2jrk06np18eve5yj unique (slug);

alter table article
    add constraint UK_la4e9l2g2otn85r9ih4btnmi unique (url);

alter table line_of_research
    add constraint UK_kqjumqm835jwj1ob2303s0gjc unique (name);

alter table member
    add constraint UK_tpsdpjkx2n7qtomomdd4hsgk9 unique (tcc_id);

alter table member
    add constraint UK_mbmcqelty0fbrvxp1q58dn57t unique (email);

alter table member
    add constraint UK_sy3u61nw081oof7u8cld7tmgs unique (link_linkedin);

alter table member
    add constraint UK_mxh114rsij7cdby1a70kox80k unique (link_portifolio);

alter table member
    add constraint UK_j265bs8yrd1iph7fb87p6y7l2 unique (slug);

alter table member
    add constraint UK_di84gqu66dvyc8rlo09vcbrkp unique (photo_id);

alter table news
    add constraint UK_owrieak0v8dvhynft9mxexw15 unique (slug);

alter table news
    add constraint UK_8w7j88fegpay3cmqqt95vynji unique (photo_id);

alter table project
    add constraint UK_fuxkbuidqwwj8bjlojr7m240c unique (slug);

alter table project
    add constraint UK_h4x37fvueliym9g3vduqg1dxb unique (photo_id);

alter table skill
    add constraint UK_dttft1cui426f03os59xsmvfd unique (skill_picture_id);

alter table tcc
    add constraint UK_2025yne7plodff12lleaokqje unique (slug);

alter table tcc
    add constraint UK_9nurlavtvg70hpoxmgij0yf5u unique (url);

alter table user
    add constraint UK_4purqiaifeeekn0sgxa1lignd unique (slug);

alter table user
    add constraint UK_ob8kqyqqgmefl0aco34akdtpe unique (email);

alter table article_line_of_research
    add constraint fk_research_article_id
        foreign key (line_of_research_id)
            references line_of_research (line_of_research_id);

alter table article_line_of_research
    add constraint fk_article_research_id
        foreign key (article_id)
            references article (article_id);

alter table article_members
    add constraint FK3t8o514gsjak66t34mefegg50
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

alter table member
    add constraint fk_member_year_class_id
        foreign key (year_class_id)
            references year_class (year_class_id);

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

alter table project_members
    add constraint FKi28gx2d4xrrhtrfnk12aef1e4
        foreign key (project_id)
            references project (project_id);

alter table skill
    add constraint fk_skill_picture_id
        foreign key (skill_picture_id)
            references skill_picture (skill_picture_id)
            on delete set null;

alter table tcc_line_of_research
    add constraint fk_research_tcc_id
        foreign key (line_of_research_id)
            references line_of_research (line_of_research_id);

alter table tcc_line_of_research
    add constraint fk_tcc_research_id
        foreign key (tcc_id)
            references tcc (tcc_id);

alter table user_group
    add constraint fk_user_group_group_id
        foreign key (group_id)
            references access_group (group_id);

alter table user_group
    add constraint fk_user_group_user_id
        foreign key (user_id)
            references user (user_id);