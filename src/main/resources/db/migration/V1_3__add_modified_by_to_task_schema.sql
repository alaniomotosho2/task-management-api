ALTER TABLE tasks
ADD COLUMN last_modified_by BIGINT;

ALTER TABLE tasks
ADD CONSTRAINT fk_last_modified_by
FOREIGN KEY (last_modified_by) REFERENCES users(id);
