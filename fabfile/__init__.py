# -*- encoding:utf-8 -*-

from fabric.api import *


@task
def firebase_deploy():
    '''Firebaseへのデプロイ'''
    import firebase
    firebase.deploy()


@task
def sg_authorize():
    '''セキュリティグループの許可'''
    import security_group
    security_group.authorize()


@task
def sg_revoke():
    '''セキュリティグループの剥奪'''
    import security_group
    security_group.revoke()


@task
def ec2_show():
    '''EC2の状態確認'''
    import ec2
    ec2.show()


@task
def ec2_start():
    '''EC2のスタート'''
    import ec2
    ec2.start()


@task
def ec2_stop():
    '''EC2のストップ'''
    import ec2
    ec2.stop()


@task
def mysql_dump():
    '''MySQLのダンプ'''
    import mysql
    mysql.dump()


@task
def mysql_restore():
    '''MySQLのリストア'''
    import mysql
    mysql.restore()


@task
def mysql_dump_light():
    '''MySQLのダンプ(軽量版)'''
    import mysql
    mysql.dump_light()


@task
def mysql_restore_light():
    '''MySQLのリストア(軽量版)'''
    import mysql
    mysql.restore_light()


@task
def mysql_show():
    '''MySQLの情報表示'''
    import mysql
    mysql.show()


@task
def crawl_user():
    '''ユーザのクロール'''
    import crawl
    crawl.user()


@task
def crawl_article():
    '''記事のクロール'''
    import crawl
    crawl.article()


@task
def deploy_json():
    '''JSONをデプロイ'''
    import create_json, s3
    create_json.create()
    s3.deploy_json()


@task
def create_json():
    '''JSONの生成'''
    import create_json
    create_json.create()


@task
def s3_deploy_json():
    '''S3へJSONをデプロイ'''
    import s3
    s3.deploy_json()


@task
def s3_upload():
    '''S3へのアップロード'''
    import s3
    s3.upload()


@task
def s3_download():
    '''S3へのダウンロード'''
    import s3
    s3.download()


@task
def export_data():
    '''データのエクスポート'''
    import export_data
    export_data.execute()
