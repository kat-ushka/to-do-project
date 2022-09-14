BEGIN;

CREATE TABLE todos (
    id serial       PRIMARY KEY,
    text            VARCHAR(140),
    is_done    BOOLEAN DEFAULT FALSE
                   );

COMMIT;