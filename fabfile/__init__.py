# -*- encoding:utf-8 -*-

from fabric.api import *


@task
def deploy():
    '''デプロイ'''
    local('git checkout develop')
    local('git pull --rebase origin develop')
    local('sbt dist')
    local('rm -rf /tmp/qiita-ranker-1.0-SNAPSHOT')
    local('unzip target/universal/qiita-ranker-1.0-SNAPSHOT.zip -d /tmp')


@task
def download_csv():
    '''-H $SSH_HOST -u $SSH_USER_NAME --port=$SSH_PORT CSVのダウンロード'''
    for table_name in TABLE_NAMES:
        download_csv_table(table_name)


def download_csv_table(table_name):
    csv_path = get_local_env('CSV_PATH')
    print('%s.csv downloading...' % table_name)
    get('/tmp/%s.csv' % table_name, '%s/%s.csv' % (csv_path, table_name))


@task
def export_data():
    '''データのエクスポート'''
    local('sudo rm -rf /tmp/*.csv')
    export_data_table('qiita_user_names', 'user_name ASC')
    export_data_table('raw_qiita_internal_user_jsons', 'crawled_date_time ASC')
    export_data_table('qiita_users', 'qiita_user_id ASC')
    export_data_table('qiita_user_contributions', 'updated_date_time ASC')
    export_data_table('qiita_user_contribution_histories', 'registered_date_time ASC')
    export_data_table('qiita_article_ids', 'id ASC')
    local('ls -alh /tmp/*.csv')
    local('wc -l /tmp/*.csv')


def export_data_table(table_name, order):
    execute_sql(export_data_sql(table_name, order))


def export_data_sql(table_name, order):
    return ' SELECT * FROM %s ' % table_name \
           + ' ORDER BY %s ' % order \
           + ' INTO OUTFILE \'/tmp/%s.csv\' ' % table_name \
           + ' FIELDS TERMINATED BY \'\t\' ' \
           + ' ENCLOSED BY \'\\"\' ' \
           + ' ESCAPED BY \'\\"\' ; '


@task
def init_db():
    '''データベースの初期化'''
    import init_db
    init_db.execute()