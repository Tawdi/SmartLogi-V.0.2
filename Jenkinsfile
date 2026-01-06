pipeline {
    agent any

    tools {
        maven 'Maven3'      // Doit correspondre au nom dans Manage Jenkins > Global Tool Configuration
        jdk   'JDK-JNK'     // Idem pour le JDK
    }

    environment {
        IMAGE_NAME   = 'tawdi/smartlogi'
        IMAGE_TAG    = '0.1'
        DOCKER_CREDS = 'docker-hub-credentials'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Récupération du code depuis GitHub...'
                git branch: 'main',
                    url: 'https://github.com/Tawdi/SmartLogi-V.0.2.git'
                // credentialsId: 'github-credentials'  // à décommenter si repo privé
            }
        }

        stage('Build & Test') {
            steps {
                echo 'Compilation et exécution des tests...'
                sh 'mvn clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/**/*.xml'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Construction de l’image Docker...'
                // Attention : pour que ${IMAGE_NAME} et ${IMAGE_TAG} soient résolus, utiliser des guillemets doubles
                sh "mvn spring-boot:build-image -Dspring-boot.build-image.imageName=${IMAGE_NAME}:${IMAGE_TAG}"
                // Alternative Dockerfile classique :
                // sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Push Docker Image') {
            when {
                branch 'main'
            }
            steps {
                echo 'Push de l’image sur Docker Hub...'
                script {
                    docker.withRegistry('', DOCKER_CREDS) {
                        sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
                        sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                        sh "docker push ${IMAGE_NAME}:latest"
                    }
                }
            }
        }

        stage('Deploy for Test') {
            when {
                branch 'main'
            }
            steps {
                echo 'Déploiement simple pour vérification...'
                sh '''
                    docker stop smartlogi-test || true
                    docker rm smartlogi-test || true
                '''
                sh """
                    docker run -d --name smartlogi-test -p 8081:8080 ${IMAGE_NAME}:${IMAGE_TAG}
                """
                echo 'Application déployée sur http://localhost:8081'
            }
        }
    }

    post {
        success {
            echo 'Pipeline réussie avec succès ! Ton application est prête.'
        }
        failure {
            echo 'Échec de la pipeline. Vérifie les logs ci-dessus.'
        }
        always {
            // Si ton agent n’a pas Docker, commente cette ligne
            sh 'docker system prune -f || true'
        }
    }
}
