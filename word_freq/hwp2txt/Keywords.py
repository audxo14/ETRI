#-*- coding: utf-8 -*-
import os
import sys
import subprocess
reload(sys)
sys.setdefaultencoding('utf-8')

current_dir = os.path.dirname(os.path.abspath(__file__))    #Get current Path
current_dir = current_dir.replace('\\', '\\\\')

exe_dir = current_dir + "\\\\parse\\\\hwp2txt.exe"
subprocess.call([exe_dir])

"""
#When the pycharm or cmd tells you that you need Java to run, use the code belows

jar_dir = current_dir + "\\\\hwp2txt.jar"
subprocess.call(['java', '-jar', jar_dir])

"""