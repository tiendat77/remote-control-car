rm -r build/*
javac -d build src/*.java &&
echo Main-Class: Main > build/manifest.txt
jar cvfm ClientServer.jar build/manifest.txt -C build/ .

