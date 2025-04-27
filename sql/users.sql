create table users
(
    id         bigint auto_increment
        primary key,
    username   varchar(50)          not null,
    password   varchar(100)         not null,
    created_at timestamp            not null,
    updated_at timestamp            not null,
    is_active  tinyint(1) default 1 not null,
    constraint username
        unique (username)
);

INSERT INTO balanceeat.users (id, username, password, created_at, updated_at, is_active) VALUES (1, 'A', 'AAA', '2025-04-25 07:08:29', '2025-04-26 18:05:15', 0);
