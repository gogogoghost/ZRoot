#include <dlfcn.h>
#include <stdio.h>
#include <unistd.h>
#include "selinux.h"

#define PROCESS_NAME "rd_runner"

//dex路径
int main(int argc,char* argv[]){

    if(argc!=3){
        printf("args error!\n");
        return -1;
    }


    char* path=argv[1];
//    char* pkgName=argv[2];
//    char* clsName=argv[3];
    char* randomString=argv[2];

    //修改目标文件信息
//     chown(argv[0], 2000, 2000);
    se::setfilecon(argv[0], "u:object_r:shell_data_file:s0");
//     chown(path, 2000, 2000);
    se::setfilecon(path, "u:object_r:shell_data_file:s0");

    //修改context
    se::init();
    char* context=nullptr;
    if (se::getcon(&context) == 0) {
        int res=0;
        res |= se::check_selinux("u:r:untrusted_app:s0", context, "binder", "call");
        res |= se::check_selinux("u:r:untrusted_app:s0", context, "binder", "transfer");
        if(res!=0){
            se::setcon("u:r:shell:s0");
        }
        se::freecon(context);
    }

    pid_t pid=fork();
    if(pid==0){
        //child
        char class_path[128]={0};
        sprintf(class_path,"-Djava.class.path=%s",path);

        char nice_name[64]={0};
        sprintf(nice_name, "--nice-name=%s", PROCESS_NAME);

        char *appProcessArgs[] = {
        	const_cast<char *>("/system/bin/app_process"),
        	class_path,
        	const_cast<char *>("/system/bin"),
        	nice_name,
        	const_cast<char *>("site.zbyte.root.Runner"),
        	//const_cast<char *>(pkgName),
        	//const_cast<char *>(clsName),
        	const_cast<char *>(randomString),
        	nullptr
        };
        execvp(appProcessArgs[0], appProcessArgs);
    }else if(pid>0){
        //parent
        return 0;
    }else{
        printf("fork error!\n");
        return pid;
    }

	return 0;
}
