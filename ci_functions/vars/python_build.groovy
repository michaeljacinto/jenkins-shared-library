def call(imageName, portNum) {
    pipeline {
        agent any
        parameters {
            booleanParam(defaultValue: false, description: "Deploy the App", name: "DEPLOY")
        }
        stages {
            stage('Python Lint') {
                steps {
                    sh 'pylint-fail-under --fail_under 5.0 *.py'
                }
            }
            stage('Package') {
                when {
                    expression { env.GIT_BRANCH == 'origin/main' }
                }
                steps {
                    withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
                        sh "docker login -u 'michaeljacinto' -p '$TOKEN' docker.io"
                        sh "docker build -t ${imageName}:v${env.BUILD_NUMBER} --tag michaeljacinto/${imageName}:v${env.BUILD_NUMBER} ."
                        sh "docker push michaeljacinto/${imageName}:v${env.BUILD_NUMBER}"
                    }
                }
            }
            stage('Vulnerability Scan') {
                steps {
                    sh "docker scan --json ${imageName}:v${env.BUILD_NUMBER}"
                }
            }
        }
    }
}
