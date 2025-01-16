#include <dlfcn.h>
#include <cstdio>
#include <cstdlib>
#include <unistd.h>
#include "selinux.h"

#define RES_BAD_ARGS 1
#define RES_SELINUX_CONTEXT_DENIED 2
#define RES_CANNOT_SET_UID 3
#define RES_SET_UID_ERROR 4

#define PROCESS_NAME "rd_runner"

//dex路径
int main(int argc,char* argv[]){

    if(argc!=3&&argc!=4){
        exit(RES_BAD_ARGS);
    }

    //starter path
    char* starterPath=argv[0];
    //dex path
    char* dexPath=argv[1];
    //pkg name
    char* packageName=argv[2];

    //修改目标文件信息
    se::setfilecon(argv[0], "u:object_r:shell_data_file:s0");
    se::setfilecon(dexPath, "u:object_r:shell_data_file:s0");

    if(argc==4){
        char* endStr;
        uint_t targetUid=strtol(argv[3],&endStr,10);
        uid_t currentUid=getuid();
        if(targetUid!=currentUid){
            //need set uid
            if(currentUid!=0){
                exit(RES_CANNOT_SET_UID);
            }
            if(setuid(targetUid)!=0){
                exit(RES_SET_UID_ERROR);
            }
        }
    }

    //check context
    se::init();
    char* context=nullptr;
    if (se::getcon(&context) == 0) {
        int res=0;
        res |= se::check_selinux("u:r:untrusted_app:s0", context, "binder", "call");
        res |= se::check_selinux("u:r:untrusted_app:s0", context, "binder", "transfer");
        if(res!=0){
            exit(RES_SELINUX_CONTEXT_DENIED);
        }
        se::freecon(context);
    }

    pid_t pid=fork();
    if(pid==0){
        //child
        char path[]="/system/bin/app_process";
        char workPath[]="/system/bin";
        char className[]="site.zbyte.root.Runner";

        char classPath[128]={0};
        sprintf(classPath,"-Djava.class.path=%s",dexPath);

        char niceName[64]={0};
        sprintf(niceName, "--nice-name=%s", PROCESS_NAME);

        char * const appProcessArgs[] = {
        	path,
            classPath,
        	workPath,
            niceName,
        	className,
            //args
            starterPath,
            dexPath,
        	packageName,
        	nullptr
        };

        printf("starting...\n");
        execvp(appProcessArgs[0], appProcessArgs);
    }else if(pid>0){
        //parent
        return 0;
    }else{
        perror("fork error!\n");
        return pid;
    }

	return 0;
}
