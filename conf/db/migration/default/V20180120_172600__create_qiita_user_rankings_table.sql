CREATE TABLE IF NOT EXISTS qiita_user_rankings (
  user_name    VARCHAR(255) NOT NULL COMMENT 'ユーザ名',
  contribution INT(11)      NOT NULL COMMENT 'いいね数',
  PRIMARY KEY (user_name)
) ENGINE InnoDB AUTO_INCREMENT 1 DEFAULT CHARSET utf8mb4 DEFAULT COLLATE utf8mb4_bin COMMENT 'Qiitaユーザランキング';
