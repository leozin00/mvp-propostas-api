-- Identidade Firebase: uid único e senha local opcional (Auth fica no Firebase)

ALTER TABLE users
    ADD COLUMN firebase_uid VARCHAR(128);

CREATE UNIQUE INDEX uk_users_firebase_uid ON users (firebase_uid);

ALTER TABLE users
    ALTER COLUMN password_hash DROP NOT NULL;
