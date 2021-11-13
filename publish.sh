#dest="/home/ghost/sec-project/bot"

./gradlew export && \
#cp build/root-driver.aar $dest/driver/libs/ && \
./gradlew runner:makeJar && \
cp runner/build/libs/runner.jar builder/src/main/resources/ && \

cd builder
rm -rf build/repo/*
#rm -rf $dest/repo/*

./gradlew uploadArchives #&& \
#cp -r build/repo/* $dest/repo/;