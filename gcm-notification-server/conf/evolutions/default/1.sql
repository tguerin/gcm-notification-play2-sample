# Device schema

# --- !Ups

CREATE TABLE Device (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    registrationId varchar(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE(registrationId)
);

# --- !Downs

DROP TABLE Device;