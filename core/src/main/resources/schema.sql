DROP TABLE IF EXISTS servers;
CREATE TABLE servers (server_id int NOT NULL AUTO_INCREMENT
                    , ip_address varchar(255) NOT NULL UNIQUE
                    , port int NOT NULL
                    , creation_time DATETIME NOT NULL
                    , last_update DATETIME
                    ,PRIMARY KEY (server_id));
                    
DROP TABLE IF EXISTS users;
CREATE TABLE users (user_id int NOT NULL AUTO_INCREMENT
                    , username varchar(255) NOT NULL UNIQUE
                    , ip_address varchar(255) NOT NULL UNIQUE
                    , server_id int REFERENCES servers.server_id
                    , creation_time DATETIME NOT NULL
                    , last_update DATETIME
                    ,PRIMARY KEY (user_id));
                    
                    