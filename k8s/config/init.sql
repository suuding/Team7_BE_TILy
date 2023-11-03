CREATE SCHEMA IF NOT EXISTS `tily` DEFAULT CHARACTER SET utf8mb4;

USE `tily`;


CREATE TABLE user_tb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    image VARCHAR(255),
    role VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roadmap_tb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    creator_id BIGINT,
    description VARCHAR(255),
    image VARCHAR(255),
    step_num INT,
    current_num BIGINT,
    code VARCHAR(255),
    is_public BOOLEAN,
    is_recruit BOOLEAN,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE step_tb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    roadmap_id BIGINT,
    description VARCHAR(255),
    due_date TIMESTAMP
);

CREATE TABLE til_tb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(255),
    submit_content VARCHAR(255),
    roadmap_id BIGINT,
    step_id BIGINT,
    writer_id BIGINT,
    comment_num INT,
    is_personal BOOLEAN,
    submit_date TIMESTAMP,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comment_tb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    til_id BIGINT,
    writer_id BIGINT,
    content VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reference_tb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    step_id BIGINT,
    category VARCHAR(255) NOT NULL,
    link VARCHAR(255) NOT NULL
);

CREATE TABLE user_roadmap_tb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(255),
    is_accept BOOLEAN,
    progress INT NOT NULL,
    role VARCHAR(255) NOT NULL,
    roadmap_id BIGINT,
    user_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_step_tb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    is_submit BOOLEAN NOT NULL,
    roadmap_id BIGINT,
    step_id BIGINT,
    user_id BIGINT
);

CREATE TABLE alarm_tb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    til_id BIGINT,
    comment_id BIGINT,
    is_checked BOOLEAN,
    receiver_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
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