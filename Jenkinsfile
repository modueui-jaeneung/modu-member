pipeline {
    agent any

    environment {
        REGION = 'ap-northeast-2'
        ECR_PATH = credentials('ecr-path')
        ECR_IMAGE = 'modu-member'
        AWS_CREDENTIAL_ID = 'build-test_ecr'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                credentialsId: 'github_access_token',
                url: 'https://github.com/modueui-jaeneung/modu-member.git'
            }
            post {
                success {
                    sh 'echo Successfully Cloned Git Repository'
                }
                failure {
                    sh 'echo Failed Cloned Git Repository'
                }
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build'
                sh 'ls -al ./build/libs'
            }
            post {
                success {
                    echo 'gradle build success'
                }
                failure {
                    echo 'gradle build failed'
                }
            }
        }

        stage('Docker build') {
            steps {
                sh """
                    docker build -t ${ECR_PATH}/${ECR_IMAGE}:${BUILD_NUMBER} -f Dockerfile .
                    docker tag ${ECR_PATH}/${ECR_IMAGE}:${BUILD_NUMBER} ${ECR_PATH}/${ECR_IMAGE}:latest
                """
            }

            post {
                success {
                    echo 'success dockerizing project'
                }
                failure {
                    error 'fail dockerizing project' // exit pipeline
                }
            }
        }

        stage('Push to ECR') {
            steps {
                sh """
                    docker push ${ECR_PATH}/${ECR_IMAGE}:${BUILD_NUMBER}
                    docker push ${ECR_PATH}/${ECR_IMAGE}:latest
                """
            }
        }
    }
}