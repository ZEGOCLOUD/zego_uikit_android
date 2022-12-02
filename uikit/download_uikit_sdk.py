#! /usr/local/bin/python3
#! encoding=utf-8

import os
import shutil
import os.path
import sys
import subprocess
import zipfile
import time


def delete(path):
    if not os.path.exists(path):
        print ("[*] {} not exists".format(path))
        return

    if os.path.isdir(path):
        shutil.rmtree(path)
    elif os.path.isfile(path):
        os.remove(path)
    elif os.path.islink(path):
        os.remove(path)
    else:
        print ("[*] unknow type for: " + path)

def insure_empty_dir(dir_path):
    if os.path.exists(dir_path):
        delete(dir_path)
    os.makedirs(dir_path)

def copy_dir(src, dst):
    import shutil
    shutil.rmtree(dst, ignore_errors=True)
    shutil.copytree(src, dst, symlinks=True)

def run_os_cmd(command_string, silence = False):
    if not silence:
        print('run_os_cmd: {}'.format(command_string))
    result = os.system(command_string)
    if result != 0:
        raise Exception('os.system fail, cmd:{}'.format(command_string))

def unzip_file(zip_file_path,dst_path):
    zFile = zipfile.ZipFile(zip_file_path, "r") 
    for fileM in zFile.namelist(): 
        zFile.extract(fileM, os.path.join(dst_path))
    zFile.close()

script_path = os.path.dirname(os.path.realpath(__file__))
work_folder = os.path.join(script_path,'tempdir')
main_dst_folder = os.path.join(script_path,'src','main')


if len(sys.argv) > 1:
    download_cmd = 'curl "https://artifact-node.zego.cloud/generic/zego_uikit/public/android/zego_uikit_release.aar?version={}" -o ./zego_uikit_release.aar'.format(sys.argv[1])
    run_os_cmd(download_cmd)
    delete(work_folder)
    delete(os.path.join(script_path,'libs','uikit.jar'))
    unzip_file(os.path.join(script_path,'zego_uikit_release.aar'),work_folder)
    delete(os.path.join(main_dst_folder,'res'))
    copy_dir(os.path.join(work_folder,'res'),os.path.join(main_dst_folder,'res'))
    delete(os.path.join(main_dst_folder,'AndroidManifest.xml'))
    shutil.copy(os.path.join(work_folder,'AndroidManifest.xml'),os.path.join(main_dst_folder,'AndroidManifest.xml'))
    if not os.path.exists(os.path.join(script_path,'libs')):
        os.makedirs(os.path.join(script_path,'libs'))
    shutil.copy(os.path.join(work_folder,'classes.jar'),os.path.join(script_path,'libs','uikit.jar'))
    delete(work_folder)
    delete(os.path.join(script_path,'zego_uikit_release.aar'))

    