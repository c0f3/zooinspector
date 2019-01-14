zooinspector
============

current version: __1.1-SNAPSHOT__

An improved zookeeper inspector

- Supports export zookeeper data to .zk, XML and folder [ctapmex/zkTreeUtil][1]
- - planned ability to restore dump from file: [fork kostapc/zkTreeUtil][2]
- centered GUI forms and dialogs
- Use async operations to speed up read
- Znodes sorted by names in tree viewer
- Timestamp and session id in more readable format in node metadata viewer
- Add a dropdown menu to show the last 10 successfully connected zookeeper addresses
- Support text search in node data viewer
- Support read-only mode for node data viewer

Build
- $git clone https://github.com/c0f3/zooinspector
- $cd zooinspector/
- $mvn clean package

Run
- java -jar target/zooinspector-1.1-SNAPSHOT-jar-with-dependencies.jar

[1]: https://github.com/ctapmex/zkTreeUtil
[2]: https://github.com/kostapc/zkTreeUtil
