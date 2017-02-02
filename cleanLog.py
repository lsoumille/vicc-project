#!/usr/bin/python

import sys, getopt

def processEachLine(readFile, writeFile):
    for line in readFile:
        if not (line.startswith('[VmScheduler.vmCreate]', 0, len(line)) or  line.startswith('0.', 0, len(line))):
            writeFile.write(line)

def main(argv):
    try:
        opts, args = getopt.getopt(argv,"hi:o:",["ifile="])
    except getopt.GetoptError:
        print 'test.py -i <inputfile>'
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print 'test.py -i <inputfile> -o <outputfile>'
            sys.exit()
        elif opt in ("-i", "--ifile"):
            inputfile = arg
    fRead = open(inputfile, 'r')
    fWrite = open('./CleanLog.txt', 'w')
    processEachLine(fRead, fWrite)


if __name__ == "__main__":
   main(sys.argv[1:])
