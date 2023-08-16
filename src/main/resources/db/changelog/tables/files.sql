create table files
(
    filename      varchar(255) not null,
    date          date  not null,
    file_content  text     not null,
    size          bigint       not null,
    user_username varchar(255),
    primary key (filename)
);