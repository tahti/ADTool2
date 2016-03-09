import os
import fnmatch

path = 'src'
keys = set()
for line in open("src/main/resources/i18n/messages.properties"):
    key = line.split('=')[0].strip()
    keys.add(key)

sourcefiles = [os.path.join(dirpath, f)
for dirpath, dirnames, files in os.walk(path) for f in fnmatch.filter(files, '*.java')]
for fileName in sourcefiles:
    i = 0
    for line in open(fileName):
        i = i + 1
        if "Options.getMsg(\"" in line:
            try:
                start = line[(line.index("Options.getMsg(\"") + len("Options.getMsg(\"")):]
                key = start[:start.index("\"")]
                if key not in keys:
                    print "No key: " + key + " in " + fileName + ":" + str(i)
            except ValueError:
                pass
