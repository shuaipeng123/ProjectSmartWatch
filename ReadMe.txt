wear connection commands:

Run following commands in AS Terminal

adb start-server
adb forward tcp:4444 localabstract:/adb-hub
adb connect 127.0.0.1:4444