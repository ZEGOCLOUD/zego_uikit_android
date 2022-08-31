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
    """
    删除一个文件/文件夹,如果路径不存在，会输出错误并返回
    如果路径是一个文件夹，则会递归的删除里面的所有文件和子目录
    :param path: 待删除的文件路径
    :return: 无
    """
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
    """
    确保为一个空目录;
    如果已经存在，则先删除掉此目录后再次创建空目录;
    如果不存在，则直接创建空目录;
    :param dir_path: 待检查的文件夹路径
    :return: 无
    """
    if os.path.exists(dir_path):
        delete(dir_path)
    os.makedirs(dir_path)

def copy_dir(src, dst):
    """Copy directory src to dst

    :param src: eg. '/var/include'
    :param dst: eg. '/usr/local/xx-include'
    """
    import shutil
    shutil.rmtree(dst, ignore_errors=True)
    shutil.copytree(src, dst, symlinks=True)

def mergefolders(root_src_dir, root_dst_dir):
    for src_dir, dirs, files in os.walk(root_src_dir):
        dst_dir = src_dir.replace(root_src_dir, root_dst_dir, 1)
        if not os.path.exists(dst_dir):
            os.makedirs(dst_dir)
        for file_ in files:
            src_file = os.path.join(src_dir, file_)
            dst_file = os.path.join(dst_dir, file_)
            if os.path.exists(dst_file):
                os.remove(dst_file)
            shutil.copy(src_file, dst_dir)

def creation_date(path_to_file):
    """
    Try to get the date that a file was created, falling back to when it was
    last modified if that isn't possible.
    获取文件的创建时间，如果获取不到，则返回文件的最后修改时间。(适配了windows和非windows)
    :path_to_file: 文件地址
    :return: 创建时间,timemills
    See http://stackoverflow.com/a/39501288/1709587 for explanation.
    """
    import platform
    if platform.system() == 'Windows':
        return os.path.getctime(path_to_file)
    else:
        stat = os.stat(path_to_file)
        try:
            return stat.st_birthtime
        except AttributeError:
            # We're probably on Linux. No easy way to get creation dates here,
            # so we'll settle for when its content was last modified.
            return stat.st_mtime

def get_time_str(timemills):
    return time.strftime("%Y-%m-%d, %H:%M:%S",time.localtime(timemills))

def get_environment_value(key):
    if key in os.environ:
        key = os.environ[key]
        return key

def run_os_cmd(command_string, silence = False):
    if not silence:
        print('run_os_cmd: {}'.format(command_string))
    result = os.system(command_string)
    if result != 0:
        raise Exception('os.system fail, cmd:{}'.format(command_string))

def mount_sdk_dirs(mount_remote_dir,mount_local_path):
    """
    挂载公司磁盘到本地
    mount_remote_dir: 这里是相对于公司磁盘根目录的地址
    mount_local_path: 挂载到本地电脑上的目录地址
    :return: 无
    """
    if not os.path.exists(mount_local_path):
        os.makedirs(mount_local_path)
    if os.path.ismount(mount_local_path):
        print('{0} is mounted,umount first...'.format(mount_local_path))
        umount_share(mount_local_path)
    if not os.path.ismount(mount_local_path):
        insure_empty_dir(mount_local_path)
        run_os_cmd('mount -t smbfs //share:share%40zego@192.168.1.3/{0} {1}'.format(mount_remote_dir,mount_local_path),True)

def umount_share(mount_local_path):
    run_os_cmd("umount -fv {0}".format(mount_local_path),True)


def get_zip_file(input_path, result):
    """
    对目录进行遍历，用于压缩文件夹
    :param input_path:
    :param result:
    :return:
    """
    files = os.listdir(input_path)
    for file in files:
        if os.path.isdir(input_path + '/' + file):
            get_zip_file(input_path + '/' + file, result)
        else:
            result.append(input_path + '/' + file)
 
 
def zip_file_path(input_path, output_path, output_name):
    """
    压缩文件夹
    :param input_path: 要压缩的文件夹的路径
    :param output_path: 输出zip的文件夹的路径
    :param output_name: zip压缩包名称
    :return:
    """
    f = zipfile.ZipFile(output_path + '/' + output_name, 'w', zipfile.ZIP_DEFLATED)
    filelists = []
    get_zip_file(input_path, filelists)
    for file in filelists:
        f.write(file)
    # 调用了close方法才会保证完成压缩
    f.close()
    return output_path + r"/" + output_name

def upload_pgyer(apk_file_path):
    """
    上传到zego的蒲公英地址
    if official:
        account = 'dev@zego.im'
        uKey = '81d88fe0b8734a051dd4cc616dbd2198'
        apiKey = 'c0b8bd1926158ff0237ce70efb66c93b'
    else:
        account = '3312273386@qq.com'
        uKey = '66bc43c2780b2d3f275a0435e1f8e536'
        apiKey = '62f3a7a0401935c9c452a2d492cf5334'
    """
    
    uKey = '66bc43c2780b2d3f275a0435e1f8e536'
    apiKey = '62f3a7a0401935c9c452a2d492cf5334'

    # 1：公开安装，2：密码安装
    # install_type = 2
    # password = "1"

    install_type = 1
    password = ""
   
    pgy_site = 'https://www.pgyer.com/apiv2/app/upload'

    upload_command = 'curl -F "file=@{0}" -F "_api_key={1}" -F "buildInstallType={2}" -F "buildPassword={3}" {4}'.format(os.path.realpath(apk_file_path), apiKey, install_type, password, pgy_site)


    print ("<< upload command {0}".format(upload_command))

    run_os_cmd(upload_command,True)

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

    