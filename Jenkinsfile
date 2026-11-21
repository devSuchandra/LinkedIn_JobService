pipeline {
    agent any

    tools {
        jdk 'jdk21'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 30, unit: 'MINUTES')
        skipDefaultCheckout(true)
    }

    environment {
        REGISTRY = 'suchandrapaul'
        SERVICE  = 'jobs-service'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare image tag') {
            steps {
                script {
                    def shortCommit = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                    env.IMAGE_TAG = "${env.BUILD_NUMBER}-${shortCommit}"
                    echo "Image: ${env.REGISTRY}/${env.SERVICE}:${env.IMAGE_TAG}"
                }
            }
        }

        stage('Build and test') {
            steps {
                sh '''
                    chmod +x mvnw
                    ./mvnw -B clean verify
                '''
            }
            post {
                always {
                    junit(
                        allowEmptyResults: true,
                        testResults: 'target/surefire-reports/*.xml'
                    )
                }
            }
        }

        stage('Build Docker image') {
            steps {
                sh '''
                    docker build \
                        --pull \
                        -t "$REGISTRY/$SERVICE:$IMAGE_TAG" \
                        -t "$REGISTRY/$SERVICE:latest" \
                        .
                '''
            }
        }

        stage('Push Docker image') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKERHUB_USERNAME',
                        passwordVariable: 'DOCKERHUB_TOKEN'
                    )
                ]) {
                    sh '''
                        echo "$DOCKERHUB_TOKEN" | docker login \
                            --username "$DOCKERHUB_USERNAME" \
                            --password-stdin

                        docker push "$REGISTRY/$SERVICE:$IMAGE_TAG"
                        docker push "$REGISTRY/$SERVICE:latest"
                        docker logout
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Build successful: ${env.REGISTRY}/${env.SERVICE}:${env.IMAGE_TAG}"
        }
        failure {
            echo "Build failed. Check the Jenkins console output."
        }
        cleanup {
            sh '''
                if [ -n "$IMAGE_TAG" ]; then
                    docker image rm "$REGISTRY/$SERVICE:$IMAGE_TAG" || true
                fi
            '''
        }
    }
}
