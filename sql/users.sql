create table users
(
    id         bigint auto_increment
        primary key,
    username   varchar(50)          not null,
    password   varchar(100)         not null,
    is_active  tinyint(1) default 1 not null,
    constraint username
        unique (username)
);

INSERT INTO balanceeat.users (id, username, password, is_active) VALUES (1, 'A', 'AAA', 0);
