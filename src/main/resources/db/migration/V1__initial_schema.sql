CREATE TABLE participant (
                             id                  BIGSERIAL PRIMARY KEY NOT NULL,
                             username            text NOT NULL,
                             name                text NOT NULL,
                             created_date        timestamp NOT NULL,
                             last_modified_date  timestamp NOT NULL,
                             version             integer NOT NULL
);