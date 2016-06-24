#!/bin/bash

wget http://ftp.gnu.org/gnu/chess/gnuchess-6.2.2.tar.gz
tar xvfz gnuchess-6.2.2.tar.gz
cd gnuchess-6.2.2
./configure && make