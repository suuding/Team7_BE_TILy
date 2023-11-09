CREATE SCHEMA IF NOT EXISTS `tily` DEFAULT CHARACTER SET utf8mb4;

USE `tily`;


create table alarm_tb (
       id bigint generated by default as identity,
        created_date timestamp,
        updated_date timestamp,
        is_read boolean,
        comment_id bigint,
        receiver_id bigint,
        til_id bigint,
        primary key (id)
    )

create table comment_tb (
       id bigint generated by default as identity,
        created_date timestamp,
        updated_date timestamp,
        content TEXT,
        is_deleted boolean,
        til_id bigint,
        writer_id bigint,
        primary key (id)
    )

create table image (
       id bigint not null,
        image_path varchar(255),
        original_image_name varchar(255),
        storage_image_name varchar(255),
        primary key (id)
    )

create table reference_tb (
       id bigint generated by default as identity,
        category varchar(255) not null,
        is_deleted boolean,
        link TEXT,
        step_id bigint,
        primary key (id)
    )

create table roadmap_tb (
       id bigint generated by default as identity,
        created_date timestamp,
        updated_date timestamp,
        category varchar(255) not null,
        code varchar(255),
        current_num bigint,
        description TEXT,
        image varchar(255),
        is_deleted boolean,
        is_public boolean,
        is_recruit boolean,
        name varchar(255) not null,
        step_num integer,
        creator_id bigint,
        primary key (id)
    )

create table step_tb (
       id bigint generated by default as identity,
        description TEXT,
        due_date timestamp,
        is_deleted boolean,
        title varchar(255) not null,
        roadmap_id bigint,
        primary key (id)
    )

create table til_tb (
       id bigint generated by default as identity,
        created_date timestamp,
        updated_date timestamp,
        comment_num integer,
        content TEXT,
        is_deleted boolean,
        is_personal boolean,
        submit_content TEXT,
        submit_date timestamp,
        title varchar(255) not null,
        roadmap_id bigint,
        step_id bigint,
        writer_id bigint,
        primary key (id)
    )

create table user_roadmap_tb (
       id bigint generated by default as identity,
        created_date timestamp,
        updated_date timestamp,
        content TEXT,
        is_accept boolean,
        is_deleted boolean,
        progress integer not null,
        role varchar(255) not null,
        roadmap_id bigint,
        user_id bigint,
        primary key (id)
    )

create table user_step_tb (
       id bigint generated by default as identity,
        is_deleted boolean,
        is_submit boolean not null,
        roadmap_id bigint,
        step_id bigint,
        user_id bigint,
        primary key (id)
    )

create table user_tb (
       id bigint generated by default as identity,
        created_date timestamp,
        updated_date timestamp,
        email varchar(50) not null,
        image varchar(255),
        is_deleted boolean,
        name varchar(50) not null,
        password varchar(100) not null,
        role varchar(255),
        primary key (id)
    )


ALTER TABLE alarm_tb
ADD FOREIGN KEY (til_id)
REFERENCES til_tb(id);

ALTER TABLE alarm_tb
ADD FOREIGN KEY (receiver_id)
REFERENCES user_tb(id);

ALTER TABLE alarm_tb
ADD FOREIGN KEY (comment_id)
REFERENCES comment_tb(id);

ALTER TABLE til_tb
ADD FOREIGN KEY (roadmap_id)
REFERENCES roadmap_tb(id);

ALTER TABLE til_tb
ADD FOREIGN KEY (step_id)
REFERENCES step_tb(id);

ALTER TABLE til_tb
ADD FOREIGN KEY (writer_id)
REFERENCES user_tb(id);

ALTER TABLE reference_tb
ADD FOREIGN KEY (step_id)
REFERENCES step_tb(id);

ALTER TABLE user_roadmap_tb
ADD FOREIGN KEY (roadmap_id)
REFERENCES roadmap_tb(id);

ALTER TABLE user_roadmap_tb
ADD FOREIGN KEY (user_id)
REFERENCES user_tb(id);

ALTER TABLE user_step_tb
ADD FOREIGN KEY (roadmap_id)
REFERENCES roadmap_tb(id);

ALTER TABLE user_step_tb
ADD FOREIGN KEY (step_id)
REFERENCES step_tb(id);

ALTER TABLE user_step_tb
ADD FOREIGN KEY (user_id)
REFERENCES user_tb(id);

ALTER TABLE reference_tb
ADD FOREIGN KEY (step_id)
REFERENCES step_tb(id);