CREATE TABLE IF NOT EXISTS qiita_user_names (
  user_name VARCHAR(255) NOT NULL COMMENT 'ユーザ名',
  PRIMARY KEY (user_name)
) ENGINE InnoDB AUTO_INCREMENT 1 DEFAULT CHARSET utf8mb4 DEFAULT COLLATE utf8mb4_bin COMMENT 'Qiitaユーザ名';
