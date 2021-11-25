def call(dockerRepoName, imageName) {
    pipeline {
        agent any
        stages {
            stage('Build') {
                steps {
                    sh 'pip install -r requirements.txt'
                }
            }
            stage('Python Lint') {
                steps {
                    sh 'pylint-fail-under --fail_under 5.0 *.py'
                }
            }
            // stage('Package') {
            //     when {
            //         expression { env.GIT_BRANCH == 'origin/master' }
            //     }
            //     steps {
            //         withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
            //             sh "docker login -u '${dockerRepoName}' -p '$TOKEN' docker.io"
            //             sh "docker build -t ${imageName} --tag ${dockerRepoName}/${imageName} ."
            //             sh "docker push ${dockerRepoName}/${imageName}"
            //         }
            //     }
            // }
            stage('Deploy') {
                steps {
                    sshagent(credentials : ['kafka-key-pair']) {
                        sh "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $SSH_USER $SSH_HOST 'mkdir test1'"
                    }
                }
            } 
            
            // cd microservices/Deployment
            // 
            //         sh "docker stop ${dockerRepoName} || true && docker rm ${dockerRepoName} || true"
            //         sh "docker-compose -f ./microservices/Deployment/docker-compose-4850.yml up -d"
            //     }
        }
    }
}
