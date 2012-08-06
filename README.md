# What's Allegro?

Allegro (アレグロ) とは、 Android の開発版アプリを管理するために作られたフロントエンドアプリケーションです。

JSON に記述された APK のリストを読み込み、 APK のダウンロード・インストールを行なうことが出来ます。

![screenshot](https://github.com/shunirr/allegro/raw/master/screenshot.png)

Allegro を利用することで、開発中のアプリケーションを手軽に配信することが出来ます。これは、非エンジニアの人に開発版のアプリを配布する際に便利です。

## 使い方

### 必要なもの

* HTTP/HTTPS で公開されているウェブサーバ (若しくは Dropbox の Public Link 機能でも可能)
* 配布したい APK ファイル
* Allegro 用 JSON ファイル

配布したい APK ファイルの URL や更新日時、ファイルサイズなどを記述した JSON ファイルをインターネットに公開します。

JSON の書き方は [sample.json](https://raw.github.com/shunirr/allegro/master/sample.json) を参考にしてください。

### URI の設定方法

* Allegro のメニューキーを押下
 * "Set URI" を選択し、 JSON の URI を設定

# License
Released under the Apache License, v2.0.

