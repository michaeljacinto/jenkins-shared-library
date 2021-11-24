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
            stage('Package') {
                when {
                    expression { env.GIT_BRANCH == 'origin/master' }
                }
                steps {
                    withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
                        sh "docker login -u '${dockerRepoName}' -p '$TOKEN' docker.io"
                        sh "docker build -t ${imageName}:latest --tag ${dockerRepoName}/${imageName}:latest ."
                        sh "docker push ${dockerRepoName}/${imageName}:latest"
                    }
                }
            }
            // stage('Deliver') {
            //     when {
            //         expression { params.DEPLOY }
            //     }
            //     steps {
            //         sh "docker stop ${dockerRepoName} || true && docker rm ${dockerRepoName} || true"
            //         sh "docker run -d -p ${portNum}:${portNum} --name ${dockerRepoName} ${dockerRepoName}:latest"
            //     }
            // }
        }
    }
}
