CREATE TABLE client_whitelist (
    id INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT 'Regular expression identifier.',
    client VARCHAR(128) NOT NULL COMMENT 'Client name.',
    regex VARCHAR(128) NOT NULL COMMENT 'Regular expression.',
    active TINYINT(1) COMMENT 'Indicates if the regular expression is active (1) or inactive (0).'
);