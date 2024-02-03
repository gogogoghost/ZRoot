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
        int targetUid=atoi(argv[3]);
        int currentUid=getuid();
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
        char class_path[128]={0};
        sprintf(class_path,"-Djava.class.path=%s",dexPath);

        char nice_name[64]={0};
        sprintf(nice_name, "--nice-name=%s", PROCESS_NAME);

        char *appProcessArgs[] = {
        	const_cast<char *>("/system/bin/app_process"),
        	class_path,
        	const_cast<char *>("/system/bin"),
        	nice_name,
        	const_cast<char *>("site.zbyte.root.Runner"),
            const_cast<char *>(starterPath),
            const_cast<char *>(dexPath),
        	const_cast<char *>(packageName),
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
